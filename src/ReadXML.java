import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReadXML {
    public static ReturnValues Read(String filename) {
        File inputFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(inputFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();

        // gets the name of the gesture
        String name = root.getAttribute("Name");

        // gets rid of the 2 numbers on the end of the name to get the gesture
        String gesture = name.substring(0, name.length() - 2);

        NodeList nList = doc.getElementsByTagName("Point");

        ArrayList<Point2D.Double> points = new ArrayList<>();

        for (int i = 0; i < nList.getLength(); i++) {
            points.add(new Point2D.Double(Double.parseDouble(nList.item(i).getAttributes().getNamedItem("X").getNodeValue()), Double.parseDouble(nList.item(i).getAttributes().getNamedItem("Y").getNodeValue())));
        }

        return new ReturnValues(gesture, points);
    }
}
