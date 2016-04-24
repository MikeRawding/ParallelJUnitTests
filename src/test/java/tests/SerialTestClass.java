package tests;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;

//Class that we are testing
import com.mikerawding.cs523project.Operations;

/**
 * Example of a class containing tests that cannot be run in parallel
 * because they need to modify the database.
 * 
 * This test has normal JUnit annotations.  It is guaranteed to be the only class
 * running and tests are run in series.
 * 
 * @author Mike Rawding
 * CS523 Project - SUNY Polytechnic Institute - Spring 2016
 */
public class SerialTestClass {
    
    /*
    * @Before has standard JUnit usage.  clearDatabase() will be run before each test.
    */
    @Before
    public void clearDatabase(){
        System.out.println("Clearing database for SerialTestClass...");
        Operations testOperations = new Operations();
        testOperations.purgeDatabase();
    }
    
    @Test
    public void testAddToDatabaseSingle(){
        System.out.println("Starting SerialTestClass.testAddToDatabaseSingle...");
        Operations testOperations = new Operations();
        
        try {
            testOperations.addToDatabase("Picard", "Captain");
            Thread.sleep(100);
            testOperations.addToDatabase("Riker", "Commander");
            Thread.sleep(100);
            testOperations.addToDatabase("Data", "Lieutenant Commander");
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        }
        
        System.out.println("Ending SerialTestClass.testAddToDatabaseSingle...");
        Assert.assertTrue(testOperations.nameInDatabase("Picard"));
        Assert.assertEquals(3, testOperations.databaseSize());
        Assert.assertFalse(testOperations.nameInDatabase("Crusher"));
    }
    
    @Test
    public void testAddToDatabseMulti(){
        System.out.println("Starting SerialTestClass.testAddToDatabaseMulti...");
        Operations testOperations = new Operations();
        Map<String, String> testData = new HashMap<>();

        for (int i = 0; i < 500; i++){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread Interupted.");
            }
            testData.put(generateString(12), "Ensign");
        }
        testOperations.addToDatabase(testData);
        
        System.out.println("Ending SerialTestClass.testAddToDatabaseMulti...");
        Assert.assertEquals(500, testOperations.databaseSize());
        Assert.assertFalse(testOperations.nameInDatabase("Picard"));
        for (String testName : testData.keySet()){
           Assert.assertTrue(testOperations.nameInDatabase(testName));
        }
    }
    
    private String generateString(int size){
        String result = "";
        for (int i = 0; i < size; i++){
            result += (char) ('a' + (Math.random() * (('z'-'a')+1)));
        }
        return result;
    }
}
