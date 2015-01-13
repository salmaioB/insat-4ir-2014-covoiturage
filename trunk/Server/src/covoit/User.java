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

    public User() {
        routes = new ArrayList<Route>();
    }

    /**
     * base64(bcrypt([password]))
     */
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
            st.setInt(4, idUser);
            st.setString(5, route.getWeekday().toString());
            st.setString(6, route.getNotifyUser() ? ("Y") : ("N"));
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

            q = "INSERT INTO covoitsopra.route (`IdUser`, `Day`, `GoHour`, `ReturnHour`, `IdPlace`) "
                    + "VALUES (?, ?, ?, ?, ?);";

            st = Conn.prepare(q);
            st.setInt(1, idUser);
            st.setString(2, route.getWeekday().toString());
            st.setString(3, route.getStartHour() + ":" + route.getStartMinute() + ":00");
            st.setString(4, route.getEndHour() + ":" + route.getEndMinute() + ":00");
            st.setInt(5, route.getPlaceID());

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
        int iduser = u.getInt("IdUser");

        u.close();
        st.close();

        q = "SELECT Day, DATE_FORMAT(GoHour, '%H') gohour_, DATE_FORMAT(GoHour, '%i') gominutes_,"
                + "DATE_FORMAT(ReturnHour, '%H') returnhour_, DATE_FORMAT(ReturnHour, '%i') returnminutes_, route.IdPlace "
                + "FROM user, route, city, place "
                + "WHERE route.IdUser = ? ";

        st = Conn.prepare(q);
        st.setString(1, Integer.toString(iduser));
        ResultSet routes = st.executeQuery();

        while (routes.next()) {
            Route route = new Route();

            route.setWeekday(Route.Weekday.valueOf(routes.getString("Day")));
            route.setStartTime(routes.getInt("gohour_"), routes.getInt("gominutes_"));
            route.setEndTime(routes.getInt("returnhour_"), routes.getInt("returnminutes_"));
            route.setPlaceID(routes.getInt("route.IdPlace"));

            r.getRoutes().add(route);
        }

        routes.close();
        st.close();

        return r;
    }

    public static ArrayList<ShortUser> searchRoutes(String mailAddr, Route.Weekday day, boolean direction) throws SQLException {

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
