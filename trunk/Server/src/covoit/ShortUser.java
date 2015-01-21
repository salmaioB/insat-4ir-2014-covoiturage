/**
 * ***************************************************************************
 */
/* server/src/covoit/Route.java                                    2015-01-07 */
/* Covoiturage Sopra - INSA Toulouse                                    Julie */
/**
 * ***************************************************************************
 */
package covoit;

import javax.json.*;

public class ShortUser {

    private String name; // l'adresse email
    private String firstName;
    private String lastName;
    private int hour;
    private int minute;
    private boolean driver;

    public ShortUser(String name, String firstName, String lastName, int hour, int minute, boolean driver){
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hour = hour;
        this.minute = minute;
        this.driver = driver;
    }
    
    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getHour() {
        return hour;
    }
    
    public int getMinute() {
        return minute;
    }
    
    public boolean getDriver() {
        return driver;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
    
    public void setMinute(int minute) {
        this.minute = minute;
    }
    
    public void setDriver (boolean driver) {
        this.driver = driver;
    }

    public JsonObject getJsonObjectShortUser() {
        JsonObjectBuilder pl = Json.createObjectBuilder();
        pl.add("name", this.getName())
                .add("firstName", this.getFirstName())
                .add("lastName", this.getLastName())
                .add("hour", this.getHour())
                .add("minute", this.getMinute())
                .add("driver", this.getDriver());
        return pl.build();
    }
}
