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

    public JsonObject getJsonObjectShortUser() {
        JsonObjectBuilder pl = Json.createObjectBuilder();
        pl.add("name", this.getName())
                .add("firstName", this.getFirstName())
                .add("lastName", this.getLastName())
                .add("hour", this.getHour());
        return pl.build();
    }
}
