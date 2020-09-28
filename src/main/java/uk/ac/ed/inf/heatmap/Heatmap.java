package uk.ac.ed.inf.heatmap;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;


public class Heatmap {

    // initialise attributes
    Double[] boundaryLatLongs;
    int [][] predictions;
    HeatmapUnit[][] heatmapGrid;
    
    public Heatmap (Double[] boundaryLatLongs, int[][] predictions) {
        // boundaryLatLongs elements in format: minLat, minLong, maxLat, maxLong 
        this.boundaryLatLongs = boundaryLatLongs;
        this.predictions = predictions;
        this.heatmapGrid = createHeatmapGrid();
    }

    public HeatmapUnit[][] createHeatmapGrid() {

        int rows = this.predictions.length;
        int columns = this.predictions[0].length;
        HeatmapUnit[][] grid = new HeatmapUnit[rows][columns];

        Double minLat = this.boundaryLatLongs[0];
        Double minLong = this.boundaryLatLongs[1];
        Double maxLat = this.boundaryLatLongs[2];
        Double maxLong = this.boundaryLatLongs[3];

        // Must use float division to avoid rounding errors
        Double longIncrement = Math.abs(maxLong-minLong) / 10.0;
        Double latIncrement = Math.abs(maxLat-minLat) / 10.0;

        for (int i = 0 ; i < rows ; i++) {
            for (int j = 0 ; j < columns ; j++) {
                // Initialise point objects and calculate coordinates
                Point topLeftCoord = Point.fromLngLat(maxLong - i*longIncrement, minLat + j*latIncrement);
                Point topRightCoord = Point.fromLngLat(topLeftCoord.longitude(), topLeftCoord.latitude() + latIncrement);
                Point bottomRightCoord = Point.fromLngLat(topRightCoord.longitude() - longIncrement, topRightCoord.latitude());
                Point bottomLeftCoord = Point.fromLngLat(bottomRightCoord.longitude(), bottomRightCoord.latitude() - latIncrement);

                // Instantiate a new HeatmapUnit object
                String name = String.format("grid_%n_%n", i, j);
                grid[i][j] = new HeatmapUnit(name, this.predictions[i][j] ,topLeftCoord, topRightCoord, bottomRightCoord, bottomLeftCoord);
            }
        }

        return grid;
    }

    public void appendHeatmapGeojson(JsonObject featureCollectionJson) {

        int rows = this.predictions.length;
        int columns = this.predictions[0].length;
        Gson gson = new Gson();

        JsonArray featuresJson = featureCollectionJson.getAsJsonArray("features");
        String boundaryJson = generateBoundaryLineStringJson();
        featuresJson.add(gson.fromJson(boundaryJson, JsonObject.class));

        for (int i = 0 ; i < rows ; i++) {
            for (int j = 0 ; j < columns ; j++) {
                String heatmapUnitJson = this.heatmapGrid[i][j].generateGeojson();
                featuresJson.add(gson.fromJson(heatmapUnitJson, JsonObject.class));
            }
        }
    }

    private String generateBoundaryLineStringJson() {

        // create json for linestring
        // use boundaryCoords to calculate thiss
        Point topLeft = Point.fromLngLat(-3.192473, 55.946233);
        Point topRight = Point.fromLngLat(-3.184319, 55.946233);
        Point bottomRight = Point.fromLngLat(-3.184319, 55.942617);
        Point bottomLeft = Point.fromLngLat(-3.192473, 55.942617);

        List<Point> boundaryCoords = new ArrayList<>(5);
        boundaryCoords.add(topLeft);
        boundaryCoords.add(topRight);
        boundaryCoords.add(bottomRight);
        boundaryCoords.add(bottomLeft);
        boundaryCoords.add(topLeft);

        LineString boundaryLineString = LineString.fromLngLats(boundaryCoords);
        Feature heatmapFeature = Feature.fromGeometry(boundaryLineString);
        heatmapFeature.addStringProperty("name", "heatmap_boundary");

        String geojson = heatmapFeature.toJson();
        return geojson;
    }
}
