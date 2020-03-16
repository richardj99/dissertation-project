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
        uiInstance.log("\n\n");
        uiInstance.log("Text Analysis Started");
        String textInput = uiInstance.getTextInput(true).replace("\n", "");
        uiInstance.log("Text Loaded into Neural Network");
        String[] spaceArray = textInput.split(" ");
        uiInstance.log(spaceArray.length + " Words To Be Analysed");
        uiInstance.setWordsAnalysed(Integer.toString(spaceArray.length));
        new Thread(() -> {
            ArrayList<String> encodedTextArray;
            Double[] results;
            Object[] retArr;
            uiInstance.log("The Neural Network Has Been started, The Control Panel and Log will be updated once " +
                    "a result has been achieved");
            if(uiInstance.isParagraphAnalysedSelected() == true){
                retArr = logicInstance.analyseText(textInput);
                results = (Double[]) retArr[0];
                encodedTextArray = (ArrayList<String>) retArr[1];
                uiInstance.setEncodedTextPanel(encodedTextArray);
            } else{
                retArr = logicInstance.analyseBySentence(textInput, uiInstance);
                results = (Double[]) retArr[0];
            }

            uiInstance.log("Neural Network Predictions Finished and Retrieved");
            uiInstance.log("Right-Leaning Score: " + results[0] + "\t Left-Leaning Score: " + results[1]);
            uiInstance.setPoliticalScores(results);
            uiInstance.setFinalResult(logicInstance.calculateFinalBiasResult(results));
        }).start();
    }
}
