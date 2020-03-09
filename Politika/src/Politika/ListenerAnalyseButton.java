package Politika;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ListenerAnalyseButton implements ActionListener {

    private PolitikaInterface uiInstance;
    private PolitikaLogic logicInstance;

    public ListenerAnalyseButton(PolitikaInterface parentUIInstance, PolitikaLogic parentLogicInstance){
        uiInstance = parentUIInstance;
        logicInstance = parentLogicInstance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        uiInstance.log("Analysis Started");
        String textInput = uiInstance.getTextInput(true);
        uiInstance.log("Inputted Text Loaded");
        String[] spaceArray = textInput.split(" ");
        uiInstance.log(Integer.toString(spaceArray.length) + " Words To Be Analysed");
        uiInstance.setWordsAnalysed(Integer.toString(spaceArray.length));
        new Thread(() -> {
            ArrayList<String> encodedTextArray;
            Double[] results;
            Object[] retArr;
            uiInstance.log("Starting Neural Network Process");
            //if(uiInstance.isParagraphAnalysedSelected() == true){
                retArr = logicInstance.analyseText(textInput);
                results = (Double[]) retArr[0];
                encodedTextArray = (ArrayList<String>) retArr[1];
            //}

            uiInstance.log("Neural Network Predictions Completed");
            uiInstance.log("Neural Network Results Retreived");
            uiInstance.setEncodedTextPanel(encodedTextArray);
            uiInstance.log("Right-Leaning Score: " + results[0] + "\t Left-Leaning Score: " + results[1]);
            uiInstance.setPoliticalScores(results);
        }).start();
    }
}
