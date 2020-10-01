package uk.ac.ed.inf.heatmap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class App
{
    public static void main( String[] args ) {
        System.out.println("start");
        String predictionsFilePath = args[0];
        int[][] predictions = Utilities.parsePredictions(predictionsFilePath);

        // boundaryLatLongs elements in format: minLat, minLong, maxLat, maxLong 
        Double[] boundaryLatLongs = {55.942617, -3.192473, 55.946233, -3.184319};
        Heatmap heatmap = new Heatmap(boundaryLatLongs, predictions);

        // load geojson from file
        String noFlyZonesJson = Utilities.readFile("src/main/resources/no-fly-zones.geojson");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject noFlyZones = gson.fromJson(noFlyZonesJson, JsonObject.class);

        // add heatmap geojson
        heatmap.appendHeatmapGeojson(noFlyZones);

        // output to file
        String heatmapJson = gson.toJson(noFlyZones);
        Utilities.writeFile("heatmap.geojson", heatmapJson);
        System.out.println("success");
    }
}
