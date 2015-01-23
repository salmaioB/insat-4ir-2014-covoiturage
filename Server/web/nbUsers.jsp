
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <%@page import="covoit.Admin" %>
    <body>
        <div>
            <%
                int nbD = Admin.nbrDrivers();
                int nbP = Admin.nbrNonDrivers();
                int nb = Admin.nbrUsers();
                out.print("Il y a "+nb+" utilisateurs de l'application.");
                out.print(nbD+" sont conducteurs.");
                out.print(nbP+" sont passagers.");
            %>
        </div> 
    </body>
</html>
