/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.DatabaseManager;

/**
 *
 * @author luis
 */
@WebServlet(name = "DatabseTest", urlPatterns = {"/DatabseTest"})
public class DatabseTest extends HttpServlet {

    private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;
    
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
        
        String device = "erro";
        
        try {
            
            /*Class.forName("com.mysql.jdbc.Driver");
            
            
            connect = DriverManager.getConnection("jdbc:mysql://192.168.3.106/Safeguard?"
              + "user=root&password=root");*/
            
            connect = DatabaseManager.getConnectionToDatabase();
            
            statement = connect.createStatement();

            resultSet = statement.executeQuery("select * from Devices");
            resultSet.next();
            device = resultSet.getString("deviceId");
        } /*catch (ClassNotFoundException ex) {
            System.out.println("erro");
            ex.printStackTrace();
        }*/ catch (SQLException ex) {
            System.out.println("erro2");
            ex.printStackTrace();
        }
        
        
     
            
        
            
        
        
        
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DatabseTest</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DatabseTest at " + request.getContextPath() + "</h1>");
            out.println("<p>" + device + "</p>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
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
