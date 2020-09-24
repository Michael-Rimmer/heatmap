package uk.ac.ed.inf.heatmap;
import java.io.BufferedReader;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import com.mapbox.geojson.Point;
import org.json.JSONObject;
import java.util.Arrays;

public class App
{
    public static void main( String[] args )
    {
        JSONObject testJson = new JSONObject();
        testJson.putOpt("type", "FeatureCollection");
        System.out.println(testJson);
        System.out.println( "Hello World!" );
        System.out.println( "First parameter is " + args[0] );
        String[] predictions = parsePredictions("src/main/resources/predicsstions.txt");
        System.out.println("The predictions are: " + Arrays.toString(predictions));
    }

    public static String[] parsePredictions(String filePath) {
        String[] predictions = new String[100];
        Path predictionsFile = Path.of(filePath);
        try {
            BufferedReader predictionsFileReader = Files.newBufferedReader(predictionsFile);
            String readLine = predictionsFileReader.readLine();
            int i = 0;
            while(readLine != null) {
                String[] rowValues = readLine.split(",");
                for (int j = 0 ; j < 10; j++) {
                    predictions[i] = rowValues[j];
                    i++;
                }
                readLine = predictionsFileReader.readLine();
            }
        } catch (Exception e) {
            System.out.println("Error occured during parsing of predictions file:\n    " + e);
            System.exit(1);
        }

        return predictions;
    }
}
