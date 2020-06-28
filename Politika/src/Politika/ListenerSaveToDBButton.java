package Politika;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class ListenerSaveToDBButton implements ActionListener {

    PolitikaLogic logicInstance;
    PolitikaInterface uiInstance;
    PolitikaSaveDB saveInstance;
    JFrame saveFrame;

    public ListenerSaveToDBButton(PolitikaSaveDB parentSaveInstance, PolitikaInterface parentUIInstance,
                                  PolitikaLogic parentLogicInstance, JFrame f){
        logicInstance = parentLogicInstance;
        uiInstance = parentUIInstance;
        saveInstance = parentSaveInstance;
        saveFrame = f;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String inputText = saveInstance.getArticleText(true);
        String articleName = saveInstance.getArticleName();
        String authorName = saveInstance.getAuthorName();
        String[] politicalScores = saveInstance.getPoliticalScores();
        String date = saveInstance.getDate();
        String politicalParty = saveInstance.getParty();
        if (articleName.equals("") || articleName.length() < 5) {
            new ValidationDialog("Save Error",
                    "The Article Name Field Must be filled in and greater than 5 characters");
        } else if (authorName.equals("")) {
            uiInstance.log("Save Error: Author Field cannot be left blank");
            new ValidationDialog("Save Error",
                    "Author Field cannot be left blank");
        } else if (politicalParty.equals("")) {
            new ValidationDialog("Save Error",
                    "Party Field cannot be left blank");
        } else {
            logicInstance.saveToDB(inputText, articleName, authorName, politicalScores, politicalParty, date, uiInstance);
            saveFrame.dispatchEvent(new WindowEvent(saveFrame, WindowEvent.WINDOW_CLOSING));
        }
    }
}
