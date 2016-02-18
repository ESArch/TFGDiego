import javafx.beans.property.StringProperty;

import java.util.ArrayList;

/**
 * Created by Arch on 1/29/2016.
 */
public class Place {
    String name, place_id, typeOfPlace;

    double latitude, longitude;

    boolean ofInterest;

    ArrayList<String> types;

    public Place (){
        ofInterest = true;
        types = new ArrayList<>();
    }

    public void addType(String type){
        types.add(type);
    }

    public String toString(){
        return "ID: " + place_id + " Name: " + name +
                "\n Latitude: " + latitude + " Longitude: " + longitude +
                "\nTypes: " + types +
                "\nType of place: " + typeOfPlace;
    }

    public void setTypeOfPlace() {
        if(types.contains("route") || types.contains("neighborhood"))
            ofInterest = false;
        else if(types.contains("art_gallery") || types.contains("museum"))
            typeOfPlace = "museum";
        else if(types.contains("church") || types.contains("city_hall") || types.contains("hindu_temple") || types.contains("mosque") || types.contains("place_of_worship") || types.contains("synagogue"))
            typeOfPlace = "monument";
        else if(types.contains("night_club"))
            typeOfPlace =  "night";
        else if(types.contains("lodging"))
            typeOfPlace =  "hotel";
        else if(types.contains("restaurant") || types.contains("bar") || types.contains("cafe") || types.contains("meal_delivery") || types.contains("meal_takeaway"))
            typeOfPlace =  "gastronomy";
        else if(types.contains("airport") || types.contains("bus_station") || types.contains("subway_station") || types.contains("taxi_stand") || types.contains("train_station"))
            typeOfPlace =  "transport";
        else if(types.contains("amusement_park") || types.contains("aquarium") || types.contains("bowling_alley") || types.contains("casino")
                || types.contains("movie_theater") || types.contains("park") || types.contains("spa") || types.contains("stadium") || types.contains("zoo"))
            typeOfPlace =  "ocio";
        else if(types.contains("department_store") || types.contains("shopping_mall") || types.contains("clothing_store") || types.contains("electronics_store") || types.contains("shoe_store") || types.contains("jewelry_store"))
            typeOfPlace =  "shopping";
        else
            typeOfPlace =  "undefined";
    }
}
