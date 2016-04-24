package com.mikerawding.cs523project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.Map;

/**
 * This class connects to a MySQL database and performs basic operations.
 * 
 * @author Mike Rawding
 * CS523 Project - SUNY Polytechnic Institute - Spring 2016
 */
public class Operations {
    
    private static final String DATABASE_URL = 
            "jdbc:mysql://localhost:3306/uss_enterprise?autoReconnect=true&useSSL=false";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "password";
    
    private Connection connection;
    private Statement sqlStatement;
    private ResultSet sqlResults;
    
    //Establish connection with database
    public Operations(){
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER,DATABASE_PASSWORD);
            sqlStatement = connection.createStatement();
        }catch (Exception ex){
            System.err.println("Error Connecting to Database.");
            ex.printStackTrace();
            
        }
    }
    
    public void addToDatabase(String name, String rank){
        String query = "INSERT INTO crew_members VALUES (\'" + name + "\', \'" + rank + "\')";
        try {
            sqlStatement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addToDatabase(Map<String, String> data){
        for (Map.Entry<String, String> datum : data.entrySet()){
            addToDatabase(datum.getKey(), datum.getValue());
        }
    }

    public boolean nameInDatabase(String name){
        return countNameOccurences(name) > 0;
    }
    
    public int countNameOccurences(String name){
        String query = "SELECT COUNT(*) FROM crew_members WHERE name=\'" + name + "\';";
        int count = -1;
        try {
            sqlResults = sqlStatement.executeQuery(query);
            while(sqlResults.next()){
                count = sqlResults.getInt("COUNT(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;
    }

    public boolean rankInDatabase(String rank){
        return countRankOccurences(rank) > 0;
    }
    
    public int countRankOccurences(String rank){
        String query = "SELECT COUNT(*) FROM crew_members WHERE rank=\'" + rank + "\';";
        int count = -1;
        try {
            sqlResults = sqlStatement.executeQuery(query);
            while(sqlResults.next()){
                count = sqlResults.getInt("COUNT(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;
    }
    
    public int databaseSize(){
        String query = "SELECT COUNT(*) FROM crew_members;";
        int count = -1;
        try {
            sqlResults = sqlStatement.executeQuery(query);
            while(sqlResults.next()){
                count = sqlResults.getInt("COUNT(*)");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;
    }
    
    public void purgeDatabase(){
        String query = "DELETE FROM crew_members;";
        try {
            sqlStatement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static long fibonacci(int num){
        if (num <= 2) return 1L;
        return fibonacci(num-1) + fibonacci(num-2);
    }

}
