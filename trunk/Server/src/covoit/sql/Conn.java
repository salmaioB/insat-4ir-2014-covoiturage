/******************************************************************************/
/* server/src/covoit/sql/Conn.java                                 2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit.sql;

import java.sql.*;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/** Connection pool planqué à l'intérieur. Plus qu'à soumettre des requêtes, et
 *  obtenir les ResultSets. */
public class Conn
{
   /** ! Penser à appeler close sur le ResultSet ! 
    *  ! Si il y a des paramètres, NE PAS CONCATENER LES CHAINES ! Utiliser une 
    *  des surchages protégées contre les injections. */
   public static ResultSet query(String query) throws SQLException
   {
      Connection con = _sPool.getConnection();
      Statement st = con.createStatement();
      st.closeOnCompletion(); // auto-closed when ResultSet is closed.
      return st.executeQuery(query);
   }
   
   /** Les '?' seront remplacés par les paramètres supplémentaires de la 
    *  méthode, de façon protégée contre les injections. 
    *  ! Penser à appeler close() sur le ResulSet ! */
   public static ResultSet query(String query, String p1) throws SQLException
   {
      Connection con = _sPool.getConnection();
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, p1);
      st.closeOnCompletion(); // auto-closed when ResultSet is closed.
      return st.executeQuery();
   }
   
      /** Les '?' seront remplacés par les paramètres supplémentaires de la 
    *  méthode, de façon protégée contre les injections. */
   public static void execute(String query, String p1, String p2) throws SQLException
   {
      Connection con = _sPool.getConnection();
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, p1);
      st.setString(2, p2);
      st.execute();
      st.close();
   }
   
/******************************************************************************/
   
   static // Should be completely synchronized 
   {
      PoolProperties p = new PoolProperties();
         p.setUrl("jdbc:mysql://localhost:3306/covoitsopra");
         p.setDriverClassName("com.mysql.jdbc.Driver");
         p.setUsername("tomcat");
         p.setPassword("G3167u5i");
         p.setJmxEnabled(false); // késako ?
         p.setValidationQuery("SELECT 1");
         p.setTestWhileIdle(false);
         p.setTestOnBorrow(true);
         p.setValidationInterval(30000);
         p.setTestOnReturn(false);
         p.setMaxWait(10000);
         p.setRemoveAbandoned(true);
         p.setRemoveAbandonedTimeout(60);
         p.setMinIdle(10);
         
         p.setJdbcInterceptors(
           "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
           "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
           
      DataSource pool = new DataSource();
      pool.setPoolProperties(p);
      _sPool = pool;
   }
   
   private static DataSource _sPool;
}
