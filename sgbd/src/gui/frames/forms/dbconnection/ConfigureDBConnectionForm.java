package gui.frames.forms.dbconnection;

import enums.DBConnectionType;
import gui.frames.forms.FormBase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigureDBConnectionForm extends FormBase implements ActionListener {

    private final JComboBox<DBConnectionType> dbOptionsSelect = new JComboBox<>(DBConnectionType.values());
    public ConfigureDBConnectionForm() {
        super(null);
        setModal(true);

        initGUI();
    }

    private void initGUI() {

        setTitle("Conectar ao DB");
        setSize(200, 100);

        contentPane.add(dbOptionsSelect);
        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        dispose();
        if (actionEvent.getSource() == btnReady) {
            switch ((DBConnectionType)dbOptionsSelect.getSelectedItem()) {
                case MYSQL -> new ConfigureMySQLConnectionForm();
            }
        }
    }

}
