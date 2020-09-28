package uk.ac.ed.inf.heatmap;

import java.awt.geom.Point2D;
import java.util.Arrays;

public class GridCoord {

    private Point2D topLeft;
    private Point2D topRight;
    private Point2D bottomRight;
    private Point2D bottomLeft;

    public GridCoord(Point2D topLeft, Point2D topRight, Point2D bottomRight, Point2D bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
    }

    public Point2D getTopLeft() {
        return this.topLeft;
    }

    public Point2D getTopRight() {
        return this.topRight;
    }

    public Point2D getBottomRight() {
        return this.bottomRight;
    }

    public Point2D getBottomLeft() {
        return this.bottomLeft;
    }
    
    public String toString() { 
        String[] str = {this.topLeft.toString(), this.topRight.toString(), this.bottomRight.toString(), this.bottomLeft.toString()};
        return Arrays.toString(str);
    } 
}
