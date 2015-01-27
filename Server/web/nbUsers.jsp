
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SOPRA Covoiturage</title>
    </head>
    <%@page import="covoit.Admin" %>
    <body>
        <div>
            <%
                int nbD = Admin.nbrDrivers();
                int nbP = Admin.nbrNonDrivers();
                int nb = Admin.nbrUsers();
                out.print("<p>Il y a " + nb + " utilisateurs de l'application.</p>");
                out.print("<p>" + nbD + " sont conducteurs.</p>");
                out.print("<p>" + nbP + " sont passagers.</p>");
            %>
        </div> 
        <br/>
        <br/>
        <br/>
        <p><a href="content.jsp">Retour</a></p>
    </body>
</html>
