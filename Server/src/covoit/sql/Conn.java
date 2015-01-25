/******************************************************************************/
/* server/src/covoit/sql/Conn.java                                 2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit.sql;

import java.sql.*;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;


/** Connection pool déjà configuré. */
public class Conn
{
   /** Comment faire vos requêtes: NE PAS CONCATENER LES CHAINES !
    *  Mettre des '?' à la place des paramètres. Sur l'objet retourné, utiliser
    *  setString(x,z) pour passer vos paramètres d'une façon protégée contre les
    *  Injections.
    *  Utilisez execute() ou executeQuery() en fonction du type de requête.
    *  Pensez à appeler close(), sur l'objet, et ses ResultSets si vous en avez. 
    */
   public static PreparedStatement prepare(String sql) throws SQLException
   {
      Connection con = _sPool.getConnection();
      PreparedStatement st = con.prepareStatement(sql);
      return st;
   }
   
/******************************************************************************/
   
   static // Should be completely synchronized 
   {
      PoolProperties p = new PoolProperties();
         p.setUrl("jdbc:mysql://192.168.1.25/covoit");
         p.setDriverClassName("com.mysql.jdbc.Driver");
         p.setUsername("tomcat_covoit");
         p.setPassword("lauss56/pint");
         p.setJmxEnabled(false); // only useful for named objects.
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