package api;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Address;
import util.Authentication;

/**
 *
 * @author claudia
 */
public class EditProfile extends HttpServlet {

    @Resource(name = "profilesRef")
    private DataSource profilesRef;

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
        response.setContentType("text/html;charset=UTF-8");

        BufferedReader bufferedReader = request.getReader();

        String token;
        int profileID = 0;
        String fullName;
        String photoLink;
        String gender;
        String observations;
        String bloodtype;
        String androidID;
        Timestamp bDay;
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
        String userID = Authentication.authenticate(response, token);
        if (userID == null) {
            return;
        }
        try {
            jsonObject = new JSONObject(sentJson);
            profileID = jsonObject.getInt("profileID");
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (profileID)");
            return;
        }
        if (profileID == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The profileID can't be 0 or empty");
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
            observations = jsonObject.getString("observations");
        } catch (JSONException ex) {
            observations = null;
        }

        try {
            bDay = Timestamp.valueOf(jsonObject.getString("bDay") + " 00:00:00");
        } catch (JSONException ex) {
            bDay = null;
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
            System.out.println(jsonAddresses.length());
            for (int i = 0; i < jsonAddresses.length(); i++) {
                try {
                    JSONObject jsonAddress = jsonAddresses.getJSONObject(i);
                    Address address = new Address(
                            jsonAddress.getString("streetAddress"),
                            jsonAddress.getString("locality"),
                            jsonAddress.getString("postCode"),
                            jsonAddress.getString("countryName"));
                    addresses.add(address);
                } catch (JSONException e) {
                    continue;
                }

            }
        } catch (JSONException ex) {
            System.out.println("Entrou");
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

        Connection connection;
        try {
            connection = profilesRef.getConnection();
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            return;
        }

        if (connection == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            return;
        }
        PreparedStatement statement ;
        ResultSet result;
        try {

            String query = "SELECT editProfile(?, ?, ?, ?, ?, ?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
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
            if (!result.getBoolean(1)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not edit profile");
                return;
            }
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            try {
                connection.close();
            } catch (SQLException ex4) {
            }
            return;
        }


        try {

            String query = "SELECT removeEmails(?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            result = statement.executeQuery();
            result.next();
            result.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("emails1");
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
                System.out.println("emails2");
            }
        }


        try {
            String query = "SELECT removePhones(?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            result = statement.executeQuery();
            result.next();
            result.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("phones1");
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
                System.out.println("phones2");
            }
        }


        try {

            String query = "SELECT removePersonalPages(?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            result = statement.executeQuery();
            result.next();
            result.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("personalPages1");
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
                System.out.println("personalPages2");
            }
        }


        try {

            String query = "SELECT removeAddresses(?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            result = statement.executeQuery();
            result.next();
            result.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("address1");
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
                System.out.println("address2");
            }
        }


        try {

            String query = "SELECT removeDiseases(?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            result = statement.executeQuery();
            result.next();
            result.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("diseases");
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
                System.out.println("diseases2");
            }
        }


        try {

            String query = "SELECT removeAllergies(?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            result = statement.executeQuery();
            result.next();
            result.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("allergies1");
        }
        for (String allergy : allergies) {
            try {

                String query = "SELECT insertAllergy(?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                statement.setString(2, allergy);
                result = statement.executeQuery();
                result.next();
                result.close();
                statement.close();
            } catch (SQLException ex) {
                System.out.println("allergies2");
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
        return "Edit Profile";
    }// </editor-fold>
}
