package gui.frames.forms.importexport;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;

import controllers.ConstantController;

import entities.cells.TableCell;

import enums.FileType;

import files.ImportFile;

public class ImportAsForm extends ImportExportAsForm {

    private final AtomicReference<Boolean> deleteCellReference;

    private TableCell tableCell;

    private final JButton headFileButton;

    public ImportAsForm(AtomicReference<Boolean> deleteCellReference) {
        this.setModal(true);

        this.deleteCellReference = deleteCellReference;
        this.tableCell = null;
        this.headFileButton = new JButton(ConstantController.getString("importAs.headerButton"));

        this.initGUI2();
    }

    public void initGUI2() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                ImportAsForm.this.closeWindow();
            }
        });

        this.headFileButton.addActionListener(this);

        this.setTitle(ConstantController.getString("importAs.importTitle"));

        this.centerPanel.add(this.headFileButton);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.revalidate();
    }

    public TableCell getResult() {
        return this.tableCell;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.csvButton) {
            this.dispose();
            this.tableCell = new ImportFile(FileType.CSV, this.deleteCellReference).getResult();
        } else if (event.getSource() == this.headFileButton) {
            this.dispose();
            this.tableCell = new ImportFile(FileType.FYI, this.deleteCellReference).getResult();
        }
    }

    protected void closeWindow() {
        this.deleteCellReference.set(true);
        this.dispose();
    }
}
