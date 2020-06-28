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
    private JLabel rightScoreLabel;
    private JLabel leftScoreLabel;
    private JLabel dayLabel;
    private JLabel monthLabel;
    private JLabel yearLabel;
    private JLabel finalResultLabel;
    private JPanel finalResultField;

    public ArticleReport(String articleName, String authors, String articleText, String date, String rightScore,
    String leftScore, String party){
        JFrame f = new JFrame("Politika: Article Report");
        ImageIcon img = new ImageIcon("res\\icon.png");
        f.setIconImage(img.getImage());
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
        rightScoreLabel.setText(rightScore);
        leftScoreLabel.setText(leftScore);
        finalResultLabel.setText(calculateScores(leftScore, rightScore));
        partyField.setText(party);
    }

    public String calculateScores(String left, String right){
        double leftScore = Double.parseDouble(left);
        double rightScore = Double.parseDouble(right);
        if((leftScore>0.7) && (rightScore<0.4)) return "Left-Leaning";
        else if((rightScore>0.7) && (leftScore<0.4)) return "Right-Leaning";
        else return "Inconclusive";
    }

}
