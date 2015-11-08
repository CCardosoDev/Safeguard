package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import util.Authentication;

/**
 *
 * @author claudia
 */
public class RemoveProfiles extends HttpServlet {

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
        ArrayList<Integer> profileIDs = new ArrayList<>();
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

        try {
            JSONArray jsonIDs = jsonObject.getJSONArray("profileIDs");
            for (int i = 0; i < jsonIDs.length(); i++) {
                profileIDs.add(jsonIDs.getInt(i));
            }
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (profiles)");
            return;
        }

        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The token can't be null or empty");
            return;
        }
        if (profileIDs.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No profiles inserted");
            return;
        }

        String userID = Authentication.authenticate(response, token);
        if (userID == null) {
            return;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        for (Integer profileID : profileIDs) {
            try {
                connection = profilesRef.getConnection();
                String query = "SELECT removeProfile(?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1, profileID);
                result = statement.executeQuery();
                result.next();
                if (!result.getBoolean(1)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "The profile does not exist");
                }
            } catch (SQLException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
            }

        }

        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException ex2) {
        }
        try {
            if (statement != null) {
                statement.close();
            }
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
        return "Remove Profiles";
    }// </editor-fold>
}
