/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import utils.DatabaseManager;

/**
 * REST Web Service
 *
 * @author luis
 */
@Stateless
@Path("Location")
public class LocationWS {

    @Context
    private UriInfo context;

    private Connection connect = null;
    
    /**
     * Creates a new instance of GetLocationResource
     */
    public LocationWS() {
    }

    /**
     * Retrieves representation of an instance of location.GetLocationResource
     * @return an instance of java.lang.String
     */
    @Path("getCurrentLocation/{externalId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public LocationSample getLocation(@PathParam("externalId") String externalId) {
        
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        LocationSample sample = null;
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        if(connect == null)
            System.err.println("null connection");
        
        try {
            preparedStatement = connect.prepareStatement("SELECT latitude, longitude FROM Location L " +
                                        "INNER JOIN Devices D " +
                                        "ON L.deviceId = D.deviceId " +
                                        "WHERE D.externalId = ? " +
                                        "ORDER BY L.createdat DESC " +
                                        "LIMIT 1");
            
            preparedStatement.setString(1, externalId);
            
            result = preparedStatement.executeQuery();
            
            while(result.next()) {
                
                double latitude = result.getDouble("latitude");
                double longitude = result.getDouble("longitude");
                
                sample = new LocationSample(latitude, longitude, externalId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if(sample==null)
            return new LocationSample(0, 0, "error");
        
        return sample;
    }

    /**
     * PUT method for updating or creating an instance of GetLocationResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
    
    
    @Path("setCurrentLocation/{externalId}/{latitude}/{longitude}")
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public String setCurrentLocation(@PathParam("externalId") String externalId,
                                        @PathParam("latitude") double latitude,
                                        @PathParam("longitude") double longitude) {
        
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        int deviceId;
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        try {
            preparedStatement = connect
                    .prepareStatement("SELECT deviceId FROM Devices " +
                                      "WHERE externalId = ? ");
            
            preparedStatement.setString(1, externalId);
            
            result = preparedStatement.executeQuery();
            
            result.next();
            
            deviceId = result.getInt("deviceId");
            
            statement = connect.createStatement();
            
            statement.executeUpdate("INSERT INTO Location values (0, " + latitude + ", " + longitude + ", default, " + deviceId + ")");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return "<status>ok</status>";
    }
}
