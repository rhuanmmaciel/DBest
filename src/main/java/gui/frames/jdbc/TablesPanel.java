package gui.frames.jdbc;

import controllers.ConstantController;
import controllers.MainController;
import database.TableCreator;
import database.jdbc.UniversalConnectionConfig;
import entities.cells.TableCell;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TablesPanel extends JPanel {
    private final JList<String> tableList;
    private final DefaultListModel<String> tableListModel;
    private final JButton importButton;
    private final JLabel titleLabel;

    public TablesPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 10, 20, 10));

        tableListModel = new DefaultListModel<>();
        tableList = new JList<>(tableListModel);
        JScrollPane scrollPane = new JScrollPane(tableList);
        titleLabel = new JLabel(ConstantController.getString("jdbc.tables.title"));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        importButton = new JButton(ConstantController.getString("jdbc.tables.import"));
        importButton.setEnabled(false);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(importButton);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        importButton.addActionListener(e -> importSelectedTables());
    }

    public void updateTables(UniversalConnectionConfig connection) {
        clear();
        if (connection != null) {
            try {
                List<String> tables = connection.getTableNames();

                if (tables != null && !tables.isEmpty()) {
                    for (String table : tables) {
                        tableListModel.addElement(table);
                    }
                    importButton.setEnabled(true);
                    titleLabel.setText(ConstantController.getString("jdbc.tables.inDatabase").replace("{0}",
                            connection.getName()));
                } else {
                    importButton.setEnabled(false);
                    titleLabel.setText(ConstantController.getString("jdbc.tables.noTablesFound"));
                }
            } catch (Exception e) {
                importButton.setEnabled(false);
                titleLabel.setText(ConstantController.getString("jdbc.tables.errorLoading"));
                JOptionPane.showMessageDialog(this,
                        ConstantController.getString("jdbc.tables.errorLoadingMessage").replace("{0}",
                                e.getMessage()),
                        ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importSelectedTables() {
        List<String> selectedTables = tableList.getSelectedValuesList();
        if (!selectedTables.isEmpty() && ConnectionListPanel.getCurrentConnection() != null) {
            try {
                for (String selectedTable : selectedTables) {
                    TableCell tableCell = TableCreator.createJDBCTable(
                            selectedTable,
                            ConnectionListPanel.getCurrentConnection(),
                            true);
                    String newName = MainController.resolveTableNameConflict(tableCell.getName());
                    tableCell.setName(newName);
                    MainController.saveTable(tableCell);
                }
                JOptionPane.showMessageDialog(this,
                        ConstantController.getString("jdbc.tables.importSuccess"),
                        ConstantController.getString("jdbc.tables.importSuccessTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        ConstantController.getString("jdbc.tables.importError").replace("{0}", e.getMessage()),
                        ConstantController.getString("jdbc.tables.importErrorTitle"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void clear() {
        tableListModel.clear();
        importButton.setEnabled(false);
        titleLabel.setText(ConstantController.getString("jdbc.tables.title"));
    }
}
