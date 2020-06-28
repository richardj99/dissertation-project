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

public class xmlParser {

    int lineCount = 0;

    JSONArray personsArray, orgArray, memArray;
    
    BufferedWriter bw;

    Stack<String[]> conStack, labStack;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        xmlParser xmlP = new xmlParser();

        xmlP.conStack = new Stack<>();
        xmlP.labStack =  new Stack<>();


        String jsonFile = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\Analysis\\parlParse\\people.json";
        Object obj = new JSONParser().parse(new FileReader(jsonFile));
        JSONObject jo = (JSONObject) obj;
        xmlP.personsArray = (JSONArray) jo.get("persons");
        xmlP.orgArray = (JSONArray) jo.get("organizations");
        xmlP.memArray = (JSONArray) jo.get("memberships");

        xmlP.bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("hansardData.csv", true), StandardCharsets.UTF_8));

        String scrapedUrl = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\Analysis\\parlParse\\scrapedxml\\debates\\";
        File scrapedDir = new File (scrapedUrl);
        File[] debateFiles = scrapedDir.listFiles();
        assert debateFiles != null;
        for(File f: debateFiles){
            System.out.println(f.getName());
            xmlP.scrapeXML(f);
        }
        xmlP.depleteStacks();
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
            StringBuilder speech = new StringBuilder(" ");
            for(int j=0; j<childNodeList.getLength(); j++) {
                Node node = childNodeList.item(j);
                if ((node.getNodeName()).equals("p")) {
                    String textContents = node.getTextContent();
                    speech.append(" ").append(textContents);
                }
            }
            double party = retrieveParty(element.getAttribute("speakerid"));
            writeToStack(speech.toString(), party);
        }
    }

    public double retrieveParty(String mpCode) {
        JSONObject memObj = null;
        for(int k = 0; k < memArray.size(); k++) {
            memObj = (JSONObject) memArray.get(k);
            if (memObj.containsKey("id")) {
                if ((memObj.get("id").equals(mpCode))) {
                    break;
                }
            }
        }
        double partyEncoded;
        assert memObj != null;
        switch((String)memObj.get("on_behalf_of_id")){
            case "conservative": partyEncoded = 0.5; break;
            case "labour": partyEncoded = 1; break;
            default: partyEncoded = 10; System.out.println("Bwah " + memObj.get("on_behalf_of_id"));
        }
         return partyEncoded;
    }

    public void writeToCSV(String[] record) throws IOException {
        String text = record[0];
        String party = record[1];
        bw.append("\"").append(text).append("\"");
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
            String[] con = conStack.pop(); System.out.println(con[0] + " " + con[1]);
            writeToCSV(con);
            lineCount++;
            String[] lab = labStack.pop(); System.out.println(lab[0] + " " + lab[1]);
            writeToCSV(lab);
            lineCount++;
        }
    }

    public void depleteStacksLibWeighted() throws IOException{
        Collections.shuffle(conStack); Collections.shuffle(labStack);
        System.out.println("Expected Output: " + labStack.size()+(labStack.size()/2));
        System.out.println("Depleting Stacks");
        int labSize = labStack.size();
        int conSize = labSize/2;
        for(int i=0; i<labSize; i++){
            writeToCSV(labStack.pop());
            lineCount++;
        }
        for(int i=0; i<conSize; i++){
            writeToCSV(conStack.pop());
            lineCount++;
        }
    }

    public void depleteStacksConWeighted() throws IOException{
        Collections.shuffle(conStack); Collections.shuffle(labStack);
        System.out.println("Expected Output: " + labStack.size()+(labStack.size()/2));
        System.out.println("Depleting Stacks");
        int conSize = labStack.size();
        int labSize = conSize/2;
        for(int i=0; i<labSize; i++){
            writeToCSV(labStack.pop());
            lineCount++;
        }
        for(int i=0; i<conSize; i++){
            writeToCSV(conStack.pop());
            lineCount++;
        }
    }

    public void writeToStack(String i_textContents, double i_party){
        // Format speech text for storage in csv
        //if((i_textContents.charAt(0)).equals("\"")){}
        i_textContents = i_textContents.replace("\"", "\"\"");
        // Count length of text to exclude shorter speeches.
        int textWordCount = 0;
        for(int i=0; i<i_textContents.length(); i++){
            if(i_textContents.charAt(i) == ' '){
                textWordCount++;
            }
        }
        if(textWordCount <= 20) {
            //System.out.println("Skipping Null/Short Text");
        } else if(i_party == 10){
            //System.out.println("Skipping Null/Irrelevant Party");
        } else {
            String[] results = new String[2];
            results[0] = i_textContents;
            results[1] = Double.toString(i_party);
            switch(Double.toString(i_party)){
                case "0.5": conStack.push(results); break;
                case "1.0": labStack.push(results);break;
                default: break;
            }
        }
    }
}
