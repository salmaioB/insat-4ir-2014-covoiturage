/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit;

import covoit.sql.Conn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.*;

/**
 *
 * @author polio
 */
public class City {
    private int id;
    private String name;
    private String zip;

    public City(int id, String name, String zip){
        this.id = id;
        this.name = name;
        this.zip = zip;
    }
    
    public int getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getZip(){
        return zip;
    }

    public static JsonArrayBuilder getCity() throws SQLException {
        JsonArrayBuilder array = Json.createArrayBuilder();
        String q = "SELECT IdCity, CityName, ZipCode FROM city;";

        PreparedStatement st = Conn.prepare(q);
        ResultSet u = st.executeQuery();

        while (u.next()) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("id", u.getInt("IdCity"));
            builder.add("name", u.getString("CityName"));
            builder.add("zip", u.getString("ZipCode"));

            array.add(builder);
        }

        return array;
    }
    
}
