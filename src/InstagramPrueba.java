import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javafx.beans.property.StringProperty;
import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.common.Pagination;
import org.jinstagram.entity.locations.LocationInfo;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

public class InstagramPrueba {
    private static final Token EMPTY_TOKEN = null;

    public static void main(String[] args) throws Exception{


        //Setup de Instagram
        String clientId = "44e6baf1dc5d45a082cae725ffa2f36d";
        String clientSecret = "c9209756c7fa49bd9de195eda95f0b54";

        String callbackUrl = "http://reveal-it.appspot.com/oauthtest";

        InstagramService service = new InstagramAuthService().apiKey(clientId)
                .apiSecret(clientSecret).callback(callbackUrl).build();

        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

        System.out.println("** Instagram Authorization ** \n\n");

        System.out.println("Copy & Paste the below Authorization URL in your browser...");
        System.out.println("Authorization URL : " + authorizationUrl);

        Scanner sc = new Scanner(System.in);

        String verifierCode;

        System.out.print("Your Verifier Code : ");
        verifierCode = sc.next();

        System.out.println();

        Verifier verifier = new Verifier(verifierCode);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);

        Instagram instagram = new Instagram(accessToken);


//        String clientId = "b725973eb4844006959ce12f96b23d57";
//        String clientSecret = "e4f6daf3c5ae450ba10995b6d182c0c6";
//        String callBackURL = "https://www.upv.es/";

        //Coordenadas de Valencia
        double latitude = 39.4561165;
        double longitude = -0.3545661;
        double radius = 100;

        //1 Junio 2015
        long timeStamp = 1433116800;
        //1 Julio 2015
        long timeStampLimit = 1435708800;

//        this.searchMedia(instagram, timeStamp, timeStampLimit, latitude, longitude);

        searchPlaces(instagram, latitude, longitude);




    }

    //Usar timestamps para los parametros
    public static void searchMedia(Instagram instagram, long fechaInicio, long fechaFin, double latitude, double longitude) throws Exception{

        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        writer.println("userId;userName;mediaId;tags;createdTime;locationName;latitude;longitude");


        while(fechaInicio < fechaFin){
            Date minTimeStamp = new Date((long)fechaInicio*1000);
            Date maxTimeStamp = new Date((long)(fechaInicio+3600)*1000);

            System.out.println(minTimeStamp.toString());
            MediaFeed feed = instagram.searchMedia(latitude, longitude, maxTimeStamp, minTimeStamp, 5000);
            List<MediaFeedData> feeds = feed.getData();


            for(MediaFeedData data : feeds){
                String line = "";

                String userId = data.getUser().getId();
                String userName = data.getUser().getUserName();
                String mediaId = data.getUser().getId();
                String tags = data.getTags().toString();
                String createdTime = data.getCreatedTime();
                String locationName = data.getLocation().getName();
                String locationLatitude = String.valueOf(data.getLocation().getLatitude());
                String locationLongitude = String.valueOf(data.getLocation().getLongitude());

                line += userId + ";" + userName + ";" + mediaId + ";" + tags + ";" + createdTime + ";" + locationName + ";" + locationLatitude  + ";" + locationLongitude;
                writer.println(line);


            }

            fechaInicio += 3600;
        }

        writer.close();

    }

    public static void searchPlaces(Instagram instagram, double latitude, double longitude) throws Exception{
        int[] distancia = {1, 10, 25, 50};

        MediaFeed feed = instagram.searchMedia(latitude, longitude, null, null, 5000);
        List<MediaFeedData> feeds = feed.getData();
        for(MediaFeedData data : feeds){
            System.out.printf("Instagram -- Nombre: %s, latitud: %f, longitud: %f\n", data.getLocation().getName(), data.getLocation().getLatitude(), data.getLocation().getLongitude() );
            for(int i = 0; i < 4; i++){
                System.out.println("CON DISTANCIA " + distancia[i]);
                System.out.println();

                ArrayList<Place> places = PlacesService.search("", data.getLocation().getLatitude(), data.getLocation().getLongitude(), distancia[i]);
                for(Place place : places){
                    System.out.printf("\tGoogle Places -- Nombre: %s, latitud: %f, longitud: %f, tipos: %s\n", place.name, place.latitude, place.longitude, place.types);
                }
                System.out.println();
            }

        }
    }
}