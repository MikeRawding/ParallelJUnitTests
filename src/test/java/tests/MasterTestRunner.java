package tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * This class runs the JUnit test classes listed in ALL_TESTS in the appropriate
 * manner based on the annotations used.
 * 
 * @author Mike Rawding
 * CS523 Project - SUNY Polytechnic Institute - Spring 2016
 */
public class MasterTestRunner {
    
    /***Register Classes Containing Tests Here***/
    Class[] ALL_TESTS={
            ParallelTestClass0.class,
            ParallelTestClass1.class,
            ParallelTestMethods.class,
            SerialTestClass.class};
    
    /***Set Maximum Threads Here***/
    private static final int MAX_THREADS = 4;

    /*
    * Use when all of the methods contained in the class can be run in parallel
    * with eachother and other classes.
    */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ParallelTestClass{
    };

    /*
    * Use when some or all of the methods contained in the class can be run in 
    * parallel with eachother, but it is not safe to run them with other classes.
    * The class is run in series with other classes.
    */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ContainsParallelTests{
    };
    
    /*
    * Use with @ContainsParallelTests to identify which methods are thread safe.
    */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ParallelTest{
    };
    
    /*
    * Use with @ContainsParallelTests to identify methods should be run before the paralell methods are run.
    */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface BeforeParallelTests{
    };
    
    @Test
    public void runAll(){
        
        Class[] parallelTests = {};
        Class[] seriesTests = {};
        
        ArrayList<Class> parallelTestsHelper = new ArrayList<>();
        ArrayList<Class> seriesTestsHelper = new ArrayList<>();
        ArrayList<Class> hybridTests = new ArrayList<>();
        ArrayList<Result> resultSet = new ArrayList<>();
        
        //sort classes int parallel, series, and hybrid.
        for (Class klass : ALL_TESTS){
            if(klass.isAnnotationPresent(ParallelTestClass.class)){
                parallelTestsHelper.add(klass);
            } else if (klass.isAnnotationPresent(ContainsParallelTests.class)){
                hybridTests.add(klass);
            } else {
                seriesTestsHelper.add(klass);
            }
        }
        
        seriesTests = seriesTestsHelper.toArray(new Class[0]);
        parallelTests = parallelTestsHelper.toArray(new Class[0]);
        
        //run series tests
        resultSet.add(JUnitCore.runClasses(seriesTests));
        
        //run parallel tests
        resultSet.add(JUnitCore.runClasses(new ParallelComputer(true, true), parallelTests));
        
        //run hybrid tests
        resultSet.addAll(testHybridClasses(hybridTests));

        //print test results
        System.out.println("\n-*-*-*-*-*- Test Results -*-*-*-*-*-\n");
        int testsFailed = 0;
        int testsRun = 0;
        Double passRate = 0.0;
        for (Result result : resultSet){
            testsRun += result.getRunCount();
            for (Failure failure : result.getFailures()){
                testsFailed++;
                System.out.println("TEST FAILURE");
                System.out.println(failure.getTestHeader());
                System.out.println(failure.getMessage());
                System.out.println("\n");
            }
        }

        DecimalFormat df = new DecimalFormat("#.##");
        passRate = 100 * ((double)testsRun - (testsFailed))/testsRun;
        
        System.out.println("Tests run: " + testsRun + ", Failures: " + testsFailed + ", Pass Rate: " + df.format(passRate) + "%");
        System.out.println("\n");
        
        Assert.assertTrue("Failures were encountered while running the test suite.  See output for full details.", testsFailed == 0);
    }
    
    //sort methods into before, parallel, and series
    private ArrayList<Result> testHybridClasses(ArrayList<Class> hybridTests){
        
        ArrayList<Result> results = new ArrayList<>();
        ArrayList<Method> serialMethods = new ArrayList<>();
        ArrayList<Method> parallelMethods = new ArrayList<>();
        ArrayList<Method> beforeMethods = new ArrayList<>();
        
        for (Class klass : hybridTests){
            for (Method method : klass.getDeclaredMethods()){
                if (method.isAnnotationPresent(Test.class)){
                    if (method.isAnnotationPresent(ParallelTest.class)){
                        parallelMethods.add(method);
                    } else {
                        serialMethods.add(method);
                    }
                } else if (method.isAnnotationPresent(BeforeParallelTests.class)){
                    beforeMethods.add(method);
                }
            }
            //run before methods
            executeBeforeMethods(klass, beforeMethods);
            
            //run parallel methods
            results.addAll(executeParallelMethods(klass, parallelMethods));
            
            //run serial methods
            results.addAll(executeSerialMethods(klass, serialMethods));
        }
        
        return results;
    }
    
    private void executeBeforeMethods(Class klass, ArrayList<Method> beforeMethods){
        for (Method method : beforeMethods){
            try {
                method.invoke(null);
            } catch (IllegalAccessException ex) {
            } catch (IllegalArgumentException ex) {
            } catch (InvocationTargetException ex) {
            }
        }
    }
    
    private ArrayList<Result> executeSerialMethods(Class klass, ArrayList<Method> serialMehtods){
        ArrayList<Result> results = new ArrayList<>();
        for (Method method : serialMehtods){
            results.add(new JUnitCore().run(Request.method(klass, method.getName())));
        }
        return results;
    }
    
    //Java built in thread safe variables
    private AtomicInteger activeThreads = new AtomicInteger(0);
    private List parallelResults = Collections.synchronizedList(new ArrayList<Result>());
    
    private ArrayList<Result> executeParallelMethods(Class klass, ArrayList<Method> parallelMehtods){
        parallelResults.clear();
        ArrayList<Result> result = new ArrayList<>();
        for(Method method : parallelMehtods){
            spawnTestThread(klass, method);
        }
        while (activeThreads.get() != 0){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread interupted");
            }
        }
        result.addAll(parallelResults);
        return result;
    }
    
    //Spawns a new test thread
    private void spawnTestThread(Class klass, Method method){
        while (activeThreads.get() == MAX_THREADS){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread interupted");
            }
        }
        activeThreads.incrementAndGet();
        new Thread (new testThread(klass, method)).start();
    }
    
    //JUnit run wrapped in a Runnable class
    private class testThread implements Runnable{

        private Class klass;
        private Method method;
        
        public testThread(Class klass, Method method){
            this.klass = klass;
            this.method = method;
        }
        
        @Override
        public void run() {
            parallelResults.add(new JUnitCore().run(Request.method(klass, method.getName())));
            activeThreads.decrementAndGet();
        }
    }
}
