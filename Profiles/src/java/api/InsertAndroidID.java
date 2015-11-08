package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author claudia
 */
public class InsertAndroidID extends HttpServlet {

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
        int profileID;
        String androidID;

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
            jsonObject = new JSONObject(sentJson);
            androidID = jsonObject.getString("androidID");
        } catch (JSONException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (androidID)");
            return;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = profilesRef.getConnection();
            String query = "SELECT insertAndroidID(?, ?);";
            statement = connection.prepareStatement(query);
            statement.setInt(1, profileID);
            statement.setString(2, androidID);
            result = statement.executeQuery();
            result.next();
            if (!result.getBoolean(1)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not find profile");
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Occured a problem accessing database");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
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
        return "Insert AndroidID";
    }// </editor-fold>
}
