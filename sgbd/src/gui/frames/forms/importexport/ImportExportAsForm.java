package gui.frames.forms.importexport;

import gui.frames.forms.FormBase;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ImportExportAsForm extends FormBase implements ActionListener {

    protected JButton csvButton;

    protected JLabel textLabel;

    protected JPanel centerPanel;

    public ImportExportAsForm() {
        super(null);

        this.csvButton = new JButton("Arquivo CSV");
        this.textLabel = new JLabel();
        this.centerPanel = new JPanel();

        this.initializeGUI();
    }

    private void initializeGUI() {
        this.contentPanel.removeAll();
        this.contentPanel.add(this.centerPanel, BorderLayout.CENTER);

        this.csvButton.addActionListener(this);

        this.centerPanel.add(this.textLabel);
        this.centerPanel.add(this.csvButton);
    }
}
