import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

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

    //FileWriter csvWriter;
    BufferedWriter bw;

    Stack<String[]> libDemStack, conStack, labStack, dupStack, snpStack;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        xmlParser xmlP = new xmlParser();

        xmlP.conStack = new Stack<String[]>();
        xmlP.labStack =  new Stack<String[]>();


        String jsonFile = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\" +
                "dissertation-project\\Analysis\\parlParse\\people.json";
        Object obj = new JSONParser().parse(new FileReader(jsonFile));
        JSONObject jo = (JSONObject) obj;
        xmlP.personsArray = (JSONArray) jo.get("persons");
        xmlP.orgArray = (JSONArray) jo.get("organizations");
        xmlP.memArray = (JSONArray) jo.get("memberships");

        //xmlP.csvWriter = new FileWriter("hansardDataOld.csv");
        xmlP.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("hansardData.csv", true)
                , StandardCharsets.UTF_8));

        xmlP.retrieveParty("uk.org.publicwhip/member/40334");
        String scrapedUrl = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\" +
                "Analysis\\parlParse\\scrapedxml\\debates\\";
        File scrapedDir = new File (scrapedUrl);
        File[] debateFiles = scrapedDir.listFiles();
        for(File f: debateFiles){
            xmlP.scrapeXML(f);
        }
        xmlP.depleteStacks();
        System.out.println(xmlP.lineCount);
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
                int party = retrieveParty(element.getAttribute("speakerid"));
                String textContents = node.getTextContent();
                String fileName = xmlFile.getName();
                //writeToCSV(fileName, textContents, party);
                writeToStack(fileName, textContents, party);
            }
        }
    }

    public int retrieveParty(String mpCode) throws IOException, ParseException {
        JSONObject memObj = null;
        for(int k = 0; k < memArray.size(); k++) {
            memObj = (JSONObject) memArray.get(k);
            if (memObj.containsKey("id")) {
                if ((memObj.get("id").equals(mpCode))) {
                    break;
                }
            }
        }
        int partyEncoded;
        switch((String)memObj.get("on_behalf_of_id")){
            case "conservative": partyEncoded = 1; break;
            case "labour": partyEncoded = 2; break;
            default: partyEncoded = 10;
        }
         return partyEncoded;
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

    public void writeToCSV(String[] record) throws IOException {
        String text = record[0];
        String party = record[1];
        bw.append("\"" + text + "\"");
        bw.append(",");
        bw.append(party);
        bw.append("\n");

    }

    public void depleteStacks() throws IOException {
        Collections.shuffle(conStack); Collections.shuffle(labStack);
        System.out.println("Expected Output: " + labStack.size()*2);
        System.out.println("Depleting Stacks");
        int labSize = labStack.size();
        for(int i=0; i<labSize; i++){
            writeToCSV(conStack.pop());
            lineCount++;
            writeToCSV(labStack.pop());
            lineCount++;
        }
    }

    public void writeToStack(String i_fileName, String i_textContents, int i_party){
        // Format speech text for storage in csv
        i_textContents = i_textContents.replace("\"", "\"\"");
        // Count length of text to exclude shorter speeches.
        int textWordCount = 0;
        for(int i=0; i<i_textContents.length(); i++){
            if(i_textContents.charAt(i) == ' '){
                textWordCount++;
            }
        }
        if((i_textContents == null) || (textWordCount<=20)) {
            //System.out.println("Skipping Null/Short Text");
        } else if(i_party == 10){
            //System.out.println("Skipping Null/Irrelevant Party");
        } else {
            String[] results = new String[2];
            results[0] = i_textContents;
            results[1] = Integer.toString(i_party);
            switch(i_party){
                case 1: conStack.push(results); break;
                case 2: labStack.push(results);break;
                default: break;
            }
        }
    }
}
