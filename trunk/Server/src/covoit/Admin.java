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
    
    public String getName() {return name;}
    public String getPassword() {return passwd;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    
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
                    return null; //got 0 rows
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
    
    
    public static void addPlace(String placeName, String placeAddress) throws SQLException {

        String q = "SELECT * FROM place WHERE PlaceName = ? OR PlaceAddress = ?;";
        PreparedStatement st = Conn.prepare(q);
        st.setString(1, placeName);
        st.setString(2, placeAddress);

        ResultSet rs = st.executeQuery();
                
        //si ni le nom, ni l'addresse n'ont jamais été rentré
        if (!rs.next()) {
            q = "INSERT INTO covoitsopra.place (`PlaceName`, `PlaceAddress`) "
                    + "VALUES (?, ?);";

            st = Conn.prepare(q);
            st.setString(1, placeName);
            st.setString(2, placeAddress);

            st.execute();
            st.close();
        }
        
        rs.close();

    }
    
    public static void deletePlace(String placeName, String placeAddress) throws SQLException {

        String q = "DELETE FROM covoitsopra.place WHERE PlaceName = ? AND PlaceAddress = ?;";
        PreparedStatement  st = Conn.prepare(q);
        
        st.setString(1, placeName);
        st.setString(2, placeAddress);

        st.execute();
        st.close();

    }
    
    public static int nbrDrivers() throws SQLException {
        String q = "SELECT COUNT(*) from `user` WHERE Driver='y';";
        PreparedStatement  st = Conn.prepare(q);
        
        ResultSet rs = st.executeQuery();
        
        int rslt = rs.getInt("COUNT(*)");
        
        st.close();
        rs.close();
        
        return rslt;
    }
    
    public static int nbrNonDrivers() throws SQLException {
        String q = "SELECT COUNT(*) from `user` WHERE Driver='n';";
        PreparedStatement  st = Conn.prepare(q);
        
        ResultSet rs = st.executeQuery();
        
        int rslt = rs.getInt("COUNT(*)");
        
        st.close();
        rs.close();
        
        return rslt;
    }
    
     public static int nbrUsers() throws SQLException {
        String q = "SELECT COUNT(*) from `user`;";
        PreparedStatement  st = Conn.prepare(q);
        
        ResultSet rs = st.executeQuery();
        
        int rslt = rs.getInt("COUNT(*)");
        
        st.close();
        rs.close();
        
        return rslt;
    }
     
       public static int nbrUserstoWorkplace(String day, int place) throws SQLException {
        String q = "select COUNT(DISTINCT IdUser) from route WHERE route.`Day` = ? AND IdPlace= ?;";
        PreparedStatement  st = Conn.prepare(q);
        st.setString(1, day);
        st.setInt(2, place);

        ResultSet rs = st.executeQuery();
        
        int rslt = rs.getInt("COUNT(*)");
        
        st.close();
        rs.close();
        
        return rslt;
    }
}
