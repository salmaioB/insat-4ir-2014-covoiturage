/******************************************************************************/
/* server/src/covoit/android/controller.java                       2014-12-08 */
/* Covoiturage Sopra - INSA                                      Félix Poisot */
/******************************************************************************/
package covoit.android;

import javax.servlet.*;
import javax.servlet.http.*;

/** Point d'arrivée des requêtes sur /android/[whatever] */
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