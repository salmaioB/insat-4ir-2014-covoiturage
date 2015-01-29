/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit.admin;

import covoit.Admin;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Kapouter
 */
@WebServlet(name="PlacesServlet", urlPatterns={"/PlacesServlet"})
public class PlacesServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PlacesServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PlacesServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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

        if (request.getParameter("Create") != null) {
            String placeName = request.getParameter("placeName");
            String placeAddress = request.getParameter("placeAddress");

            if (!placeName.isEmpty() && !placeAddress.isEmpty()) {
                try {
                    Admin.addPlace(placeName, placeAddress);
                    request.getRequestDispatcher("manageWorkplaces.jsp").forward(request, response);
                } catch (SQLException e) {
                    String erreur = e.getMessage();
                    request.setAttribute("erreur", erreur);
                    request.getRequestDispatcher("manageWorkplaces.jsp").forward(request, response);
                    return;
                }
            } else {
                response.sendRedirect("manageWorkplaces.jsp");
                return;
            }
        } else if (request.getParameter("Delete") != null) {
            String name = (String) request.getParameter("nomlieu");

            try {
                Admin.deletePlace(name);
                request.getRequestDispatcher("manageWorkplaces.jsp").forward(request, response);
                return;
            } catch (SQLException ex) {
                request.setAttribute("erreurD", ex.getMessage());
                request.getRequestDispatcher("manageWorkplaces.jsp").forward(request, response);
                return;
            }
        } else {
            response.sendRedirect("manageWorkplaces.jsp");
            return;
        }
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
