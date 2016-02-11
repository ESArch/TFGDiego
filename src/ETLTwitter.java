import com.google.gson.stream.JsonReader;
import net.maritimecloud.internal.core.javax.json.Json;
import net.maritimecloud.internal.core.javax.json.stream.JsonParser;
import net.maritimecloud.internal.core.javax.json.stream.JsonParser.Event;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Arch on 2/4/2016.
 */
public class ETLTwitter {



    public static void main(String[] args) throws Exception {
//        JSONObject jsonObject = parsearJSON("tuitsValencia.json");
        procesarGSON("valencia.json");
//        System.out.println(noGeo);
    }


    public static JSONObject parsearJSON(String fileName) throws IOException{

        File file = new File(fileName);

        int len;
        char[] chr = new char[4096];
        final StringBuilder json = new StringBuilder();
        final FileReader reader = new FileReader(file);


        //Read the json file
        try {
            while ((len = reader.read(chr)) > 0) {
                json.append(chr, 0, len);
            }
        }finally {
            reader.close();
        }


        //Build a JSONObject from the file
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(json.toString());
        }
        catch (JSONException e){
            System.out.println("Error processing JSON results" + e);
        }

        return jsonObject;
    }

    public static void procesarJSON(String fileName){
        int noGeolocalizados = 0;

        try (InputStream is = new FileInputStream(fileName);
             JsonParser parser = Json.createParser(is)) {

            while (parser.hasNext()) {
                Event event = parser.next();

                if(event == Event.KEY_NAME){
                    if(parser.getString().equals("text")){
                        parser.next();
                        System.out.println(parser.getString());
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void procesarGSON(String fileName) throws IOException{
        List<Tweet> tweets = readJsonStream(new FileInputStream(fileName));
    }

    public static List<Tweet> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public static List<Tweet> readMessagesArray(JsonReader reader) throws IOException {
        List<Tweet> messages = new ArrayList<Tweet>();

        int i = 0;
        //reader.beginObject();
        while (reader.hasNext() && i < 20) {
            messages.add(readMessage(reader));
//            i++;
            //break;
        }
        //reader.endObject();
        return messages;
    }

    public static Tweet readMessage(JsonReader reader) throws IOException {
        Tweet tweet = new Tweet();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if(name.equals("id_str")) {
                tweet.id = reader.nextString();
            } else if (name.equals("text")) {
                String text = reader.nextString();
                text = text.replaceAll("'", "''");
                tweet.text = text;
            } else if (name.equals("lang")) {
                tweet.lang = reader.nextString();
            } else if (name.equals("user")) {
                tweet.user = readUser(reader);
            } else if (name.equals("coordinates")) {
                readCoordinates(reader, tweet);
            } else if (name.equals("entities")) {
                readHashTags(reader, tweet);
            } else if (name.equals("created_at")) {
                reader.beginObject();
                reader.nextName();
                long createdAt = reader.nextLong();
                tweet.timestamp = createdAt;
                reader.endObject();
            } else {
                reader.skipValue();
            }

        }

        DBLoader.connect();
        DBLoader.insertToDB(tweet);
        reader.endObject();
        return tweet;
    }

    public static TwitterUser readUser(JsonReader reader) throws IOException{
        TwitterUser user = new TwitterUser();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("name")) {
                user.name = reader.nextString();
            } else if (name.equals("id_str")) {
                user.id = reader.nextString();
            } else if (name.equals("lang")) {
                user.lang = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return user;
    }

    public static void readCoordinates(JsonReader reader, Tweet tweet) throws IOException{
        reader.beginObject();

        while(reader.hasNext()){
            String name = reader.nextName();

            if(name.equals("coordinates")){
                reader.beginArray();

                tweet.longitude = reader.nextDouble();
                tweet.latitude = reader.nextDouble();

                reader.endArray();
            } else {
                reader.skipValue();
            }

        }

        reader.endObject();
    }

    public static void readHashTags(JsonReader reader, Tweet tweet) throws IOException {
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("hashtags")) {
                reader.beginArray();
                while(reader.hasNext()){
                    reader.beginObject();
                    while(reader.hasNext()){
                        if(reader.nextName().equals("text")){
                            tweet.hashTags.add(reader.nextString());
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
}
