package uk.ac.ed.inf.heatmap;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.List;
import java.util.ArrayList;

/**
 * Each HeatmapUnit object represents a single grid unit in a Heatmap.
 */

public class HeatmapUnit {

    final String name;
    final String fill;
    final Double fillOpacity = 0.75;
    final int prediction;

    // store the coordinates for each corner of the polygon
    final Point topLeftCoord;
    final Point topRightCoord;
    final Point bottomRightCoord;
    final Point bottomLeftCoord;

    public HeatmapUnit (String name, int prediction, Point topLeftCoord, Point topRightCoord, Point bottomRightCoord, Point bottomLeftCoord) {
        this.name = name;
        this.prediction = prediction;
        this.fill = setFillColour(prediction);
        this.topLeftCoord = topLeftCoord;
        this.topRightCoord = topRightCoord;
        this.bottomRightCoord = bottomRightCoord;
        this.bottomLeftCoord = bottomLeftCoord;
    }

    public String getName() {
        return this.name;
    }

    public String getFill() {
        return this.fill;
    }

    public Double getFillOpacity() {
        return this.fillOpacity;
    }

    public int getPrediction() {
        return this.prediction;
    }

    // generate Geojson for single HeatmapUnit object
    public String generateGeojson() {
        var unitCoords = new ArrayList<Point>(5);
        unitCoords.add(this.topLeftCoord);
        unitCoords.add(this.topRightCoord);
        unitCoords.add(this.bottomRightCoord);
        unitCoords.add(this.bottomLeftCoord);
        unitCoords.add(this.topLeftCoord);

        // Geojson Polygon constructor requires nested lists
        var coordsList = new ArrayList<List<Point>>(1);
        coordsList.add(unitCoords);

        Polygon heatmapUnitPoly = Polygon.fromLngLats(coordsList);
        Feature heatmapUnitFeature = Feature.fromGeometry(heatmapUnitPoly);
        
        heatmapUnitFeature.addStringProperty("name", this.name);
        heatmapUnitFeature.addStringProperty("fill", this.fill);
        heatmapUnitFeature.addStringProperty("rgb-string", this.fill);
        heatmapUnitFeature.addNumberProperty("fill-opacity", this.fillOpacity);
        
        return heatmapUnitFeature.toJson();
    }

    private String setFillColour (int prediction) {
        if (prediction >= 0 && prediction <32) {
            return "#00ff00";
        } else if (prediction >= 32 && prediction < 64) {
            return "#40ff00";
        } else if (prediction >= 64 && prediction < 96) {
            return "#80ff00";
        } else if (prediction >= 96 && prediction < 128) {
            return "#c0ff00";
        } else if (prediction >= 128 && prediction < 160) {
            return "#ffc000";
        } else if (prediction >= 160 && prediction < 192) {
            return "#ff8000";
        } else if (prediction >= 192 && prediction < 224) {
            return "#ff4000";
        } else if (prediction >= 224 && prediction < 256) {
            return "#ff0000";
        } else {
            throw new IllegalArgumentException("Prediction value must be in range [0, 256))");
        }
    }
}
