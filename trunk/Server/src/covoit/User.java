/******************************************************************************/
/* server/src/covoit/User.java                                     2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit;

import java.sql.*;
import covoit.sql.Conn;

/** User account, with their personal informations. */
public class User
{
   public static User load(String name) throws SQLException
   {
      User r = new User();
      
      String q = "SELECT Password, FirstName, LastName, Driver "+
                 "FROM user "+
                 "WHERE MailAddress = ?";
      PreparedStatement st = Conn.prepare(q);
      st.setString(1, name);
      ResultSet u = st.executeQuery();
      
      if (!u.next())
         return null; //got 0 rows
         
      r.name = name;
      r.passwd = u.getString("Password");
      r.firstName = u.getString("FirstName");
      r.lastName = u.getString("LastName");
      r.driver = u.getString("Driver").equals("Y");
      
      
      u.close();
      st.close();
      return r;
   }
   
   public static User create(String name, String pwdHash, 
                             String firstName, String lastName, boolean driver
                             ) throws SQLException
   {
      User u = new User();
      u.name = name;
      u.passwd = pwdHash;
      u.firstName = firstName;
      u.lastName = lastName;
      u.driver = driver;
      
      String req = "INSERT INTO user "+
            "       (MailAddress, Password, FirstName, LastName, Driver) "+
            "VALUES (?, ?, ?, ?, ?)";
      PreparedStatement st = Conn.prepare(req);
      st.setString(1, name);
      st.setString(2, pwdHash);
      st.setString(3, firstName);
      st.setString(4, lastName);
      st.setString(5, (driver) ? ("Y") : ("N"));
      st.execute();
      st.close();
      
      return u;
   }

   /** base64(bcrypt([password])) */
   public String getPassword()      {return passwd;}
   public String getFirstName()     {return firstName;}
   public String getLastName()      {return lastName;}
   public boolean isDriver()        {return driver;}
   
/******************************************************************************/

   private String name; // aussi l'adresse email ?
   private String passwd; 
   private String firstName;
   private String lastName;
   private boolean driver; // Vrai si la personne préfère conduire elle-même.
   private ArrayList<Route> routes;
   
   private User()    {}
}