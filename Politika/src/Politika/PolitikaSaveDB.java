package Politika;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class PolitikaSaveDB {
    private JPanel mainPanel;
    private JTextField articleNameField;
    private JTextField authorField;
    private JTextField partyField;
    private JButton saveDBButton;
    private JTextArea articleTextArea;
    private JLabel rightVal;
    private JLabel leftVal;
    private JComboBox dayBox;
    private JComboBox monthBox;
    private JComboBox yearBox;

    PolitikaInterface uiInstance;
    PolitikaLogic logicInstance;

    public PolitikaSaveDB(PolitikaInterface pUIInstance, PolitikaLogic pLogicInstance){
        uiInstance = pUIInstance;
        logicInstance = pLogicInstance;

        JFrame f = new JFrame("Politika: Article Save");
        ImageIcon img = new ImageIcon("res\\icon.png");
        f.setIconImage(img.getImage());
        f.setContentPane(this.mainPanel);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for(int i=1; i<=31; i++) dayBox.addItem(i);

        for(int i=1; i<=12; i++) monthBox.addItem(i);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        for(int i=1850; i<=year; i++) yearBox.addItem(i);

        saveDBButton.addActionListener(new ListenerSaveToDBButton(this, uiInstance, logicInstance, f));
        f.pack();
        f.setMinimumSize(new Dimension(800, 500));
        f.setVisible(true);
    }

    public String getArticleText(boolean removeNewLines){
        if(removeNewLines) return articleTextArea.getText().replace("\n", " ");
        else return articleTextArea.getText();
    }

    public String getArticleName(){return articleNameField.getText();}

    public String[] getPoliticalScores(){
        String[] politicalScores = new String[2];
        politicalScores[0] = rightVal.getText();
        politicalScores[1] = leftVal.getText();
        return politicalScores;
    }

    public String getAuthorName(){return authorField.getText();}

    public String getParty(){return partyField.getText();}

    public String getDate(){
        String date = null;
        String day = dayBox.getSelectedItem().toString();
        String month = (String) monthBox.getSelectedItem().toString();
        String year = (String) yearBox.getSelectedItem().toString();
        date = "" + year + "-" + month + "-" + day;
        return date;
    }

    public void setArticleTextArea(String input){articleTextArea.setText(input);}

    public void setPoliticalScores(String[] predictions){
        rightVal.setText(predictions[0]);
        leftVal.setText(predictions[1]);
    }
}
