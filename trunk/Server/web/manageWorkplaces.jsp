
<%@page import="covoit.Admin"%>
<%@page import="covoit.Workplaces"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gestion lieux de travail</title>
    </head>
    <body>
        <h1>Gestion lieux de travail</h1>
        <div style="text-align:center;">
            <h3>Ajouter un lieu de travail</h3>
            <form action="PlacesServlet" method="post">
                                Nom : <input type="text" name="placeName" />
                                Adresse : <input type="text" name="placeAddress" />
                                <input type="submit" name="Create" value="Creer" />
            </form>
            <%
                String erreur = (String) request.getAttribute("erreur");
                if (erreur != null) {
                    out.print(String.format("Erreur: %s", erreur));
                }
            %>
        </div>
        <div style="text-align:center;">
            <h3>Liste des lieux de travail</h3>
            <table border="1">
                <tr>
                    <th>Nom</th>
                    <th>Adresse</th>
                </tr>
                <%
                    ArrayList<Workplaces> listPlaces = Admin.loadPlaces();
                    for (int i = 0; i < listPlaces.size(); i++) {
                        out.print("<tr>");
                        out.print(String.format("<td>%s</td>", listPlaces.get(i).getName()));
                        out.print(String.format("<td>%s</td>", listPlaces.get(i).getAddress()));
                        out.print("</tr>");
                    }
                %>
            </table>
        </div>
        <div style="text-align:center;">
            <h3>Suppression des lieux de travail</h3>
            <form>
                <select name="nomlieu">
                    <%
                        for (int i = 0; i < listPlaces.size(); i++) {
                            out.print(String.format("<option>%s</option>", listPlaces.get(i).getName()));
                        }
                        String erreurD = (String) request.getAttribute("erreurD");
                        if (erreurD != null) {
                            out.print(String.format("Erreur: %s", erreurD));
                        }
                    %>
                </select>                     
                <form action="PlacesServlet" method="post">
                    <input type="submit" name="Delete" value="Supprimer" />
                </form>
            </form>
        </div>
    </body>
</html>
