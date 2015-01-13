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
 * @author remi
 */
public class Workplaces {
    
	public static JsonArrayBuilder getWorkplaces() throws SQLException {
		JsonArrayBuilder array = Json.createArrayBuilder();
        String q = "SELECT IdPlace, PlaceName, PlaceAddress "
                + "FROM place ";
		
        PreparedStatement st = Conn.prepare(q);
        ResultSet u = st.executeQuery();
		
		while(u.next()) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("id", u.getInt("IdPlace"));
			builder.add("name", u.getString("PlaceName"));
			builder.add("address", u.getString("PlaceAddress"));
			
			array.add(builder);
        }
		
		return array;
	}
}
