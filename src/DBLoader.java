import pojos.*;
import catastro.accesoCatastro;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by Diego on 2/11/2016.
 */
public class DBLoader {
    static Connection connection = null;

    public static void main(String[] args) {

//        filterUsersByTweets(5);
//        setFechaMaxima();
//        setFechaMinima();
//        filterByDays(15);
//        cleanUnusedHashTags();

//          tagTypeOfPlace();


        //Buscar los sitios desde los que se ha twiteado
//        boolean terminado = false;
//        int keyNumber = 1;
//        while(!terminado && keyNumber < 10){
//            System.out.println("Cambiando a la siguiente key de Google Places...");
//            PlacesService.nextKey();
//            terminado = getPlacesFromTweets();
//            keyNumber++;
//        }


//        etiquetarPorProximidad(10);


//        desetiquetarTweets();
//        etiquetarPorMonumentos(50);
//        etiquetarPorParcelas(10);
//        etiquetarPorPOIs(15);
////        etiquetarPorPrioridad(50);
////        etiquetarPorProximidad(15);
//        etiquetarPorPrioridadYDistancia();
//        etiquetarTuristas();



//        getPolygonFromPoint();
//        buffPoints();


        extraerRecorridos();

//        printParcelas("parcelas");
//        printPerfilZonas("perfilZonas");
        printRecorridos("recorridos");
//        printOcupacionDistritos("ocupacionDistritos");

    }

    public static void connect() {
        if (connection == null)
            getConnection();
    }

    private static Connection getConnection() {
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

    private static void insertUser(Connection connection, Tweet tweet) {
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            //Insert usuario
            sql = "INSERT INTO usuario (usu_id, usu_idioma, usu_filtrado)"
                    + "VALUES ('" + tweet.user.id + "','" + tweet.user.lang + "', false);";
            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            System.err.println(sql);
        }
    }

    private static void insertTweet(Connection connection, Tweet tweet) {
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            if (tweet.media != null)
                //Insert tweet
                sql = "INSERT INTO tweet (twe_id, twe_texto, twe_usuario, twe_coordenadas, twe_fecha_creacion, twe_hora_creacion, twe_idioma, twe_localizado, twe_media)"
                        + "VALUES ('" + tweet.id + "','" + tweet.text + "','" + tweet.user.id + "',ST_GeomFromText('POINT(" + tweet.longitude + " " + tweet.latitude + ")', 4326), '"
                        + tweet.getDate() + "', '" + tweet.getTime() + "', '" + tweet.lang + "', false, '" + tweet.media + "');";
            else
                sql = "INSERT INTO tweet (twe_id, twe_texto, twe_usuario, twe_coordenadas, twe_fecha_creacion, twe_hora_creacion, twe_idioma, twe_localizado)"
                        + "VALUES ('" + tweet.id + "','" + tweet.text + "','" + tweet.user.id + "',ST_GeomFromText('POINT(" + tweet.longitude + " " + tweet.latitude + ")', 4326), '"
                        + tweet.getDate() + "', '" + tweet.getTime() + "', '" + tweet.lang + "', false);";
            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static void insertPlace(Place place) {
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            sql = "INSERT INTO lugar (lu_id, lu_nombre, lu_keywords, lu_tipo_lugar, lu_coordenadas)"
                    + "VALUES ('" + place.place_id + "','" + place.name + "','" + place.types.toString() + "','" + place.typeOfPlace + "',ST_GeomFromText('POINT(" + place.longitude + " " + place.latitude + ")', 4326)); ";

            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void insertMonument(Monument monument) {
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            sql = "INSERT INTO monumento (mon_codigo, mon_nombre, mon_tipo_lugar, mon_ta_posicion, mon_coordenadas)"
                    + "VALUES ('" + monument.id + "','" + monument.name.replaceAll("'", "''") + "','" + monument.typeOfPlace + "','" + monument.taPos + "',ST_Transform(ST_GeomFromText('POINT(" + monument.longitude + " " + monument.latitude + ")', 25830), 4326)); ";

            statement.executeUpdate(sql);
            statement.close();
//            connection.commit();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void insertPOI(POI poi) {
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            sql = "INSERT INTO punto_interes (poi_nombre, poi_tipo_lugar, poi_poligono)"
                    + " VALUES ('" + poi.name.replaceAll("'", "''") + "','" + poi.typeOfPlace + "', ST_MULTI('" + poi.geom + "')); ";

            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void insertDistrict(District district) {
        Statement statement = null;
        String sql = "";

        try {
            statement = connection.createStatement();

            sql = "INSERT INTO distrito (dis_codigo, dis_nombre, dis_poligono)"
                    + "VALUES ('" + district.id + "','" + district.name.replaceAll("'", "''") + "', ST_Transform(ST_GeomFromText('" + district.geom + "', 25830), 4326)); ";

            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static void insertHashTags(Connection connection, Tweet tweet) {
        Statement statement = null;
        String sql = "";
        boolean existingHT;

        for (String hashTag : tweet.hashTags) {
            //Insertar hashTag
            existingHT = false;
            try {
                statement = connection.createStatement();

                sql = "INSERT INTO hashtag (has_etiqueta)"
                        + "VALUES ('" + hashTag + "');";
                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

                ResultSet rs = statement.getGeneratedKeys();

                if (rs.next()) {
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

            if (existingHT) {
                try {
                    statement = connection.createStatement();

                    sql = "SELECT has_codigo FROM hashtag "
                            + "WHERE has_etiqueta = '" + hashTag + "';";
                    ResultSet rs = statement.executeQuery(sql);

                    if (rs.next()) {
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

    public static void setFechaMaxima() {
        Statement statement = null;
        String sql = "";
        ResultSet rs = null;

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();

            sql = "SELECT DISTINCT t1.twe_usuario, t1.twe_fecha_creacion " +
                    "FROM tweet t1 " +
                    "WHERE t1.twe_fecha_creacion >= ALL (SELECT t2.twe_fecha_creacion FROM tweet t2 WHERE t1.twe_usuario = t2.twe_usuario);";

            rs = statement.executeQuery(sql);

            statement = connection.createStatement();
            while (rs.next()) {
                String userID = rs.getString("twe_usuario");
                Date date = rs.getDate("twe_fecha_creacion");

                sql = "UPDATE usuario " +
                        "SET usu_fecha_max = '" + date + "' " +
                        "WHERE usu_id = '" + userID + "';";

                statement.executeUpdate(sql);
            }
            statement.close();


        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void setFechaMinima() {
        Statement statement = null;
        String sql = "";
        ResultSet rs = null;

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();

            sql = "SELECT DISTINCT t1.twe_usuario, t1.twe_fecha_creacion " +
                    "FROM tweet t1 " +
                    "WHERE t1.twe_fecha_creacion <= ALL (SELECT t2.twe_fecha_creacion FROM tweet t2 WHERE t1.twe_usuario = t2.twe_usuario);";

            rs = statement.executeQuery(sql);

            statement = connection.createStatement();
            while (rs.next()) {
                String userID = rs.getString("twe_usuario");
                Date date = rs.getDate("twe_fecha_creacion");

                sql = "UPDATE usuario " +
                        "SET usu_fecha_min = '" + date + "' " +
                        "WHERE usu_id = '" + userID + "';";

                statement.executeUpdate(sql);
            }
            statement.close();


        } catch (SQLException e) {
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
                    "WHERE usu_id IN (SELECT usu_id FROM usuario WHERE (usu_fecha_max - usu_fecha_min + 1) > " + minDays + ");";
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void cleanUnusedHashTags() {
        Statement statement = null;
        String sql = "DELETE FROM hashtag h WHERE h.has_codigo NOT IN (SELECT t.has_codigo FROM tweet_hashtag t)";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }


    public static boolean tagTypeOfPlace() {

        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT twe_id, ST_AsLatLonText(twe_coordenadas, 'D.DDDDDD') as coordenadas FROM tweet WHERE twe_tipo_lugar is null;";
        boolean terminado = true;


        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);


            while (rs.next()) {
                String tweetID = rs.getString("twe_id");
                String coordenadas = rs.getString("coordenadas");
//                System.out.println(coordenadas);

                String typeOfPlace = getTypeOfPlace(coordenadas);
                System.out.println(typeOfPlace);

                if (typeOfPlace == null) {
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

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }

        return terminado;
    }

    public static boolean getPlacesFromTweets() {

        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT twe_id, ST_AsLatLonText(twe_coordenadas, 'D.DDDDDD') as coordenadas " +
                "FROM tweet " +
                "WHERE twe_usuario IN (SELECT usu_id FROM usuario WHERE usu_filtrado = false) " +
                "AND twe_localizado = false;";

        boolean terminado = true;

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);


            while (rs.next()) {
                String tweetID = rs.getString("twe_id");
                String coordenadas = rs.getString("coordenadas");
                System.out.println(coordenadas);

                terminado = findPlacesFromGP(coordenadas);

                if (!terminado) {
                    System.out.println("Límite de Google Places superado");
                    break;
                }

                statement = connection.createStatement();

                sql = "UPDATE tweet " +
                        "SET twe_localizado = true " +
                        "WHERE twe_id = '" + tweetID + "';";
                statement.executeUpdate(sql);
                statement.close();
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }

        return terminado;
    }

    private static boolean findPlacesFromGP(String coordenadas) {
        String[] latLong = coordenadas.split(" ");
        Double latitude = Double.parseDouble(latLong[0]);
        Double longitude = Double.parseDouble(latLong[1]);

        ArrayList<Place> places = PlacesService.search("", latitude, longitude, 10);
        if (places.size() == 0) return false;

        for (Place place : places) {
            place.setTypeOfPlace();
            if (place.ofInterest) {
                System.out.println(place);
                insertPlace(place);
            }

        }

        return true;
    }

    public static void etiquetarTuristas(){
        etiquetarTuristasPorMonumentosVisitados(3);
        etiquetarTuristasAlojadosHotel();
        etiquetarTuristasPlayaVisitada();
    }

    private static void etiquetarTuristasPorMonumentosVisitados(int minimo){
        Statement statement = null;
        String sql = "UPDATE usuario\n" +
                "SET usu_turista = true\n" +
                "WHERE usu_id IN (SELECT usu_id\n" +
                "FROM usuario\n" +
                "WHERE NOT usu_filtrado \n" +
                "AND " + minimo + "<= (SELECT COUNT(DISTINCT twe_punto_interes) FROM tweet WHERE twe_usuario = usu_id AND twe_tipo_lugar = 'monument'));";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static void etiquetarTuristasAlojadosHotel(){
        Statement statement = null;
        String sql = "UPDATE usuario\n" +
                "SET usu_turista = true\n" +
                "WHERE usu_id IN (SELECT DISTINCT twe_usuario FROM tweet WHERE twe_tipo_lugar = 'hotel');";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static void etiquetarTuristasPlayaVisitada(){
        Statement statement = null;
        String sql = "UPDATE usuario\n" +
                "SET usu_turista = true\n" +
                "WHERE usu_id IN (SELECT DISTINCT twe_usuario FROM tweet WHERE twe_tipo_lugar = 'beach');";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }


    public static void etiquetarPorMonumentos(int distancia) {
        Statement statement = null;
        String sql = "SELECT twe_id FROM tweet, usuario WHERE twe_usuario = usu_id AND usu_filtrado = false AND twe_lugar IS NULL";
        ResultSet rs1, rs2 = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            rs1 = statement.executeQuery(sql);

            while (rs1.next()) {
                String tweetID = rs1.getString("twe_id");
                sql = "SELECT mon_codigo, mon_tipo_lugar " +
                        "FROM tweet, monumento " +
                        "WHERE ST_DWithin(mon_coordenadas::geography, twe_coordenadas::geography, " + distancia + ") " +
                        "AND twe_id = '" + tweetID + "'" +
                        "ORDER BY ST_DISTANCE(mon_coordenadas::geography, twe_coordenadas::geography) ASC " +
                        "LIMIT 1;";

                statement = connection.createStatement();
                rs2 = statement.executeQuery(sql);
                String sql2 = "";

                if (rs2.next()) {
                    String mon_codigo = rs2.getString("mon_codigo");
                    String mon_tipo_lugar = rs2.getString("mon_tipo_lugar");
                    sql2 = "UPDATE tweet SET twe_monumento = '" + mon_codigo + "', twe_tipo_lugar = '" + mon_tipo_lugar + "' WHERE twe_id = '" + tweetID + "';";
                    statement = connection.createStatement();
                    statement.executeUpdate(sql2);
                    statement.close();
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void etiquetarPorParcelas(int distancia) {
        Statement statement = null;
        String sql = "SELECT twe_id FROM tweet, usuario WHERE twe_usuario = usu_id AND usu_filtrado = false AND twe_lugar IS NULL";
        ResultSet rs1, rs2 = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            rs1 = statement.executeQuery(sql);

            while (rs1.next()) {
                String tweetID = rs1.getString("twe_id");
                sql = "SELECT mon_codigo, mon_tipo_lugar " +
                        "FROM tweet, monumento " +
                        "WHERE ST_DWithin(mon_parcela::geography, twe_coordenadas::geography, " + distancia + ") " +
                        "AND twe_id = '" + tweetID + "'" +
                        "ORDER BY ST_DISTANCE(mon_parcela::geography, twe_coordenadas::geography) ASC " +
                        "LIMIT 1;";

                statement = connection.createStatement();
                rs2 = statement.executeQuery(sql);
                String sql2 = "";

                if (rs2.next()) {
                    String mon_codigo = rs2.getString("mon_codigo");
                    String mon_tipo_lugar = rs2.getString("mon_tipo_lugar");
                    sql2 = "UPDATE tweet SET twe_monumento = '" + mon_codigo + "', twe_tipo_lugar = '" + mon_tipo_lugar + "' WHERE twe_id = '" + tweetID + "';";
                    statement = connection.createStatement();
                    statement.executeUpdate(sql2);
                    statement.close();
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void etiquetarPorPOIs(int distancia) {
        Statement statement = null;
        String sql = "SELECT twe_id FROM tweet, usuario WHERE twe_usuario = usu_id AND usu_filtrado = false AND twe_lugar IS NULL";
        ResultSet rs1, rs2 = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            rs1 = statement.executeQuery(sql);

            while (rs1.next()) {
                String tweetID = rs1.getString("twe_id");
                sql = "SELECT poi_id, poi_tipo_lugar " +
                        "FROM tweet, punto_interes " +
                        "WHERE ST_DWithin(poi_poligono::geography, twe_coordenadas::geography, " + distancia + ") " +
                        "AND twe_id = '" + tweetID + "'" +
                        "ORDER BY ST_DISTANCE(ST_Centroid(poi_poligono), twe_coordenadas) ASC " +
                        "LIMIT 1;";

                statement = connection.createStatement();
                rs2 = statement.executeQuery(sql);
                String sql2 = "";

                if (rs2.next()) {
                    int poi_id = rs2.getInt("poi_id");
                    String poi_tipo_lugar = rs2.getString("poi_tipo_lugar");
                    sql2 = "UPDATE tweet SET twe_punto_interes = " + poi_id + ", twe_tipo_lugar = '" + poi_tipo_lugar + "' WHERE twe_id = '" + tweetID + "';";
                    statement = connection.createStatement();
                    statement.executeUpdate(sql2);
                    statement.close();
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void etiquetarPorProximidad(int distancia) {
        Statement statement = null;
        String sql = "SELECT twe_id FROM tweet WHERE twe_localizado = true AND twe_tipo_lugar IS NULL AND twe_lugar IS NULL";
        ResultSet rs1, rs2 = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            rs1 = statement.executeQuery(sql);

            while (rs1.next()) {
                String tweetID = rs1.getString("twe_id");
                sql = "SELECT t.twe_id, l.lu_tipo_lugar,l.lu_id, ST_DISTANCE(t.twe_coordenadas::geography, l.lu_coordenadas::geography) " +
                        "FROM lugar l, tweet t " +
                        "WHERE t.twe_id = '" + tweetID + "' " +
                        "AND ST_DWITHIN(t.twe_coordenadas::geography, l.lu_coordenadas::geography, " + distancia + ") " +
                        "ORDER BY ST_DISTANCE(t.twe_coordenadas::geography, l.lu_coordenadas::geography) ASC " +
                        "LIMIT 1;";

                statement = connection.createStatement();
                rs2 = statement.executeQuery(sql);

                String sql2 = "";
                if (rs2.next()) {
                    String place_id = rs2.getString("lu_id");
                    String tipoLugar = rs2.getString("lu_tipo_lugar");
                    sql2 = "UPDATE tweet SET twe_lugar = '" + place_id + "', twe_tipo_lugar = '" + tipoLugar + "' WHERE twe_id = '" + tweetID + "';";
                } else {
                    sql2 = "UPDATE tweet SET twe_tipo_lugar = 'undefined' WHERE twe_id = '" + tweetID + "';";
                }
                statement.close();

                statement = connection.createStatement();
                statement.executeUpdate(sql2);
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void etiquetarPorPrioridad(int distancia) {
        Statement statement = null;
        String sql = "SELECT twe_id FROM tweet WHERE twe_localizado = true AND twe_tipo_lugar IS NULL AND twe_lugar IS NULL";
        ResultSet rs1, rs2 = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            rs1 = statement.executeQuery(sql);

            while (rs1.next()) {
                String tweetID = rs1.getString("twe_id");
                sql = "SELECT t.twe_id, l.lu_tipo_lugar,l.lu_id, ST_DISTANCE(t.twe_coordenadas::geography, l.lu_coordenadas::geography) " +
                        "FROM lugar l, tweet t " +
                        "WHERE t.twe_id = '" + tweetID + "' " +
                        "AND ST_DWITHIN(t.twe_coordenadas::geography, l.lu_coordenadas::geography, " + distancia + "); ";

                statement = connection.createStatement();
                rs2 = statement.executeQuery(sql);


                HashMap<String, Place> places = new HashMap<>();

                while (rs2.next()) {
                    Place place = new Place();
                    place.place_id = rs2.getString("lu_id");
                    place.typeOfPlace = rs2.getString("lu_tipo_lugar");
                    places.put(place.typeOfPlace, place);

                }


                String sql2 = "";
                Place chosenPlace = elegirPlace(places);
                if (chosenPlace == null) {
                    sql2 = "UPDATE tweet SET twe_tipo_lugar = 'undefined' WHERE twe_id = '" + tweetID + "';";
                } else {
                    sql2 = "UPDATE tweet SET twe_lugar = '" + chosenPlace.place_id + "', twe_tipo_lugar = '" + chosenPlace.typeOfPlace + "' WHERE twe_id = '" + tweetID + "';";
                }

                statement = connection.createStatement();
                statement.executeUpdate(sql2);
                statement.close();
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static Place elegirPlace(HashMap<String, Place> places) {

        if (places.size() == 0)
            return null;

        Set<String> typesSet = places.keySet();

        if (typesSet.contains("museum"))
            return places.get("museum");
        else if (typesSet.contains("monument"))
            return places.get("monument");
        else if (typesSet.contains("night"))
            return places.get("night");
        else if (typesSet.contains("hotel"))
            return places.get("hotel");
        else if (typesSet.contains("gastronomy"))
            return places.get("gastronomy");
        else if (typesSet.contains("transport"))
            return places.get("transport");
        else if (typesSet.contains("leisure"))
            return places.get("leisure");
        else if (typesSet.contains("shopping"))
            return places.get("shopping");
        else
            return places.get("undefined");
    }

    public static void desetiquetarTweets() {
        Statement statement = null;
        String sql = "UPDATE tweet SET twe_lugar = null, twe_monumento = null, twe_tipo_lugar = null;";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }

    }

    public static void desetiquetarUsuarios() {
        Statement statement = null;
        String sql = "UPDATE usuario SET usu_turista = false";

        if (connection == null)
            getConnection();

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }


    private static String getTypeOfPlace(String coordenadas) {
        String[] latLong = coordenadas.split(" ");
        Double latitude = Double.parseDouble(latLong[0]);
        Double longitude = Double.parseDouble(latLong[1]);

        HashSet<String> typesSet = new HashSet<>();

        ArrayList<Place> places = PlacesService.search("", latitude, longitude, 10);
        if (places.size() == 0) return null;

        for (Place place : places) {
            System.out.printf("Name: %s, types: %s, latitude %f, longitude %f\n", place.name, place.types, place.latitude, place.longitude);
            for (String type : place.types)
                typesSet.add(type);
        }

        return getTypeOfPlaceString(typesSet);
    }

    public static void etiquetarPorPrioridadYDistancia() {

        Statement statement = null;
        String sql = "SELECT twe_id FROM tweet WHERE twe_localizado = true AND twe_tipo_lugar IS NULL AND twe_lugar IS NULL";
        ResultSet rs = null;

        if (connection == null) {
            getConnection();
        }

        try {

            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            while (rs.next()) {
                String tweetID = rs.getString("twe_id");

                Place chosenPlace = searchPlace(tweetID);

                if (chosenPlace == null) {
                    sql = "UPDATE tweet SET twe_tipo_lugar = 'undefined' WHERE twe_id = '" + tweetID + "';";
                } else {
                    sql = "UPDATE tweet SET twe_lugar = '" + chosenPlace.place_id + "', twe_tipo_lugar = '" + chosenPlace.typeOfPlace + "' WHERE twe_id = '" + tweetID + "';";
                }

                statement = connection.createStatement();
                statement.executeUpdate(sql);
                statement.close();

            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static Place searchPlace(String tweetID) {

        Place place = null;

//        place = searchPlaceByType(tweetID, "museum", 25);
//        if (place != null) return place;
//        place = searchPlaceByType(tweetID, "monument", 50);
//        if (place != null) return place;
        place = searchPlaceByType(tweetID, "night", 25);
        if (place != null) return place;
        place = searchPlaceByType(tweetID, "hotel", 35);
        if (place != null) return place;
        place = searchPlaceByType(tweetID, "gastronomy", 25);
        if (place != null) return place;
        place = searchPlaceByType(tweetID, "leisure", 25);
//        if (place != null) return place;
//        place = searchPlaceByType(tweetID, "transport", 15);
        if (place != null) return place;
        place = searchPlaceByType(tweetID, "shopping", 15);
        if (place != null) return place;
        place = searchPlaceByType(tweetID, "undefined", 10);
        if (place != null) return place;

        return place;

    }

    private static Place searchPlaceByType(String tweetID, String typeOfPlace, int distance) {
        Statement statement = null;
        String sql = "";
        ResultSet rs = null;
        Place place = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            sql = "SELECT l.lu_id " +
                    "FROM lugar l, tweet t " +
                    "WHERE t.twe_id = '" + tweetID + "' " +
                    "AND l.lu_tipo_lugar = '" + typeOfPlace + "' " +
                    "AND ST_DWITHIN(t.twe_coordenadas::geography, l.lu_coordenadas::geography, " + distance + ") " +
                    "ORDER BY ST_DISTANCE(t.twe_coordenadas::geography, l.lu_coordenadas::geography) ASC " +
                    "LIMIT 1; ";
            rs = statement.executeQuery(sql);

            if (rs.next()) {
                place = new Place();
                place.place_id = rs.getString("lu_id");
                place.typeOfPlace = typeOfPlace;
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }

        return place;
    }

    public static void getPolygonFromPoint() {
        Statement statement = null;
        String sql = "SELECT mon_codigo, mon_nombre, ST_X(mon_coordenadas) as x, ST_Y(mon_coordenadas) as y FROM monumento;";
        ResultSet rs = null;

        if (connection == null) {
            getConnection();
        }

        try {

            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            while (rs.next()) {
                String monumentID = rs.getString("mon_codigo");
                String monumentName = rs.getString("mon_nombre");
                String longitude = rs.getString("x");
                String latitude = rs.getString("y");

                String RC = "";
                String[] parcel = null;

                try {
                    RC = accesoCatastro.readServiceRC(longitude, latitude);
                    parcel = accesoCatastro.readServiceParcel(RC);

                    String sql2 = "UPDATE monumento SET mon_parcela = ST_Transform(ST_GeomFromText('POLYGON((";

                    for (int i = 1; i < parcel.length; i += 2)
                        sql2 += parcel[i] + " " + parcel[i + 1] + ",";

                    sql2 = sql2.substring(0, sql2.length() - 1);
                    sql2 += "))', 25830), 4326) WHERE mon_codigo = '" + monumentID + "';";

                    System.out.println(parcel.length + " " + sql2);

                    statement = connection.createStatement();
                    statement.executeUpdate(sql2);
                    statement.close();

                } catch (Exception e) {
                    System.out.println(monumentID + " " + monumentName);
                    e.printStackTrace();
                }

            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void buffPoints() {
        Statement statement = null;
        String sql = "SELECT mon_codigo,ST_X(mon_coordenadas) as x, ST_Y(mon_coordenadas) as y FROM monumento WHERE mon_parcela IS NULL;";
        ResultSet rs = null;

        if (connection == null) {
            getConnection();
        }

        try {

            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            while (rs.next()) {
                String monumentID = rs.getString("mon_codigo");
                String longitude = rs.getString("x");
                String latitude = rs.getString("y");

                String sql2 = "UPDATE monumento SET mon_parcela = ST_TRANSFORM(ST_BUFFER(ST_Transform(ST_GeomFromText('Point(" + longitude + " " + latitude + ")', 4326), 26986), 50), 4326) WHERE mon_codigo = '" + monumentID + "';";

                statement = connection.createStatement();
                statement.executeUpdate(sql2);
                statement.close();

            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    public static void extraerRecorridos() {
        Statement statement = null;
        String sql = "SELECT twe_usuario, twe_fecha_creacion FROM tweet GROUP BY twe_usuario, twe_fecha_creacion HAVING COUNT(*) > 5";
        ResultSet rs1, rs2 = null;

        if (connection == null) {
            getConnection();
        }

        try {
            statement = connection.createStatement();
            rs1 = statement.executeQuery(sql);

            while (rs1.next()) {
                String user = rs1.getString("twe_usuario");
                Date date = rs1.getDate("twe_fecha_creacion");

                sql = "SELECT ST_X(twe_coordenadas) as long, ST_Y(twe_coordenadas) as lat " +
                        "FROM tweet " +
                        "WHERE twe_usuario = '" + user + "' " +
                        "AND twe_fecha_creacion = '" + date + "' " +
                        "ORDER BY twe_hora_creacion ;";

                statement = connection.createStatement();
                rs2 = statement.executeQuery(sql);


                String lineString = "";
                while (rs2.next()) {
                    double longitude = rs2.getDouble("long");
                    double latitude = rs2.getDouble("lat");
                    lineString += longitude + " " + latitude + ",";
                }

                lineString = lineString.substring(0, lineString.length() - 1);


                String sql2 = "INSERT INTO recorrido (rut_usuario, rut_fecha, rut_coordenadas) " +
                        "VALUES ('" + user + "', '" + date + "', ST_GeomFromText('LINESTRING(" + lineString + ")',4326));";

                statement = connection.createStatement();
                statement.executeUpdate(sql2);
                statement.close();
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        }
    }

    private static String getTypeOfPlaceString(HashSet<String> typesSet) {
//        String[] museums = {"art_gallery", "museum"};
//        String[] monuments = {"church", "city_hall", "hindu_temple", "mosque", "place_of_worship", "synagogue"};
//        String[] night = {"nightclub"};
//        String[] hotels = {"lodging"};
//        String[] gastronomy = {"restaurant", "bar", "cafe", "food", "meal_delivery", "meal_takeaway"};
//        String[] transport = {"airport", "bus_station", "subway_station", "taxi_stand", "train_station"};
//        String[] leisure = {"amusement_park", "aquarium", "bowling_alley", "casino", "movie_theater", "park", "spa", "stadium", "zoo"};
//        String[] shopping = {"department_store", "shopping_mall", "store"};

        if (typesSet.contains("art_gallery") || typesSet.contains("museum"))
            return "museum";
        else if (typesSet.contains("church") || typesSet.contains("city_hall") || typesSet.contains("hindu_temple") || typesSet.contains("mosque") || typesSet.contains("place_of_worship") || typesSet.contains("synagogue"))
            return "monument";
        else if (typesSet.contains("night_club"))
            return "night";
        else if (typesSet.contains("lodging"))
            return "hotel";
        else if (typesSet.contains("restaurant") || typesSet.contains("bar") || typesSet.contains("cafe") || typesSet.contains("food") || typesSet.contains("meal_delivery") || typesSet.contains("meal_takeaway"))
            return "gastronomy";
        else if (typesSet.contains("airport") || typesSet.contains("bus_station") || typesSet.contains("subway_station") || typesSet.contains("taxi_stand") || typesSet.contains("train_station"))
            return "transport";
        else if (typesSet.contains("amusement_park") || typesSet.contains("aquarium") || typesSet.contains("bowling_alley") || typesSet.contains("casino")
                || typesSet.contains("movie_theater") || typesSet.contains("park") || typesSet.contains("spa") || typesSet.contains("stadium") || typesSet.contains("zoo"))
            return "leisure";
        else if (typesSet.contains("department_store") || typesSet.contains("shopping_mall") || typesSet.contains("store"))
            return "shopping";
        else
            return "undefined";
    }

    public static void printParcelas(String fileName) {
        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT mon_nombre, mon_tipo_lugar, ST_AsText(mon_parcela) as geo FROM monumento;";

        if (connection == null)
            connect();

        try {
            PrintWriter writer = new PrintWriter(fileName + ".csv", "UTF-8");
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            writer.println("monumento;wkt");

            while (rs.next()) {
                String nombre = rs.getString("mon_nombre");
                String tipo = rs.getString("mon_tipo_lugar");
                String json = rs.getString("geo");
                writer.println(nombre + ";" + tipo + ";" + json);
            }

            statement.close();
            writer.flush();
            writer.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static void printPerfilZonas(String fileName) {
        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT dis_nombre,\n" +
                "ST_AsText(dis_poligono) as geom,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar = 'monument') as monuments,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar = 'museum') as museums,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar = 'gastronomy') as gastronomy,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar = 'leisure') as leisure,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar = 'shopping') as shopping,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar = 'night') as night,\n" +
                "(SELECT twe_tipo_lugar FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar != 'undefined' GROUP BY twe_tipo_lugar ORDER BY COUNT(DISTINCT twe_usuario) DESC LIMIT 1) as perfil\n" +
                "FROM distrito;";

        if (connection == null)
            connect();

        try {
            PrintWriter writer = new PrintWriter(fileName + ".csv", "UTF-8");
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            writer.println("distrito;wkt;monuments;museums;gastronomy;leisure;shopping;night;perfil");
            while (rs.next()) {
                String nombre = rs.getString(1);
                String wkt = rs.getString(2);
                int monuments = rs.getInt(3);
                int museums = rs.getInt(4);
                int gastronomy = rs.getInt(5);
                int leisure = rs.getInt(6);
                int shopping = rs.getInt(7);
                int night = rs.getInt(8);
                String perfil = rs.getString(9);

                writer.println(nombre + ";" + wkt + ";" + monuments + ";" + museums + ";" + gastronomy + ";" + leisure + ";" + shopping + ";" + night + ";" + perfil);
            }

            statement.close();
            writer.flush();
            writer.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void printRecorridos(String fileName) {
        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT rut_usuario, rut_fecha, ST_ASTEXT(rut_coordenadas), true\n" +
                "FROM recorrido, usuario\n" +
                "WHERE rut_usuario = usu_id\n" +
                "AND usu_filtrado = false\n" +
                "UNION\n" +
                "SELECT rut_usuario, rut_fecha, ST_ASTEXT(rut_coordenadas), false\n" +
                "FROM recorrido, usuario\n" +
                "WHERE rut_usuario = usu_id\n" +
                "AND usu_filtrado = true;";

        if (connection == null)
            connect();

        try {
            PrintWriter writer = new PrintWriter(fileName + ".csv", "UTF-8");
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            writer.println("usuario;fecha;geom;turista");
            while (rs.next()) {
                String nombre = rs.getString(1);
                Date date = rs.getDate(2);
                String wkt = rs.getString(3);
                boolean turista = rs.getBoolean(4);
                writer.println(nombre + ";" + date + ";" + wkt + ";" + turista);
            }

            statement.close();
            writer.flush();
            writer.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void printOcupacionDistritos(String fileName) {
        Statement statement = null;
        ResultSet rs = null;
        String sql = "SELECT dis_nombre,\n" +
                "ST_AsText(dis_poligono) as geom,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar IS NOT NULL AND EXTRACT(HOUR FROM twe_hora_creacion) >= 0 AND EXTRACT(HOUR FROM twe_hora_creacion) < 6) as night,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar IS NOT NULL AND EXTRACT(HOUR FROM twe_hora_creacion) >= 6 AND EXTRACT(HOUR FROM twe_hora_creacion) < 12) as morning,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar IS NOT NULL AND EXTRACT(HOUR FROM twe_hora_creacion) >= 12 AND EXTRACT(HOUR FROM twe_hora_creacion) < 18) as afternoon,\n" +
                "(SELECT COUNT(DISTINCT twe_usuario) FROM tweet WHERE ST_CONTAINS(dis_poligono, twe_coordenadas) AND twe_tipo_lugar IS NOT NULL AND EXTRACT(HOUR FROM twe_hora_creacion) >= 18) as evening\n" +
                "FROM distrito;";

        if (connection == null)
            connect();

        try {
            PrintWriter writer = new PrintWriter(fileName + ".csv", "UTF-8");
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            writer.println("distrito;geom;00:00-05:59;06:00-11:59;12:00-17:59;18:00-23:59");
            while (rs.next()) {
                String nombre = rs.getString(1);
                String wkt = rs.getString(2);
                int night = rs.getInt(3);
                int morning = rs.getInt(4);
                int afternoon = rs.getInt(5);
                int evening = rs.getInt(6);
                writer.println(nombre + ";" + wkt + ";" + night + ";" + morning + ";" + afternoon + ";" + evening);
            }

            statement.close();
            writer.flush();
            writer.close();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(sql);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
