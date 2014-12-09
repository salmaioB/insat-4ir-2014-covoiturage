/******************************************************************************/
/* server/src/covoit/android/controller.java                       2014-12-08 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit.android;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;

/** Point d'arrivée des requêtes qur /android/[whatever] */
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

/* COMMANDES ******************************************************************/

   /**  */
   private static void doLogin(HttpServletRequest req, HttpServletResponse resp,
                               JsonObject reqBody) throws ServletException
   {
      if (reqBody.getJsonString("name") == null
            || reqBody.getJsonString("password") == null)
         throw new ServletException("Malformed login command");
       
      String user = reqBody.getJsonString("name").getString();
      String pwd = reqBody.getJsonString("password").getString();
   
      // TODO vérifier avec la BDD
      if (user.equals("whatsthepassword") && pwd.equals("password")) 
      {
         req.getSession().setAttribute("username", user);
         
         // L'ancien SessID est invalidé. Protège de qqch mais je sais plus quoi.
         req.changeSessionId(); 
      } 
      else {
         req.getSession().invalidate();
         resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }
   }

/* DISPATCH *******************************************************************/

   /** Réponds aux requêtes GET : detailsAccount, listRoutes, detailsRoute, 
    *  detailsPlace. */
   public void doGet(HttpServletRequest req, HttpServletResponse resp) 
                                                         throws ServletException
   {
      throw new ServletException("not implemented!");
   }

   /** Réponds aux requêtes POST : Login, logout, forgottenPsswd, createAccount,
    *  userDeleteAccount, updateAccount. */
   public void doPost(HttpServletRequest req, HttpServletResponse resp)
                                                         throws ServletException
   {
      String cmd = req.getRequestURI().substring("/android/".length());
      String user = getUsername(req);
      JsonObject reqBody;
      try {
         reqBody = Json.createReader(req.getInputStream()).readObject();
      } 
      catch (IOException e) {
         throw new ServletException("Cannot decode JSON");
      }
      
      if (cmd.equals("login")) {
         doLogin(req, resp, reqBody);
      }
      else
         throw new ServletException("Unsupported command: "+cmd);
   }
   

}