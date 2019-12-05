package project;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Project extends JFrame {

    // GUI Component dimensions.
    private final int CANVAS_INITIAL_WIDTH = 800;
    private final int CANVAS_INITIAL_HEIGHT = 640;
    private final int CONTROL_PANEL_WIDTH = 200;
    private final int MESSAGE_AREA_HEIGHT = 100;

    JTabbedPane tabbedPane;
    JPanel searchPanel, passagePanel, sentencePanel, clusterPanel, controlPanel;
    JLabel textAreaLabel;
    JTextArea textArea;
    UIManager uiManager;


    public static void main(String[] args) {
        Project projectInstance = new Project();
    }

    public Project(){  // Start Constructor
        Font houseFont;
        setTitle("Political Bias Analyser");
        setLayout(new BorderLayout());
        // Application Icon
        setIconImage(new ImageIcon("res/icon.png").getImage());
        // TODO: Build GUI
        // TODO: Pull Droid Sans Font
        // Control Panel
        controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control Panel"));
        controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, CANVAS_INITIAL_HEIGHT));
        add(controlPanel, BorderLayout.LINE_START);
        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(CANVAS_INITIAL_WIDTH, CANVAS_INITIAL_HEIGHT));
        passagePanel = new JPanel();
        tabbedPane.addTab("Passage Level", passagePanel);
        sentencePanel = new JPanel();
        tabbedPane.addTab("Sentence Level", sentencePanel);
        searchPanel = new JPanel();
        tabbedPane.addTab("Search", searchPanel);
        clusterPanel = new JPanel();
        tabbedPane.addTab("Cluster Analysis", clusterPanel);
        tabbedPane.addChangeListener(new TabbedPaneChangeListener());
        add(tabbedPane);

        //Passage Panel
        textAreaLabel = new JLabel();
        textAreaLabel.setText("Write or Copy in your Text:");
        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(CANVAS_INITIAL_WIDTH-45, CANVAS_INITIAL_HEIGHT-20));
        passagePanel.add(textAreaLabel);
        passagePanel.add(textArea);

        //Sentence Panel
        sentencePanel.add(textAreaLabel);
        sentencePanel.add(textArea);

        // End of GUI Building

        try {
            houseFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res/DroidSans.ttf"));
            //textAreaLabel.setFont(houseFont);
            //UIManager.put(Label.font, houseFont);
        } catch(Exception e){
            ;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }  // End Constructor
}
