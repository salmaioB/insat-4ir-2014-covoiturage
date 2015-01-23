
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h3>Formulaire de connexion</h3>
        <div>
            <form action="LoginServlet" method="post">
                                login : <input type="text" name="login" />
                                password : <input type="password" name="password" />
                                <input type="submit" value="connexion" />
            </form>
                <%
                    String erreur = (String) request.getAttribute("erreur");
                    if (erreur != null) {
                        out.print(String.format("Erreur: %s", erreur));
                    }
                %>
        </div>
    </body>
</html>
