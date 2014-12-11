/******************************************************************************/
/* server/src/covoit/sql/Conn.java                                 2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit.sql;

import java.sql.*;

/** Connection pool planqué à l'intérieur. Plus qu'uà soumettre des requêtes, et
 *  obtenir les ResultSets. */
class Conn
{
   /** ! Penser à appeler close sur le ResultSet ! */
   public static ResultSet execute(String query)
   {
      // ! mettre la Statement en autoClose.
      return null;
   }
   
/******************************************************************************/
   
   static // Should be completely synchronized 
   {
   }
}
