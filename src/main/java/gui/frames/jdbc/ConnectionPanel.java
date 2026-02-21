package gui.frames.jdbc;

import controllers.ConstantController;
import database.jdbc.DynamicDriverManager;
import database.jdbc.UniversalConnectionConfig;
import enums.DatabaseType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConnectionPanel extends JPanel {
    private JTextField nameTextField;
    private JComboBox<DatabaseType> databaseTypeComboBox;
    private JTextField hostTextField;
    private JTextField portTextField;
    private JTextField databaseTextField;
    private JComboBox<String> databaseComboBox;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JTextField filePathTextField;
    private JButton browseButton;
    private JButton fetchDatabasesButton;
    private JButton downloadDriverButton;
    private JTextField connectionURLField;
    private JPanel dynamicFieldsPanel;
    private CardLayout dynamicFieldsLayout;

    private ConnectionListPanel listPanel;
    private UniversalConnectionConfig currentConnection;

    private static final String CARD_SERVER_BASED = "server";
    private static final String CARD_FILE_BASED = "file";

    public ConnectionPanel() {
        initGUI();
        setupDatabaseTypeListener();

        DatabaseType defaultType = (DatabaseType) databaseTypeComboBox.getSelectedItem();

        if (defaultType != null) {
            updateUIForDatabaseType(defaultType);
        }
    }

    public void setListPanel(ConnectionListPanel listPanel) {
        this.listPanel = listPanel;
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 5, 20, 5));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel(ConstantController.getString("jdbc.form.name")), gbc);
        gbc.gridy++;
        nameTextField = new JTextField(20);
        fieldsPanel.add(nameTextField, gbc);
        gbc.gridy++;
        fieldsPanel.add(new JLabel(ConstantController.getString("jdbc.form.databaseType")), gbc);
        gbc.gridy++;

        JPanel databaseTypePanel = new JPanel(new BorderLayout(5, 0));
        databaseTypeComboBox = new JComboBox<>(DatabaseType.values());
        databaseTypePanel.add(databaseTypeComboBox, BorderLayout.CENTER);

        downloadDriverButton = new JButton(ConstantController.getString("jdbc.form.downloadDriver"));
        downloadDriverButton.setFont(downloadDriverButton.getFont().deriveFont(11f));
        downloadDriverButton.setPreferredSize(new Dimension(120, 25));
        databaseTypePanel.add(downloadDriverButton, BorderLayout.EAST);

        fieldsPanel.add(databaseTypePanel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.anchor = GridBagConstraints.NORTH;

        dynamicFieldsLayout = new CardLayout();
        dynamicFieldsPanel = new JPanel(dynamicFieldsLayout);
        dynamicFieldsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        createServerBasedPanel();
        createFileBasedPanel();

        fieldsPanel.add(dynamicFieldsPanel, gbc);

        // Connection URL (read-only)
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new JLabel(ConstantController.getString("jdbc.form.connectionURL")), gbc);
        gbc.gridy++;
        connectionURLField = new JTextField(20);
        connectionURLField.setEditable(false);
        connectionURLField.setBackground(Color.LIGHT_GRAY);
        fieldsPanel.add(connectionURLField, gbc);

        add(fieldsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton(ConstantController.getString("jdbc.form.save"));
        JButton testButton = new JButton(ConstantController.getString("jdbc.form.test"));
        JButton deleteButton = new JButton(ConstantController.getString("jdbc.form.delete"));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(saveButton);
        buttonPanel.add(testButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        saveButton.addActionListener(this::saveConnection);
        testButton.addActionListener(this::testConnection);
        deleteButton.addActionListener(this::deleteConnection);
        downloadDriverButton.addActionListener(this::downloadDriver);
        browseButton.addActionListener(this::browseFile);
        fetchDatabasesButton.addActionListener(this::fetchDatabases);
    }

    private void createServerBasedPanel() {
        JPanel serverPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Host
        serverPanel.add(new JLabel(ConstantController.getString("jdbc.form.host")), gbc);
        gbc.gridy++;
        hostTextField = new JTextField(20);
        hostTextField.setText("localhost");
        serverPanel.add(hostTextField, gbc); // Port

        // Port
        gbc.gridy++;
        serverPanel.add(new JLabel(ConstantController.getString("jdbc.form.port")), gbc);
        gbc.gridy++;
        portTextField = new JTextField(20);
        serverPanel.add(portTextField, gbc);

        // Username
        gbc.gridy++;
        serverPanel.add(new JLabel(ConstantController.getString("jdbc.form.username")), gbc);
        gbc.gridy++;
        userTextField = new JTextField(20);
        serverPanel.add(userTextField, gbc);

        // Password
        gbc.gridy++;
        serverPanel.add(new JLabel(ConstantController.getString("jdbc.form.password")), gbc);
        gbc.gridy++;
        passwordField = new JPasswordField(20);
        serverPanel.add(passwordField, gbc);

        gbc.gridy++;
        serverPanel.add(new JLabel(ConstantController.getString("jdbc.form.database")), gbc);
        gbc.gridy++;

        JPanel databasePanel = new JPanel(new BorderLayout(5, 0));
        databaseTextField = new JTextField(20);
        databaseComboBox = new JComboBox<>();
        databaseComboBox.setEditable(true);
        databaseComboBox.setVisible(false);
        fetchDatabasesButton = new JButton("📋");
        fetchDatabasesButton.setFont(fetchDatabasesButton.getFont().deriveFont(11f));
        fetchDatabasesButton.setToolTipText(ConstantController.getString("jdbc.form.fetchDatabases"));
        fetchDatabasesButton.setPreferredSize(new Dimension(30, 25));
        fetchDatabasesButton.setVisible(false);

        databasePanel.add(databaseTextField, BorderLayout.CENTER);
        databasePanel.add(fetchDatabasesButton, BorderLayout.EAST);
        serverPanel.add(databasePanel, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        serverPanel.add(new JPanel(), gbc);

        dynamicFieldsPanel.add(serverPanel, CARD_SERVER_BASED);
    }

    private void createFileBasedPanel() {
        JPanel filePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        filePanel.add(new JLabel(ConstantController.getString("jdbc.form.databaseFile")), gbc);
        gbc.gridy++;

        JPanel filePathPanel = new JPanel(new BorderLayout(5, 0));
        filePathTextField = new JTextField(20);
        browseButton = new JButton(ConstantController.getString("jdbc.form.browse"));

        filePathPanel.add(filePathTextField, BorderLayout.CENTER);
        filePathPanel.add(browseButton, BorderLayout.EAST);
        filePanel.add(filePathPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        filePanel.add(new JPanel(), gbc);

        dynamicFieldsPanel.add(filePanel, CARD_FILE_BASED);
    }

    private void setupDatabaseTypeListener() {
        databaseTypeComboBox.addActionListener(e -> {
            DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
            if (selectedType != null) {
                updateUIForDatabaseType(selectedType);
                updateConnectionURL();
            }
        });

        nameTextField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateConnectionURL));
        hostTextField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateConnectionURL));
        portTextField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateConnectionURL));
        databaseTextField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateConnectionURL));
        filePathTextField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateConnectionURL));
        userTextField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateConnectionURL));

        databaseComboBox.addActionListener(e -> updateConnectionURL());
    }

    private void updateUIForDatabaseType(DatabaseType databaseType) {
        updateDriverStatus(databaseType);

        portTextField.setText(String.valueOf(databaseType.getDefaultPort()));

        if (databaseType.isFileBased()) {
            dynamicFieldsLayout.show(dynamicFieldsPanel, CARD_FILE_BASED);
        } else {
            dynamicFieldsLayout.show(dynamicFieldsPanel, CARD_SERVER_BASED);
        }

        fetchDatabasesButton.setVisible(databaseType.supportsMultipleDatabases());

        resetDatabaseFieldToTextMode();

        revalidate();
        repaint();
    }

    private void updateDriverStatus(DatabaseType databaseType) {
        boolean driverAvailable = DynamicDriverManager.isDriverAvailable(databaseType);

        if (driverAvailable) {
            downloadDriverButton.setText(ConstantController.getString("jdbc.form.driverInstalled"));
            downloadDriverButton.setEnabled(false);
            downloadDriverButton.setBackground(new Color(230, 255, 230));
            downloadDriverButton.setForeground(new Color(0, 128, 0));
        } else {
            downloadDriverButton.setText(ConstantController.getString("jdbc.form.downloadDriver"));
            downloadDriverButton.setEnabled(true);
            downloadDriverButton.setBackground(null);
            downloadDriverButton.setForeground(null);
        }
    }

    private void resetDatabaseFieldToTextMode() {
        if (databaseComboBox.isVisible()) {
            JPanel parent = (JPanel) databaseComboBox.getParent();
            parent.remove(databaseComboBox);
            parent.add(databaseTextField, BorderLayout.CENTER);
            databaseTextField.setVisible(true);
            databaseComboBox.setVisible(false);

            databaseTextField.setText("");

            parent.revalidate();
            parent.repaint();
        }
    }

    private void updateConnectionURL() {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
                if (selectedType == null)
                    return;

                String url;
                if (selectedType.isFileBased()) {
                    url = selectedType.getUrlTemplate().replace("{file}", filePathTextField.getText());
                } else {
                    url = selectedType.getUrlTemplate();
                    url = url.replace("{host}", hostTextField.getText());
                    url = url.replace("{port}", portTextField.getText());

                    String database = databaseComboBox.isVisible() && databaseComboBox.getSelectedItem() != null
                            ? databaseComboBox.getSelectedItem().toString()
                            : databaseTextField.getText();
                    url = url.replace("{database}", database);
                }

                connectionURLField.setText(url);
            } catch (Exception e) {
            }
        });
    }

    private void saveConnection(ActionEvent e) {
        if (!validateInput()) {
            return;
        }

        if (currentConnection != null) {
            currentConnection.delete();
        }

        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();

        UniversalConnectionConfig newConnection;
        if (selectedType.isFileBased()) {
            newConnection = new UniversalConnectionConfig(
                    nameTextField.getText().trim(),
                    selectedType,
                    filePathTextField.getText().trim());
        } else {
            Integer port = null;
            try {
                port = Integer.parseInt(portTextField.getText().trim());
            } catch (NumberFormatException ex) {
                port = selectedType.getDefaultPort();
            }

            String database = databaseComboBox.isVisible() && databaseComboBox.getSelectedItem() != null
                    ? databaseComboBox.getSelectedItem().toString()
                    : databaseTextField.getText().trim();

            newConnection = new UniversalConnectionConfig(
                    nameTextField.getText().trim(),
                    selectedType,
                    hostTextField.getText().trim(),
                    port,
                    database,
                    userTextField.getText().trim(),
                    new String(passwordField.getPassword()));
        }

        newConnection.save();
        listPanel.updateConnectionList();
        listPanel.setCurrentConnection(newConnection);
        displayConnectionDetails(newConnection);

        boolean connectionIsValid = newConnection.test();
        displayTestResult(connectionIsValid);
    }

    private void testConnection(ActionEvent e) {
        if (!validateInputForTest())
            return;

        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();

        UniversalConnectionConfig tempConnection;
        if (selectedType.isFileBased()) {
            tempConnection = new UniversalConnectionConfig(
                    "test",
                    selectedType,
                    filePathTextField.getText().trim());
        } else {
            Integer port = null;
            try {
                port = Integer.parseInt(portTextField.getText().trim());
            } catch (NumberFormatException ex) {
                port = selectedType.getDefaultPort();
            }

            String database = databaseComboBox.isVisible() && databaseComboBox.getSelectedItem() != null
                    ? databaseComboBox.getSelectedItem().toString()
                    : databaseTextField.getText().trim();

            tempConnection = new UniversalConnectionConfig(
                    "test",
                    selectedType,
                    hostTextField.getText().trim(),
                    port,
                    database,
                    userTextField.getText().trim(),
                    new String(passwordField.getPassword()));
        }

        boolean connectionIsValid = tempConnection.test();
        displayTestResult(connectionIsValid);
    }

    private boolean validateInputForTest() {
        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
        if (selectedType == null) {
            JOptionPane.showMessageDialog(this,
                    ConstantController.getString("jdbc.form.validation.selectDatabaseType"));
            return false;
        }

        if (!DynamicDriverManager.isDriverAvailable(selectedType)) {
            JOptionPane.showMessageDialog(this,
                    ConstantController.getString("jdbc.form.validation.driverNotAvailable"));
            return false;
        }

        if (selectedType.isFileBased()) {
            if (filePathTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        ConstantController.getString("jdbc.form.validation.selectDatabaseFile"));
                return false;
            }
        } else {
            if (hostTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        ConstantController.getString("jdbc.form.validation.enterHost"));
                return false;
            }
        }

        return true;
    }

    private void deleteConnection(ActionEvent e) {
        if (currentConnection != null) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    ConstantController.getString("jdbc.form.delete.message"),
                    ConstantController.getString("jdbc.form.delete.title"),
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                currentConnection.delete();
                listPanel.updateConnectionList();
                clearForm();
            }
        }
    }

    private void downloadDriver(ActionEvent e) {
        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
        if (selectedType == null)
            return;

        DriverDownloadDialog dialog = new DriverDownloadDialog(
                SwingUtilities.getWindowAncestor(this), selectedType);

        DynamicDriverManager.DriverDownloadListener listener = new DynamicDriverManager.DriverDownloadListener() {
            @Override
            public void onDownloadStarted(DatabaseType databaseType) {
                dialog.setStatus(ConstantController.getString("jdbc.driver.download.starting"));
            }

            @Override
            public void onDownloadProgress(DatabaseType databaseType, int progress) {
                dialog.updateProgress(progress);
            }

            @Override
            public void onDownloadCompleted(DatabaseType databaseType, boolean success) {
                if (success) {
                    dialog.setStatus(ConstantController.getString("jdbc.driver.download.loading"));
                } else {
                    dialog.setCompleted(false);
                }
            }

            @Override
            public void onDriverLoaded(DatabaseType databaseType, boolean success) {
                dialog.setCompleted(success);
                SwingUtilities.invokeLater(() -> {
                    updateDriverStatus(databaseType);
                    revalidate();
                });
            }
        };

        DynamicDriverManager.addListener(listener);

        CompletableFuture<Boolean> downloadFuture = DynamicDriverManager.downloadAndLoadDriver(selectedType);

        dialog.setCancelAction(() -> {
            downloadFuture.cancel(true);
            DynamicDriverManager.removeListener(listener);
        });

        dialog.setVisible(true);

        DynamicDriverManager.removeListener(listener);
    }

    private void browseFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
        if (selectedType == DatabaseType.SQLITE) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "SQLite Database Files (*.db, *.sqlite, *.sqlite3)", "db", "sqlite", "sqlite3"));
        }

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void fetchDatabases(ActionEvent e) {
        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
        if (selectedType == null || !selectedType.supportsMultipleDatabases())
            return; // Validate connection parameters first
        if (hostTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, ConstantController.getString("jdbc.form.validation.enterHost"));
            return;
        }

        if (!DynamicDriverManager.isDriverAvailable(selectedType)) {
            JOptionPane.showMessageDialog(this,
                    ConstantController.getString("jdbc.form.validation.driverNotAvailable"));
            return;
        }

        // Show progress
        fetchDatabasesButton.setEnabled(false);
        fetchDatabasesButton.setText("⏳");

        // Use SwingWorker for background processing
        SwingUtilities.invokeLater(() -> {
            try {
                Integer port = null;

                try {
                    port = Integer.parseInt(portTextField.getText().trim());
                } catch (NumberFormatException ex) {
                    port = selectedType.getDefaultPort();
                }

                String defaultDb = getDefaultDatabaseForConnection(selectedType);

                UniversalConnectionConfig tempConnection = new UniversalConnectionConfig(
                        "temp", selectedType,
                        hostTextField.getText().trim(),
                        port,
                        defaultDb,
                        userTextField.getText().trim(),
                        new String(passwordField.getPassword()));

                List<String> databases = tempConnection.getDatabaseNames();

                if (!databases.isEmpty()) {
                    switchToDatabaseDropdown(databases);
                } else {
                    JOptionPane.showMessageDialog(this,
                            ConstantController.getString("jdbc.form.validation.noDatabasesFound"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ConstantController
                        .getString("jdbc.form.validation.fetchDatabasesError").replace("{0}", ex.getMessage()));
            } finally {
                fetchDatabasesButton.setEnabled(true);
                fetchDatabasesButton.setText("📋");
            }
        });
    }

    private String getDefaultDatabaseForConnection(DatabaseType databaseType) {
        switch (databaseType) {
            case POSTGRESQL:
                return "postgres";
            case MYSQL:
            case MARIADB:
                return "mysql";
            case ORACLE:
                return "XE";
            default:
                return "";
        }
    }

    private void switchToDatabaseDropdown(List<String> databases) {
        // Only switch if not already in dropdown mode
        if (!databaseComboBox.isVisible()) {
            JPanel parent = (JPanel) databaseTextField.getParent();
            parent.remove(databaseTextField);

            databaseComboBox.removeAllItems();
            for (String db : databases) {
                databaseComboBox.addItem(db);
            }

            parent.add(databaseComboBox, BorderLayout.CENTER);
            databaseComboBox.setVisible(true);
            databaseTextField.setVisible(false);

            parent.revalidate();
            parent.repaint();
        } else {
            // Just update the existing dropdown
            databaseComboBox.removeAllItems();
            for (String db : databases) {
                databaseComboBox.addItem(db);
            }
        }
    }

    public void displayConnectionDetails(UniversalConnectionConfig connection) {
        currentConnection = connection;

        if (connection != null) {
            nameTextField.setText(connection.getName());
            databaseTypeComboBox.setSelectedItem(connection.getDatabaseType());

            if (connection.getDatabaseType().isFileBased()) {
                filePathTextField.setText(connection.getFilePath() != null ? connection.getFilePath() : "");
            } else {
                hostTextField.setText(connection.getHost() != null ? connection.getHost() : "");
                portTextField.setText(connection.getPort() != null ? connection.getPort().toString() : "");

                // Set database field (ensure text mode first)
                resetDatabaseFieldToTextMode();
                databaseTextField.setText(connection.getDatabase() != null ? connection.getDatabase() : "");

                userTextField.setText(connection.getUsername() != null ? connection.getUsername() : "");
                passwordField.setText(connection.getPassword() != null ? connection.getPassword() : "");
            }

            updateUIForDatabaseType(connection.getDatabaseType());
            updateConnectionURL();
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        currentConnection = null;
        nameTextField.setText("");
        hostTextField.setText("localhost");
        portTextField.setText("");

        // Reset database field to text mode
        resetDatabaseFieldToTextMode();
        databaseTextField.setText("");

        userTextField.setText("");
        passwordField.setText("");
        filePathTextField.setText("");
        connectionURLField.setText("");

        // Reset to first database type
        databaseTypeComboBox.setSelectedIndex(0);
    }

    private boolean validateInput() {
        if (nameTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, ConstantController.getString("jdbc.form.validation.enterName"));
            return false;
        }

        DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
        if (selectedType == null) {
            JOptionPane.showMessageDialog(this,
                    ConstantController.getString("jdbc.form.validation.selectDatabaseType"));
            return false;
        }

        if (!DynamicDriverManager.isDriverAvailable(selectedType)) {
            JOptionPane.showMessageDialog(this,
                    ConstantController.getString("jdbc.form.validation.driverNotAvailable"));
            return false;
        }

        if (selectedType.isFileBased()) {
            if (filePathTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        ConstantController.getString("jdbc.form.validation.selectDatabaseFile"));

                return false;
            }
        } else {
            if (hostTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        ConstantController.getString("jdbc.form.validation.enterHost"));

                return false;
            }
        }

        return true;
    }

    private void displayTestResult(boolean testResult) {
        String dialogTitle = ConstantController.getString("jdbc.form.test.title");

        if (testResult) {
            JOptionPane.showMessageDialog(
                    this,
                    ConstantController.getString("jdbc.form.test.success"),
                    dialogTitle,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    ConstantController.getString("jdbc.form.test.failed"),
                    dialogTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Simple document listener helper
    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable action;

        public SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            action.run();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            action.run();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            action.run();
        }
    }
}
