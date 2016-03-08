import pojos.District;
import pojos.Monument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Arch on 3/7/2016.
 */
public class ETLDistritos {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("Distritos.csv"));
        for(String line : lines){
            District district = new District();

            String[] splittedLine = line.split(";");
            district.geom = splittedLine[0].substring(1, splittedLine[0].length()-1);
            district.name = splittedLine[1];
            district.id = splittedLine[2];

//            System.out.println(district);
            DBLoader.connect();
            DBLoader.insertDistrict(district);

        }

    }
}
