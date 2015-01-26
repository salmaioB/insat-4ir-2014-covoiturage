<%-- 
    Document   : nbUsersHouseWorkplace
    Created on : 23 janv. 2015, 12:17:34
    Author     : Kapouter
--%>

<%@page import="covoit.Workplaces"%>
<%@page import="java.util.ArrayList" %>
<%@page import="covoit.Admin" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SOPRA Covoiturage</title>
    </head>
    <body>
        <h1 style="text-align:center;">SOPRA CoVoiturage</h1>
        <h3 style="text-align:center;">Rapports - Nombre d'utilisateurs par couples Domicile/Travail</h3>
        <br />
        <br />
        <h4 style="text-align:center;">Veuillez sélectionner un lieu de travail et une ville</h4>
        <div style="text-align:center;">

<%
ArrayList<Workplaces> liste = (ArrayList<Workplaces>) Admin.loadPlaces();
%>
            <form action="SearchServlet" method="post">
            <select name="placeSelected">
            <option value="vide" selected>-- choisissez un lieu de travail</option>
<%
for (int i=0; i<liste.size(); i++)
{
    int item0 = liste.get(i).getId();
    String item1 = liste.get(i).getName();
    String item2 = liste.get(i).getAddress();
%>
            <option value="<%=item0%>"><%=item1+" - "+item2%></option>
<%
}
%>
            </select>
            <br>
            <select name="CitySelected">
            <option value="vide" selected>-- choisissez une ville</option>
            </select>
            <br>
            <input type="submit" value="Recherche" />
            </form>
        </div>
    </body>
</html>
