package gui.frames.jdbc;

import controllers.ConstantController;
import enums.DatabaseType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DriverDownloadDialog extends JDialog {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JButton cancelButton;
    private Runnable cancelAction;
    private boolean completed = false;

    public DriverDownloadDialog(Window parent, DatabaseType databaseType) {
        super(parent, ConstantController.getString("jdbc.driver.download.title"), ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents(databaseType);
        setupLayout();
        setupEventHandlers();
    }

    private void initComponents(DatabaseType databaseType) {
        statusLabel = new JLabel(ConstantController.getString("jdbc.driver.download.preparing").replace("{0}",
                databaseType.getDisplayName()));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");

        cancelButton = new JButton(ConstantController.getString("jdbc.driver.download.cancel"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        contentPanel.add(statusLabel, BorderLayout.NORTH);
        contentPanel.add(progressBar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(cancelButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!completed) {
                    if (cancelAction != null) {
                        cancelAction.run();
                    }
                    dispose();
                } else {
                    dispose();
                }
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (!completed && cancelAction != null) {
                    cancelAction.run();
                }
                dispose();
            }
        });
    }

    public void updateProgress(int progress) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            progressBar.setString(progress + "%");
        });
    }

    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }

    public void setCompleted(boolean success) {
        SwingUtilities.invokeLater(() -> {
            completed = true;
            if (success) {
                progressBar.setValue(100);
                progressBar.setString("100%");
                statusLabel.setText(ConstantController.getString("jdbc.driver.download.success"));
                cancelButton.setText(ConstantController.getString("jdbc.driver.download.close"));

                Timer timer = new Timer(2000, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setText(ConstantController.getString("jdbc.driver.download.failed"));
                cancelButton.setText(ConstantController.getString("jdbc.driver.download.close"));
            }
        });
    }

    public void setCancelAction(Runnable cancelAction) {
        this.cancelAction = cancelAction;
    }
}
