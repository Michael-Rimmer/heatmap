package uk.ac.ed.inf.heatmap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class App
{
    public static void main( String[] args ) {

        // parse command line argument
        String predictionsFilePath = args[0];

        // parse the predictions file
        int[][] predictions = Utilities.parsePredictions(predictionsFilePath);

        // boundaryLatLongs elements in format: minLong, minLat, maxLong, maxLat
        Double[] boundaryLongLats = {-3.192473, 55.942617, -3.184319, 55.946233};

        // generate Heatmap object
        Heatmap heatmap = new Heatmap(boundaryLongLats, predictions);

        // load existing map geojson from file
        String noFlyZonesJson = Utilities.readFileFromResources("/no-fly-zones.geojson");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject noFlyZones = gson.fromJson(noFlyZonesJson, JsonObject.class);

        // add heatmap geojson to existing map geojson
        heatmap.appendHeatmapGeojson(noFlyZones);

        // output to file
        String heatmapJson = gson.toJson(noFlyZones);
        Utilities.writeFile("heatmap.geojson", heatmapJson);
    }
}
