package Politika;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListenerSearchButton implements ActionListener {

    private PolitikaInterface uiInstance;
    private PolitikaLogic logicInstance;


    public ListenerSearchButton(PolitikaInterface i_uiInstance, PolitikaLogic i_logicInstance){
        uiInstance = i_uiInstance;
        logicInstance = i_logicInstance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] searchInfo = uiInstance.getSearchInfo();
        try {
            ArrayList<ArrayList<String>> searchResults = logicInstance.search(searchInfo);
            uiInstance.populateTable(searchResults);
        } catch(SQLException sql_e){
            sql_e.printStackTrace();
        }
    }
}
