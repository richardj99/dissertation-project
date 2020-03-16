package Politika;

import javax.swing.*;
import java.awt.*;

public class ArticleReport {
    private JPanel mainPanel;
    private JTextArea articleTextArea;
    private JPanel articleNamePanel;
    private JPanel authorPanel;
    private JPanel biasPanel;
    private JPanel polPartyPanel;
    private JPanel datePanel;
    private JTextField articleNameField;
    private JTextField authorsField;
    private JTextField partyField;
    private JLabel leftScoreLabel;
    private JLabel rightScoreLabel;
    private JLabel dayLabel;
    private JLabel monthLabel;
    private JLabel yearLabel;
    private JLabel finalResultLabel;
    private JPanel finalResultField;

    public ArticleReport(String articleName, String authors, String articleText, String date, String leftScore,
    String rightScore, String party){
        JFrame f = new JFrame("Article Save");
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setMinimumSize(new Dimension(500, 600));
        f.setVisible(true);


        f.setContentPane(this.mainPanel);
        articleNameField.setText(articleName);
        authorsField.setText(authors);
        articleTextArea.setText(articleText);
        String[] dateArray = date.split("-");
        yearLabel.setText(dateArray[0]);
        monthLabel.setText(dateArray[1]);
        dayLabel.setText(dateArray[2]);
        leftScoreLabel.setText(leftScore);
        rightScoreLabel.setText(rightScore);
        finalResultLabel.setText(calculateScores(leftScore, rightScore));
        partyField.setText(party);
    }

    public String calculateScores(String left, String right){
        double leftScore = Double.parseDouble(left);
        double rightScore = Double.parseDouble(right);
        if((leftScore>0.7) && (rightScore<0.4)) return "Labour";
        else if((rightScore>0.7) && (leftScore<0.4)) return "Conservative";
        else return "Inconclusive";
    }

}
