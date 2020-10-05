package uk.ac.ed.inf.heatmap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class App
{
    // expects one command line argument - file path to predictions file
    public static void main( String[] args ) {

        // declare constant variables used throughout method
        final String heatmapGeojsonFile = "heatmap.geojson";
        final String mapGeojsonFile = "/no-fly-zones.geojson";

        // boundaryLongLats elements in format: minLong, minLat, maxLong, maxLat
        final Double[] boundaryLongLats = {-3.192473, 55.942617, -3.184319, 55.946233};

        // parse predictions file
        final String predictionsFilePath = args[0];
        final int[][] predictions = Utilities.parsePredictions(predictionsFilePath);
        System.out.println("Parsed predictions file.");

        // initialise Heatmap object
        Heatmap heatmap = new Heatmap(boundaryLongLats, predictions);

        // load existing map geojson from file
        String noFlyZonesJson = Utilities.readFileFromResources(mapGeojsonFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject noFlyZones = gson.fromJson(noFlyZonesJson, JsonObject.class);
        System.out.println("Loaded map Geojson.");

        // add heatmap geojson to existing map geojson
        heatmap.appendHeatmapGeojson(noFlyZones);
        System.out.println("Generated heatmap Geojson.");

        // output to file
        String heatmapJson = gson.toJson(noFlyZones);
        Utilities.writeFile(heatmapGeojsonFile, heatmapJson);
        System.out.println("Output heatmap Geojson to file: " + heatmapGeojsonFile);
    }
}
