import java.util.ArrayList;

public class RecognizerAlgorithmBase {
    // return n best list of gestures
    public static ArrayList<ReturnValues> Recognize(ReturnValues points, ArrayList<ArrayList<ReturnValues>> templates) {

        ArrayList<ReturnValues> nbestList = new ArrayList<>();

        // for each gesture type, get the highest score and store it in an arraylist
        for (int i = 0; i < templates.size(); i++) {
            double b = Double.POSITIVE_INFINITY;
            int gestureNumber = 0;

            // test each testing sample for each gesture type
            for (int j = 0; j < templates.get(i).size(); j++) {
                double d = RecognizerAlgorithm.DistanceAtBestAngle(points.points, templates.get(i).get(j).points, -1 * Math.toRadians(45), Math.toRadians(45), Math.toRadians(2));

                if (d < b) {
                    b = d;
                    gestureNumber = j + 1;
                }
            }

            double score = 1 - b / (.5 * Math.sqrt(250 * 250 + 250 * 250));

            // store current gesture type, best score out of all the gestures, and the gesture number that has the best score
            nbestList.add(new ReturnValues(templates.get(i).get(0).gesture, score, gestureNumber));
        }

        // sort and return n best list
        return RecognizerAlgorithm.NBestListSort(nbestList);
    }
}
