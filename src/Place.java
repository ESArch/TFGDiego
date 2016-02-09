import javafx.beans.property.StringProperty;

import java.util.ArrayList;

/**
 * Created by Arch on 1/29/2016.
 */
public class Place {
    String name, place_id, reference;

    double latitude, longitude;

    ArrayList<String> types;

    public Place (){
        types = new ArrayList<>();
    }

    public void addType(String type){
        types.add(type);
    }
}
