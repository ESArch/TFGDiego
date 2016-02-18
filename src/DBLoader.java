import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Diego on 2/11/2016.
 */
public class DBLoader {
    static Connection connection = null;

    public static void main(String[] args){

//        filterUsersByTweets(5);
//        setFechaMaxima();
//        setFechaMinima();
//        filterByDays(15);
//        cleanUnusedHashTags();

//          tagTypeOfPlace();
        getPlacesFromTweets();
    }

    public static void connect(){
        if(connection == null)
            getConnection();
    }

    private static Connection getConnection(){
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

//        Connection connection = getConnection();

        insertUser(connection, tweet);

        insertTweet(connection, tweet);

        insertHashTags(connection, tweet);
//        try {
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }


    }

    private static void insertUser(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            //Insert usuario
            sql = "INSERT INTO usuario (usu_id, usu_idioma, usu_filtrado)"
                    + "VALUES ('" + tweet.user.id + "','" + tweet.user.lang +  "', false);";
            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            System.err.println(sql);
        }
    }

    private static void insertTweet(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            if(tweet.media != null)
            //Insert tweet
                sql = "INSERT INTO tweet (twe_id, twe_texto, twe_usuario, twe_coordenadas, twe_fecha_creacion, twe_hora_creacion, twe_idioma, twe_localizado, twe_media)"
                    + "VALUES ('" + tweet.id + "','" + tweet.text + "','" + tweet.user.id + "',ST_GeomFromText('POINT(" +tweet.latitude + " " +tweet.longitude + ")', 4326), '"
                    + tweet.getDate() + "', '" + tweet.getTime() + "', '" + tweet.lang +"', false, '" + tweet.media + "');";
            else
                sql = "INSERT INTO tweet (twe_id, twe_texto, twe_usuario, twe_coordenadas, twe_fecha_creacion, twe_hora_creacion, twe_idioma, twe_localizado)"
                        + "VALUES ('" + tweet.id + "','" + tweet.text + "','" + tweet.user.id + "',ST_GeomFromText('POINT(" +tweet.latitude + " " +tweet.longitude + ")', 4326), '"
                        + tweet.getDate() + "', '" + tweet.getTime() + "', '" + tweet.lang +"', false);";
            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static void insertHashTags(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";
        boolean existingHT;

        for(String hashTag : tweet.hashTags){
            //Insertar hashTag
            existingHT = false;
            try {
                statement = connection.createStatement();

                sql = "INSERT INTO hashtag (has_etiqueta)"
                        + "VALUES ('" + hashTag +"');";
                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

                ResultSet rs = statement.getGeneratedKeys();

                if(rs.next()) {
                    sql = "INSERT INTO tweet_hashtag(twe_id, has_codigo)"
                            + "VALUES ('" + tweet.id + "','" + rs.getInt("has_codigo") + "');";
                    statement.executeUpdate(sql);
                }

                statement.close();

            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.err.println(sql);
                existingHT = true;
            }

            if(existingHT) {
                try {
                    statement = connection.createStatement();

                    sql = "SELECT has_codigo FROM hashtag "
                            + "WHERE has_etiqueta = '" + hashTag + "';";
                    ResultSet rs = statement.executeQuery(sql);

                    if(rs.next()) {
                        sql = "INSERT INTO tweet_hashtag(twe_id, has_codigo)"
                                + "VALUES ('" + tweet.id + "','" + rs.getInt("has_codigo") + "');";
                        statement.executeUpdate(sql);
                    }

                    statement.close();

                } catch (SQLException e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.err.println(sql);
                }
            }

        }

    }

    public static void filterUsersByTweets(int minTweets) {
        Statement statement = null;
        String sql = "";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();

            sql = "UPDATE usuario " +
                    "SET usu_filtrado = true " +
                    "WHERE usu_id IN (SELECT twe_usuario FROM tweet GROUP BY twe_usuario HAVING COUNT(*) < " + minTweets + ");";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void setFechaMaxima(){
        Statement statement = null;
        String sql = "";
        ResultSet rs = null;

        if(connection == null)
            getConnection();

        try {
            statement = connection.createStatement();

            sql = "SELECT DISTINCT t1.twe_usuario, t1.twe_fecha_creacion " +
                    "FROM tweet t1 " +
                    "WHERE t1.twe_fecha_creacion >= ALL (SELECT t2.twe_fecha_creacion FROM tweet t2 WHERE t1.twe_usuario = t2.twe_usuario);";

            rs = statement.executeQuery(sql);

            statement = connection.createStatement();
            while (rs.next()){
                String userID = rs.getString("twe_usuario");
                Date date = rs.getDate("twe_fecha_creacion");

                sql = "UPDATE usuario " +
                        "SET usu_fecha_max = '" + date + "' " +
                        "WHERE usu_id = '" + userID + "';";

                statement.executeUpdate(sql);
            }
            statement.close();


        }catch (SQLException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void setFechaMinima(){
        Statement statement = null;
        String sql = "";
        ResultSet rs = null;

        if(connection == null)
            getConnection();

        try {
            statement = connection.createStatement();

            sql = "SELECT DISTINCT t1.twe_usuario, t1.twe_fecha_creacion " +
                    "FROM tweet t1 " +
                    "WHERE t1.twe_fecha_creacion <= ALL (SELECT t2.twe_fecha_creacion FROM tweet t2 WHERE t1.twe_usuario = t2.twe_usuario);";

            rs = statement.executeQuery(sql);

            statement = connection.createStatement();
            while (rs.next()){
                String userID = rs.getString("twe_usuario");
                Date date = rs.getDate("twe_fecha_creacion");

                sql = "UPDATE usuario " +
                        "SET usu_fecha_min = '" + date + "' " +
                        "WHERE usu_id = '" + userID + "';";

                statement.executeUpdate(sql);
            }
            statement.close();


        }catch (SQLException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void filterByDays(int minDays) {
        Statement statement = null;
        String sql = "";
        ResultSet rs = null;

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            sql = "UPDATE usuario " +
                    "SET usu_filtrado = true " +
                    "WHERE usu_id IN (SELECT usu_id FROM usuario WHERE (usu_fecha_max - usu_fecha_min + 1) > " + minDays +");";
            statement.executeUpdate(sql);
            statement.close();

        }catch (SQLException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void cleanUnusedHashTags(){
        Statement statement = null;
        String sql = "DELETE FROM hashtag h WHERE h.has_codigo NOT IN (SELECT t.has_codigo FROM tweet_hashtag t)";

        if(connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        }catch (SQLException e){
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        System.err.println(sql);
        }
    }


    public static boolean tagTypeOfPlace() {

        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT twe_id, ST_AsLatLonText(twe_coordenadas, 'D.DDDDDD') as coordenadas FROM tweet WHERE twe_tipo_lugar is null;";
        boolean terminado = true;


        if(connection == null)
            getConnection();

        try{
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);


            while (rs.next()){
                String tweetID = rs.getString("twe_id");
                String coordenadas = rs.getString("coordenadas");
//                System.out.println(coordenadas);

                String typeOfPlace = getTypeOfPlace(coordenadas);
                System.out.println(typeOfPlace);

                if(typeOfPlace == null){
                    System.out.println("Límite de Google Places superado");
                    terminado = false;
                    break;

                }

                statement = connection.createStatement();

                sql = "UPDATE tweet " +
                        "SET twe_tipo_lugar = '" + typeOfPlace + "' " +
                        "WHERE twe_id = '" + tweetID + "';";
                statement.executeUpdate(sql);
                statement.close();
            }

        }catch (SQLException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }

        return  terminado;
    }

    public static boolean getPlacesFromTweets() {

        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT twe_id, ST_AsLatLonText(twe_coordenadas, 'D.DDDDDD') as coordenadas " +
                "FROM tweet " +
                "WHERE twe_usuario IN (SELECT usu_id FROM usuario WHERE usu_filtrado = false) " +
                "AND twe_localizado = false LIMIT 10;";

        boolean terminado = true;

        if(connection == null)
            getConnection();

        try{
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);


            while (rs.next()){
                String tweetID = rs.getString("twe_id");
                String coordenadas = rs.getString("coordenadas");
                System.out.println(coordenadas);

                terminado = searchPlaces(coordenadas);

                if(!terminado){
                    System.out.println("Límite de Google Places superado");
                    break;
                }

//                statement = connection.createStatement();
//
//                sql = "UPDATE tweet " +
//                        "SET twe_localizado = true " +
//                        "WHERE twe_id = '" + tweetID + "';";
//                statement.executeUpdate(sql);
//                statement.close();
            }

        }catch (SQLException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }

        return  terminado;
    }

    private static boolean searchPlaces(String coordenadas) {
        String [] latLong = coordenadas.split(" ");
        Double latitude = Double.parseDouble(latLong[1]);
        Double longitude = Double.parseDouble(latLong[0]);

        ArrayList<Place> places = PlacesService.search("", latitude, longitude, 10);
        if(places.size() == 0) return false;

        for(Place place : places){
            place.setTypeOfPlace();
            if(place.ofInterest)
                System.out.println(place);
        }

        return true;
    }

    private static String getTypeOfPlace(String coordenadas) {
        String [] latLong = coordenadas.split(" ");
        Double latitude = Double.parseDouble(latLong[1]);
        Double longitude = Double.parseDouble(latLong[0]);

        HashSet<String> typesSet = new HashSet<>();

        ArrayList<Place> places = PlacesService.search("", latitude, longitude, 10);
        if(places.size() == 0) return null;

        for(Place place : places){
            System.out.printf("Name: %s, types: %s, latitude %f, longitude %f\n", place.name, place.types, place.latitude, place.longitude);
            for(String type: place.types)
                typesSet.add(type);
        }

        return getTypeOfPlaceString(typesSet);
    }

    private static String getTypeOfPlaceString(HashSet<String> typesSet) {
//        String[] museums = {"art_gallery", "museum"};
//        String[] monuments = {"church", "city_hall", "hindu_temple", "mosque", "place_of_worship", "synagogue"};
//        String[] night = {"nightclub"};
//        String[] hotels = {"lodging"};
//        String[] gastronomy = {"restaurant", "bar", "cafe", "food", "meal_delivery", "meal_takeaway"};
//        String[] transport = {"airport", "bus_station", "subway_station", "taxi_stand", "train_station"};
//        String[] ocio = {"amusement_park", "aquarium", "bowling_alley", "casino", "movie_theater", "park", "spa", "stadium", "zoo"};
//        String[] shopping = {"department_store", "shopping_mall", "store"};

        if(typesSet.contains("art_gallery") || typesSet.contains("museum"))
            return "museum";
        else if(typesSet.contains("church") || typesSet.contains("city_hall") || typesSet.contains("hindu_temple") || typesSet.contains("mosque") || typesSet.contains("place_of_worship") || typesSet.contains("synagogue"))
            return "monument";
        else if(typesSet.contains("night_club"))
            return "night";
        else if(typesSet.contains("lodging"))
            return "hotel";
        else if(typesSet.contains("restaurant") || typesSet.contains("bar") || typesSet.contains("cafe") || typesSet.contains("food") || typesSet.contains("meal_delivery") || typesSet.contains("meal_takeaway"))
            return "gastronomy";
        else if(typesSet.contains("airport") || typesSet.contains("bus_station") || typesSet.contains("subway_station") || typesSet.contains("taxi_stand") || typesSet.contains("train_station"))
            return "transport";
        else if(typesSet.contains("amusement_park") || typesSet.contains("aquarium") || typesSet.contains("bowling_alley") || typesSet.contains("casino")
                || typesSet.contains("movie_theater") || typesSet.contains("park") || typesSet.contains("spa") || typesSet.contains("stadium") || typesSet.contains("zoo"))
            return "ocio";
        else if(typesSet.contains("department_store") || typesSet.contains("shopping_mall") || typesSet.contains("store"))
            return "shopping";
        else
            return "undefined";
    }

}
