/******************************************************************************/
/* server/src/covoit/User.java                                     2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit;

import java.sql.*;

/** User account, with their personnal informations. */
public class User
{
   public static User load(String name) throws SQLException
   {
      User r = new User();
      String req = "SELECT Password, Driver FROM user WHERE MailAddress = ?";
      ResultSet u = covoit.sql.Conn.execute(req, name);
      
      if (!u.next())
         return null; //got 0 rows
         
      r.name = name;
      r.passwd = u.getString("Password");
      r.driver = u.getString("Driver").equals("Y");
      
      u.close();
      return r;
   }
   
   public static User create(String name, String pwdHash) throws SQLException
   {
      User u = new User();
      u.name = name;
      u.passwd = pwdHash;
      
      String req = "INSERT INTO user (MailAddress, Password) VALUES (?, ?)";
      ResultSet r = covoit.sql.Conn.execute(req, name, pwdHash);
      r.close();
      
      return u;
   }

   /** base64(bcrypt([password])) */
   public String getPassword()      {return passwd;}
   
/******************************************************************************/

   private String name; // aussi l'adresse email ?
   private String passwd; 
   private boolean driver; // Vrai si la personne préfère conduire elle-même.
   
   private User()    {}
}