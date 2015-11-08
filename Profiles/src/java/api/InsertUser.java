package api;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.json.JSONObject;
import util.Authentication;

/**
 *
 * @author claudia
 */
public class InsertUser extends HttpServlet {

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

        String token;
        String userID;
        String sentJson = new String();
        JSONObject jsonObject;
        BufferedReader bufferedReader = request.getReader();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sentJson += line;
        }
        try {
            jsonObject = new JSONObject(sentJson);
            token = jsonObject.getString("token");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with JSON sent");
            return;
        }

        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token can't be null or empty");
            return;
        }

        userID = Authentication.authenticate(response, token);
        if (userID == null) {
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = profilesRef.getConnection();
            String query = "SELECT insertUser(?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, userID);
            result = statement.executeQuery();
            result.next();
            if (!result.getBoolean(1)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "The user already exists");
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
                } catch (SQLException ex2) {
                }
            }
        }

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
        return "Insert Profile";
    }// </editor-fold>
}
