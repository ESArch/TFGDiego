import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;
import twitter4j.*;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Arch on 1/22/2016.
 */
public class PruebaTwitter {

    public static void main(String [] args) throws Exception {
        HashSet<String> typeSet = new HashSet<>();

        GooglePlaces client = new GooglePlaces("AIzaSyDwnYqIM4r8BJ0iXYYCWdAwtS_laXMRM38");

        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();




        Query query = new Query("from:otobus_amarillo");
        QueryResult result = twitter.search(query);

        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
//            GeoLocation geo = status.getGeoLocation();
//            if(geo != null) {
//                List<Place> places = client.getNearbyPlaces(geo.getLatitude(), geo.getLongitude(), 20);
//                for(Place p : places)
//                    typeSet.addAll(p.getTypes());
//            }
        }


//        PrintWriter writer = new PrintWriter("tuits.txt", "UTF-8");
//
//        String usuario = "google";
//
//        //Extraer los 150 últimos tuits de un usuario
//        Paging paging = new Paging(1, 150);
//        List<Status> statuses = twitter.getUserTimeline(usuario, paging);
//        for (Status status : statuses){
//            //System.out.printf("@%s with id: %s twitted \"%s\"\n", status.getUser().getName(), status.getUser().getId(), status.getText());
//            String tuit = "@" + status.getUser().getName() + " with id " + status.getUser().getId() + " twitted \"" + status.getText() +"\".";
//            writer.println(tuit);
//        }
//        System.out.printf("%d twits found", statuses.size());
//
//        writer.close();
    }
}
