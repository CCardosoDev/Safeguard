package api;

import util.Authentication;
import com.es.georeferencingservice.Coordinate;
import com.es.georeferencingservice.FenceID;
import com.es.georeferencingservice.Georeferencing;
import com.es.georeferencingservice.GeoreferencingAuthenticationException_Exception;
import com.es.georeferencingservice.GeoreferencingCoordinatesException_Exception;
import com.es.georeferencingservice.GeoreferencingEmptyException_Exception;
import com.es.georeferencingservice.GeoreferencingSQLException_Exception;
import com.es.georeferencingservice.GeoreferencingUserNotExistsException_Exception;
import com.es.georeferencingservice.GeorefererencingException_Exception;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.IDEmail;

/**
 *
 * @author claudia
 */
public class UpdateLocation extends HttpServlet {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/safeguardtracking.no-ip.org_8080/NotificationsService/NotificationsServiceImp.wsdl")
    private NotificationsServiceImp_Service service_1;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/192.168.8.217_7706/GeoreferencingService/Georeferencing.wsdl")
    private Georeferencing service;

    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        }
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        String token = request.getParameter("token");
        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token can't be null or empty");
            return;
        }

        IDEmail iDEmail = Authentication.authenticate(response, token);
        if (iDEmail == null) {
            return;
        }
        Integer profileID = 0;
        try {
            profileID = Integer.parseInt(request.getParameter("profileID"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "ProfileID has to be a number > 0");
        }
        if (profileID <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "ProfileID can't be null or empty");
            return;
        }

        String latitudeString = request.getParameter("latitude");
        if (latitudeString == null || latitudeString.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Latitude can't be null or empty");
            return;
        }
        Double latitude;
        try {
            latitude = Double.parseDouble(latitudeString);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Latitude value is not a double");
            return;
        }

        String longitudeString = request.getParameter("longitude");
        if (longitudeString == null || longitudeString.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Longitude can't be null or empty");
            return;
        }

        Double longitude;
        try {
            longitude = Double.parseDouble(longitudeString);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Longitude value is not a double");
            return;
        }

        String georefToken = Authentication.getServerAccess(response, token, "georeferencing");
        if (georefToken == null || georefToken.isEmpty()) {
            return;
        }

        String calendarToken = Authentication.getServerAccess(response, token, "calendar");
        if (calendarToken == null || calendarToken.isEmpty()) {
            return;
        }

        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(latitude);
        coordinate.setLongitude(longitude);
        try {
            insertCoordinate(georefToken, profileID.toString(), coordinate);
        } catch (GeoreferencingEmptyException_Exception | GeoreferencingUserNotExistsException_Exception | GeoreferencingAuthenticationException_Exception | GeoreferencingCoordinatesException_Exception | GeoreferencingSQLException_Exception ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());

            return;
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

        //coordenada -> 40.6340000, -8.6581500
        //fence -> (40.6700667, -8.7026667), (40.6605333, -8.5965833), (40.6194667, -8.6053667), (40.6189333, -8.7037833), (40.6700667, -8.7026667)
        URL obj = new URL("https://localhost:5001/event/" + profileID + "/" + sdf.format(date) + "/" + sdf.format(date) + "?token=" + calendarToken);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setUseCaches(false); // ignore caches
        con.setDoInput(true); // defaults to true, but doesn't hurt
        con.connect(); // connect to the server

        // Determine the proper inputstream to read	
        InputStream is;
        BufferedReader bufferedReader;
        if (con.getResponseCode() == 200) {
            is = con.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, con.getResponseMessage());
            return;
        }

        String sentJson = new String();
        ArrayList<Integer> activeFenceIDs = new ArrayList<>();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sentJson += line;
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(sentJson);
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON");
            return;
        }
        JSONObject jsonEventList;
        JSONArray activeFences = null;
        try {
            jsonEventList = jsonObject.getJSONObject("eventList");
            activeFences = jsonEventList.getJSONArray(profileID.toString());
        } catch (JSONException ex) {
        }

        try {
            if (activeFences != null && activeFences.length() > 0) {
                for (int i = 0; i < activeFences.length(); i++) {
                    activeFenceIDs.add(activeFences.getJSONObject(i).getInt("location"));
                }
            } else {
                return; // no fences active so he can't be out or inside
            }
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Can't extract fences location from calendar");
            return;
        }


        coordinate.setLatitude(latitude);
        coordinate.setLongitude(longitude);
        com.es.georeferencingservice.GeoreferencingImpl port = service.getGeoreferencingImplPort();
        try {
            java.util.List<FenceID> fencesResult = port.fencesContainingCoordinate(georefToken, profileID.toString(), coordinate);

            boolean found = false;
            for (FenceID fenceID : fencesResult) {
                if (activeFenceIDs.remove(new Integer(fenceID.getId()))) {
                    found = true;
                    break;
                }
            }
            //if he is not inside an active fence
            if (!found && iDEmail.getEmail() != null) {
                System.out.println("Sending mail");
                String fullName = Authentication.getFullName(response, token, profileID);
                if (fullName != null) {
                    this.sendEmail("safeguardapp@gmail.com", "appsafeguard", iDEmail.getEmail(), "Safeguard warning",
                            "The user " + fullName + " is in a not safe zone");
                } else {
                    this.sendEmail("safeguardapp@gmail.com", "appsafeguard", iDEmail.getEmail(), "Safeguard warning",
                            "The user " + profileID + " is in a not safe zone");
                }
            }

        } catch (GeoreferencingCoordinatesException_Exception | GeorefererencingException_Exception | CouldNotSendEmailException_Exception ex) {
            System.out.println(ex.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
    }

    private int insertCoordinate(String token, java.lang.String childUser, com.es.georeferencingservice.Coordinate coordinate) throws GeoreferencingEmptyException_Exception, GeoreferencingUserNotExistsException_Exception, GeoreferencingAuthenticationException_Exception, GeoreferencingCoordinatesException_Exception, GeoreferencingSQLException_Exception {
        com.es.georeferencingservice.GeoreferencingImpl port = service.getGeoreferencingImplPort();
        return port.insertCoordinate(token, childUser, coordinate);
    }

    private void sendEmail(java.lang.String sourceEmail, java.lang.String sourcePassword, java.lang.String destinationEmail, java.lang.String subject, java.lang.String body) throws CouldNotSendEmailException_Exception {
        api.NotificationsServiceImp port = service_1.getNotificationsServiceImpPort();
        port.sendEmail(sourceEmail, sourcePassword, destinationEmail, subject, body, null, null);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
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
        return "Location Update";
    }// </editor-fold>
}
