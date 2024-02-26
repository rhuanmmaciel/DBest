package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controllers.ConstantController;
import gui.frames.forms.FormBase;

public abstract class ImportExportAsForm extends FormBase implements ActionListener {

    protected JButton btnCsv;

    protected JLabel textLabel;

    protected JPanel centerPanel;

    protected ImportExportAsForm() {
        super(null);

        this.btnCsv = new JButton(ConstantController.getString("importExportAs.csvButton"));
        this.textLabel = new JLabel();
        this.centerPanel = new JPanel();

        this.contentPanel.removeAll();
        this.contentPanel.add(this.centerPanel, BorderLayout.CENTER);

        this.btnCsv.addActionListener(this);

        this.centerPanel.add(this.textLabel);
        this.centerPanel.add(this.btnCsv);

    }

}
