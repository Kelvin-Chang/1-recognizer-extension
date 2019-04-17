import java.awt.geom.Point2D;
import java.util.ArrayList;

// functions to normalize and manipulate points
public class HelperFunctions {

    // Takes a user and gesture type to read a series of XML files from the specified user of the specified gesture
    // Stores all of the user's gestures from the specified gesture type in an array and returns the array
    public static ArrayList<ReturnValues> BuildGestures(String user, String gestureType) {
        ArrayList<ReturnValues> templates = new ArrayList<>();

        String filename = "";
        String one = "resources/xml_logs/";
        String speed = "/medium/";
        String gestureNumber = "";
        String two = ".xml";

        // for each gesture type

        // add all gestures into list
        for (int i = 1; i <= 10; i++) {
            if (i != 10) {
                gestureNumber = "0" + i;
            } else {
                gestureNumber = Integer.toString(i);
            }

            // get appropriate file
            filename = one + user + speed + gestureType + gestureNumber + two;

            // read the file, normalize the points, add to templates
            ReturnValues temp = NormalizePoints(ReadXML.Read(filename));

            templates.add(new ReturnValues(temp.gesture, temp.points, Integer.parseInt(gestureNumber)));
        }

        // preliminary shuffle for randomness
//        Collections.shuffle(templates);


        return templates;
    }

    // function to normalize points
    public static ReturnValues NormalizePoints(ReturnValues template) {

        // resamples points
        ArrayList<Point2D.Double> resampledPoints = RecognizerAlgorithm.Resample(template.points, 64);

        // finds the indicative angle
        double indicativeAngle = RecognizerAlgorithm.IndicativeAngle(resampledPoints);

        // rotates by the indicative angle
        // indicative angle is multiplied by -1 to make it a negative value
        ArrayList<Point2D.Double> rotatedPoints = RecognizerAlgorithm.RotateBy(resampledPoints, -1 * indicativeAngle);

        // scale the gesture to a 250x250 sized plane
        ArrayList<Point2D.Double> scaledPoints = RecognizerAlgorithm.ScaleTo(rotatedPoints, 250);

        // translates gesture to the origin
        ArrayList<Point2D.Double> translatedPoints = RecognizerAlgorithm.TranslateTo(scaledPoints, new Point2D.Double(0, 0));

        // stores the normalized points and the gesture name in an object
        ReturnValues normalizedPoints = new ReturnValues(template.gesture, translatedPoints);

        return normalizedPoints;
    }

    // gets the corresponding gesture type based on an int value
    public static String GestureType(int gestureNumber) {
        String gestureType = "";
        switch (gestureNumber) {
            case 0:
                gestureType = "arrow";
                break;
            case 1:
                gestureType = "caret";
                break;
            case 2:
                gestureType = "check";
                break;
            case 3:
                gestureType = "circle";
                break;
            case 4:
                gestureType = "delete_mark";
                break;
            case 5:
                gestureType = "left_curly_brace";
                break;
            case 6:
                gestureType = "left_sq_bracket";
                break;
            case 7:
                gestureType = "pigtail";
                break;
            case 8:
                gestureType = "question_mark";
                break;
            case 9:
                gestureType = "rectangle";
                break;
            case 10:
                gestureType = "right_curly_brace";
                break;
            case 11:
                gestureType = "right_sq_bracket";
                break;
            case 12:
                gestureType = "star";
                break;
            case 13:
                gestureType = "triangle";
                break;
            case 14:
                gestureType = "v";
                break;
            case 15:
                gestureType = "x";
                break;
        }
        return gestureType;
    }
}
