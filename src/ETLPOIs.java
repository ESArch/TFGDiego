import pojos.District;
import pojos.POI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Arch on 3/15/2016.
 */
public class ETLPOIs {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("pois.csv"));
        for(String line : lines){
            System.out.println(line);
            POI poi = new POI();

            String[] splittedLine = line.split(",");
            poi.geom = splittedLine[1];
            poi.name = splittedLine[2];
            poi.typeOfPlace = splittedLine[3];

//            System.out.println(district);
            DBLoader.connect();
            DBLoader.insertPOI(poi);

        }

    }

}
