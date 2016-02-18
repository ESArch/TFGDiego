import com.google.gson.stream.JsonReader;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Arch on 2/4/2016.
 */
public class ETLTwitter {



    public static void main(String[] args) throws Exception {
        procesarGSON("valencia.json");
    }

    public static void procesarGSON(String fileName) throws IOException{
        List<Tweet> tweets = readJsonStream(new FileInputStream(fileName));
    }

    public static List<Tweet> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        try {
            return readTweetsArray(reader);
        } finally {
            reader.close();
        }
    }

    public static List<Tweet> readTweetsArray(JsonReader reader) throws IOException {
        List<Tweet> messages = new ArrayList<Tweet>();

        int i = 0;
        while (reader.hasNext() && i < 20) {
            messages.add(readTweet(reader));
//            i++;
        }
        return messages;
    }

    public static Tweet readTweet(JsonReader reader) throws IOException {
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
                readEntities(reader, tweet);
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

//        System.out.println(tweet);

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

    public static void readEntities(JsonReader reader, Tweet tweet) throws IOException {
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("hashtags")) {
                readHashTags(reader, tweet);
            } else if( name.equals("media")) {
                readMedia(reader, tweet);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private static void readMedia(JsonReader reader, Tweet tweet) throws IOException{
        reader.beginArray();
        while(reader.hasNext()){
            reader.beginObject();
            while(reader.hasNext()){
                if(reader.nextName().equals("media_url")){
                    tweet.media = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
    }

    private static void readHashTags(JsonReader reader, Tweet tweet) throws IOException{
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
    }


}
