import java.io.PrintWriter;

/**
 * Created by Arch on 3/8/2016.
 */
public class PruebaYelp {
    public static void main(String[] args) throws Exception{
        YelpAPI yelpAPI = new YelpAPI("8wazNzB9sixKLsmCM--x8w","f9QuqvKM0Z4KVwfPRLrZi420OOc","Ea_VzTmC9o0gUiykpF3GirQriYLM9rmH","n2CvKeX5l5Qw1sJjVmC3tedO_Io");

        String reply = yelpAPI.searchForBusinessesByCoordinates("39.47431465", "-0.37547222", 20 );
        PrintWriter writer = new PrintWriter("PlazaReina.json");
        writer.print(reply);
        writer.flush();
        writer.close();
    }
}
