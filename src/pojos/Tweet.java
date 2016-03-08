package pojos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Arch on 2/4/2016.
 */
public class Tweet {

    public String text, id, lang, media;
    public double latitude, longitude;
    public TwitterUser user;
    public ArrayList<String> hashTags;
    public long timestamp;

    public Tweet(){
        hashTags = new ArrayList<>();
    }

    public String toString(){
        return "id: " + id + " with text: " + text +
                "\nCoordinates: " + latitude + ", " + longitude +
                "\nHashtags: " + hashTags +
                "\nMedia: " + media +
                "\nDate: " + getDate() + " " + getTime() +
                "\nUsuario: " + user.toString();
    }

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        return sdf.format(new Date(timestamp));
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

}
