package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * The class is used to read files.
 *
 * @author Artur Haavisto
 */
public class FileReader {

    /**
     * Method reads the sentences of the objects from the XML-file.
     *
     * @return Returns an array that contains the object sentences.
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
        } catch(Exception e) {}
        return array;
    }

    /**
     * Gets object sentence file name depending on language.
     *
     * @return Object sentence file name.
     */
    public static String getFileName() {
        if(GameConfiguration.open("language").equals("fi_FI") ||
                ((new Locale("fi", "FI").equals(Locale.getDefault())) &&
                        GameConfiguration.open("language").equals(GameConfiguration.noValue))) {
            return "Parit.xml";
        } else {
            return "Pairs.xml";
        }
    }

    /**
     * Gets the tutorial map from a text file.
     *
     * @return Tutorial map as an array.
     */
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
