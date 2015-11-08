/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package device;

import gcm.GoogleCloudMessagingClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import utils.DatabaseManager;

/**
 * REST Web Service
 *
 * @author luis
 */
@Path("Registration")
public class RegistrationWS {

    @Context
    private UriInfo context;
    
    private Connection connect = null;
    
    private GoogleCloudMessagingClient gcmClient;

    /**
     * Creates a new instance of RegistrationWS
     */
    public RegistrationWS() {
    }

    /**
     * Retrieves representation of an instance of device.RegistrationWS
     * @return an instance of java.lang.String
     */
    @Path("{androidId}/{gcmId}")
    @GET
    @Produces("application/xml")
    public String newDevice(@PathParam("androidId") String androidId,
                            @PathParam("gcmId") String gcmId) {
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        /* Verifica se já existe algum device com aquele androidId */
        if(checkIfEntryExists(androidId, gcmId)) {
            
            return "<deviceId>" + getDeviceId(androidId) + "</deviceId>";
        }
        
        /* caso não exista procede ao registo */
        createNewDeviceEntry(androidId, gcmId);
        
        return "<deviceId>" + getDeviceId(androidId) + "</deviceId>";
    }

    @Path("updateToken/{androidId}/{token}")
    @GET
    @Produces("application/xml")
    public String setToken(@PathParam("androidId") String androidId,
                            @PathParam("token") String token) {
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        /* Verifica se já existe algum device com aquele androidId, senao da erro */
        if(!checkIfEntryExists(androidId)) {
            
            return "<status>error</status>";
        }
        
        /* Actualizar a BD */
        updateToken(androidId, token);
        
        /* informar o telemóvel do seu token */
        String gcmId;
        
        gcmId = getGcmIdByAndroidId(androidId);
        
        if(gcmId.equals("fail")) {
            
            System.err.println("Não foi possível encontrar o device na BD");
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendToken(gcmId, token);
        
        return "<status>ok</status>";
    }
    
    private void updateToken(String androidId, String token) {
        
        PreparedStatement preparedStatement = null;
        
        try {
            preparedStatement = connect
                    .prepareStatement("UPDATE Devices SET token = ? WHERE androidID = ?");
            
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, androidId);
            
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean checkIfEntryExists(String androidId) {
        
        Statement statement;
        ResultSet results = null;
        
        try {
            statement = connect.createStatement();
            
            results = statement.executeQuery("SELECT androidId FROM Devices");
            
             while(results.next()) {
            
                 if(results.getString("androidId").equals(androidId)) {
                     
                     return true;
                 }
                 
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }

    private boolean checkIfEntryExists(String androidId, String gcmId) {
        
        Statement statement;
        ResultSet results = null;
        
        try {
            statement = connect.createStatement();
            
            results = statement.executeQuery("SELECT androidId, gcmId FROM Devices");
            
             while(results.next()) {
            
                 if(results.getString("androidId").equals(androidId)
                        && results.getString("gcmId").equals(gcmId)) {
                     
                     return true;
                 }
                 /* login com o mesmo smartphone */
                 else if(results.getString("androidId").equals(androidId)) {
                     
                     updateEntryByAndroidId(androidId, gcmId);
                     return true;
                 }
                 /* login com outro smartphone */
                 else if(results.getString("gcmId").equals(gcmId)) {
                     
                     updateEntryByGcmId(androidId, gcmId);
                     return true;
                 }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
    

    private int getDeviceId(String androidId) {
        
        Statement statement;
        ResultSet results = null;
        
        try {
            statement = connect.createStatement();
            
            results = statement.executeQuery("SELECT deviceId, androidId FROM Devices");
            
             while(results.next()) {
            
                 if(results.getString("androidId").equals(androidId)) {
                     
                     return results.getInt("deviceId");
                 }
                 
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // never happens (+/-)
        return -1;
    }
    
    private void createNewDeviceEntry(String androidId, String gcmId) {
        
        PreparedStatement preparedStatement = null;
        
        try {
            preparedStatement = connect
                    .prepareStatement("INSERT INTO Devices values ( 0, ?, ?, default, ? )");
            
            preparedStatement.setString(1, androidId);
            preparedStatement.setString(2, gcmId);
            preparedStatement.setString(3, "");
            
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } 
    }


    private void updateEntryByAndroidId(String androidId, String gcmId) {
        
        PreparedStatement preparedStatement = null;
        
        try {
            preparedStatement = connect
                    .prepareStatement("UPDATE Devices SET gcmId = ? WHERE androidID = ?");
            
            preparedStatement.setString(1, gcmId);
            preparedStatement.setString(2, androidId);
            
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    

    private void updateEntryByGcmId(String androidId, String gcmId) {
        
        PreparedStatement preparedStatement = null;
        
        try {
            preparedStatement = connect
                    .prepareStatement("UPDATE Devices SET androidId = ? WHERE gcmId = ?");
            
            preparedStatement.setString(1, androidId);
            preparedStatement.setString(2, gcmId);
            
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String getGcmIdByAndroidId(String androidId) {
        
        Statement statement;
        ResultSet results = null;
        
        try {
            statement = connect.createStatement();
            
            results = statement.executeQuery("SELECT androidId, gcmId FROM Devices");
            
             while(results.next()) {
            
                 if(results.getString("androidId").equals(androidId)) {
                     
                     return results.getString("gcmId");
                 }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return "fail";
    }
}
