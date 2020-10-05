package uk.ac.ed.inf.heatmap;

import com.mapbox.geojson.*;
import java.util.ArrayList;
import com.google.gson.*;

/**
 * The main class for representing a heatmap. Stores the map grid in a 2D array of HeatmapUnits.
 */

public class Heatmap {

    // boundaryLongLats elements in format: minLong, minLat, maxLong, maxLat
    final Double[] boundaryLongLats;

    // the coordinates for each corner of the heatmap
    final Point topLeftCoord;
    final Point bottomLeftCoord;
    final Point topRightCoord;
    final Point bottomRightCoord;

    final int [][] predictions;
    final HeatmapUnit[][] heatmapGrid;

    public Heatmap (Double[] boundaryLongLats, int[][] predictions) {
        this.boundaryLongLats = boundaryLongLats;
        this.bottomLeftCoord = Point.fromLngLat(boundaryLongLats[0], boundaryLongLats[1]);
        this.topRightCoord = Point.fromLngLat(boundaryLongLats[2], boundaryLongLats[3]);
        this.topLeftCoord = Point.fromLngLat(boundaryLongLats[0], boundaryLongLats[3]);
        this.bottomRightCoord = Point.fromLngLat(boundaryLongLats[2], boundaryLongLats[1]);
        this.predictions = predictions;
        this.heatmapGrid = createHeatmapGrid();
    }

    public Point getTopLeft() {
        return this.topLeftCoord;
    }

    public Point getBottomLeft() {
        return this.bottomLeftCoord;
    }

    public Point getTopRight() {
        return this.topRightCoord;
    }

    public Point getBottomRight() {
        return this.bottomRightCoord;
    }

    public int[][] getPredictions() {
        return this.predictions;
    }

    public HeatmapUnit[][] getHeatmapGrid() {
        return this.heatmapGrid;
    }

    private HeatmapUnit[][] createHeatmapGrid() {
        final int rows = this.predictions.length;
        final int columns = this.predictions[0].length;

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
                String name = String.format("grid_%d_%d", i,j);
                grid[i][j] = new HeatmapUnit(name, this.predictions[i][j] ,topLeftCoord, topRightCoord, bottomRightCoord, bottomLeftCoord);
            }
        }

        return grid;
    }

    // takes as input a JsonObject representing an existing Geojson featureCollection 
    // and appends the Geojson for the heatmap
    public void appendHeatmapGeojson(JsonObject featureCollectionJson) {
        final int rows = this.predictions.length;
        final int columns = this.predictions[0].length;
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

    // generate Geojson for the outer line that surrounds the heatmap
    private String generateBoundaryLineStringJson() {
        var boundaryCoords = new ArrayList<Point>(5);
        boundaryCoords.add(this.topLeftCoord);
        boundaryCoords.add(this.topRightCoord);
        boundaryCoords.add(this.bottomRightCoord);
        boundaryCoords.add(this.bottomLeftCoord);
        boundaryCoords.add(this.topLeftCoord);

        LineString boundaryLineString = LineString.fromLngLats(boundaryCoords);
        Feature heatmapFeature = Feature.fromGeometry(boundaryLineString);
        heatmapFeature.addStringProperty("name", "heatmap_boundary");

        String geojson = heatmapFeature.toJson();
        return geojson;
    }
}
