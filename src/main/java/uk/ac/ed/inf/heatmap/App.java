package uk.ac.ed.inf.heatmap;
import java.io.BufferedReader;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.awt.geom.Point2D;
import com.mapbox.geojson.Point;
import org.json.JSONObject;
import java.util.Arrays;

public class App
{
    public static void main( String[] args ) {
        int i = 10;
        int j = 12;
        String[][] test = new String[i][j];
        test[0][0] = "hello";
        test[0][1] = "you";
//        System.out.println( "Hello World!" );
//        int[][] predictions = parsePredictions("src/main/resources/predictions.txt");
//        System.out.println("The predictions are: " + Arrays.deepToString(predictions));
//        JSONObject json = readJSONFromFile("src/main/resources/no-fly-zones.geojson");
//        System.out.println(json);
//        GridCoord[][] gridCoords = calcHeatmapGridCoords(-3.192473, 55.942617, -3.184319, 55.946233);
//        System.out.println("The grid coords are: " + Arrays.deepToString(gridCoords));
//        System.out.println(gridCoords[0][0].getTopLeft().getX());
    }

    public static JSONObject readJSONFromFile(String filePath) {
        Path jsonFile = Path.of(filePath);
        JSONObject json = new JSONObject();

        try {
            String jsonContent = Files.readString(jsonFile);
            json = new JSONObject(jsonContent);
        } catch (Exception e) {
            System.out.println("Error occured during reading of json file:\n    " + e);
            System.exit(1);
        }

        return json;

    }

    public static int[][] parsePredictions(String filePath) {
        int[][] predictions = new int[10][10];
        Path predictionsFile = Path.of(filePath);
        try {
            BufferedReader predictionsFileReader = Files.newBufferedReader(predictionsFile);
            String readLine = predictionsFileReader.readLine();
            int i = 0;
            while(readLine != null && i < predictions.length) {
                String[] rowValues = readLine.split(",");
                for (int j = 0 ; j < 10; j++) {
                    predictions[i][j] = Integer.parseInt(rowValues[j].replaceAll("\\s+",""));
                }
                i++;
                readLine = predictionsFileReader.readLine();
            }
        } catch (Exception e) {
            System.out.println("Error occurred during parsing of predictions file:\n    " + e);
            System.exit(1);
        }

        return predictions;
    }

    public static GridCoord[][] calcHeatmapGridCoords(Double minX, Double minY, Double maxX, Double maxY) {
        GridCoord[][] gridCoords = new GridCoord[10][10];
        // Must use float division to avoid rounding errors
        Double xIncrement = Math.abs(maxX-minX) / 10.0;
        Double yIncrement = Math.abs(maxY-minY) / 10.0;

        for (int i = 0 ; i < 10 ; i++) {
            for (int j = 0 ; j < 10 ; j++) {
                // Initialise coordinate objects
                // Using fully fully qualified class name due to import collision
                Point2D topLeftCoord = new Point2D.Double();
                Point2D topRightCoord = new Point2D.Double();
                Point2D bottomLeftCoord = new Point2D.Double();
                Point2D bottomRightCoord = new Point2D.Double();

                // Calculate coordinates
                topLeftCoord.setLocation(minX + j*xIncrement, maxY - i*yIncrement);
                topRightCoord.setLocation(topLeftCoord.getX() + xIncrement, topLeftCoord.getY());
                bottomRightCoord.setLocation(topRightCoord.getX(), bottomRightCoord.getY() - yIncrement);
                bottomLeftCoord.setLocation(bottomRightCoord.getX() - xIncrement, bottomRightCoord.getY());

                // Instantiate a new GridCoord object
                gridCoords[i][j] = new GridCoord(topLeftCoord, topRightCoord, bottomRightCoord, bottomLeftCoord);

            }
        }

        return gridCoords;
    }
}
