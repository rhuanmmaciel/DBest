package gui.frames.forms.dbconnection;

import dbconnection.MySQLConnection;
import gui.frames.forms.FormBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigureMySQLConnectionForm extends FormBase implements ActionListener {

    private final JTextField hostField = new JTextField();
    private final JTextField portField = new JTextField("3306");
    private final JTextField databaseField = new JTextField();
    private final JTextField userField = new JTextField("root");
    private final JTextField passwordField = new JPasswordField();

    public ConfigureMySQLConnectionForm() {
        super(null);
        setModal(true);

        initGUI();
    }

    private void initGUI() {

        setTitle("Conectar ao DB - MySQL");
        setSize(400, 180);

        JPanel formFieldsPane = new JPanel(new GridLayout(3, 2, 5, 2));
        JLabel hostLabel = new JLabel("Host:");
        formFieldsPane.add(hostLabel);
        formFieldsPane.add(hostField);
        JLabel portLabel = new JLabel("Port:");
        formFieldsPane.add(portLabel);
        formFieldsPane.add(portField);
        JLabel databaseLabel = new JLabel("Database:");
        formFieldsPane.add(databaseLabel);
        formFieldsPane.add(databaseField);
        JLabel userLabel = new JLabel("User:");
        formFieldsPane.add(userLabel);
        formFieldsPane.add(userField);
        JLabel passwordLabel = new JLabel("Password:");
        formFieldsPane.add(passwordLabel);
        formFieldsPane.add(passwordField);
        contentPane.add(formFieldsPane);
        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnReady) {
            MySQLConnection connection = new MySQLConnection(
                    hostField.getText(),
                    portField.getText(),
                    databaseField.getText(),
                    userField.getText(),
                    passwordField.getText()
            );

            if (connection.isValid()) {
            } else {
                // FIXME: A mensagem é exibida mas fica má posicionada */
                contentPane.add(new JLabel("Dados inválidos!"), BorderLayout.PAGE_START);
                SwingUtilities.updateComponentTreeUI(contentPane);
            }
        }

    }

}
