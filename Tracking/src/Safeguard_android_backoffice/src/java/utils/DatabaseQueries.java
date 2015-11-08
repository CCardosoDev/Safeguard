/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author luis
 */
public class DatabaseQueries {
    
    private static Connection connect = null;
    
    public static boolean checkIfEntryExistsByToken(String token) {
        
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        try {
            preparedStatement = connect.prepareStatement("SELECT token " +
                                                            "FROM Devices " + 
                                                            "WHERE token = ?");
            preparedStatement.setString(1, token);
            
            results = preparedStatement.executeQuery();
            
            if(results.next()) {
                
                return true;
            }
        } 
        catch (SQLException ex) {
            
            ex.printStackTrace();
        }
        
        return false;
    }
    
    public static String getGcmIdByToken(String token) {
        
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        try {
            preparedStatement = connect.prepareStatement("SELECT gcmId " +
                                                            "FROM Devices " + 
                                                            "WHERE token = ?");
            preparedStatement.setString(1, token);
            
            results = preparedStatement.executeQuery();
            
            if(results.next()) {
                
                return results.getString("gcmId");
            }
        } 
        catch (SQLException ex) {
            
            ex.printStackTrace();
        }
        
        return null;
    }
}
