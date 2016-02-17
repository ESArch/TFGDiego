import se.walkercrou.places.GooglePlaces;


import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created by Arch on 1/26/2016.
 */
public class PruebaGoogle {

    public static void main (String[] args) throws Exception{

//        GooglePlaces client = new GooglePlaces("AIzaSyDwnYqIM4r8BJ0iXYYCWdAwtS_laXMRM38");
//
//        String [] sitios =  {"Valencia", "Madrid", "Barcelona", "Bilbao", "Extremadura"};
//        for(int i = 1; i < 5; i++){
//            List<Place> places = client.getPlacesByQuery(sitios[i]);
//            for(Place place : places)
//                System.out.printf("\tGoogle Places -- Nombre: %s, latitud: %f, longitud: %f, tipos: %s\n", place.getName(), place.getLatitude(), place.getLongitude(), place.getTypes());
//        }


//        getJson();
        searchByCoordenates();





    }

    public static void searchByCoordenates(){
        double latitude = 39.4561165;
        double longitude = -0.3545661;

        ArrayList<Place> places = PlacesService.search("Rumbo 144", latitude, longitude, 5000);
        for(Place place : places){
            System.out.printf("Name: %s, types: %s, latitude %f, longitude %f\n", place.name, place.types, place.latitude, place.longitude);
        }
    }

    public static void getJson() throws Exception{

        String name = "Real Basílica De Nuestra Señora De Los Desamparados";
        String reference ="CpQBigAAACPX7Vn3CD1LwSv1kl5EvZlEMIPo72mjNCFpc8mB6vJ2TRYIza74bumzMFazHfJDDp-BftflY-MUvJQ1qxSNE2Y8eD0--JK7-IHSDvfiUKGW9IkMsTvQIFZxQ9b1-fFN8TCtFa74VlZ2dzBRtqZE8FUDHeAw7Ooe5uVjoxSMEmVQD5eAe7xrbyqsKMML6EVIahIQyLUNTzaFw3R4JRCJ_83rOBoUqkWzeo6sYcDWWqkHOoYRyJGg-2E";

        PlacesService.details(name, reference);



    }

}
