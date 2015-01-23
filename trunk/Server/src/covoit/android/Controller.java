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
import java.math.BigDecimal;
import java.sql.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import javax.json.*;
import java.util.ArrayList;

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
                write(resp, 500, e.toString());
                //peut être à cause d'autre chose, mais ce sera souvent ça.
                status = "INVALID_NAME";
 				return;
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
                        .add("city", u.getCity())
                        .add("zipCode", u.getZipCode())
                        .add("notifyByMail", u.getNotifyByMail())
                        .add("notifyByPush", u.getNotifyByPush())
                        .add("notifyAddress", u.getNotifyAddress())
                        .add("routes", Route.getJsonObjectRoutes(u.getRoutes()));
            }
            write(resp, 200, pl.build());
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed detailsAccount command: " + reqBody);
        }
    }

    /**
     * Modifie un champ du profil de l'utilisateur.
     */
    private static void doModifyAccountField(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            try {
                String name = getString(reqBody, "name");   //@mail
				JsonObject obj = getObject(reqBody, "value");
                User.updateFirstName(name, obj.getString("firstName"));
                User.updateLastName(name, obj.getString("lastName"));
                //User.updatePassword(name, obj.getString("password"));
                User.updateDriver(name, obj.getBoolean("driver"));
                User.updateCity(name, obj.getString("city"), obj.getString("zip"));
 
                JsonObject pl = Json.createObjectBuilder()
                        .add("status", "OK")
                        .build();
                write(resp, 200, pl);

            } catch (SQLException ex) {
                write(resp, 500, ex.toString());
            }
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed modifyAccountField command: " + reqBody);
        }
    }
	
	    /**
     * Modifie un champ du profil de l'utilisateur.
     */
    private static void doModifyNotifications(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            try {
                String name = getString(reqBody, "name");   //@mail
				JsonObject obj = getObject(reqBody, "value");
                User.setNotifySettings(name, obj.getBoolean("notifyByMail"), obj.getBoolean("notifyByPush"), obj.getString("notifyAddress"));
 
                JsonObject pl = Json.createObjectBuilder()
                        .add("status", "OK")
                        .build();
                write(resp, 200, pl);

            } catch (SQLException ex) {
                write(resp, 500, ex.toString());
            }
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed modifyNotification command: " + reqBody);
        }
    }

    /**
     * Modifie un trajet
     */
    private static void doModifyRoute(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            try {
                String name = getString(reqBody, "name");   //@mail
                JsonObject route = getObject(reqBody, "route"); // trajet à modifier

                User.updateRoute(name, new Route(route));

                JsonObject pl = Json.createObjectBuilder()
                        .add("status", "OK")
                        .build();
                write(resp, 200, pl);

            } catch (SQLException ex) {
                write(resp, 500, ex.toString());
            }
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed modifyRoute command: " + reqBody);
        }
    }

    /**
     * Ajoute un trajet
     */
    private static void doAddRoute(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            try {
                String name = getString(reqBody, "name");   //@mail
                JsonObject route = getObject(reqBody, "route"); // trajet à ajouter

                User.addRoute(name, new Route(route));

                JsonObject pl = Json.createObjectBuilder()
                        .add("status", "OK")
                        .build();
                write(resp, 200, pl);

            } catch (SQLException ex) {
                write(resp, 500, ex.toString());
            }
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed addRoute command: " + reqBody);
        }
    }

    /**
     * Supprime un trajet
     */
    private static void doRemoveRoute(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            try {
                String name = getString(reqBody, "name");   //@mail
                String weekday = getString(reqBody, "weekday"); // trajet à ajouter

                User.removeRoute(name, Route.Weekday.valueOf(weekday));

                JsonObject pl = Json.createObjectBuilder()
                        .add("status", "OK")
                        .build();
                write(resp, 200, pl);

            } catch (SQLException ex) {
                write(resp, 500, ex.toString());
            }
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed removeRoute command: " + reqBody);
        }
    }

    /**
     * Renvoie la liste des lieux de travail existants.
     */
    private static void doListWorkplaces(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("value", Workplaces.getWorkplaces());
            write(resp, 200, builder.build());
        } catch (SQLException e) {
            write(resp, 500, e.toString());
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed listWorkplaces command: " + reqBody);
        }
    }

    /**
     * Renvoie la liste des trajets correspondant à une route. name = mail
     * address of the user weekday = weekday of the route direction = go: false,
     * return: true
     */
    private static void doSearchRoutes(HttpServletRequest req,
            HttpServletResponse resp, JsonObject reqBody) {
        try {
            try {
                String name = getString(reqBody, "name");   //@mail
                String weekday = getString(reqBody, "weekday"); // trajet à rechercher
                Boolean direction = getBool(reqBody, "direction"); // go or return

                ArrayList<ShortUser> l = User.searchRoutes(name, Route.Weekday.valueOf(weekday), direction);

                JsonArrayBuilder jab = Json.createArrayBuilder();
                if (l != null) {
                    for (int i = 0; i < l.size(); i++) {
                        jab.add(l.get(i).getJsonObjectShortUser());
                    }
                }
				
				JsonObjectBuilder job = Json.createObjectBuilder();
				job.add("value", jab);
				
                write(resp, 200, job.build());
            } catch (SQLException ex) {
                write(resp, 500, ex.toString());
            }
        } catch (InvalidParameterException e) {
            write(resp, 400, "Malformed searchRoutes command: " + reqBody);
        }
    }


    /* DISPATCH *******************************************************************/
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String cmd = req.getRequestURI().substring("/Server/android/".length());
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
            } else if (cmd.equals("modifyAccountField")) {
                doModifyAccountField(req, resp, reqBody);
            } else if (cmd.equals("modifyNotification")) {
                doModifyNotifications(req, resp, reqBody);
            } else if (cmd.equals("modifyRoute")) {
                doModifyRoute(req, resp, reqBody);
            } else if (cmd.equals("addRoute")) {
                doAddRoute(req, resp, reqBody);
            } else if (cmd.equals("removeRoute")) {
                doRemoveRoute(req, resp, reqBody);
            } else if (cmd.equals("listWorkplaces")) {
                doListWorkplaces(req, resp, reqBody);
            } else if (cmd.equals("searchRoutes")) {
                doSearchRoutes(req, resp, reqBody);
            } else {
                write(resp, 400, "Commande non supportée: " + cmd);
            }
        } else {
            write(resp, 400, "Authentification requise, ou commande non supportée: " + req.getRequestURI());
        }
    }
}
