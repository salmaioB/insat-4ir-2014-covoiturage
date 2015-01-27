
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SOPRA CoVoiturage</title>
    </head>
    <body>
        <h1 style="text-align:center;">SOPRA CoVoiturage</h1>
        <h3 style="text-align:center;">Administration du site</h3>
        <br />
        <br />
        <br />
        <div style="text-align:center;">
            <form action="LoginServlet" method="post">
                                Identifiant : <input type="text" name="login" />
                                Mot de passe : <input type="password" name="password" />
                                <input type="submit" value="Connexion" />
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
