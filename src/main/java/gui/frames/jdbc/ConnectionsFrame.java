package gui.frames.jdbc;

import controllers.ConstantController;
import controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectionsFrame extends JFrame {
    public ConnectionsFrame() {
        initGUI();
    }

    private void initGUI() {
        setSize(1200, 700);
        setResizable(true);
        setLocationRelativeTo(null);
        setTitle(ConstantController.getString("jdbc.title"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                MainController.connectionsFrame = null;
            }
        });

        ConnectionPanel connectionPanel = new ConnectionPanel();
        ConnectionListPanel leftPanel = new ConnectionListPanel();
        TablesPanel tablesPanel = new TablesPanel();

        connectionPanel.setListPanel(leftPanel);
        leftPanel.setRightPanel(connectionPanel);
        leftPanel.setTablesPanel(tablesPanel);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(leftPanel);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setLeftComponent(connectionPanel);
        rightSplitPane.setRightComponent(tablesPanel);

        mainSplitPane.setRightComponent(rightSplitPane);

        leftPanel.setPreferredSize(new Dimension(300, 0));
        connectionPanel.setPreferredSize(new Dimension(500, 0));
        tablesPanel.setPreferredSize(new Dimension(300, 0));

        mainSplitPane.setDividerLocation(300);
        rightSplitPane.setDividerLocation(500);

        getContentPane().add(mainSplitPane);

        setVisible(true);
    }
}
