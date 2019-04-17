import java.awt.geom.Point2D;
import java.util.ArrayList;

// allows you to return multiple types of values
// needed to keep track of each individual gesture, its corresponding points, its score in relation to the gesture that is being tested
public class ReturnValues {

    public final String gesture;
    public final double score;
    public final ArrayList<Point2D.Double> points;
    public final int gestureNumber;

    public ReturnValues(String gesture, double score) {
        this.gesture = gesture;
        this.score = score;
        this.points = null;
        this.gestureNumber = 0;
    }

    public ReturnValues(String gesture, ArrayList<Point2D.Double> points) {
        this.gesture = gesture;
        this.score = 0;
        this.points = points;
        this.gestureNumber = 0;
    }

    // constructor for returning n best list values
    public ReturnValues(String gesture, double score, int gestureNumber) {
        this.gesture = gesture;
        this.score = score;
        this.points = null;
        this.gestureNumber = gestureNumber;
    }

    public ReturnValues(String gesture, ArrayList<Point2D.Double> points, int gestureNumber) {
        this.gesture = gesture;
        this.score = 0;
        this.points = points;
        this.gestureNumber = gestureNumber;
    }
}
