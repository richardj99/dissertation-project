package Politika;

import java.awt.*;
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
        uiInstance.log("\n");
        uiInstance.log("Text Analysis Started");
        String textInput = uiInstance.getTextInput(true);
        String[] spaceArray = textInput.split(" ");
        if(spaceArray.length < 15){
            new ValidationDialog("Analysis Error",
                    "Input Text must be longer than 15 words");
            return;
        } else {
            uiInstance.log(spaceArray.length + " Words To Be Analysed");
            uiInstance.setWordsAnalysed(Integer.toString(spaceArray.length));
        }
        new Thread(() -> {
            ArrayList<String> encodedTextArray;
            Double[] results;
            Object[] retArr;
            uiInstance.log("Neural Network Started ");
            uiInstance.log("The Control Panel and Log will be updated once analysis is complete");
            if(uiInstance.isParagraphAnalysedSelected() == true){
                retArr = logicInstance.analyseText(textInput);
                results = (Double[]) retArr[0];
                encodedTextArray = (ArrayList<String>) retArr[1];
                uiInstance.setEncodedTextPanel(encodedTextArray);
                Color highlightColor;
                switch(logicInstance.calculateFinalBiasResult(results)){
                    case "Left-Leaning": highlightColor = Color.RED; break;
                    case "Right-Leaning": highlightColor = Color.CYAN; break;
                    default: highlightColor = Color.GRAY; break;
                }
                uiInstance.highlightText(highlightColor, 0, textInput.length());
                uiInstance.setAnalysedTextHash(textInput);
            } else{
                retArr = logicInstance.analyseBySentence(textInput, uiInstance);
                results = (Double[]) retArr[0];
            }
            uiInstance.log("Analysis Complete");
            uiInstance.log("Right-Leaning Score: " + results[0] + "\t Left-Leaning Score: " + results[1]);
            uiInstance.setPoliticalScores(results);
            uiInstance.setFinalResult(logicInstance.calculateFinalBiasResult(results));
        }).start();
    }
}
