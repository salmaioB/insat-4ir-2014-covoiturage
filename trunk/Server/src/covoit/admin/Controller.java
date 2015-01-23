/******************************************************************************/
/* server/src/covoit/admin/controller.java                         2014-12-08 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit.admin;

import covoit.*;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.*;
import javax.servlet.http.*;

/** Point d'arrivée des requêtes pour les pages d'admin (/admin/*) (à modifier) */
public class Controller extends HttpServlet
{ 
    
   public void doGet(HttpServletRequest req, HttpServletResponse resp)
                                                         throws ServletException
   {
      throw new ServletException("not implemented!");
   }

   public void doPost(HttpServletRequest req, HttpServletResponse resp)
                                                         throws ServletException
   {
      throw new ServletException("not implemented!");
   }
}