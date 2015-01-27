/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit;

import java.sql.*;
import covoit.sql.Conn;
import java.util.ArrayList;

/**
 *
 * @author yorrickbarnay
 */
public class Admin {

    private String name; //correspond à l'adresse email
    private String passwd;
    private String firstName;
    private String lastName;

    public Admin() {
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

    public static void updatePassword(String mailAddr, String password) throws SQLException {
        String q = "UPDATE user SET Password = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, password);
        st.setString(2, mailAddr);
        st.execute();
        st.close();
    }

    public static Admin load(String name) throws SQLException {
        Admin r = new Admin();

        String q = "SELECT IdAdmin, Password, FirstName, LastName "
                + "FROM admin "
                + "WHERE Login = ? ;";

        PreparedStatement st = Conn.prepare(q);
        st.setString(1, name);
        ResultSet u = st.executeQuery();

        if (!u.next()) {
            throw new SQLException(""); //got 0 rows
        }
        r.name = name;
        r.passwd = u.getString("Password");
        r.firstName = u.getString("FirstName");
        r.lastName = u.getString("LastName");
        int iduser = u.getInt("IdAdmin");

        u.close();
        st.close();

        return r;
    }

    ////////////////////////////////////////////////////////////////////////////
    //****************************************************************************
    //ACTIONS SUR LES WORKPLACES
    //****************************************************************************
    ////////////////////////////////////////////////////////////////////////////
    /**
     *
     * @return la liste de toutes les workplaces
     * @throws SQLException
     */
    public static ArrayList<Workplaces> loadPlaces() throws SQLException {
        ArrayList<Workplaces> listPlaces = new ArrayList<Workplaces>();

        String q = "SELECT IdPlace,PlaceName,PlaceAddress from place ORDER BY PlaceName,PlaceAddress;";

        PreparedStatement st = Conn.prepare(q);
        ResultSet u = st.executeQuery();

        while (u.next()) {
            listPlaces.add(new Workplaces(u.getInt("IdPlace"), u.getString("PlaceName"), u.getString("PlaceAddress")));

        }

        u.close();
        st.close();

        return listPlaces;
    }

    public static void addPlace(String placeName, String placeAddress) throws SQLException {

        String q = "SELECT IdPlace FROM place WHERE PlaceName = ? AND PlaceAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, placeName);
        st.setString(2, placeAddress);

        ResultSet rs = st.executeQuery();

        //si ni le nom, ni l'addresse n'ont jamais été rentré
        if (!rs.next()) {
            q = "INSERT INTO place (PlaceName, PlaceAddress) VALUES (?, ?);";

            st = Conn.prepare(q);
            st.setString(1, placeName);
            st.setString(2, placeAddress);

            st.execute();
            st.close();
        } else {
            throw new SQLException("Ce lieu de travail existe déjà.");
        }

        rs.close();
    }

    public static void deletePlace(String placeName) throws SQLException {
        String q = "DELETE FROM place WHERE PlaceName = ?;";
        PreparedStatement st = Conn.prepare(q);

        st.setString(1, placeName);

        st.execute();
        st.close();
    }

    ////////////////////////////////////////////////////////////////////////////
    //****************************************************************************
    //RAPPORTS
    //****************************************************************************
    ////////////////////////////////////////////////////////////////////////////
    public static int nbrDrivers() throws SQLException {
        String q = "SELECT COUNT(*) _nb, Driver FROM user WHERE Driver='y';";
        PreparedStatement st = Conn.prepare(q);

        ResultSet rs = st.executeQuery();

        if (!rs.next()) {
            return -1; //got 0 rows
            // throw exception
        }
        int rslt = rs.getInt("_nb");

        st.close();
        rs.close();

        return rslt;
    }

    public static int nbrNonDrivers() throws SQLException {
        String q = "SELECT COUNT(*) FROM user WHERE Driver='n';";
        PreparedStatement st = Conn.prepare(q);

        ResultSet rs = st.executeQuery();
        if (!rs.next()) {
            return -1; //got 0 rows
        }
        int rslt = rs.getInt("COUNT(*)");

        st.close();
        rs.close();

        return rslt;
    }

    public static int nbrUsers() throws SQLException {
        String q = "SELECT COUNT(*), Driver FROM user;";
        PreparedStatement st = Conn.prepare(q);

        ResultSet rs = st.executeQuery();
        if (!rs.next()) {
            return -1; //got 0 rows
        }
        int rslt = rs.getInt("COUNT(*)");

        st.close();
        rs.close();

        return rslt;
    }

    public static int nbrUserstoWorkplace(String day, int place) throws SQLException {
        String q = "select COUNT(DISTINCT IdUser) from route WHERE route.`Day` = ? AND IdPlace= ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, day);
        st.setInt(2, place);

        ResultSet rs = st.executeQuery();

        int rslt = rs.getInt("COUNT(*)");

        st.close();
        rs.close();

        return rslt;
    }

    ////////////////////////////////////////////////////////////////////////////
    //****************************************************************************
    //ACTIONS SUR LES USERS
    //****************************************************************************
    ////////////////////////////////////////////////////////////////////////////
    public static ArrayList<ShortUser> getUsers() throws SQLException {
        ArrayList<ShortUser> userList = new ArrayList<>();
        String req;

        req = "select MailAddress, FirstName, LastName, Driver from user ORDER BY LastName, Firstname ";

        PreparedStatement st = Conn.prepare(req);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            userList.add(new ShortUser(rs.getString("MailAddress"), rs.getString("FirstName"), rs.getString("LastName"), (rs.getString("Driver").equals("Y"))));
        }
        return userList;
    }

    public static void deleteUser(String mailAddress) throws SQLException {
        String q = "DELETE FROM user WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);

        st.setString(1, mailAddress);

        st.execute();
        st.close();
    }

    public static void chMailUser(String mailAddress, String newMail) throws SQLException {
        String q = "UPDATE user SET MailAddress = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);

        st.setString(1, newMail);
        st.setString(2, mailAddress);

        st.execute();
        st.close();
    }

    public static void chLastNameUser(String mailAddress, String newLName) throws SQLException {
        String q = "UPDATE user SET LastName = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);

        st.setString(1, newLName);
        st.setString(2, mailAddress);

        st.execute();
        st.close();
    }

    public static void chFirstNameUser(String mailAddress, String newFName) throws SQLException {
        String q = "UPDATE user SET FirstName = ? WHERE MailAddress = ?;";
        PreparedStatement st = Conn.prepare(q);

        st.setString(1, newFName);
        st.setString(2, mailAddress);

        st.execute();
        st.close();
    }

    public static void chPwdUser(String mailAddress, String Pwd) throws SQLException {

    }

    public static void chDriverUser(String mailAddress, boolean drives) throws SQLException {
    }
    ////////////////////////////////////////////////////////////////////////////
    //****************************************************************************
    //ACTIONS SUR LES CITY
    //****************************************************************************
    ////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @return la liste de toutes les city
     * @throws SQLException
     */
    public static ArrayList<City> loadCity() throws SQLException {
        ArrayList<City> listPlaces = new ArrayList<City>();

        String q = "SELECT IdCity, CityName, ZipCode FROM city ORDER BY CityName,ZipCode ;";

        PreparedStatement st = Conn.prepare(q);
        ResultSet u = st.executeQuery();

        while (u.next()) {
            listPlaces.add(new City(u.getInt("IdCity"), u.getString("CityName"), u.getString("ZipCode")));
        }

        u.close();
        st.close();

        return listPlaces;
    }

}
