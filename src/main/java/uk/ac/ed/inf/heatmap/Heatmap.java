package uk.ac.ed.inf.heatmap;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

/**
 * The main class for representing a heatmap. Stores the map grid in a 2D array of HeatmapUnits.
 */

public class Heatmap {

    // boundaryLatLongs elements in format: minLong, minLat, maxLong, maxLat
    Double[] boundaryLongLats;

    // the coordinates for each corner of the heatmap
    Point topLeft;
    Point bottomLeft;
    Point topRight;
    Point bottomRight;

    int [][] predictions;
    HeatmapUnit[][] heatmapGrid;

    public Heatmap (Double[] boundaryLongLats, int[][] predictions) {

        this.boundaryLongLats = boundaryLongLats;
        this.bottomLeft = Point.fromLngLat(boundaryLongLats[0], boundaryLongLats[1]);
        this.topRight = Point.fromLngLat(boundaryLongLats[2], boundaryLongLats[3]);
        this.topLeft = Point.fromLngLat(boundaryLongLats[0], boundaryLongLats[3]);
        this.bottomRight = Point.fromLngLat(boundaryLongLats[2], boundaryLongLats[1]);
        this.predictions = predictions;
        this.heatmapGrid = createHeatmapGrid();
    }

    public HeatmapUnit[][] createHeatmapGrid() {

        int rows = this.predictions.length;
        int columns = this.predictions[0].length;

        HeatmapUnit[][] grid = new HeatmapUnit[rows][columns];

        Double minLong = this.boundaryLongLats[0];
        Double minLat = this.boundaryLongLats[1];
        Double maxLong = this.boundaryLongLats[2];
        Double maxLat = this.boundaryLongLats[3];

        // must use float division to avoid rounding errors
        Double longIncrement = Math.abs(maxLong-minLong) / 10.0;
        Double latIncrement = Math.abs(maxLat-minLat) / 10.0;

        // calculate the coordinates of each grid unit 
        for (int i = 0 ; i < rows ; i++) {
            for (int j = 0 ; j < columns ; j++) {
                // initialise point objects and calculate coordinates
                Point topLeftCoord = Point.fromLngLat(maxLong - i*longIncrement, minLat + j*latIncrement);
                Point topRightCoord = Point.fromLngLat(topLeftCoord.longitude(), topLeftCoord.latitude() + latIncrement);
                Point bottomRightCoord = Point.fromLngLat(topRightCoord.longitude() - longIncrement, topRightCoord.latitude());
                Point bottomLeftCoord = Point.fromLngLat(bottomRightCoord.longitude(), bottomRightCoord.latitude() - latIncrement);

                // instantiate a new HeatmapUnit object
                String name = String.format("grid_%d,%d", i,j);
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

    // generate geojson for the outer line that surrounds the heatmap
    private String generateBoundaryLineStringJson() {

        List<Point> boundaryCoords = new ArrayList<>(5);
        boundaryCoords.add(this.topLeft);
        boundaryCoords.add(this.topRight);
        boundaryCoords.add(this.bottomRight);
        boundaryCoords.add(this.bottomLeft);
        boundaryCoords.add(this.topLeft);

        LineString boundaryLineString = LineString.fromLngLats(boundaryCoords);
        Feature heatmapFeature = Feature.fromGeometry(boundaryLineString);
        heatmapFeature.addStringProperty("name", "heatmap_boundary");

        String geojson = heatmapFeature.toJson();
        return geojson;
    }
}
