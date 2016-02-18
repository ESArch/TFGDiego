import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * @author saxman
 */
public class PlacesService {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/search";

    private static final String OUT_JSON = "/json";

    // KEY TFGDiego
//  private static final String API_KEY = "AIzaSyDwnYqIM4r8BJ0iXYYCWdAwtS_laXMRM38";
    // KEY TFGDiego 2
//    private static final String API_KEY = "AIzaSyB0MJiB8oSOJZSwgAYIelhLzf8grvcZDBI";
    // KEY TFGDiego 3
//    private static final String API_KEY = "AIzaSyAG_UeWcd0gs0x6yfEHrBMlMtsMAPNiZYI";
    // KEY TFGDiego 4
//    private static final String API_KEY = "AIzaSyDT4Zuo42oTn61TqsuTTVxZXXD1CqVhKlU";
    // KEY TFGDiego 5
//    private static final String API_KEY = "AIzaSyASLxaQalnk-aHtC6YS0_xAKFwbE9Xv-VM";
    // KEY TFGDiego 6
//    private static final String API_KEY = "AIzaSyC0JdK_ZvFzqO8FT7T2P9ZYF-RmQZ-1DQs";
    // KEY TFGDiego 7
//    private static final String API_KEY = "AIzaSyDO2m62Zu9i7TZC2gDDzY2PPI-NYdq9fSg";
    // KEY TFGDiego 8
//    private static final String API_KEY = "AIzaSyBjFfOSaD8oD1xwWNYxG65meRLz36y7_74";
    // KEY TFGDiego 9
//    private static final String API_KEY = "AIzaSyAnHGQP9_giFPes2arPbjWMuMTlw1dHuXc";
    // KEY TFGDiego 10
    private static final String API_KEY = "AIzaSyDr94a5pt5VsiKIdg2cYYlqssidWcIrdIo";


    public static ArrayList<Place> autocomplete(String input) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_AUTOCOMPLETE);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            System.out.println("Error processing Places API URL" + e);
            return resultList;
        } catch (IOException e) {
            System.out.println("Error connecting to Places API" + e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
//                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
//                place.name = predsJsonArray.getJSONObject(i).getString("description");
                resultList.add(place);
            }
        } catch (JSONException e) {
            System.out.println("Error processing JSON results" + e);
        }

        return resultList;
    }

    public static ArrayList<Place> search(String keyword, double lat, double lng, int radius) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));

            System.out.println(sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            System.out.println("Error processing Places API URL" + e);
            return resultList;
        } catch (IOException e) {
            System.out.println("Error connecting to Places API" + e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                //name
                place.name = predsJsonArray.getJSONObject(i).getString("name");
                //place_id
                place.place_id = predsJsonArray.getJSONObject(i).getString("place_id");
                //types
                JSONArray jsonArray = predsJsonArray.getJSONObject(i).getJSONArray("types");
                for(int j = 0; j < jsonArray.length(); j++){
                    place.addType((String) jsonArray.get(j));
                }
                //latitude
                place.latitude = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                //longitude
                place.longitude = predsJsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                resultList.add(place);
            }
        } catch (JSONException e) {
            System.out.println("Error processing JSON results" + e);
        }

        return resultList;
    }

    public static void details(String name ,String reference) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_DETAILS);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            System.out.println("Error processing Places API URL" + e);
//            return null;
        } catch (IOException e) {
            System.out.println("Error connecting to Places API" + e);
//            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try{
            PrintWriter writer = new PrintWriter(name+".json", "UTF-8");
            writer.print(jsonResults.toString());
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }


//        Place place = null;
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
//
//            place = new Place();
////            place.icon = jsonObj.getString("icon");
//            place.name = jsonObj.getString("name");
////            place.formatted_address = jsonObj.getString("formatted_address");
////            if (jsonObj.has("formatted_phone_number")) {
////                place.formatted_phone_number = jsonObj.getString("formatted_phone_number");
////            }
//        } catch (JSONException e) {
//            System.out.println("Error processing JSON results" + e);
//        }
//
//        return place;
    }
}