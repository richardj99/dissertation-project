package Politika;

import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
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
    private JLabel finalResultLabel;
    private JTextArea nnSpecsArea;

    private PolitikaLogic logicInstance;

    private int analysedTextHash = 0;
    private final String[] searchTerms = new String[]{"Name", "Date", "Author", "Party"};

    public PolitikaInterface(PolitikaLogic parentLogicInstance){
        JFrame f = new JFrame("Politika");
        ImageIcon img = new ImageIcon("res\\icon.png");
        f.setIconImage(img.getImage());
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
        searchResultsTable.addMouseListener(new ListenerReportButton(this));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.pack();
        f.setMinimumSize(new Dimension(700, 500));
        f.setVisible(true);
        this.log("Politika Started");
        this.log("Loading in Neural Network Specs");
        new Thread(() -> {
            ArrayList<String> nnSpecs = logicInstance.retreiveNNSpecs();
            appendNNSpecs(nnSpecs);
        }).start();
    }

    public void appendNNSpecs(ArrayList<String> nnSpecs){
        nnSpecsArea.append("Vocab Size - \t" + nnSpecs.get(0)+"\n\n");
        nnSpecsArea.append("Number of Layers - \t" + nnSpecs.get(1));
        nnSpecsArea.append("\n\n");
        for(int i=1; i<=Integer.parseInt(nnSpecs.get(1)); i++){
            String[] lc = nnSpecs.get(i+1).split(" ");
            nnSpecsArea.append("Layer " + i + ":\n\t");
            String param1, param2;
            if(i == 1){
                param1 = "Input Size: ";
                param2 = "Output Size: ";
            }else{
                param1 = "Layer Size: ";
                param2 = "Activation Function: ";

            }
            nnSpecsArea.append("Name: " + lc[0] + "\n\t" + param1 + lc[1] + "\n\t" + param2 + lc[2] + "\n\n");
        }
        this.log("Neural Network Configuration Loaded");
        this.log("Details on the Neural Network used in this version of Politika can be found in the Neural Network Configurations Tab");
    }

    public int getAnalysedTextHash(){return analysedTextHash;}

    public void setAnalysedTextHash(String analysedText){analysedTextHash = analysedText.hashCode();}

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

    void clearEncodedTextPanel(){
        encodedTextArea.setText(null);
    }

    void setEncodedTextPanel(ArrayList<String> encodedTextArray) {
        for (int i = 0; i < encodedTextArray.size(); i++) {
            String item = encodedTextArray.get(i);
            encodedTextArea.append(item + "\n");
        }
    }

    void setFinalResult(String finalResult){
        finalResultLabel.setText(finalResult);
    }

    public void setWordsAnalysed(String wordsAnalysed){wordsAnalysedLabel.setText(wordsAnalysed);}

    public void populateTable(ArrayList<ArrayList<String>> results){
        String[] columnNames = {"Article ID", "Article Name", "Author(s)", "Date", "Article Text",
                "Right-Leaning Score", "Left-Leaning Score", "Party"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0){
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        for(ArrayList<String> row : results){
            String[] rowArray = row.toArray(new String[row.size()]);
            model.addRow(rowArray);
        }
        searchResultsTable.setModel(model);
        TableColumnModel tcm = searchResultsTable.getColumnModel();
        tcm.removeColumn(tcm.getColumn(0));
        tcm.removeColumn(tcm.getColumn((3)));

    }

    public void highlightText(Color color, int startIndex, int endIndex){
        Highlighter highlighter = textInputArea.getHighlighter();
        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(color);
        try{highlighter.addHighlight(startIndex, endIndex, painter);}
        catch (BadLocationException e) {e.printStackTrace();}
    }

    public static void main(String args[]){
        PolitikaLogic logicInstance = new PolitikaLogic();
        new Thread(() -> {
            try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
            catch (UnsupportedLookAndFeelException | IllegalAccessException e) {e.printStackTrace();} catch (InstantiationException e) {e.printStackTrace();}
            catch (ClassNotFoundException e) {e.printStackTrace();}
            PolitikaInterface uiInstance = new PolitikaInterface(logicInstance);
        }).start();
    }
}
