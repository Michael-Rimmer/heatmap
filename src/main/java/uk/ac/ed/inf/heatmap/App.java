package uk.ac.ed.inf.heatmap;
import java.io.BufferedReader;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.awt.geom.Point2D;
import com.mapbox.geojson.*;
import com.google.gson.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class App
{
    public static void main( String[] args ) {
//        int[][] predictions = parsePredictions("src/main/resources/predictions.txt");
//        System.out.println("The predictions are: " + Arrays.deepToString(predictions));
       
//        GridCoord[][] gridCoords = calcHeatmapGridCoords(-3.192473, 55.942617, -3.184319, 55.946233);
//        System.out.println("The grid coords are: " + Arrays.deepToString(gridCoords));
        
        
        // playing around with mapbox geojson
        // create json for linestring
        Point topLeft = Point.fromLngLat(-3.192473, 55.946233);
        Point topRight = Point.fromLngLat(-3.184319, 55.946233);
        Point bottomRight = Point.fromLngLat(-3.184319, 55.942617);
        Point bottomLeft = Point.fromLngLat(-3.192473, 55.942617);
        List<Point> heatmapCoords = new ArrayList<>(5);
        heatmapCoords.add(topLeft);
        heatmapCoords.add(topRight);
        heatmapCoords.add(bottomRight);
        heatmapCoords.add(bottomLeft);
        heatmapCoords.add(topLeft);
        
        // constructor requires nested lists
        LineString heatmapLineString = LineString.fromLngLats(heatmapCoords);
        
        Feature heatmapFeature = Feature.fromGeometry(heatmapLineString);
        
        heatmapFeature.addStringProperty("name", "heatmap");
        
        // create json for a single heatmap unit
        topLeft = Point.fromLngLat(-3.1916576, 55.946233);
        topRight = Point.fromLngLat(-3.1908422, 55.946233);
        bottomRight = Point.fromLngLat(-3.1908422, 55.9458714);
        bottomLeft = Point.fromLngLat(-3.1916576, 55.9458714);
        List<Point> unitCoords = new ArrayList<>(5);
        unitCoords.add(topLeft);
        unitCoords.add(topRight);
        unitCoords.add(bottomRight);
        unitCoords.add(bottomLeft);
        unitCoords.add(topLeft);

        List<List<Point>> coordsList = new ArrayList<>(1);
        coordsList.add(unitCoords);

        // constructor requires nested lists
        Polygon heatmapUnitPoly = Polygon.fromLngLats(coordsList);
        
        Feature heatmapUnitFeature = Feature.fromGeometry(heatmapUnitPoly);
        
        heatmapUnitFeature.addStringProperty("name", String.format("grid_%d_%d", 0, 0));
        heatmapUnitFeature.addStringProperty("fill", "#ff0000");
        heatmapUnitFeature.addStringProperty("rgb-string", "#ff0000");
        heatmapUnitFeature.addNumberProperty("fill-opacity", 0.75);
        
        // load json from file
        String noFlyZonesJson = readFile("src/main/resources/no-fly-zones.geojson");
        Gson gson = new Gson();
        JsonObject noFlyZones = gson.fromJson(noFlyZonesJson, JsonObject.class);
        JsonArray noFlyZonesFeatures = noFlyZones.getAsJsonArray("features");
        
        // append heatmap unit to existing json
        noFlyZonesFeatures.add(gson.fromJson(heatmapFeature.toJson(), JsonObject.class));
        noFlyZonesFeatures.add(gson.fromJson(heatmapUnitFeature.toJson(), JsonObject.class));
        
        // output to file
        String finalJson = noFlyZones.toString();
        System.out.println(finalJson);
        
        
    }

    public static String readFile(String filePath) {
        Path file = Path.of(filePath);
        String fileContent = new String();

        try {
            fileContent = Files.readString(file);
        } catch (Exception e) {
            System.out.println("Error occured during reading of file: " + filePath + "\n" + e);
            System.exit(1);
        }

        return fileContent;
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
        
        System.out.println("the xincrement is:" + xIncrement.toString());
        System.out.println("the yincrement is:" + yIncrement.toString());

        for (int i = 0 ; i < 10 ; i++) {
            for (int j = 0 ; j < 10 ; j++) {
                // Initialise coordinate objects
                // Using fully qualified class name due to import collision
                Point2D topLeftCoord = new Point2D.Double();
                Point2D topRightCoord = new Point2D.Double();
                Point2D bottomLeftCoord = new Point2D.Double();
                Point2D bottomRightCoord = new Point2D.Double();

                // Calculate coordinates
                topLeftCoord.setLocation(minX + j*xIncrement, maxY - i*yIncrement);
                topRightCoord.setLocation(topLeftCoord.getX() + xIncrement, topLeftCoord.getY());
                bottomRightCoord.setLocation(topRightCoord.getX(), topRightCoord.getY() - yIncrement);
                bottomLeftCoord.setLocation(bottomRightCoord.getX() - xIncrement, bottomRightCoord.getY());

                // Instantiate a new GridCoord object
                gridCoords[i][j] = new GridCoord(topLeftCoord, topRightCoord, bottomRightCoord, bottomLeftCoord);
            }
        }

        return gridCoords;
    }
}
