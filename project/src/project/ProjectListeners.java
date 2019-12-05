package project;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class TabbedPaneChangeListener implements ChangeListener{
    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane source = (JTabbedPane)e.getSource();

    }
}

