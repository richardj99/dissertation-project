package Politika;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListenerSaveButton implements ActionListener {

    private PolitikaInterface uiInstance;
    private PolitikaLogic logicInstance;

    public ListenerSaveButton(PolitikaInterface parentUIInstance, PolitikaLogic parentLogicInstance){
        uiInstance = parentUIInstance;
        logicInstance = parentLogicInstance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currentInputText = uiInstance.getTextInput(true);
        int analysedTextHash = uiInstance.getAnalysedTextHash();
        System.out.println(analysedTextHash);
        System.out.println(currentInputText.hashCode());
        if(analysedTextHash == 0){
            new ValidationDialog("Paragraph Analysis", "Articles can only be saved after Analysis");
            return;
        }else if(analysedTextHash != currentInputText.hashCode()){
            new ValidationDialog("Analysis Text Has Changed",
                    "The Input Text cannot be saved after the article has been changed");
            return;
        }
        PolitikaSaveDB saveDBInstance = new PolitikaSaveDB(uiInstance, logicInstance);
        saveDBInstance.setArticleTextArea(uiInstance.getTextInput(false));
        saveDBInstance.setPoliticalScores(uiInstance.getPoliticalScores());
    }
}
