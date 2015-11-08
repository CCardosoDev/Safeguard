/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package poking;

import gcm.GoogleCloudMessagingClient;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import utils.DatabaseQueries;
import utils.Globals;

/**
 * REST Web Service
 *
 * @author luis
 */
@Path("poking")
public class PokingWS {

    @Context
    private UriInfo context;
    
    private GoogleCloudMessagingClient gcmClient;

    /**
     * Creates a new instance of PokingWS
     */
    public PokingWS() {
    }

    @GET
    @Path("/request/{token}/{androidId}")
    @Produces("application/xml")
    public String pokeRequest(@PathParam("token") String token,
            @PathParam("androidId") String androidId) {
        
        /* Verifica se existe um Device com o androidID fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByAndroidId(androidId)) {
            
            return "<status>error</status>";
        }
        
        /* Nr do pedido */
        long requestId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        
        /* Envia mensagem de POKE_REQUEST ao Device */
        String gcmId;
        
        gcmId = DatabaseQueries.getGcmIdByAndroidId(androidId);
        
        if(gcmId == null) {
            
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendPokeRequest(gcmId, token, requestId);
        
        return "<status>" + requestId + "</status>";
    }

    @GET
    @Path("/offer/{token}/{androidId}/{requestId}/{poke}")
    @Produces("application/xml")
    public String pokeOffer(@PathParam("token") final String token,
            @PathParam("androidId") String androidId,
            @PathParam("requestId") final String requestId,
            @PathParam("poke") final String poke) {
        
        /* Verifica se existe um Device com o token fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByTokenByAndroidId(token, androidId)) {
            
            return "<status>error</status>";
        }
        
        /* informa composição do poke */
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
                                        "/SafeGuard_Composition/PokeUpdate?" +
                                        "token=" + token + 
                                        "&updateID=" + requestId +
                                        "&pokeType=" + poke);
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
                        System.out.println("OK to poke from Composition");
                    } else {
                        is = urlConnection.getErrorStream();
                        System.out.println(urlConnection.getResponseMessage());
                    }

                    urlConnection.disconnect();
                }
                catch(Exception ex) {

                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }).start();
        
        return "<status>ok</status>";
    }
}
