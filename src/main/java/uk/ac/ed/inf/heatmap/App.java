package uk.ac.ed.inf.heatmap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class App
{
    public static void main( String[] args ) {

        int[][] predictions = Utilities.parsePredictions(args[0]);

        // boundaryLatLongs elements in format: minLat, minLong, maxLat, maxLong 
        Double[] boundaryLatLongs = {55.942617, -3.192473, 55.946233, -3.184319};
        Heatmap heatmap = new Heatmap(boundaryLatLongs, predictions);

        // load geojson from file
        String noFlyZonesJson = Utilities.readFile("src/main/resources/no-fly-zones.geojson");
        Gson gson = new Gson();
        JsonObject noFlyZones = gson.fromJson(noFlyZonesJson, JsonObject.class);

        // add heatmap geojson
        heatmap.appendHeatmapGeojson(noFlyZones);

        // output to file
        String finalJson = noFlyZones.toString();
        System.out.println(finalJson);
    }
}
