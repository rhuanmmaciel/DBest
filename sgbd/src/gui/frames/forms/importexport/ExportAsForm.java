package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.utils.cells.CellUtils;

import enums.FileType;

import files.ExportFile;

public class ExportAsForm extends ImportExportAsForm implements ActionListener {

    private final AtomicReference<Boolean> cancelService;

    private final JButton fyiDatabaseButton;

    private final JButton sqlScriptButton;

    private final Cell cell;

    public ExportAsForm(mxCell jCell, AtomicReference<Boolean> cancelService) {
        this.setModal(true);

        this.cancelService = cancelService;
        this.fyiDatabaseButton = new JButton("Fyi Database");
        this.sqlScriptButton = new JButton("Script SQL");
        this.cell = CellUtils.getActiveCell(jCell).orElse(null);

        this.initGUI();
    }

    private void initGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                ExportAsForm.this.closeWindow();
            }
        });

        JPanel pane = new JPanel(new FlowLayout());

        pane.add(this.fyiDatabaseButton);
        pane.add(this.csvButton);
        pane.add(this.sqlScriptButton);

        this.contentPanel.add(pane, BorderLayout.CENTER);
        this.sqlScriptButton.addActionListener(this);
        this.fyiDatabaseButton.addActionListener(this);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.cancelButton) {
            this.cancelService.set(true);
            this.closeWindow();
        } else if (event.getSource() == this.csvButton) {
            this.closeWindow();

            if (!this.cancelService.get()) {
                new ExportFile(this.cell, FileType.CSV);
            }
        } else if (event.getSource() == this.fyiDatabaseButton) {
            this.closeWindow();

            if (!this.cancelService.get()) {
                new ExportFile(this.cell, FileType.FYI);
            }
        } else if (event.getSource() == this.sqlScriptButton) {
            this.closeWindow();

            if (!this.cancelService.get()) {
                new ExportFile(this.cell, FileType.SQL);
            }
        }
    }

    protected void closeWindow() {
        this.dispose();
    }
}
