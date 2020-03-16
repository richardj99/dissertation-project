package Politika;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ListenerReportButton implements MouseListener {
    PolitikaInterface uiInstance;


    public ListenerReportButton(PolitikaInterface i_uiInstance){
        uiInstance = i_uiInstance;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        Point point = e.getPoint();
        int row = table.rowAtPoint(point);
        if(e.getClickCount() == 2 && table.getSelectedRow() != -1){
            String articleID = (String) table.getModel().getValueAt(row, 0);
            String articleName = (String) table.getValueAt(row, 0);
            String authors = (String) table.getValueAt(row, 1);
            String articleText = (String) table.getModel().getValueAt(row, 4);
            String articleDate = (String) table.getValueAt(row, 2);
            String rightScore = (String) table.getValueAt(row, 3);
            String leftScore = (String) table.getValueAt(row, 4);
            String Party = (String) table.getValueAt(row, 5);
            ArticleReport reportInstance = new ArticleReport(articleName, authors, articleText, articleDate,
                    rightScore, leftScore, Party);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
