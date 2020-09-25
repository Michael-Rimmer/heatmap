# heatmap

ILP Coursework 1

## Requirements

1. parse input file
2. create heatmap json
3. generate relevant geojson using existing geojson of Edinburgh campus
4. output heatmap.geojson to 'default output directory'

## Unknowns

3. Java
    * output file
    * junit testing
    * docstrings
    * rename default classes ie App.java?
    * make api request to render image?
    * geojson bbox?

## Next Steps
I played with geojson to make the outer perimiter, now:
* use geojson point instead of 2d https://docs.mapbox.com/android/api/mapbox-java/libjava-geojson/5.5.0/index.html
* create 10x10 grid in this before generating with java
* generate geojson for small squares with with java given conditions ie sensor readings
* first create polygon then use feature . fromGeometry()
* use google json package https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.5/com/google/gson/JsonElement.html but for understanding check if org would work