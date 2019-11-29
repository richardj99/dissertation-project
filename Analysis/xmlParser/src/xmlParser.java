import java.io.File;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import javax.management.Attribute;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class xmlParser {

    int lineCount = 0;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        xmlParser xmlP = new xmlParser();
        String scrapedUrl = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\" +
                "parlParse\\scrapedxml\\debates\\";
        File scrapedDir = new File (scrapedUrl);
        File[] debateFiles = scrapedDir.listFiles();
        for(File f: debateFiles){
            xmlP.scrapeXML(f);
        }
        System.out.println(xmlP.lineCount);
    }

    public void scrapeXML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        NodeList nodeList=doc.getElementsByTagName("speech");
        for(int i=0; i<nodeList.getLength(); i++){
            Element element = (Element)nodeList.item(i);
            NodeList childNodeList = element.getChildNodes();
            Node node = childNodeList.item(1);
            if(node.getNodeName() == "p"){
                System.out.println(xmlFile.getName() + "\n" + node.getTextContent());
                lineCount++;
            }
        }
    }
}
