/**
 * Created by Arch on 2/21/2016.
 */
public class Monument {
    public String id, name, typeOfPlace;
    public double latitude, longitude;
    public int taPos;

    public String toString(){
        return typeOfPlace.toUpperCase() + " " + name + " with ID " + id +
                "\nTripAdvisor position: " + taPos +
                "\nLatitude: " + latitude + " longitude: " + longitude;
    }

}
