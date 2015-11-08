/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package relayToComposition;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.Globals;

/**
 *
 * @author luis
 */
public class GetProfiles extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
            
        
        String token = request.getParameter("token");

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

                        // Tell the URLConnection to use a SocketFactory from our SSLContext
                        URL url = new URL("https://192.168.8.217:7706/PofilesService/GetProfiles?token=28GvXDoBtHazTMVDqLgu4Z");
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
                        InputStream in = urlConnection.getInputStream();


                int n = 1;
                while (n>0) {
                    byte[] b = new byte[4096];
                    n =  in.read(b);


                    if (n>0)
                        output.append(new String(b, 0, n));
                }
        }
        catch(Exception ex) {

                ex.printStackTrace();
        }
                
                

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.print(output.toString());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
