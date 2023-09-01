package gui.frames.forms.importexport;

import entities.cells.TableCell;
import enums.FileType;
import files.ImportFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

public class ImportAsForm extends ImportExportAsForm {

    private final AtomicReference<Boolean> deleteCellReference;

    private TableCell tableCell;

    private final JButton headFileButton;

    public ImportAsForm(AtomicReference<Boolean> deleteCellReference) {
        this.setModal(true);

        this.deleteCellReference = deleteCellReference;
        this.tableCell = null;
        this.headFileButton = new JButton("Arquivo head");

        this.initializeGUI();
    }

    private void initializeGUI() {
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                ImportAsForm.this.closeWindow();
            }
        });

        this.headFileButton.addActionListener(this);

        this.setTitle("Importar");

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
