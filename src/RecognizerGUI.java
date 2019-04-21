import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class RecognizerGUI extends Application {

    private ArrayList<ArrayList<ReturnValues>> templates;
    private ArrayList<Point2D.Double> points;


    public RecognizerGUI() {
        this.templates = new ArrayList<>();
        this.points = new ArrayList<>();

        // populate templates with gestures
        for (int i = 0; i < 16; i++) {
            String gestureType = HelperFunctions.GestureType(i);
            templates.add(HelperFunctions.BuildGestures("s02", gestureType));
        }
    }

    @Override
    public void start (Stage primaryStage) throws Exception {
        // create new window to draw on
        primaryStage.setTitle("Canvas");
        Group root = new Group();
        Canvas canvas = new Canvas(1000, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.fillText("Hi", 100, 100);

        Scene scene = new Scene(root, 1000, 800);

//        add point to array as mouse is clicked and dragged
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // clear previously stored points before logging
                points.clear();

                gc.clearRect(0, 0, 1000, 800);
                gc.beginPath();
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                points.add(new Point2D.Double(mouseEvent.getX(), mouseEvent.getY()));
                gc.stroke();
            }
        });

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                points.add(new Point2D.Double(mouseEvent.getX(), mouseEvent.getY()));
                gc.stroke();
            }
        });


        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<Point2D.Double> normalizedPoints = HelperFunctions.NormalizePoints(new ReturnValues("", points)).points;

                // get n best lists
                StopWatch stopWatchBase = new StopWatch();
                ArrayList<ReturnValues> nbestListBase = RecognizerAlgorithmBase.Recognize(new ReturnValues("", normalizedPoints), templates);
                double timeBase = stopWatchBase.getElapsedTime();

                StopWatch stopWatchFlatCutoff = new StopWatch();
                ArrayList<ReturnValues> nbestListFlatCutoff = RecognizerAlgorithmFlatCutoff.Recognize(new ReturnValues("", normalizedPoints), templates);
                double timeFlatCutoff = stopWatchFlatCutoff.getElapsedTime();

                StopWatch stopWatchPercentageDifference = new StopWatch();
                ArrayList<ReturnValues> nbestListPercentageDifference = RecognizerAlgorithmPercentageDifference.Recognize(new ReturnValues("", normalizedPoints), templates);
                double timePercentageDifference = stopWatchPercentageDifference.getElapsedTime();

                StopWatch stopWatchFlatDifference = new StopWatch();
                ArrayList<ReturnValues> nbestListFlatDifference = RecognizerAlgorithmFlatDifference.Recognize(new ReturnValues("", normalizedPoints), templates);
                double timeFlatDifference = stopWatchFlatDifference.getElapsedTime();

                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.TOP);
                gc.fillText("Base Algorithm " + "gesture: " + nbestListBase.get(0).gesture + " score: " + nbestListBase.get(0).score + " time: " + timeBase, 100, 100);
                gc.fillText("Flat Cutoff " + "gesture: " + nbestListFlatCutoff.get(0).gesture + " score: " + nbestListFlatCutoff.get(0).score + " time: " + timeFlatCutoff, 100, 120);
                gc.fillText("Percentage Difference " + "gesture: " + nbestListPercentageDifference.get(0).gesture + " score: " + nbestListPercentageDifference.get(0).score + " time: " + timePercentageDifference, 100, 140);
                gc.fillText("Flat Difference " + "gesture: " + nbestListFlatDifference.get(0).gesture + " score: " + nbestListFlatDifference.get(0).score + " time: " + timeFlatDifference, 100, 160);
            }
        });


        root.getChildren().add(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
