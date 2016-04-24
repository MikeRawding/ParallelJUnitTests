package tests;

import org.junit.Assert;
import org.junit.Test;


import com.mikerawding.cs523project.Operations;
import tests.MasterTestRunner.ParallelTestClass;

/**
 * This is an example of a class containing tests that are completely thread safe.
 * By applying the @ParallelTestClass annotation it is implied that all the methods in this class
 * can be run with each other as well as other any other methods listed in a @ParallelTestClass.
 * 
 * No guarantees are made about what order tests will be run in or what other test may be running
 * concurrently.
 * 
 * @author Mike Rawding
 * CS523 Project - SUNY Polytechnic Institute - Spring 2016
 */
@ParallelTestClass
public class ParallelTestClass1 {
    
    @Test
    public void fib50a(){
        System.out.println("Starting ParallelTestClass1.testfib50a...");
        long result = Operations.fibonacci(45);
        Assert.assertEquals(1134903170L, result);
        System.out.println("Ending ParallelTestClass1.testfib50a...");
    }
    
    @Test
    public void fib50b(){
        System.out.println("Starting ParallelTestClass1.testfib50b...");
        long result = Operations.fibonacci(45);
        Assert.assertEquals(1134903170L, result);
        System.out.println("Ending ParallelTestClass1.testfib50b...");
    }
}
