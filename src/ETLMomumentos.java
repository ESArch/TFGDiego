import pojos.Monument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Arch on 2/21/2016.
 */
public class ETLMomumentos {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("monumentos.csv"));
        for(String line : lines){
            Monument monument = new Monument();

            String[] splittedLine = line.split(";");
            monument.taPos = Integer.parseInt(splittedLine[0]);
            monument.longitude = Double.parseDouble(splittedLine[1]);
            monument.latitude = Double.parseDouble(splittedLine[2]);
            monument.id = splittedLine[5];
            monument.name = splittedLine[3];
            monument.typeOfPlace = splittedLine[9];

//            System.out.println(monument);
            DBLoader.connect();
            DBLoader.insertMonument(monument);

        }

    }
}
