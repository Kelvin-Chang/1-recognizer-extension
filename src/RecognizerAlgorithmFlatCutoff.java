import java.awt.geom.Point2D;
import java.util.ArrayList;


public class RecognizerAlgorithmFlatCutoff {
    // return n best list of gestures
    public static ArrayList<ReturnValues> Recognize(ReturnValues points, ArrayList<ArrayList<ReturnValues>> templates) {

        ArrayList<ReturnValues> nbestList = new ArrayList<>();

        // for each gesture type, get the highest score and store it in an arraylist
        for (int i = 0; i < templates.size(); i++) {
            double b = Double.POSITIVE_INFINITY;
            int gestureNumber = 0;

            // test each testing sample for each gesture type
            for (int j = 0; j < templates.get(i).size(); j++) {
                double d = DistanceAtBestAngle(points.points, templates.get(i).get(j).points, -1 * Math.toRadians(45), Math.toRadians(45), Math.toRadians(2));

                if (d < b) {
                    b = d;
                    gestureNumber = j + 1;
//                    gestureResult = templates.get(i).get(j).gesture;
                }
            }

            double score = 1 - b / (.5 * Math.sqrt(250 * 250 + 250 * 250));

            // store current gesture type, best score out of all the gestures, and the gesture number that has the best score
            nbestList.add(new ReturnValues(templates.get(i).get(0).gesture, score, gestureNumber));
        }

        // sort n best list and store into temp
        ArrayList<ReturnValues> temp = new ArrayList<>();

        // find greatest score/gesture, append to temp, remove from original list
        while (!nbestList.isEmpty()) {
            int bestGesture = 0;
            double bestScore = Double.NEGATIVE_INFINITY;

            // search list for best gesture score and index
            for (int i = 0; i < nbestList.size(); i++) {
                if (nbestList.get(i).score > bestScore) {
                    bestScore = nbestList.get(i).score;
                    bestGesture = i;
                }
            }

            // add the gesture to the temp list
            temp.add(nbestList.get(bestGesture));

            // remove the gesture from the original list
            nbestList.remove(bestGesture);
        }

        return temp;
    }

    // n gives the number of resampled points
    public static ArrayList<Point2D.Double> Resample(ArrayList<Point2D.Double> arrayList, int n) {
        ArrayList<Point2D.Double> newPoints = new ArrayList<>();
        double I = PathLength(arrayList) / (n - 1.0);
        double D = 0.0;

        newPoints.add(arrayList.get(0));

        for (int i = 1; i < arrayList.size(); i++) {
            double d = Distance(arrayList.get(i), arrayList.get(i - 1));
            if (d + D >= I) {
                double qx = arrayList.get(i - 1).getX() + ((I - D) / d) * (arrayList.get(i).getX() - arrayList.get(i - 1).getX());
                double qy = arrayList.get(i - 1).getY() + ((I - D) / d) * (arrayList.get(i).getY() - arrayList.get(i - 1).getY());
                newPoints.add(new Point2D.Double(qx, qy));
                arrayList.add(i, new Point2D.Double(qx, qy));
                D = 0.0;
            }
            else {
                D = D + d;
            }
        }
//        if (newPoints.size() == n - 1) {
//            newPoints.add(new Point2D.Double(arrayList.get(arrayList.size() - 1).getX(), arrayList.get(arrayList.size() - 1).getY()));
//        }


        return newPoints;
    }

    public static double PathLength(ArrayList<Point2D.Double> arrayList) {
        double distance = 0;

        for (int i = 1; i < arrayList.size(); i++) {
            distance += Distance(arrayList.get(i - 1), arrayList.get(i));
        }

        return distance;
    }


    public static double Distance(Point2D.Double points1, Point2D.Double points2) {
        double dx = points2.getX() - points1.getX();
        double dy = points2.getY() - points1.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    // return the centroid by adding all the points together and dividing them by the number of points
    public static Point2D.Double Centroid(ArrayList<Point2D.Double> arrayList) {
        double x = 0.0, y = 0.0;
        for (int i = 0; i < arrayList.size(); i++) {
            x += arrayList.get(i).getX();
            y += arrayList.get(i).getY();
        }

        return new Point2D.Double(x / (double) arrayList.size(), y / (double) arrayList.size());
    }

    //
    public static double IndicativeAngle(ArrayList<Point2D.Double> points) {
        Point2D.Double centroid = Centroid(points);
        return Math.atan2((centroid.getY() - points.get(0).getY()), (centroid.getX() - points.get(0).getX()));
    }

    public static ArrayList<Point2D.Double> RotateBy(ArrayList<Point2D.Double> arrayList, double radians) {
//        System.out.println("arraylist for the rotate function" + arrayList.toString());

        Point2D.Double centroid = Centroid(arrayList);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        ArrayList<Point2D.Double> newPoints = new ArrayList<>();

        for(int i = 0; i < arrayList.size(); i++) {
            double qx = (arrayList.get(i).getX() - centroid.getX()) * cos - (arrayList.get(i).getY() - centroid.getY()) * sin + centroid.getX();
            double qy = (arrayList.get(i).getX() - centroid.getX()) * sin + (arrayList.get(i).getY() - centroid.getY()) * cos + centroid.getY();
            newPoints.add(new Point2D.Double(qx, qy));
        }

        return newPoints;
    }

    public static ArrayList<Point2D.Double> ScaleTo(ArrayList<Point2D.Double> arrayList, double size) {
        ArrayList<Point2D.Double> newPoints = new ArrayList<>();

        // create bounding box
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        // bounding box
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getX() < minX) {
                minX = arrayList.get(i).getX();
            }
            if (arrayList.get(i).getX() > maxX) {
                maxX = arrayList.get(i).getX();
            }
            if (arrayList.get(i).getY() < minY) {
                minY = arrayList.get(i).getY();
            }
            if (arrayList.get(i).getY() > maxY) {
                maxY = arrayList.get(i).getY();
            }
        }

        double width = maxX - minX;
        double height = maxY - minY;

        // not sure how to deal with height or width being 0, hopefully never happens?
        for (int i = 0; i < arrayList.size(); i++) {
            double qx = arrayList.get(i).getX() * size / width;
            double qy = arrayList.get(i).getY() * size / height;
            newPoints.add(new Point2D.Double(qx, qy));
        }

        return newPoints;
    }

    public static ArrayList<Point2D.Double> TranslateTo(ArrayList<Point2D.Double> arrayList, Point2D.Double origin) {
        ArrayList<Point2D.Double> newPoints = new ArrayList<>();

        Point2D.Double centroid = Centroid(arrayList);

        for (int i = 0; i < arrayList.size(); i++) {
            newPoints.add(new Point2D.Double(arrayList.get(i).getX() + origin.getX() - centroid.getX(), arrayList.get(i).getY() + origin.getY() - centroid.getY()));
        }

        return newPoints;
    }

    public static double DistanceAtBestAngle(ArrayList<Point2D.Double> points, ArrayList<Point2D.Double> template, double angleA, double angleB, double angleDelta) {
        double phi = 1.0 / 2.0 * (-1 + Math.sqrt(5));

        double x1 = phi * angleA + (1.0 - phi) * angleB;
        double f1 = DistanceAtAngle(points, template, x1);

        double x2 = (1.0 - phi) * angleA + phi * angleB;
        double f2 = DistanceAtAngle(points, template, x2);

        while (Math.abs(angleB - angleA) > angleDelta) {
            if (f1 < f2) {
                angleB = x2;
                x2 = x1;
                f2 = f1;
                x1 = phi * angleA + (1.0 - phi) * angleB;
                f1 = DistanceAtAngle(points, template, x1);
            }
            else {
                angleA = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1.0 - phi) * angleA + phi * angleB;
                f2 = DistanceAtAngle(points, template, x2);
            }
        }

        return Math.min(f1, f2);
    }

    public static double DistanceAtAngle(ArrayList<Point2D.Double> arrayList, ArrayList<Point2D.Double> template, double radians) {
        ArrayList<Point2D.Double> newPoints = RotateBy(arrayList, radians);

        double d = PathDistance(newPoints, template);

        return d;
    }

    public static double PathDistance(ArrayList<Point2D.Double> points1, ArrayList<Point2D.Double> points2) {
        double d = 0.0;

        for (int i = 0; i < points1.size() && i < points2.size() ; i++) {
            d += Distance(points1.get(i), points2.get(i));
        }

        return d / points1.size();
    }
}
