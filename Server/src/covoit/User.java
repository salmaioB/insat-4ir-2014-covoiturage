/**
 * ***************************************************************************
 */
/* server/src/covoit/User.java                                     2014-12-11 */
/* Covoiturage Sopra - INSA Toulouse                     Félix Julie Philippe */
/*----------------------------------------------------------------------------*/
/*  BD
    
 Table user :
 1- IdUser : Int(10) ---------- CP
 2- MailAddress : Varchar(100)
 3- LastName : Varchar(50)
 4- FirstName : Varchar(50)
 5- Password : Varchar(60)
 6- Driver : Enum(Y,N)
 7- IdCity : Int(10) ---------- FK

 Table route :
 1- IdUser : Int(10) ---------- CP
 2- Day : Enum(Monday,...) ---- CP
 3- GoHour : Time
 4- ReturnHour : Time
 5- IdPlace : Int(10) --------- FK

 Table place :
 1- IdPlace : Int(10) --------- CP
 2- PlaceName : Varchar(50)
 3- PlaceAddress : Text

 Table city :
 1- IdCity : Int(10) ---------- CP
 2- CityName : Varchar(50)
 3- ZIPCode : Varchar(50)
 */
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
    private String name; // l'adresse email
    private String passwd;
    private String firstName;
    private String lastName;
    private String city;
    private int zipcode;
    private boolean driver; // Vrai si la personne préfère conduire elle-même.
    private ArrayList<Route> routes;
	private boolean notifyByMail;
	private boolean notifyByPush;
	private String notifyAddress;

    public User() {
        routes = new ArrayList<>();
    }

    /**
     * base64(bcrypt([password]))
     */
	
	public boolean getNotifyByMail() {
		return notifyByMail;
	}
	
	public boolean getNotifyByPush() {
		return notifyByPush;
	}
	
	public String getNotifyAddress() {
		return notifyAddress;
	}
	
	public static void setNotifySettings(String emailAddr, boolean email, boolean push, String address) throws SQLException {
		String q = "UPDATE user SET NotifyByEmail = ?, NotifyByPush = ?, NotifyAddress = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setBoolean(1, email);
        st.setBoolean(2, push);
        st.setString(3, address);
        st.setString(4, emailAddr);
        st.execute();
        st.close();
	}
	
    public String getName() {
        return name;
    }

    public String getPassword() {
        return passwd;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public boolean isDriver() {
        return driver;
    }

    public String getCity() {
        return city;
    }

    public int getZipCode() {
        return zipcode;
    }

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
            rs.next();
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

    // Mise à jour de la route (Philippe : Ajout requête et différents traitements)
    public static void updateRoute(String mailAddr, Route route) throws SQLException {
        // Recherche de l'IdUser pour toutes les requêtes suivantes
        String q = "SELECT IdUser FROM user WHERE MailAddress = ?";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, mailAddr);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            int idUser = rs.getInt("IdUser");

            q = "UPDATE route SET GoHour = ?, ReturnHour = ?, IdPlace = ?, Notify = ? WHERE route.IdUser = ? AND route.Day = ?;";

            st = Conn.prepare(q);
            st.setString(1, route.getStartHour() + ":" + route.getStartMinute() + ":00");
            st.setString(2, route.getEndHour() + ":" + route.getEndMinute() + ":00");
            st.setInt(3, route.getPlaceID());
            st.setString(4, route.getNotifyUser() ? ("Y") : ("N"));
            st.setInt(5, idUser);
            st.setString(6, route.getWeekday().toString());
            st.execute();
            st.close();
        }

        rs.close();
    }

    public static void addRoute(String mailAddr, Route route) throws SQLException {
        // Recherche de l'IdUser pour toutes les requêtes suivantes
        String q = "SELECT IdUser FROM user WHERE MailAddress = ?";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, mailAddr);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            int idUser = rs.getInt("IdUser");

            q = "INSERT INTO covoitsopra.route (`IdUser`, `Day`, `GoHour`, `ReturnHour`, `IdPlace`, `Notify`) "
                    + "VALUES (?, ?, ?, ?, ?, ?);";

            st = Conn.prepare(q);
            st.setInt(1, idUser);
            st.setString(2, route.getWeekday().toString());
            st.setString(3, route.getStartHour() + ":" + route.getStartMinute() + ":00");
            st.setString(4, route.getEndHour() + ":" + route.getEndMinute() + ":00");
            st.setInt(5, route.getPlaceID());
            st.setString(6, route.getNotifyUser() ? ("Y") : ("N"));

            st.execute();
            st.close();
        }
        rs.close();
    }

    public static void removeRoute(String mailAddr, Route.Weekday day) throws SQLException {
        // Recherche de l'IdUser pour toutes les requêtes suivantes
        String q = "SELECT IdUser FROM user WHERE MailAddress = ?";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, mailAddr);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            int idUser = rs.getInt("IdUser");

            q = "DELETE FROM covoitsopra.route WHERE `IdUser` = ? AND `Day` = ?;";
            st = Conn.prepare(q);
            st.setInt(1, idUser);
            st.setString(2, day.toString());

            st.execute();
            st.close();
        }
        rs.close();
    }

    /**
     * @param name adresse mail
     */
    // Julie : Ajout requêtes et traitement
    public static User load(String name) throws SQLException {
        User r = new User();

        String q = "SELECT IdUser, Password, FirstName, LastName, Driver, ZipCode, CityName "
                + "FROM user, city "
                + "WHERE MailAddress = ? "
                + "AND city.IdCity = user.IdCity";
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
        r.city = u.getString("CityName");
        r.zipcode = u.getInt("ZipCode");
        r.notifyByMail = u.getBoolean("NotifyByMail");
        r.notifyByPush = u.getBoolean("NotifyByPush");
        r.notifyAddress = u.getString("NotifyAddress");
        r.zipcode = u.getInt("ZipCode");
         int iduser = u.getInt("IdUser");

        u.close();
        st.close();

        q = "SELECT Day, DATE_FORMAT(GoHour, '%H') gohour_, DATE_FORMAT(GoHour, '%i') gominutes_,"
                + "DATE_FORMAT(ReturnHour, '%H') returnhour_, DATE_FORMAT(ReturnHour, '%i') returnminutes_, route.IdPlace, route.Notify "
                + "FROM user, route, place "
                + "WHERE user.IdUser = ? AND route.IdUser = user.IdUser AND place.`IdPlace` = route.`IdPlace`";

        st = Conn.prepare(q);
        st.setString(1, Integer.toString(iduser));
        ResultSet routes = st.executeQuery();

        while (routes.next()) {
            Route route = new Route();

            route.setWeekday(Route.Weekday.valueOf(routes.getString("Day")));
            route.setStartTime(routes.getInt("gohour_"), routes.getInt("gominutes_"));
            route.setEndTime(routes.getInt("returnhour_"), routes.getInt("returnminutes_"));
            route.setPlaceID(routes.getInt("route.IdPlace"));
            route.setNotifyUser(routes.getBoolean("route.Notify"));

            r.getRoutes().add(route);
        }

        routes.close();
        st.close();

        return r;
    }

    public static ArrayList<ShortUser> searchRoutes(String mailAddr, Route.Weekday day, boolean direction) throws SQLException {
        ArrayList<ShortUser> userList = new ArrayList<>();
        String req;

        // Récup des infos de l'utilisateur courant
        if (direction) 
            req = "select DATE_FORMAT(ReturnHour, '%H') hour_,DATE_FORMAT(ReturnHour, '%i') minute_,Driver,IdPlace,IdCity,user.IdUser from user,route "
                    + "where user.IdUser = route.IdUser AND route.Day = ? "
                    + "AND MailAddress = ?";
            
        else
            req = "select DATE_FORMAT(GoHour, '%H') hour_,DATE_FORMAT(GoHour, '%i') minute_,Driver,IdPlace,IdCity,user.IdUser from user,route "
                    + "where user.IdUser = route.IdUser AND route.Day = ? "
                    + "AND MailAddress = ?";
        PreparedStatement st = Conn.prepare(req);
        st.setString(1, day.toString());
        st.setString(2, mailAddr);
        ResultSet u = st.executeQuery();
        
		if(!u.next()) {
			return null;
		}

		// Requête pour récup des utilisateurs correspondants
        if (direction) 
            req = "select MailAddress,FirstName,LastName,DATE_FORMAT(ReturnHour, '%H') hour_,DATE_FORMAT(ReturnHour, '%i') minute_,Driver from user,route "
                    + "where user.IdUser = route.IdUser AND route.Day = ? "
                    + "AND DATE_FORMAT(ReturnHour, '%H') = ?"
                    + "AND DATE_FORMAT(ReturnHour, '%i') = ?"
                    + "AND IdPlace = ?"
 					+ "AND user.IdUser <> ? "
                    + "AND IdCity = ?";
         else
            req = "select MailAddress,FirstName,LastName,DATE_FORMAT(GoHour, '%H') hour_,DATE_FORMAT(GoHour, '%i') minute_,Driver from user,route "
                    + "where user.IdUser = route.IdUser AND route.Day = ? "
                    + "AND DATE_FORMAT(GoHour, '%H') = ?"
                    + "AND DATE_FORMAT(GoHour, '%i') = ?"
                    + "AND IdPlace = ?"
					+ "AND user.IdUser <> ? "
                    + "AND IdCity = ?";
				
        PreparedStatement st2 = Conn.prepare(req);
        st2.setString(1, day.toString());
        st2.setString(2, u.getString("hour_"));
        st2.setString(3, u.getString("minute_"));
        st2.setString(4, u.getString("IdPlace"));
		st2.setInt(5, u.getInt("user.IdUser"));
        st2.setString(6, u.getString("IdCity"));
        ResultSet u2 = st2.executeQuery();

        while (u2.next()) {
            userList.add(new ShortUser(u2.getString("MailAddress"),u2.getString("FirstName"),u2.getString("LastName"),u2.getInt("hour_"),u2.getInt("minute_"),(u2.getString("Driver").equals("Y"))));
        }
        return userList;
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
                + "       (MailAddress, Password, FirstName, LastName, Driver, NotifyAddress) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement st = Conn.prepare(req);
        st.setString(1, name);
        st.setString(2, pwdHash);
        st.setString(3, firstName);
        st.setString(4, lastName);
        st.setString(5, (driver) ? ("Y") : ("N"));
        st.setString(6, name);
        st.execute();
        st.close();

        return u;
    }
}
