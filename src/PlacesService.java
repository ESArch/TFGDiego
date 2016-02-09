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

    // KEY!
    private static final String API_KEY = "AIzaSyDwnYqIM4r8BJ0iXYYCWdAwtS_laXMRM38";

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
                //reference
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
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