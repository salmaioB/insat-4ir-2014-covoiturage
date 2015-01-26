
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SOPRA Covoiturage</title>
    </head>
    <%@page import="covoit.Admin" %>
    <body>
        <%
            if (request.getSession().getAttribute("admin") == null) {
                out.print("Vous n'êtes pas connecté. Cliquez <a href=\"FormConnexion.jsp\">ici</a> pour vous authentifier");

            } else {
                Admin currentAdmin = (Admin) request.getSession().getAttribute("admin");
                out.print(String.format("Bonjour visiteur ! Tu es connecté en tant que : %s", currentAdmin.getName()));
        %>
        <h3>Rapports</h3>
        <ul>
            <li><% out.print("<p><a href=\"nbConnexions.jsp\">Nombre de connexions</a></p>"); %></li>
            <li><% out.print("<p><a href=\"nbUsers.jsp\">Nombre d'utilisateurs</a></p>"); %></li>
            <li><% out.print("<p><a href=\"nbUsersHouseWorkplace.jsp\">Nombre d'utilisateurs par couples Domicile/Travail</a></p>"); %></li>
            <li><% out.print("<p><a href=\"manageWorkplaces.jsp\">Gestion lieux de travail</a></p>"); %></li>
            <li><% out.print("<p><a href=\"manageUsers.jsp\">Gestion utilisateurs</a></p>"); %></li>
        </ul>        
        
        <%
            out.print("<br />");
            out.print("<br />");
            out.print("<br />");
            out.print("<p><a href=\"LogoutServlet\">Déconnexion</a></p>");
        }
        %>
    </body>
</html>
