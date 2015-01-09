/**
 * ***************************************************************************
 */
/* server/src/covoit/android/controller.java                       2014-12-08 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/**
 * ***************************************************************************
 */
package covoit.android;

import covoit.*;
import covoit.lib.BCrypt;
import java.io.*;
import java.sql.*;
import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;

/**
 * Point d'arrivée des requêtes sur /android/[whatever]
 */
public class Controller extends HttpServlet {

    /* UTILITAIRE *****************************************************************/
    /**
     * Renvoie le nom de compte de l'utilisateur si il est déjà connecté, ""
     * sinon.
     */
    private static String getUsername(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) {
            return "";
        }

        String name = (String) s.getAttribute("username");
        return (name != null) ? (name) : ("");
    }

    /**
     * Envoie une réponse en texte brut (pour la mise au point).
     */
    private static void write(HttpServletResponse resp, int code, String message) {
        try {
            resp.setStatus(code);
            resp.setContentType("text/plain");
            resp.getOutputStream().print(message);
        } catch (IOException e) {
            // pas grand chose à faire...
        }
    }

    private static void write(HttpServletResponse resp, int code, JsonStructure data) {
        try {
            resp.setStatus(code);
            resp.setContentType("application/json");
            JsonWriter w = Json.createWriter(resp.getOutputStream());
            w.write(data);
        } catch (IOException e) {
            // pas grand chose à faire...
        }
    }

    /**
     * Renvoie l'attribut du nom donné, si il existe et que c'est bien un
     * String.
     */
    private static String getString(JsonObject o, String name) {
        JsonString jss = o.getJsonString(name);
        if (jss == null) {
            throw new InvalidParameterException("Not present");
        }
        return jss.getString();
    }

    /**
     * Renvoie l'attribut du nom donné, si il existe et que c'est bien un
     * booléen
     */
    private static boolean getBool(JsonObject o, String name) {
        try {
            return o.getBoolean(name);
        } catch (Throwable e) {
            throw new InvalidParameterException("Not present");
        }
    }
    
     /**
     * Renvoie l'attribut du nom donné, si il existe et que c'est bien un
     * JsonObject
     */
    private static JsonObject getObject(JsonObject o, String name) {
        try {
            return o.getJsonObject(name);
        } catch (Throwable e) {
            throw new InvalidParameterException("Not present");
        }
    }

    /* COMMANDES ******************************************************************/
    /**
     * Ouvre une session HTTP (par cookie) si le login est bon.
     */
    private static void doLogin(HttpServletRequest req, HttpServletResponse resp,
            JsonObject reqBody) {
        try {
            String username = getString(reqBody, "name");
            String pwd = getString(reqBody, "password");

            User user = null;
            try {
                user = User.load(username);
            } catch (SQLException e) {
                write(resp, 500, e.toString());
                return;
            }

            boolean loggedIn;
            if (user != null && BCrypt.checkpw(pwd, user.getPassword())) {
                req.getSession().setAttribute("username", username);
                loggedIn = true;
                // L'ancien SessID est invalidé. Protège de qqch mais je sais plus quoi.
                req.changeSessionId();
            } else {
                req.getSession().invalidate();
                loggedIn = false;
            }
            JsonObject pl = Json.createObjectBuilder()
                    .add("status", (loggedIn) ? ("OK") : ("INCORRECT_CRED"))
                    .build();
            write(resp, 200, pl);
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed login command: " + reqBody);
        }

    }

    /**
     * Ajoute un compte en BDD
     */
    private static void doCreateAccount(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            String username = getString(reqBody, "name");
            String pwd = getString(reqBody, "password");
            String firstName = getString(reqBody, "firstName");
            String lastName = getString(reqBody, "lastName");
            boolean driver = getBool(reqBody, "driver");

            String status;

            String hash = BCrypt.hashpw(pwd, BCrypt.gensalt());

            try {
                User.create(username, hash, firstName, lastName, driver);
                status = "OK";
            } catch (SQLException e) {
            //write(resp, 500, e.toString());
                //peut être à cause d'autre chose, mais ce sera souvent ça.
                status = "INVALID_NAME";
            }

            JsonObject pl = Json.createObjectBuilder()
                    .add("status", status)
                    .build();
            write(resp, 200, pl);
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed createAccount command: " + reqBody);
        }
    }

    /**
     * Renvoie les détails / infos persos du compte spécifié.
     */
    private static void doDetailsAccount(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            String name = getString(reqBody, "name");

            User u = null;
            try {
                u = User.load(name);
            } catch (SQLException e) {
                write(resp, 500, e.toString());
            }

            JsonObjectBuilder pl = Json.createObjectBuilder();
            if (u != null) {
                pl.add("name", name)
                        .add("firstName", u.getFirstName())
                        .add("lastName", u.getLastName())
                        .add("driver", u.isDriver())
                        .add("routes", Route.getJsonObjectRoutes(u.getRoutes()));
            }
            write(resp, 200, pl.build());
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed createAccount command: " + reqBody);
        }
    }

    /**
     * Modifie un champ du profil de l'utilisateur.
     */
    private static void doModifyAccountField(HttpServletRequest req,
        HttpServletResponse resp, JsonObject reqBody) {
        try {
            String name = getString(reqBody, "name");   //@mail
            String field = getString(reqBody, "field"); //champ à modifier
                                                        //nouvelle valeur: value
            switch (name) {
                case "FirstName":
                    String fn = getString(reqBody, "value");
                    User.updateFirstName(name, fn);
                    break;
                case "LastName":
                    String ln = getString(reqBody, "value");
                    User.updateLastName(name, ln);
                    break;
                case "Password":
                    String p = getString(reqBody, "value");
                    User.updatePassword(name, p);
                    break;
                case "Driver":
                    Boolean d = getBool(reqBody, "value");
                    User.updateDriver(name, d);
                    break;
                case "City":
                    JsonObject o = getObject(reqBody, "value");
                    String c = o.getString("city");
                    String z = o.getString("zip");
                    User.updateCity(name, c, z);
                    break;
                default:
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* DISPATCH *******************************************************************/
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String cmd = req.getRequestURI().substring("/android/".length());
        String user = getUsername(req);
        JsonObject reqBody;
        try {
            reqBody = Json.createReader(req.getInputStream()).readObject();
        } catch (IOException | JsonException e) {
            write(resp, 400, "Cannot decode Json");
            return;
        }

        // commandes ne nécessaitant pas d'être connecté.
        if (cmd.equals("login")) {
            doLogin(req, resp, reqBody);
        } else if (cmd.equals("logout")) {
            req.getSession().invalidate();
            JsonObject pl = Json.createObjectBuilder().build();
            write(resp, 200, pl);
        } else if (cmd.equals("createAccount")) {
            doCreateAccount(req, resp, reqBody);
        } else if (user.length() != 0) {  // connexion requise
            if (cmd.equals("detailsAccount")) {
                doDetailsAccount(req, resp, reqBody);
            } 
            else if (cmd.equals("modifyAccountField")){
                doModifyAccountField(req, resp, reqBody);
            }
            else {
                write(resp, 400, "Commande non supportée: " + cmd);
            }
        } else {
            write(resp, 400, "Authentification requise, ou commande non supportée");
        }
    }

}
