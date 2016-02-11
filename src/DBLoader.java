import java.sql.*;

/**
 * Created by Diego on 2/11/2016.
 */
public class DBLoader {
    static Connection connection = null;

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

    private static void insertTweet(Connection connection, Tweet tweet){
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

    private static void insertHashTags(Connection connection, Tweet tweet){
        Statement statement = null;
        String sql = "";

        for(String hashTag : tweet.hashTags){
            //Insertar hashTag
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
            }

        }

    }
}
