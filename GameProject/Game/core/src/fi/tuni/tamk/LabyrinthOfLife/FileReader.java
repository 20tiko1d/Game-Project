package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * The class is used to read files and also maybe store them in the future.
 */
public class FileReader {

    /**
     * Method reads the sentences of the pair objects from the XML-file.
     * @return Returns an array that contains the read sentences.
     */

    public static Array<String> getPairElements() {
        Array<String> array = new Array<>();
        try {

            FileHandle fileHandle = Gdx.files.internal(getFileName());
            InputStream is = fileHandle.read();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("element");

            for(int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    NodeList nList2 = eElement.getElementsByTagName("sentence");
                    for(int i = 0; i < nList2.getLength(); i++) {
                        Node node = nList2.item(i);

                        if(node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            array.add(element.getTextContent());
                        }
                    }
                }
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public static String getFileName() {
        if(GameConfiguration.open("language").equals("fi_FI")) {
            return "Parit.xml";
        } else {
            return "Pairs.xml";
        }
    }

    public static int[][][] getTutorialMap() {
        int[][][] map = new int[10][10][4];
        FileHandle file = Gdx.files.internal("tutorialMap.txt");
        String mapString = file.readString();
        int randomCounter = 0;
        for(int row = 0; row < 10; row++) {
            for(int column = 0; column < 10; column++) {
                int side = 0;
                while(side < 4) {
                    char chr = mapString.charAt(row * 40 + column * 4 + side + randomCounter);
                    if(chr == '0' || chr == '1' || chr == '2' || chr == '3') {
                        int num = Integer.parseInt("" + chr);
                        map[row][column][side] = num;
                        side++;
                    } else {
                        randomCounter++;
                    }
                }
            }
        }
        return map;
    }
}
