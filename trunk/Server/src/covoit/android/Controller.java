/******************************************************************************/
/* server/src/covoit/android/controller.java                       2014-12-08 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit.android;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;

/** Point d'arrivée des requêtes sur /android/[whatever] */
public class Controller extends HttpServlet
{

/* UTILITAIRE *****************************************************************/

   /** Renvoie le nom de compte de l'utilisateur si il est déjà connecté, 
    *  "" sinon. */
   private static String getUsername(HttpServletRequest req)
   {
      HttpSession s = req.getSession(false);
      if (s == null)
         return "";
         
      String name = (String)s.getAttribute("username");
      return (name != null) ? (name) : ("");
   }

   /** Envoie une réponse en texte brut (pour la mise au point). */
   private static void write(HttpServletResponse resp, int code, String message)
   {
      try {
         resp.setStatus(code);
         resp.setContentType("text/plain");
         resp.getOutputStream().print(message);
      } catch (IOException e) {
         // pas grand chose à faire...
      }
   }
   
   private static void write(HttpServletResponse resp, int code, JsonStructure data)
   {
      try {
         resp.setStatus(code);
         resp.setContentType("application/json");
         JsonWriter w = Json.createWriter(resp.getOutputStream());
         w.write(data);
      } catch (IOException e) {
         // pas grand chose à faire...
      }
   }
   
   /** Renvoie l'attribut du nom donné, si il existe et que c'est bien un String. */
   private static String getString(JsonObject o, String name)
   {
      JsonString jss = o.getJsonString(name);
      if (jss == null)
         return null;
      return jss.getString();
   }  
   
/* COMMANDES ******************************************************************/

   /** Ouvre une session HTTP (par cookie) si le login est bon. */
   private static void doLogin(HttpServletRequest req, HttpServletResponse resp,
                               JsonObject reqBody)
   {
      String user = getString(reqBody, "name");
      String pwd =  getString(reqBody, "password");
   
      if (user == null || pwd == null) {
         write(resp, 400, "Malformed login command: "+reqBody);
      }
   
      // TODO vérifier avec la BDD
      if (user.equals("whatsthepassword") && pwd.equals("password")) 
      {
         req.getSession().setAttribute("username", user);
         
         // L'ancien SessID est invalidé. Protège de qqch mais je sais plus quoi.
         req.changeSessionId(); 
         resp.setStatus(200); // le nouveau cookie va redescendre, pas besoin de corps.
      } 
      else {
         req.getSession().invalidate();
         write(resp, 400, "Invalid login/password");
      }
   }
   
   private static void doDetailsAccount(HttpServletRequest req, 
                                   HttpServletResponse resp, JsonObject reqBody)
   {
      String name = getString(reqBody, "name");
      
      if (name == null) {
         write(resp, 400, "Malformed detailsAccount command: "+reqBody);
      }
      
      JsonObject pl = Json.createObjectBuilder()
                        .add("format", "à définir")
                      .build();
      write(resp, 200, pl);
   }

/* DISPATCH *******************************************************************/

   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse resp)
   {
      String cmd = req.getRequestURI().substring("/android/".length());
      String user = getUsername(req);
      JsonObject reqBody;
      try {
         reqBody = Json.createReader(req.getInputStream()).readObject();
      } 
      catch (IOException | JsonException e) {
         write(resp, 400, "Cannot decode Json");
         return;
      }
      
      // commandes ne nécessaitant pas d'être connecté.
      if (cmd.equals("login")) {
         doLogin(req, resp, reqBody);
      }
      else if (user.length() != 0)
      {  // connexion requise
         if (cmd.equals("detailsAccount")) {
            doDetailsAccount(req, resp, reqBody);
         }
         else
            write(resp, 400, "Commande non supportée: "+cmd);
      }
      else
         write(resp, 400, "Authentification requise, ou commande non supportée");
   }
   

}