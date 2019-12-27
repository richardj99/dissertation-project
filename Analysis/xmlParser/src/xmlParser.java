import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.json.simple.parser.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class xmlParser {

    int lineCount = 0;

    JSONArray personsArray, orgArray, memArray;

    FileWriter csvWriter;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        xmlParser xmlP = new xmlParser();

        String jsonFile = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\" +
                "dissertation-project\\Analysis\\parlParse\\people.json";
        Object obj = new JSONParser().parse(new FileReader(jsonFile));
        JSONObject jo = (JSONObject) obj;
        xmlP.personsArray = (JSONArray) jo.get("persons");
        xmlP.orgArray = (JSONArray) jo.get("organizations");
        xmlP.memArray = (JSONArray) jo.get("memberships");

        xmlP.csvWriter = new FileWriter("hansardData.csv");

        xmlP.retrieveParty("uk.org.publicwhip/member/40334");
        String scrapedUrl = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\" +
                "Analysis\\parlParse\\scrapedxml\\debates\\";
        File scrapedDir = new File (scrapedUrl);
        File[] debateFiles = scrapedDir.listFiles();
        for(File f: debateFiles){
            xmlP.scrapeXML(f);
        }
    }

    public void scrapeXML(File xmlFile) throws ParserConfigurationException, IOException, SAXException, ParseException {
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
                JSONObject party = retrieveParty(element.getAttribute("speakerid"));
                String textContents = node.getTextContent();
                String fileName = xmlFile.getName();
                writeToCSV(fileName, textContents, party);
                //System.out.println(xmlFile.getName() + "\n" + node.getTextContent());
                lineCount++;
            }
        }
    }

    public JSONObject retrieveParty(String mpCode) throws IOException, ParseException {
        JSONObject memObj = null;
        for(int k = 0; k < memArray.size(); k++){
            memObj = (JSONObject) memArray.get(k);
            if(memObj.containsKey("id")) {
                if ((memObj.get("id").equals(mpCode))) {
                    break;
                }
            }
        }
         return memObj;
    }

    public JSONObject retrievePerson(String mpCode) throws IOException, ParseException{
        JSONObject personObj = null;
        for(int k = 0; k < personsArray.size(); k++){
            personObj = (JSONObject) personsArray.get(k);
            if( (personObj.get("id")).equals(mpCode)){
                break;
            }
        }
        System.out.println(personObj.get("id"));
        return personObj;
    }

    public void writeToCSV(String i_fileName, String i_textContents, JSONObject i_party) throws IOException {
        System.out.println("Now Writing: " + i_party.get("on_behalf_of_id") + " of " + i_textContents);
        i_textContents = i_textContents.replace("\"", "\"\"");
        csvWriter.append("\"" + i_textContents + "\"");
        csvWriter.append(", ");
        csvWriter.append((String) i_party.get("on_behalf_of_id"));
        csvWriter.append("\n");
        System.out.println(lineCount);
    }
}
