/******************************************************************************/
/* server/src/covoit/User.java                                     2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                             Félix Poisot */
/******************************************************************************/
package covoit;

import java.sql.*;
import covoit.sql.Conn;
import java.util.ArrayList;

/** User account, with their personal informations. */
	public class User 
	{
	public User() {
		routes = new ArrayList<Route>();
	}
	
	public static void updateFirstName(String name, String firstName) throws SQLException
	{
		String q = "SELECT IdUser FROM user WHERE MailAddress = ?";
		PreparedStatement st = Conn.prepare(q);
		st.setString(1, name);
		ResultSet id = st.executeQuery();
		int iduser = id.getInt("IdUser");
		id.close();
		
		
	}
	
	public static void updateLastName(String name, String lastName) throws SQLException
	{
		
	}
	
	public static void updatePassword(String name, String password) throws SQLException
	{
		
	}
	
	public static void updateDriver(String name, boolean driver) throws SQLException
	{
		
	}
	
	public static void updateRoute(String name, Route route) throws SQLException
	{
		
	}
	
	/** @param name adresse mail */
   public static User load(String name) throws SQLException
   {
      User r = new User();
      
      String q = "SELECT IdUser, Password, FirstName, LastName, Driver "+
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
	  int iduser = u.getInt("IdUser");  // getInt existe?
	  
	  u.close();
	  
	  q = "SELECT Day, DATE_FORMAT(GoHour, '%H') gohour_, DATE_FORMAT(GoHour, '%m') gominutes_, "+
				 "DATE_FORMAT(ReturnHour, '%H') returnhour_, DATE_FORMAT(ReturnHour, '%m') returnminutes_, "+
				 "CityName, ZIPCode, PlaceName, PlaceAddress "+
                 "FROM user, route, city, place "+ 
                 "WHERE user.IdCity = city.IdCity "+
				 "AND user.IdPlace = place.IdPlace "+
				 "AND user.IdUser = route.IdUser "+
				 "AND user.IdUser = ?";
      st = Conn.prepare(q);
      st.setString(1, Integer.toString(iduser));
      ResultSet routes = st.executeQuery();
      
	  Route route = new Route();
	  
	  while (!routes.next())
	  {
		  route.setWeekday(routes.getString("Day"));
		  route.setStartTime(routes.getInt("gohour_"), routes.getInt("gominutes_"));
		  route.setEndTime(routes.getInt("returnhour_"), routes.getInt("returnminutes_"));
		  route.setStart(routes.getString("CityName")+" "+routes.getString("ZIPCode"));
		  route.setEnd(routes.getString("PlaceName")+" "+routes.getString("PlaceAddress"));
		  r.getRoutes().add(route);
	  }
	  
	  routes.close();
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
   public String getPassword()      	{return passwd;}
   public String getFirstName()     	{return firstName;}
   public String getLastName()      	{return lastName;}
   public ArrayList<Route> getRoutes()	{return routes;}
   public boolean isDriver()        	{return driver;}
   
/******************************************************************************/

   private String name; // aussi l'adresse email ?
   private String passwd; 
   private String firstName;
   private String lastName;
   private boolean driver; // Vrai si la personne préfère conduire elle-même.
   private ArrayList<Route> routes;
   
   private User()    {}
}