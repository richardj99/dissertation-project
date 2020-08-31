package Politika;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ValidationDialog extends JDialog implements ActionListener{

    public ValidationDialog(String Title, String message){
        setTitle(Title);
        JPanel messageP = new JPanel();
        JPanel buttonP = new JPanel();
        messageP.add(new JLabel(message), BorderLayout.CENTER);
        JButton closeDialogButton = new JButton("Close");
        closeDialogButton.addActionListener(this);
        buttonP.add(closeDialogButton, BorderLayout.CENTER);

        add(messageP, BorderLayout.PAGE_START);
        add(buttonP, BorderLayout.PAGE_END);
        setSize(new Dimension(50, 75));
        pack();
        //setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

}
