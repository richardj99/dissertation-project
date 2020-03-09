package Politika;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListenerSaveToDBButton implements ActionListener {

    PolitikaLogic logicInstance;
    PolitikaInterface uiInstance;
    PolitikaSaveDB saveInstance;

    public ListenerSaveToDBButton(PolitikaSaveDB parentSaveInstance, PolitikaInterface parentUIInstance,
                                  PolitikaLogic parentLogicInstance){
        logicInstance = parentLogicInstance;
        uiInstance = parentUIInstance;
        saveInstance = parentSaveInstance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String inputText = saveInstance.getArticleText(true);
        String articleName = saveInstance.getArticleName();
        String authorName = saveInstance.getAuthorName();
        String[] politicalScores = saveInstance.getPoliticalScores();
        String date = saveInstance.getDate();
        String politicalParty = saveInstance.getParty();
        logicInstance.saveToDB(inputText, articleName, authorName, politicalScores, politicalParty, date);
    }
}
