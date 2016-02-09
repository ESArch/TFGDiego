import com.google.gson.stream.JsonReader;
import net.maritimecloud.internal.core.javax.json.Json;
import net.maritimecloud.internal.core.javax.json.stream.JsonParser;
import net.maritimecloud.internal.core.javax.json.stream.JsonParser.Event;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Arch on 2/4/2016.
 */
public class ExtractorTwitter {



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
            //i++;
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

        insertToDB(tweet);
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

    public static Connection getConnection(){
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgis_22_sample",
                            "postgres", "postgres");
            connection.setAutoCommit(true);
            System.out.println("Opened database succesfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return connection;
    }

    public static void insertToDB(Tweet tweet) {

        Connection connection = getConnection();

        insertUser(connection, tweet);

        insertTweet(connection, tweet);

        insertHashTags(connection, tweet);
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    public static void insertUser(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            //Insert usuario
            sql = "INSERT INTO usuario (usu_id, usu_idioma)"
                    + "VALUES ('" + tweet.user.id + "','" + tweet.user.lang + "');";
            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void insertTweet(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            //Insert tweet
            sql = "INSERT INTO tweet (twe_id, twe_texto, twe_usuario, twe_coordenadas, twe_fecha_creacion, twe_hora_creacion)"
                    + "VALUES ('" + tweet.id + "','" + tweet.text + "','" + tweet.user.id + "',ST_GeomFromText('POINT(" +tweet.latitude + " " +tweet.longitude + ")', 4326), '" + tweet.getDate() + "', '" + tweet.getTime() +"');";
            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void insertHashTags(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";

        for(String hashTag : tweet.hashTags){
            //Insertar hashTag
            try {
                statement = connection.createStatement();

                sql = "INSERT INTO hashtag (has_etiqueta)"
                        + "VALUES ('" + hashTag +"');";
                statement.executeUpdate(sql);
                statement.close();
//                connection.commit();

            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.err.println(sql);
            }

            //Obtener id del hashTag insertado
            try {
                statement = connection.createStatement();

                sql = "SELECT has_codigo FROM hashtag WHERE has_etiqueta ='" + hashTag + "';";
                ResultSet rs = statement.executeQuery(sql);

                if(rs.next()){
                    sql = "INSERT INTO tweet_hashtag(twe_id, has_codigo)"
                            + "VALUES ('" + tweet.id + "','" + rs.getString("has_codigo") + "');";
                    statement.executeUpdate(sql);
                    statement.close();
//                    connection.commit();
                } else
                    statement.close();

            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.err.println(sql);
            }
        }

    }
}
