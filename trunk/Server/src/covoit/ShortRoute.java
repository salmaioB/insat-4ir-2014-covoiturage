/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author yorrickbarnay
 */
public class ShortRoute {
    private int User;
    private int City;
    private int Place;
    

    public ShortRoute(int usr, int city, int place){
        this.User=usr;
        this.City= city;
        this.Place = place;
    }
    
    public JsonObject getJsonObjectShortUser() {
        JsonObjectBuilder pl = Json.createObjectBuilder();
        pl.add("IdUser", this.getUser())
                .add("IdCity", this.getCity())
                .add("IdPlace", this.getPlace());
        return pl.build();
    }
    
    public int getUser() {
        return User;
    }
    public int getCity() {
        return City;
    }
    public int getPlace() {
        return Place;
    }
}
