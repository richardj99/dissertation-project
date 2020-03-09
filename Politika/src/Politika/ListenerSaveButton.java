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
        PolitikaSaveDB saveDBInstance = new PolitikaSaveDB(uiInstance, logicInstance);
        saveDBInstance.setArticleTextArea(uiInstance.getTextInput(false));
        saveDBInstance.setPoliticalScores(uiInstance.getPoliticalScores());
    }
}
