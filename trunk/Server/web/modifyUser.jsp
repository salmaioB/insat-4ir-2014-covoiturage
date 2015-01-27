
<%@page import="covoit.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Modifier un utilisateur</title>
    </head>
    <body>
        <h1>Modifier un utilisateur</h1>
        <div style="text-align:center">
            <h3>Informations de l'utilisateur</h3>
            <%
                String name = (String) request.getAttribute("mailUser");
                User u = User.load(name);
                out.print("<p>Vous voulez modifier l'utilisateur suivant : </p>");
                out.print(String.format("<p>Prénom : %s</p>", u.getFirstName()));
                out.print(String.format("<p>Nom : %s</p>", u.getLastName()));
                out.print(String.format("<p>Adresse Mail : %s</p>", u.getName()));
                String d = u.isDriver() ? "Oui" : "Non";
                out.print(String.format("<p>Conducteur : %s</p>", d));
            %>
        </div>
        <div style="text-align:center">
            <h3>Veuillez modifier les champs nécessaires</h3>
            <form action="ModifyUserServlet" method="post">
                Prénom : <input type="text" name="firstName" />
                Nom de famille : <input type="text" name="lastName" />
                Mot de passe : <input type="text" name="password" />
                <select name="driver">
                    <option>YES</option>
                    <option>NO</option>
                </select>
                <input type="submit" name="Modify" value="Enregistrer les modifications" />
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
