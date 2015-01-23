
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
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
        <form action="GenerateReports" method="post">
                            login : <input type="text" name="login" />
                            password : <input type="password" name="password" />
                            <input type="submit" value="connexion" />
        </form>
        <%
                out.print("<br />");
                out.print("<p><a href=\"LogoutServlet\">Déconnexion</a></p>");
            }
        %>
    </body>
</html>
