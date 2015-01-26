/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit.admin;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author polio
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/SearchServlet"})
public class SearchServlet extends HttpServlet {

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
            out.println("<title>Servlet SearchServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SearchServlet at " + request.getContextPath() + "</h1>");
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
        
            String idPlace = request.getParameter("placeSelected");
            String idCity = request.getParameter("citySelected");

            if (!idPlace.equals("0") && !idCity.equals("0")) {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    // --- Commentaire pour la suite ---
                    // Lancer la requête qui récupère les infos à afficher avec les 2 Id
                    // Présenter ceci sous forme de tableau
                    // Je ne sais pas comment afficher le tableau dans la JSP d'origine
                    // Donc obligation de formater une nouvelle fenêtre comme ci-dessous
                    
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Servlet SearchServlet</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h1>Idplace = " + idPlace + "</h1>");
                    out.println("<h1>IdCity = " + idCity + "</h1>");
                    out.println("</body>");
                    out.println("</html>");
                }
            }
            else if (idPlace.equals("0")) {
                String erreur = "Veuillez sélectionner un lieu de travail";
                request.setAttribute("erreur", erreur);
                request.getRequestDispatcher("nbUsersHouseWorkplace.jsp").forward(request, response);
            }
            else if (idCity.equals("0")) {
                String erreur = "Veuillez sélectionner un domicile";
                request.setAttribute("erreur", erreur);
                request.getRequestDispatcher("nbUsersHouseWorkplace.jsp").forward(request, response);
            }
            else {
                response.sendRedirect("nbUsersHouseWorkplace.jsp");
                return;
            }
        //processRequest(request, response);
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
