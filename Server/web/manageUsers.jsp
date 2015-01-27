<%-- 
    Document   : manageUsers
    Created on : 26 janv. 2015, 18:35:53
    Author     : Kapouter
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gestion utilisateurs</title>
    </head>
    <body>
        <h1>Gestion utilisateurs</h1>
        <div style="text-align:center;">
            <h3>Ajouter un utilisateur</h3>
            <form action="UsersServlet" method="post">
                                Adresse Mail : <input type="text" name="mailAddress" />
                                Prénom : <input type="text" name="firstName" />
                                Nom de famille : <input type="text" name="lastName" />
                                <select name="driver">
                                    <option>YES</option>
                                    <option>NO</option>
                                </select>
                                <input type="submit" name="Create" value="Creer" />
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
