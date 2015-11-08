package api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
public class GetProfiles extends HttpServlet {

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

        String token = request.getParameter("token");
        if (token == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
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
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            return;
        }

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String query = "SELECT * FROM getProfiles(?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, userID);
            result = statement.executeQuery();
            while (result.next()) {
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("profileID", result.getInt(1));
                jsonMap.put("fullName", result.getString(2));
                jsonMap.put("photoLink", result.getString(3));
                jsonMap.put("gender", result.getString(4));
                String bDay = result.getString(5);
                if (bDay != null) {
                    jsonMap.put("bDay", Timestamp.valueOf(bDay).toString().split(" ")[0]);
                } else {
                    jsonMap.put("bDay", null);
                }
                jsonMap.put("observations", result.getString(6));
                jsonMap.put("bloodType", result.getString(7));
                jsonMap.put("androidID", result.getString(8));
                mapList.add(jsonMap);
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            try {
                connection.close();
            } catch (SQLException ex2) {
            }
            return;
        }

        for (Map<String, Object> map : mapList) {
            try {
                String query = "SELECT * FROM address WHERE fk_profile = ?;";
                statement = connection.prepareStatement(query);
                statement.setInt(1, (int) map.get("profileID"));
                result = statement.executeQuery();
                ArrayList<Address> addresses = new ArrayList<>();
                while (result.next()) {
                    addresses.add(new Address(result.getString(3), result.getString(4), result.getString(5), result.getString(6)));
                }
                map.put("addresses", addresses);
                result.close();
                statement.close();
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
                try {
                    connection.close();
                } catch (SQLException ex2) {
                }
                return;
            }

            try {
                String query = "SELECT * FROM allergy WHERE fk_profile = ?;";
                statement = connection.prepareStatement(query);
                statement.setInt(1, (int) map.get("profileID"));
                result = statement.executeQuery();
                ArrayList<String> allergies = new ArrayList<>();
                while (result.next()) {
                    allergies.add(result.getString(3));
                }
                map.put("allergies", allergies);
                result.close();
                statement.close();
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
                try {
                    connection.close();
                } catch (SQLException ex2) {
                }
                return;
            }

            try {
                String query = "SELECT * FROM disease WHERE fk_profile = ?;";
                statement = connection.prepareStatement(query);
                statement.setInt(1, (int) map.get("profileID"));
                result = statement.executeQuery();
                ArrayList<String> diseases = new ArrayList<>();
                while (result.next()) {
                    diseases.add(result.getString(3));
                }
                map.put("diseases", diseases);
                result.close();
                statement.close();
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
                try {
                    connection.close();
                } catch (SQLException ex2) {
                }
                return;
            }

            try {
                String query = "SELECT * FROM email WHERE fk_profile = ?;";
                statement = connection.prepareStatement(query);
                statement.setInt(1, (int) map.get("profileID"));
                result = statement.executeQuery();
                ArrayList<String> emails = new ArrayList<>();
                while (result.next()) {
                    emails.add(result.getString(3));
                }
                map.put("emails", emails);
                result.close();
                statement.close();
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
                try {
                    connection.close();
                } catch (SQLException ex2) {
                }
                return;
            }

            try {
                String query = "SELECT * FROM personalPage WHERE fk_profile = ?;";
                statement = connection.prepareStatement(query);
                statement.setInt(1, (int) map.get("profileID"));
                result = statement.executeQuery();
                ArrayList<String> personalPages = new ArrayList<>();
                while (result.next()) {
                    personalPages.add(result.getString(3));
                }
                map.put("personalPages", personalPages);
                result.close();
                statement.close();
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
                try {
                    connection.close();
                } catch (SQLException ex2) {
                }
                return;
            }

            try {
                String query = "SELECT * FROM phone WHERE fk_profile = ?;";
                statement = connection.prepareStatement(query);
                statement.setInt(1, (int) map.get("profileID"));
                result = statement.executeQuery();
                ArrayList<String> phones = new ArrayList<>();
                while (result.next()) {
                    phones.add(result.getString(3));
                }
                map.put("phones", phones);
                result.close();
                statement.close();
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
                try {
                    connection.close();
                } catch (SQLException ex2) {
                }
                return;
            }
        }

        ArrayList<JSONObject> jsonos = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            JSONObject jSONObject = new JSONObject(map);
            jsonos.add(jSONObject);
        }

        JSONArray jSONArray = new JSONArray(jsonos);
        try {
            response.getWriter().write(jSONArray.toString(4));
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems creating json");
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
        processRequest(request, response);

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
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Get Profiles";
    }// </editor-fold>
}
