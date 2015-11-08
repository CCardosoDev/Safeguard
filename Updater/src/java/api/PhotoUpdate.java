package api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.Authentication;

/**
 *
 * @author claudia
 */
public class PhotoUpdate extends HttpServlet {
    
    UpdateState updateState = UpdateState.getInstance();
    

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

        //linkType: http://localhost:8080/SafeGuard_Composition/PhotoUpdate?updateID=12345&photoURL=http://teste.com

        response.setContentType("text/html;charset=UTF-8");

        String token = request.getParameter("token");
        if (token == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token can't be null or empty");
            return;
        }

        String userID = Authentication.authenticateID(response, token);
        if(userID == null){
            return;
        }

        String updateID = request.getParameter("updateID");
        if (updateID == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "updateID can't be null or empty");
            return;
        }
        String photoURL = request.getParameter("photoURL");
        if (photoURL == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "photoURl can't be null or empty");
            return;
        }
        
        updateState.photos.put(updateID, photoURL);
        System.out.println("Photo update: " + updateID + " " + photoURL);
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
        return "Photo Update";
    }// </editor-fold>
}
