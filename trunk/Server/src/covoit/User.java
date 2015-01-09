/**
 * ***************************************************************************
 */
/* server/src/covoit/User.java                                     2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                     Félix Julie Philippe */
/**
 * ***************************************************************************
 */
package covoit;

import java.sql.*;
import covoit.sql.Conn;
import java.util.ArrayList;

/**
 * User account, with their personal informations.
 */
public class User {

    private String name; // aussi l'adresse email ?
    private String passwd;
    private String firstName;
    private String lastName;
    private boolean driver; // Vrai si la personne préfère conduire elle-même.
    private ArrayList<Route> routes;
    
    public User() {
        routes = new ArrayList<Route>();
    }
    
     /**
     * base64(bcrypt([password]))
     */
    public String getPassword() {return passwd;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public ArrayList<Route> getRoutes() {return routes;}
    public boolean isDriver() {return driver;}

    // Mise à jour du prénom (Philippe : Ajout requête)
    public static void updateFirstName(String mailAddr, String firstName) throws SQLException {
        String q = "UPDATE user SET FirstName = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, firstName);
        st.setString(2, mailAddr);
        st.execute();
        st.close();
    }

    // Mise à jour du nom (Philippe : Ajout requête)
    public static void updateLastName(String mailAddr, String lastName) throws SQLException {
        String q = "UPDATE user SET LastName = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, lastName);
        st.setString(2, mailAddr);
        st.execute();
        st.close();
    }

    // Mise à jour du mot de passe (Philippe : Ajout requête)
    public static void updatePassword(String mailAddr, String password) throws SQLException {
        String q = "UPDATE user SET Password = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, password);
        st.setString(2, mailAddr);
        st.execute();
        st.close();
    }

    // Mise à jour du conducteur (Philippe : Ajout requête)
    public static void updateDriver(String mailAddr, boolean driver) throws SQLException {
        String q = "UPDATE user SET Driver = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, (driver) ? ("Y") : ("N"));
        st.setString(2, mailAddr);
        st.execute();
        st.close();
    }

    // Mise à jour de la ville (Philippe : Ajout requête)
    public static void updateCity(String mailAddr, String city, String zip) throws SQLException {
        int idCity;

        // Est-ce que ville existe ?
        String q = "SELECT IdCity FROM city WHERE CityName = ? AND ZIPCode = ?";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, city);
        st.setString(2, zip);
        ResultSet rs = st.executeQuery();

        if (!rs.next()) {
            rs.close();
            // Création
            q = "INSERT INTO city (CityName, ZIPCode) VALUES (?, ?)";
            st = Conn.prepare(q);
            st.setString(1, city);
            st.setString(2, zip);
            st.execute();

            q = "SELECT IdCity FROM city WHERE CityName = ? AND ZIPCode = ?";
            st = Conn.prepare(q);
            st.setString(1, city);
            st.setString(2, zip);
            rs = st.executeQuery();
        }
        idCity = rs.getInt("IdCity");
        rs.close();

        // Affectation
        q = "UPDATE user SET IdCity = ? WHERE MailAddress = ?;";
        st = Conn.prepare(q);
        st.setInt(1, idCity);
        st.setString(2, mailAddr);
        st.execute();
        st.close();

    }

	public static HashMap<String, String> getPlaces() throws SQLException {
	 // retourner les places prédéfinies
        String q = "SELECT PlaceName, PlaceAddress FROM place ORDER BY PlaceName";
        PreparedStatement st = Conn.prepare(q);
        ResultSet rs = st.executeQuery();
		
		HashMap<String, String> rslt = new HashMap<String, String>();
		
		while(!rs.next())
		{
			rslt.put(rs.getString("PlaceName"), rs.getString("PlaceAddress"));
		}
		rs.close();
		
		return rslt;
	
	}
	
    // Mise à jour/création de la route (Philippe : Ajout requête et différents traitements)
    public static void updateRoute(String mailAddr, Route route) throws SQLException {
        // Recherche de l'IdUser pour toutes les requêtes suivantes
        String q = "SELECT IdUser FROM user WHERE MailAddress = ?";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, mailAddr);
        ResultSet rs = st.executeQuery();

        int idUser = rs.getInt("IdUser");  		// Comment gérer aucun IdUser ? car "void" !
        rs.close();

        /* Syntaxe si on veut des ResultSet modifiables (plus facile à coder), mais il faut tester
         Statement stmt = Conn.createStatement(
         ResultSet.TYPE_SCROLL_INSENSITIVE,
         ResultSet.CONCUR_UPDATABLE);
         ResultSet rs = stmt.executeQuery("SELECT a, b FROM TABLE2");*/
		//A faire !!!
        //Mise à jour de la place si différente (on change l'ID, uniquement Admin peut modifier les 'places')
		//Mise à jour du lieu de vie si différent (On crée si n'existe pas encore, sinon sélection d'un lieu existant)
        //Mise à jour de la partie route (si le couple User/jour n'existe pas encore on ajoute toute la ligne, sinon on modifie les heures)
        /*String q = "UPDATE route SET Day = ?, GoHour = ?, EndHour = ? WHERE route.IdUser = (SELECT user.IdUser FROM user WHERE MailAddress = ?);";
         PreparedStatement st = Conn.prepare(q);
         st.setString(1, route.getWeekday());
         st.setString(2, );
         st.setString(3, );
         st.setString(4, mailAddr);
         st.execute();
         st.close();*/
    }

    /**
     * @param name adresse mail
     */
    // Julie : Ajout requêtes et traitement
    public static User load(String name) throws SQLException {
        User r = new User();

        String q = "SELECT IdUser, Password, FirstName, LastName, Driver "
                + "FROM user "
                + "WHERE MailAddress = ?";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, name);
        ResultSet u = st.executeQuery();

        if (!u.next()) {
            return null; //got 0 rows
        }
        r.name = name;
        r.passwd = u.getString("Password");
        r.firstName = u.getString("FirstName");
        r.lastName = u.getString("LastName");
        r.driver = u.getString("Driver").equals("Y");
        int iduser = u.getInt("IdUser");  // getInt existe?

        u.close();

        q = "SELECT Day, DATE_FORMAT(GoHour, '%H') gohour_, DATE_FORMAT(GoHour, '%m') gominutes_, "
                + "DATE_FORMAT(ReturnHour, '%H') returnhour_, DATE_FORMAT(ReturnHour, '%m') returnminutes_, "
                + "CityName, ZIPCode, PlaceName, PlaceAddress "
                + "FROM user, route, city, place "
                + "WHERE user.IdCity = city.IdCity "
                + "AND route.IdPlace = place.IdPlace "
                + "AND user.IdUser = route.IdUser "
                + "AND user.IdUser = ?";
        st = Conn.prepare(q);
        st.setString(1, Integer.toString(iduser));
        ResultSet routes = st.executeQuery();

        Route route = new Route();

        while (!routes.next()) {
            route.setWeekday(Route.Weekday.valueOf(routes.getString("Day")));
            route.setStartTime(routes.getInt("gohour_"), routes.getInt("gominutes_"));
            route.setEndTime(routes.getInt("returnhour_"), routes.getInt("returnminutes_"));
            route.setStart(routes.getString("CityName") + " " + routes.getString("ZIPCode"));
            route.setEnd(routes.getString("PlaceName") + " " + routes.getString("PlaceAddress"));
            r.getRoutes().add(route);
        }

        routes.close();
        st.close();

        return r;
    }

    public static User create(String name, String pwdHash,
            String firstName, String lastName, boolean driver
    ) throws SQLException {
        User u = new User();
        u.name = name;
        u.passwd = pwdHash;
        u.firstName = firstName;
        u.lastName = lastName;
        u.driver = driver;

// Ne pas créer un utilisateur qui existe déjà.
//??????????
//?????????
        String req = "INSERT INTO user "
                + "       (MailAddress, Password, FirstName, LastName, Driver) "
                + "VALUES (?, ?, ?, ?, ?)";
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
}
