package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FileReader {

    //private static final String fileName = "Pairs.xml";

    public static Array<String> getPairElements() {
        Array<String> array = new Array<String>();
        try {

            File fxmlFile = new File("android/assets/Pairs.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fxmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("element");

            for(int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    array.add(eElement.getElementsByTagName("sentence1").item(0).getTextContent());
                    array.add(eElement.getElementsByTagName("sentence2").item(0).getTextContent());
                    array.add(eElement.getElementsByTagName("sentence3").item(0).getTextContent());
                    array.add(eElement.getElementsByTagName("sentence4").item(0).getTextContent());
                    array.add(eElement.getElementsByTagName("sentence5").item(0).getTextContent());
                    array.add(eElement.getElementsByTagName("sentence6").item(0).getTextContent());
                }
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        return array;
    }
}
