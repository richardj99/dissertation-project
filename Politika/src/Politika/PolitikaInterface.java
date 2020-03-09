package Politika;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PolitikaInterface {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField searchInput;
    private JTabbedPane tabbedPane2;
    private JRadioButton analyseByParagraphRadioButton;
    private JRadioButton analyseBySentenceRadioButton;
    private JButton saveButton;
    private JTextArea textInputArea;
    private JLabel rightVal;
    private JLabel leftVal;
    private JTextArea encodedTextArea;
    private JPanel controlPanel;
    private JPanel informationPanel;
    private JButton analyseButton;
    private JLabel wordsAnalysedLabel;
    private JTable searchResultsTable;
    private JComboBox searchTermBox;
    private JButton searchButton;
    private JTextArea logArea;

    private PolitikaLogic logicInstance;

    private final String[] searchTerms = new String[]{"Name", "Date", "Author", "Party"};

    public PolitikaInterface(PolitikaLogic parentLogicInstance){
        JFrame f = new JFrame("Article Save");
        logicInstance = parentLogicInstance;
        f.setContentPane(this.mainPanel);
        ButtonGroup bg = new ButtonGroup();
        bg.add(analyseByParagraphRadioButton);
        bg.add(analyseBySentenceRadioButton);
        analyseByParagraphRadioButton.setSelected(true);
        for(String s: searchTerms) searchTermBox.addItem(s);
        searchButton.addActionListener(new ListenerSearchButton(this, logicInstance));
        saveButton.addActionListener(new ListenerSaveButton(this, logicInstance));
        analyseButton.addActionListener(new ListenerAnalyseButton(this, logicInstance));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.pack();
        f.setMinimumSize(new Dimension(1280, 720));
        f.setVisible(true);
        this.log("Politika Started");
    }

    public void log(String message){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy '-' HH:mm:ss z");
        logArea.append(formatter.format(date) + ": " + message + "\n");
    }

    public Boolean isParagraphAnalysedSelected(){
        if(analyseByParagraphRadioButton.isSelected()) return true;
        else return false;
    }

    public String getTextInput(boolean removeNewLines){
        if(removeNewLines){
            String input = (textInputArea.getText()).replace("\n", " ");
            return input;
        }else return textInputArea.getText();
    }

    public String[] getPoliticalScores(){
        String[] politicalScores = new String[2];
        politicalScores[0] = rightVal.getText();
        politicalScores[1] = leftVal.getText();
        return politicalScores;
    }

    public String[] getSearchInfo(){
        String[] searchInfo = new String[2];
        searchInfo[0] = (String) searchTermBox.getSelectedItem();
        searchInfo[1] = searchInput.getText();
        return searchInfo;
    }

    void setPoliticalScores(Double[] results){
        rightVal.setText("" + results[0]);
        leftVal.setText("" + results[1]);
    }

    void setEncodedTextPanel(ArrayList<String> encodedTextArray) {
        encodedTextArea.setText(null);
        for (int i = 0; i < encodedTextArray.size(); i++) {
            String item = encodedTextArray.get(i);
            encodedTextArea.append(item + "\n");
        }
    }

    public void setWordsAnalysed(String wordsAnalysed){wordsAnalysedLabel.setText(wordsAnalysed);}

    public static void main(String args[]){
        PolitikaLogic logicInstance = new PolitikaLogic();
        new Thread(() -> {
            PolitikaInterface uiInstance = new PolitikaInterface(logicInstance);
        }).start();

    }
}
