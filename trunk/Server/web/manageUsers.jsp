<%-- 
    Document   : manageUsers
    Created on : 26 janv. 2015, 18:35:53
    Author     : Kapouter
--%>

<%@page import="covoit.ShortUser"%>
<%@page import="covoit.Admin"%>
<%@page import="java.util.ArrayList"%>
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
        <div style="text-align:center">
            <h3>Liste des utilisateurs</h3>
            <table border="1">
                <tr>
                    <th>Prénom</th>
                    <th>Nom</th>
                    <th>Adresse Mail</th>
                    <th>Conducteur</th>
                </tr>
                <%
                    ArrayList<ShortUser> listUsers = Admin.getUsers();
                    for (int i = 0; i < listUsers.size(); i++) {
                        out.print("<tr>");
                        out.print(String.format("<td>%s</td>", listUsers.get(i).getFirstName()));
                        out.print(String.format("<td>%s</td>", listUsers.get(i).getLastName()));
                        out.print(String.format("<td>%s</td>", listUsers.get(i).getName()));
                        String d = listUsers.get(i).getDriver() ? "Oui" : "Non";
                        out.print(String.format("<td>%s</td>", d));
                        out.print("</tr>");
                    }
                %>
            </table>
        </div>
        <div style="text-align:center">
            <h3>Modifier ou supprimer un utilisateur</h3>
            <form action="UsersServlet" method="post">
                <select name="name">
                    <%
                        for (int i = 0; i < listUsers.size(); i++) {
                            out.print(String.format("<option>%s</option>", listUsers.get(i).getName()));
                        }
                    %>
                </select>
                <input type="submit" name="Modify" value="Modifier" />
                <input type="submit" name="Delete" value="Supprimer" />
            </form>
            <%
                String erreurD = (String) request.getAttribute("erreurD");
                if (erreurD != null) {
                    out.print(String.format("Erreur: %s", erreurD));
                }
            %>
        </div>
    </body>
</html>
