/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package location;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ejb.Stateless;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import utils.DatabaseManager;
import utils.Globals;

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
    @Path("getCurrentLocation/{token}/{androidId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public LocationSample getLocation(@PathParam("token") String token,
                                        @PathParam("androidId") String androidId) {
        
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
                                        "WHERE D.androidId = ? " +
                                        "ORDER BY L.createdat DESC " +
                                        "LIMIT 1");
            
            preparedStatement.setString(1, androidId);
            
            result = preparedStatement.executeQuery();
            
            while(result.next()) {
                
                double latitude = result.getDouble("latitude");
                double longitude = result.getDouble("longitude");
                
                sample = new LocationSample(latitude, longitude, androidId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if(sample==null)
            return new LocationSample(0, 0, "error");
        
        return sample;
    }

    
    
    @Path("setCurrentLocation/{token}/{androidId}/{profileId}/{latitude}/{longitude}")
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public String setCurrentLocation(@PathParam("token") final String token,
                                        @PathParam("androidId") String androidId,
                                        @PathParam("profileId") final String profileId,
                                        @PathParam("latitude") final double latitude,
                                        @PathParam("longitude") final double longitude) {
        
        String toReturn = "<status>ok</status>";
        ResultSet result = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        int deviceId;
        
        connect = DatabaseManager.getConnectionToDatabase();
        
        try {
            preparedStatement = connect
                    .prepareStatement("SELECT deviceId FROM Devices " +
                                      "WHERE androidId = ? ");
            
            preparedStatement.setString(1, androidId);
            
            result = preparedStatement.executeQuery();
            
            result.next();
            
            deviceId = result.getInt("deviceId");
            
            statement = connect.createStatement();
            
            statement.executeUpdate("INSERT INTO Location values (0, " + latitude + ", " + longitude + ", default, " + deviceId + ")");
            
        } catch (SQLException ex) {
            
            return "<status>error</status>";
        }
        
        /* Informar composição */
        new Thread(new Runnable() {

            @Override
            public void run() {

                StringBuffer output = new StringBuffer();
                CertificateFactory cf = null;
                try {
                        cf = CertificateFactory.getInstance("X.509");
                } catch (CertificateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                InputStream caInput = null;
                try {

                    caInput = new BufferedInputStream(new FileInputStream("/var/www/safeguard.redes-215.crt"));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Certificate ca = null;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } catch (CertificateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } finally {
                    try {
                            caInput.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                try {

                    // Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry("ca", ca);

                    // Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(keyStore);

                    // Create an SSLContext that uses our TrustManager
                    SSLContext context = SSLContext.getInstance("TLS");
                    context.init(null, tmf.getTrustManagers(), null);

                    String urlParameters = "";
                    
                    // Tell the URLConnection to use a SocketFactory from our SSLContext
                    URL url = new URL(Globals.COMPOSITION_SERVER_ADDRESS_SECURE +
                                        "/SafeGuard_Composition/UpdateLocation?" + 
                                        "token=" + token + 
                                        "&profileID="+ profileId +
                                        "&latitude=" + latitude +
                                        "&longitude=" + longitude);
                    HttpsURLConnection urlConnection =
                        (HttpsURLConnection)url.openConnection();
                    urlConnection.setHostnameVerifier(new HostnameVerifier() {

                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                    // TODO Auto-generated method stub
                                    return true;
                            }
                    });
                    urlConnection.setSSLSocketFactory(context.getSocketFactory());
                    
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST"); 
                    urlConnection.setRequestProperty("Content-Type", "application/json"); 
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

                    urlConnection.connect();

                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    InputStream is;
                    if (urlConnection.getResponseCode() == 201) {
                        is = urlConnection.getInputStream();
                    } else {
                        is = urlConnection.getErrorStream();
//                        toReturn = urlConnection.getResponseMessage();
                    }

                    urlConnection.disconnect();
                }
                catch(Exception ex) {

                    ex.printStackTrace();
                }
            }
        }).start();
        
        return toReturn;
    }
}