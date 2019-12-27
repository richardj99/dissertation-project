import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.awt.*;

public class Project extends JFrame {

    //static Project projectInstance;

    // GUI Component dimensions.
    private final int MAIN_INITIAL_WIDTH = 800;
    private final int MAIN_INITIAL_HEIGHT = 640;
    private final int CONTROL_PANEL_WIDTH = 200;
    private final int MESSAGE_AREA_HEIGHT = 100;

    JTabbedPane tabbedPane;
    JPanel searchPanel, passagePanel, sentencePanel, clusterPanel;
    JPanel searchBoxPanel, searchTablePanel;
    JPanel controlPanel, partiesPanel, resultPanel;
    JLabel textAreaLabel, party1Label, party2Label, party3Label, party4Label,
            party1Value, party2Value, party3Value, party4Value, resultsLabel;
    JTextArea textArea;
    JTextField searchBoxTextField;
    JScrollPane textAreaScrollPane;
    JButton analyseButton;


    public static void main(String[] args) {
        Project projectInstance = new Project();
    }

    public void reconfigureTabs(int index){
        switch(index){
            case 0: passagePanel.add(textAreaLabel); passagePanel.add(textAreaScrollPane); break;
            case 1: sentencePanel.add(textAreaLabel); sentencePanel.add(textAreaScrollPane); break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    public Project(){  // Start Constructor
        setTitle("Political Bias Analyser");
        setLayout(new BorderLayout());

        // Application Icon
        setIconImage(new ImageIcon("res/icon.png").getImage());
        // TODO: Build GUI
        // TODO: Pull Droid Sans Font

        // Control Panel
        controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control Panel"));
        controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, MAIN_INITIAL_HEIGHT));

        partiesPanel = new JPanel();
        partiesPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH-15, (MAIN_INITIAL_HEIGHT)/3));
        partiesPanel.setLayout(new GridLayout(0, 1));
        partiesPanel.setBorder(new TitledBorder(new EtchedBorder(), "Party Activations"));
        party1Label = new JLabel("Party 1", SwingConstants.CENTER);
        party1Value = new JLabel("0.00", SwingConstants.CENTER);
        partiesPanel.add(party1Label);
        partiesPanel.add(party1Value);
        party2Label = new JLabel("Party 2", SwingConstants.CENTER);
        party2Value = new JLabel("0.00", SwingConstants.CENTER);
        partiesPanel.add(party2Label);
        partiesPanel.add(party2Value);
        party3Label = new JLabel("Party 3", SwingConstants.CENTER);
        party3Value = new JLabel("0.00", SwingConstants.CENTER);
        partiesPanel.add(party3Label);
        partiesPanel.add(party3Value);
        party4Label = new JLabel("Party 4", SwingConstants.CENTER);
        party4Value = new JLabel("0.00", SwingConstants.CENTER);
        partiesPanel.add(party4Label);
        partiesPanel.add(party4Value);
        controlPanel.add(partiesPanel);

        resultPanel = new JPanel();
        resultPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH-15, (MAIN_INITIAL_HEIGHT)/3));
        resultPanel.setLayout(new GridLayout(0, 1));
        resultPanel.setBorder(new TitledBorder(new EtchedBorder(), "Results"));
        resultsLabel = new JLabel("Activated Party", SwingConstants.CENTER);
        resultPanel.add(resultsLabel);
        controlPanel.add(resultPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new TitledBorder(new EtchedBorder(), "Analyse Text"));
        buttonPanel.setLayout(new GridLayout(0,1));
        buttonPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH-10, 60));
        analyseButton = new JButton("Analyse");
        buttonPanel.add(analyseButton);
        controlPanel.add(buttonPanel);

        add(controlPanel, BorderLayout.LINE_START);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(MAIN_INITIAL_WIDTH, MAIN_INITIAL_HEIGHT));
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
        //Passage and Sentence Panel
        textAreaLabel = new JLabel();
        textAreaLabel.setText("Write or Copy in your Text:");
        textArea = new JTextArea();
        textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setPreferredSize(new Dimension(MAIN_INITIAL_WIDTH - 60, MAIN_INITIAL_HEIGHT -60));
        textAreaScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Text Input"));
        passagePanel.add(textAreaLabel, BorderLayout.LINE_START);
        passagePanel.add(textAreaScrollPane);
        //Search Panel
        searchBoxPanel = new JPanel();
        searchBoxPanel.setPreferredSize(new Dimension(MAIN_INITIAL_WIDTH-30, 55));
        searchBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Search"));
        searchBoxTextField = new JTextField();
        searchBoxTextField.setPreferredSize(new Dimension(MAIN_INITIAL_WIDTH-40, 25));
        searchBoxPanel.add(searchBoxTextField);
        searchPanel.add(searchBoxPanel);
        JScrollPane searchTableScrollPane = new JScrollPane();
        // End of GUI Building

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);

    }  // End Constructor

    // Start of Listener Classes

    // TabbedPane Change Listener START
    class TabbedPaneChangeListener implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent e) {
            JTabbedPane source = (JTabbedPane)e.getSource();
            reconfigureTabs(source.getSelectedIndex());
        }
    }  // TabbedPane Change Listener END

    // End of Listener Classes
}
