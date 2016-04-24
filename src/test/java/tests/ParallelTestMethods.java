package tests;

import org.junit.Assert;
import org.junit.Test;

import tests.MasterTestRunner.ContainsParallelTests;
import tests.MasterTestRunner.ParallelTest;
import tests.MasterTestRunner.BeforeParallelTests;

import com.mikerawding.cs523project.Operations;

/**
 * This is an example of a class that contains some tests that are safe to run concurrently with each other,
 * but may be effected by outside actions.  No other classes will be executed while these tests are
 * being run.
 * 
 * The order of events is:
 * 1) methods labeled @BeforeParallelTests (in series, no guaranteed order)
 * 2) tests labeled @ParallelTest (in parallel)
 * 3) other tests (in series, no guaranteed order)
 * 
 * @author Mike Rawding
 * CS523 Project - SUNY Polytechnic Institute - Spring 2016
 */
@ContainsParallelTests
public class ParallelTestMethods {

    @BeforeParallelTests
    public static void setUpClass() {
        System.out.println("Initializing Bridge...");
        Operations testOperations = new Operations();
        testOperations.purgeDatabase();
        testOperations.addToDatabase("Picard", "Captain");
        testOperations.addToDatabase("Riker", "Commander");
        testOperations.addToDatabase("Data", "Lieutenant Commander");
        testOperations.addToDatabase("Worf", "Lieutenant");
        testOperations.addToDatabase("La Forge", "Lieutenant");
    }
   
    @ParallelTest
    @Test
    public void test0(){
        System.out.println("Starting ParallelTestMethods.test0...");
        Operations testOperations = new Operations();
        try{
            Assert.assertTrue(testOperations.nameInDatabase("Picard"));
            Thread.sleep(1000);
            Assert.assertTrue(testOperations.rankInDatabase("Captain"));
            Thread.sleep(1000);
            Assert.assertEquals(2, testOperations.countRankOccurences("Lieutenant"));
            Thread.sleep(1000);
            Assert.assertEquals(1, testOperations.countRankOccurences("Captain"));
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        }
        System.out.println("Ending ParallelTestMethods.test0...");
    }
    
    @ParallelTest
    @Test
    public void test1(){
        System.out.println("Starting ParallelTestMethods.test1...");
        Operations testOperations = new Operations();
        try{
            Assert.assertTrue(testOperations.nameInDatabase("Riker"));
            Thread.sleep(1000);
            Assert.assertFalse(testOperations.rankInDatabase("Ensign"));
            Thread.sleep(1000);
            Assert.assertEquals(1, testOperations.countNameOccurences("Data"));
            Thread.sleep(1000);
            Assert.assertTrue(testOperations.nameInDatabase("La Forge"));
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        }
        System.out.println("Ending ParallelTestMethods.test1...");
    }
    
    @ParallelTest
    @Test
    public void test2(){
        System.out.println("Starting ParallelTestMethods.test2...");
        Operations testOperations = new Operations();
        try{
            Assert.assertFalse(testOperations.nameInDatabase("Crusher"));
            Thread.sleep(1000);
            Assert.assertFalse(testOperations.rankInDatabase("Counselor"));
            Thread.sleep(1000);
            Assert.assertEquals(5, testOperations.databaseSize());
            Thread.sleep(1000);
            Assert.assertEquals(1, testOperations.countRankOccurences("Lieutenant Commander"));
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        }
        System.out.println("Ending ParallelTestMethods.test2...");
    }
    
    @ParallelTest
    @Test
    public void test3(){
        System.out.println("Starting ParallelTestMethods.test3...");
        Operations testOperations = new Operations();
        try{
            Assert.assertTrue(testOperations.nameInDatabase("Riker"));
            Thread.sleep(1000);
            Assert.assertFalse(testOperations.rankInDatabase("Ensign"));
            Thread.sleep(1000);
            Assert.assertEquals(1, testOperations.countNameOccurences("Data"));
            Thread.sleep(1000);
            Assert.assertTrue(testOperations.nameInDatabase("La Forge"));
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        }
        System.out.println("Ending ParallelTestMethods.test3...");
    }
    
    @Test
    public void test4(){
        System.out.println("Starting ParallelTestMethods.test4 (not marked parallel)...");
        Operations testOperations = new Operations();
        try{
            testOperations.addToDatabase("Crusher", "Doctor");
            testOperations.addToDatabase("Troi", "Counselor");
            Thread.sleep(1000);
            Assert.assertTrue(testOperations.nameInDatabase("Crusher"));
            Assert.assertTrue(testOperations.rankInDatabase("Counselor"));
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        }
        System.out.println("Ending ParallelTestMethods.test4 (not marked parallel)...");
    }
}
