package gui.frames.jdbc;

import database.jdbc.UniversalConnectionConfig;
import enums.DatabaseType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controllers.ConstantController;

import java.awt.*;
import java.util.List;

public class ConnectionListPanel extends JPanel {
    private static UniversalConnectionConfig currentConnection;
    private JList<UniversalConnectionConfig> connectionList;
    private DefaultListModel<UniversalConnectionConfig> connectionListModel;
    private ConnectionPanel rightPanel;
    private TablesPanel tablesPanel;
    private final JLabel titleLabel;

    public ConnectionListPanel() {
        titleLabel = new JLabel(ConstantController.getString("jdbc.connections.title"));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        initGui();
    }

    public void setRightPanel(ConnectionPanel rightPanel) {
        this.rightPanel = rightPanel;
    }

    public void setTablesPanel(TablesPanel tablesPanel) {
        this.tablesPanel = tablesPanel;
    }

    private void initGui() {
        setLayout(new BorderLayout());
        connectionListModel = new DefaultListModel<>();
        connectionList = new JList<>(connectionListModel);
        connectionList.setCellRenderer(new ConnectionListCellRenderer());
        updateConnectionList();
        setBorder(new EmptyBorder(20, 10, 20, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JScrollPane configuredConnectionsList = new JScrollPane(connectionList);
        add(configuredConnectionsList, BorderLayout.CENTER);

        JButton newButton = new JButton(ConstantController.getString("jdbc.connections.new"));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(newButton);
        add(buttonPanel, BorderLayout.SOUTH);

        newButton.addActionListener(e -> {
            rightPanel.displayConnectionDetails(null);
            connectionList.clearSelection();
            updateConnectionList();
        });

        connectionList.addListSelectionListener(e -> {
            if (tablesPanel != null) {
                tablesPanel.setVisible(true);
            }

            if (!e.getValueIsAdjusting()) {
                int selectedIndex = connectionList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentConnection = connectionListModel.getElementAt(selectedIndex);
                    rightPanel.displayConnectionDetails(currentConnection);
                    if (currentConnection.test()) {
                        if (tablesPanel != null) {
                            tablesPanel.updateTables(currentConnection);
                        }
                    } else {
                        if (tablesPanel != null) {
                            tablesPanel.clear();
                        }
                    }
                }
            }
        });
    }

    public static UniversalConnectionConfig getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(UniversalConnectionConfig connection) {
        currentConnection = connection;
        if (tablesPanel != null) {
            tablesPanel.setVisible(true);
            if (connection != null && connection.test()) {
                tablesPanel.updateTables(connection);
            } else {
                tablesPanel.clear();
            }
        }
    }

    public void updateConnectionList() {
        connectionListModel.clear();
        List<UniversalConnectionConfig> configuredConnections = UniversalConnectionConfig.getAllConfiguredConnections();
        for (UniversalConnectionConfig connection : configuredConnections) {
            connectionListModel.addElement(connection);
        }
        connectionList.revalidate();
        if (tablesPanel != null) {
            tablesPanel.updateTables(null);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int width = getParent() != null ? getParent().getWidth() / 4 : 300;
        return new Dimension(width, super.getPreferredSize().height);
    }


    private static class ConnectionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof UniversalConnectionConfig config) {
                DatabaseType type = config.getDatabaseType();
                setText(type.getDisplayIcon() + " " + config.getName() + " (" + type.getDisplayName() + ")");
            }

            return this;
        }
    }
}
