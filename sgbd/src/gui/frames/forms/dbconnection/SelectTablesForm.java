package gui.frames.forms.dbconnection;

import dbconnection.MySQLConnection;
import gui.frames.forms.FormBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class SelectTablesForm extends FormBase implements ActionListener {

    private JList<String> tablesList = new JList<>();
    private final MySQLConnection connection;

    public SelectTablesForm(MySQLConnection connection) throws SQLException {
        super(null);
        setModal(true);
        this.connection = connection;

        initGUI(connection);
    }

    private void initGUI(MySQLConnection connection) throws SQLException {

        setTitle("Escolha as tabelas");
        setSize(400, 200);

        ArrayList<String> tables = connection.getTableNames();
        tablesList = new JList<>(tables.toArray(new String[0]));
        JPanel formFieldsPane = new JPanel(new GridLayout(2, 1));
        JLabel hostLabel = new JLabel("Tabelas:");
        formFieldsPane.add(hostLabel);
        formFieldsPane.add(new JScrollPane(tablesList));
        contentPane.add(formFieldsPane);

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == btnReady) {
            try {
                connection.saveTables(tablesList.getSelectedValuesList());
                dispose();
            } catch (SQLException ignored) {
            }
        }
    }
}
