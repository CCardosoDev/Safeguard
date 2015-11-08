/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author luis
 */
public class DatabaseManager {
    
    private static DatabaseManager dbManager = null;
    private String dbUrl = Globals.MYSQL_SERVER_ADDRESS;
    private String dbName = "Safeguard";
    private String username = "root";
    private String password = "root";
    private static Connection connect = null;
    
    private DatabaseManager() {
        
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
            
            connect = DriverManager.getConnection("jdbc:mysql://" + dbUrl + "/" + dbName
                                                   + "?user=" + username + "&password=" + password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnectionToDatabase() {
        
        if(dbManager == null) {
            
            dbManager = new DatabaseManager();
        }
        
        return connect;
    }
    
    
    
    
}
