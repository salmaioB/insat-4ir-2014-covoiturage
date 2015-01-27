/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit.admin;

import covoit.Admin;
import covoit.User;
import covoit.lib.BCrypt;
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
@WebServlet(name = "UsersServlet", urlPatterns = {"/UsersServlet"})
public class UsersServlet extends HttpServlet {

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
            out.println("<title>Servlet UsersServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UsersServlet at " + request.getContextPath() + "</h1>");
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
            String firstName = (String) request.getParameter("firstName");
            String lastName = (String) request.getParameter("lastName");
            String mailAddress = (String) request.getParameter("mailAddress");
            String driverN = (String) request.getParameter("driver");
            boolean driver = false;
            if (driverN.equals("YES")) {
                driver = true;
            }

            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                try {
                    String hash = BCrypt.hashpw("password", BCrypt.gensalt());
                    User.create(mailAddress, hash, firstName, lastName, driver);   
                } catch (SQLException e) {
                    String erreur = e.getMessage();
                    request.setAttribute("erreur", erreur);
                    request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                    return;
                }
                request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
            } else {
                response.sendRedirect("manageUsers.jsp");
            }

        } else if (request.getParameter("Modify") != null) {
            String firstName = (String)request.getParameter("firstName");
            String lastName = (String)request.getParameter("lastName");
            String password = (String)request.getParameter("password");
            String driverN = (String) request.getParameter("driver");
            boolean driver = false;
            if (driverN.equals("YES")) {
                driver = true;
            }
            String name = (String) request.getParameter("mailUser");
            try {
                User.updateDriver(name, driver);
                request.setAttribute("erreurD", driverN);
            } catch (SQLException e) {
                String erreur = e.getMessage();
                request.setAttribute("erreurD", erreur);
                request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                return;
            }
            if (!firstName.isEmpty()) {
                try {
                    User.updateFirstName(name, firstName);
                } catch (SQLException e) {
                    String erreur = e.getMessage();
                    request.setAttribute("erreurD", erreur);
                    request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                    return;
                }
            }
            if (!lastName.isEmpty()) {
                try {
                    User.updateLastName(name, lastName);
                } catch (SQLException e) {
                    String erreur = e.getMessage();
                    request.setAttribute("erreurD", erreur);
                    request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                    return;
                }
            }
            if (!password.isEmpty()) {
                try {
                    String hash = BCrypt.hashpw(password, BCrypt.gensalt());
                    User.updatePassword(name, hash);
                } catch (SQLException e) {
                    String erreur = e.getMessage();
                    request.setAttribute("erreurD", erreur);
                    request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                    return;
                }
            }
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);

        } else if (request.getParameter("Delete") != null) {
            String name = (String) request.getParameter("name");
            try {
                Admin.deleteUser(name);
            } catch (SQLException ex) {
                String erreur = ex.getMessage();
                request.setAttribute("erreurD", erreur);
                request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                return;
            }
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
        } else {
            response.sendRedirect("manageUsers.jsp");
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
