package api;

import com.es.georeferencingservice.Georeferencing;
import com.es.georeferencingservice.GeoreferencingAlreadyExistsException_Exception;
import com.es.georeferencingservice.GeoreferencingAuthenticationException_Exception;
import com.es.georeferencingservice.GeoreferencingSQLException_Exception;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.ws.WebServiceRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Address;
import util.Authentication;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 *
 * @author claudia
 */
public class InsertProfile extends HttpServlet {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/192.168.8.217_7706/GeoreferencingService/Georeferencing.wsdl")
    private Georeferencing service;
    @Resource(name = "profilesRef")
    private DataSource profilesRef;

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("InsertProfile");
        response.setContentType("text/html;charset=UTF-8");

        BufferedReader bufferedReader = request.getReader();

        String token;
        String fullName;
        String photoLink;
        String gender;
        Timestamp bDay;
        String observations;
        String bloodtype;
        String androidID;
        ArrayList<String> emails = new ArrayList<>();
        ArrayList<String> phones = new ArrayList<>();
        ArrayList<String> personalPages = new ArrayList<>();
        ArrayList<Address> addresses = new ArrayList<>();
        ArrayList<String> diseases = new ArrayList<>();
        ArrayList<String> allergies = new ArrayList<>();

        String sentJson = new String();
        JSONObject jsonObject;

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sentJson += line;
        }
        try {
            jsonObject = new JSONObject(sentJson);
            token = jsonObject.getString("token");
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (token)");
            return;
        }
        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The token can't be null or empty");
            return;
        }

        try {
            fullName = jsonObject.getString("fullName");
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (fullName)");
            return;
        }
        if (fullName == null || fullName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The fullName can't be null or empty");
            return;
        }

        try {
            photoLink = jsonObject.getString("photoLink");
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (photoLink)");
            return;
        }

        if (photoLink == null || photoLink.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The photoLink can't be null or empty");
            return;
        }

        try {
            gender = jsonObject.getString("gender");
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (gender)");
            return;
        }

        if (gender == null || gender.length() != 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The gender can't be null, empty or have more than 1 letter");
            return;
        }

        try {
            bDay = Timestamp.valueOf(jsonObject.getString("bDay") + " 00:00:00");
        } catch (JSONException ex) {
            bDay = null;
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The bDay must have the yyyy-mm-dd format");
            return;
        }

        try {
            observations = jsonObject.getString("observations");
        } catch (JSONException ex) {
            observations = null;
        }

        try {
            bloodtype = jsonObject.getString("bloodType");
        } catch (JSONException ex) {
            bloodtype = null;
        }

        try {
            JSONArray jsonEmails = jsonObject.getJSONArray("emails");
            for (int i = 0; i < jsonEmails.length(); i++) {
                emails.add(jsonEmails.getString(i));
            }
        } catch (JSONException ex) {
        }

        try {
            JSONArray jsonPhones = jsonObject.getJSONArray("phones");
            for (int i = 0; i < jsonPhones.length(); i++) {
                phones.add(jsonPhones.getString(i));
            }
        } catch (JSONException ex) {
        }

        try {
            JSONArray jsonPages = jsonObject.getJSONArray("personalPages");
            for (int i = 0; i < jsonPages.length(); i++) {
                personalPages.add(jsonPages.getString(i));
            }
        } catch (JSONException ex) {
        }

        try {
            JSONArray jsonAddresses = jsonObject.getJSONArray("addresses");
            for (int i = 0; i < jsonAddresses.length(); i++) {

                JSONObject jsonAddress = jsonAddresses.getJSONObject(i);
                Address address = new Address(
                        jsonAddress.getString("streetAddress"),
                        jsonAddress.getString("locality"),
                        jsonAddress.getString("postCode"),
                        jsonAddress.getString("countryName"));
                addresses.add(address);

            }
        } catch (JSONException ex) {
        }

        try {
            JSONArray jsonDiseases = jsonObject.getJSONArray("diseases");
            for (int i = 0; i < jsonDiseases.length(); i++) {
                diseases.add(jsonDiseases.getString(i));
            }
        } catch (JSONException ex) {
        }

        try {
            JSONArray jsonAllergies = jsonObject.getJSONArray("allergies");
            for (int i = 0; i < jsonAllergies.length(); i++) {
                allergies.add(jsonAllergies.getString(i));
            }
        } catch (JSONException ex) {
        }

        try {
            androidID = jsonObject.getString("androidID");
        } catch (JSONException ex) {
            androidID = null;
        }

        String userID = Authentication.authenticate(response, token);
        if (userID == null) {
            return;
        }
        Connection connection;
        try {
            connection = profilesRef.getConnection();
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            return;
        }

        if (connection == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not insert profile - no connection to database");
            return;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        int profileID;
        try {
            String query = "SELECT insertProfile(?, ?, ?, ?, ?, ?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, userID);
            statement.setString(2, fullName);
            statement.setString(3, photoLink);
            statement.setString(4, gender);
            statement.setTimestamp(5, bDay);
            statement.setString(6, observations);
            if (bloodtype == null) {
                statement.setString(7, null);
            } else {
                statement.setString(7, bloodtype.toUpperCase());
            }
            if (androidID == null || androidID.isEmpty()) {
                statement.setString(8, null);
            } else {
                statement.setString(8, androidID);
            }
            result = statement.executeQuery();
            result.next();
            profileID = result.getInt(1);
            if (profileID == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not insert profile");
                try {
                    statement.close();
                    result.close();
                    connection.close();
                } catch (SQLException ex2) {
                }
                result.close();
                statement.close();
                return;
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex2) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex2) {
                }
            }
            try {
                connection.close();
            } catch (SQLException ex2) {
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            return;
        }


        for (String email : emails) {
            try {

                String query = "SELECT insertEmail(?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, email);
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("email");
            }
        }
        for (String phone : phones) {
            try {

                String query = "SELECT insertPhone(?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, phone);
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("phone");
            }
        }
        for (String personalPage : personalPages) {
            try {

                String query = "SELECT insertPersonalPage(?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, personalPage);
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("personalPage");
            }
        }
        for (Address address : addresses) {
            try {

                String query = "SELECT insertAddress(?, ?, ?, ?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, address.getStreetAddress());
                statement.setString(3, address.getLocality());
                statement.setString(4, address.getPostCode());
                statement.setString(5, address.getCountryName());
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("address");
            }
        }
        for (String disease : diseases) {
            try {

                String query = "SELECT insertDisease(?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, disease);
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("disease");
            }
        }
        for (String allergy : allergies) {
            try {

                String query = "SELECT insertallergy(?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, allergy);
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("allergy");
            }
        }
        try {
            result.close();
        } catch (SQLException ex2) {
        }
        try {
            statement.close();
        } catch (SQLException ex2) {
        }
        try {
            connection.close();
        } catch (SQLException ex2) {
        }


        String calendarToken = Authentication.getServerAccess(response, token, "calendar");
        String georefToken = Authentication.getServerAccess(response, token, "georeferencing");

        //georef insert user
        System.out.println(georefToken);
        if (georefToken != null) {
            try {
                this.createUser(georefToken, (new Integer(profileID)).toString());
                System.out.println((new Integer(profileID)).toString());
            } catch (GeoreferencingSQLException_Exception | GeoreferencingAuthenticationException_Exception | GeoreferencingAlreadyExistsException_Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        //inert user calendar
        if (calendarToken != null) {
            URL url = new URL("https://localhost:5001/user?token=" + calendarToken);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.connect();

            url = new URL("https://localhost:5001/profile/" + profileID + "?token=" + calendarToken);

            con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.connect();

            if (con.getResponseCode() == 200) {
                InputStream in = con.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                while ((line = bufferedReader.readLine()) != null) {
                    sentJson += line;
                }
            }/* else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not insert profile in calendar");
            }*/


        }
    }

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
        return "Insert Profile";
    }// </editor-fold>

    private void createUser(java.lang.String token, java.lang.String childUserID) throws GeoreferencingSQLException_Exception, GeoreferencingAuthenticationException_Exception, GeoreferencingAlreadyExistsException_Exception {
        com.es.georeferencingservice.GeoreferencingImpl port = service.getGeoreferencingImplPort();
        port.createUser(token, childUserID);
    }
}
