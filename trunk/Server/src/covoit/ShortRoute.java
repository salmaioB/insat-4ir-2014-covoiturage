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
    private String User;
    private String City;
    private int Place;
    private String Day;
    private String goTime;
    private String returnTime; 
    

    public ShortRoute(String usr, String city, int place, String day, String go, String rtrn){
        this.User=usr;
        this.City= city;
        this.Place = place;
        this.Day = day;
        this.goTime = go;
        this.returnTime = rtrn;
    }
    
    public JsonObject getJsonObjectShortUser() {
        JsonObjectBuilder pl = Json.createObjectBuilder();
        pl.add("IdUser", this.getUser())
                .add("IdCity", this.getCity())
                .add("IdPlace", this.getPlace())
                .add("Day", this.getDay())
                .add("GoHour", this.getGoHour())
                .add("ReturnHour", this.getReturnHour());

        return pl.build();
    }
    
    public String getUser() {
        return User;
    }
    public String getCity() {
        return City;
    }
    public int getPlace() {
        return Place;
    }
    public String getDay() {
        return Day;
    }
    public String getGoHour() {
        return goTime;
    }
    public String getReturnHour() {
        return returnTime;
    }
}
