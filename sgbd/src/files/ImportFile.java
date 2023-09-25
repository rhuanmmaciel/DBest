package files;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import controllers.MainController;
import database.TableCreator;
import entities.Column;
import entities.cells.FYITableCell;
import entities.cells.TableCell;
import enums.FileType;
import files.csv.CSVInfo;
import gui.frames.forms.importexport.CSVRecognizerForm;
import gui.frames.main.MainFrame;
import sgbd.source.table.Table;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ImportFile {

    private final JFileChooser fileUpload = new JFileChooser();

    private AtomicReference<Boolean> exitReference;

    private final StringBuilder tableName = new StringBuilder();

    private final Map<Integer, Map<String, String>> content = new LinkedHashMap<>();

    private final List<Column> columns = new ArrayList<>();

    private FileType fileType;

    private TableCell tableCell = null;

    public ImportFile(FileType fileType, AtomicReference<Boolean> exitReference) {
        this.exitReference = exitReference;
        this.fileType = fileType;
        this.fileUpload.setCurrentDirectory(MainController.getLastDirectory());

        this.importFile();
    }

    private void importFile() {
        FileNameExtensionFilter filter;

        switch (this.fileType) {
            case CSV -> filter = new FileNameExtensionFilter("CSV files", "csv");
            case EXCEL -> filter = new FileNameExtensionFilter("Sheets files", "xlsx", "xls", "ods");
            case FYI -> filter = new FileNameExtensionFilter("Headers files", "head");
            case SQL -> throw new UnsupportedOperationException(String.format("Unimplemented case: %s", this.fileType));
            default -> throw new IllegalArgumentException(String.format("Unexpected value: %s", this.fileType));
        }

        this.fileUpload.setFileFilter(filter);

        if (this.fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            MainController.setLastDirectory(new File(this.fileUpload.getCurrentDirectory().getAbsolutePath()));

            switch (this.fileType) {
                case CSV -> {
                    CSVInfo info = this.csv();

                    if (!this.exitReference.get()) {
                        TableCreator tableCreator = new TableCreator(
                            this.tableName.toString(), this.columns, info, false
                        );

                        this.tableCell = tableCreator.getTableCell();
                    }
                }
                case EXCEL -> this.excel();
                case FYI -> {
                    AtomicReference<Table> table = new AtomicReference<>();
                    this.header(table);

                    if (!this.exitReference.get()) {
                        String selectedFileName = this.fileUpload.getSelectedFile().getName();

                        String fileName = selectedFileName.endsWith(FileType.HEADER.EXTENSION) ?
                            selectedFileName.substring(0, selectedFileName.indexOf(".")) :
                            selectedFileName;

                        mxCell jCell = (mxCell) MainFrame
                            .getGraph()
                            .insertVertex(
                                MainFrame.getGraph().getDefaultParent(), null,
                                fileName, 0, 0, 80, 30, FileType.FYI.ID
                            );

                        table.get().open();

                        this.tableCell = new FYITableCell(jCell, fileName, table.get(), this.fileUpload.getSelectedFile());
                    }
                }
                case SQL -> throw new UnsupportedOperationException(String.format("Unimplemented case: %s", this.fileType));
                default -> throw new IllegalArgumentException(String.format("Unexpected value: %s", this.fileType));
            }
        } else {
            this.exitReference.set(true);
        }
    }

    private void header(AtomicReference<Table> table) {
        if (!this.fileUpload.getSelectedFile().getName().toLowerCase().endsWith(FileType.HEADER.EXTENSION)) {
            JOptionPane.showMessageDialog(null, String.format("%s %s", ConstantController.getString("file.error.selectRightExtension"), FileType.HEADER.EXTENSION));
            this.exitReference.set(true);
            return;
        }

        String file = this.fileUpload.getSelectedFile().getAbsolutePath();

        table.set(Table.loadFromHeader(file));
    }

    private void excel() {
        /*
         * try {
         *
         * FileInputStream file = new
         * FileInputStream(fileUpload.getSelectedFile().getAbsolutePath());
         *
         * tableName.append(fileUpload.getSelectedFile().getName().toUpperCase()
         * .substring(0, fileUpload.getSelectedFile().getName().indexOf("."))
         * .replaceAll("[^a-zA-Z0-9_-]", ""));
         *
         * XSSFWorkbook workbook = new XSSFWorkbook(file); XSSFSheet sheet =
         * workbook.getSheetAt(0);
         *
         * Iterator<Row> rowIterator = sheet.iterator();
         *
         * Row firstRow = rowIterator.next(); Iterator<Cell> firstRowCellIterator =
         * firstRow.cellIterator();
         *
         * while(firstRowCellIterator.hasNext()) {
         *
         * Cell cell = firstRowCellIterator.next();
         * columnsName.add(cell.getStringCellValue().replace("\"", "").replace(" ",
         * ""));
         *
         * }
         *
         * while (rowIterator.hasNext()) {
         *
         * Row row = rowIterator.next(); Iterator<Cell> cellIterator =
         * row.cellIterator();
         *
         * List<String> line = new ArrayList<>();
         *
         * while (cellIterator.hasNext()) {
         *
         * Cell cell = cellIterator.next(); switch (cell.getCellType()) {
         *
         * case NUMERIC:
         *
         * line.add(String.valueOf(cell.getNumericCellValue())); break;
         *
         * case STRING:
         *
         * line.add(cell.getStringCellValue()); break;
         *
         * case BLANK:
         *
         * line.add("");
         *
         * case BOOLEAN: case ERROR: case FORMULA: case _NONE:
         *
         * default:
         *
         * break;
         *
         * } }
         *
         * lines.add(line);
         *
         * }
         *
         * file.close(); workbook.close();
         *
         * List<List<String>> aux = new ArrayList<>(); aux.add(columnsName);
         * aux.addAll(lines);
         *
         * new FormFramePrimaryKey(aux, pkName, exitReference);
         *
         * if(!exitReference.get()) new FormFrameColumnType(columns, aux, tableName,
         * tablesName, exitReference);
         *
         * } catch (IOException e) {
         *
         * e.printStackTrace();
         *
         * }
         */

    }

    private CSVInfo csv() {
        if (!this.fileUpload.getSelectedFile().getName().toLowerCase().endsWith(FileType.CSV.EXTENSION)) {
            JOptionPane.showMessageDialog(null, "Por favor, selecione um arquivo CSV.");

            this.exitReference.set(true);

            return null;
        }

        return new CSVRecognizerForm(
            Path.of(this.fileUpload.getSelectedFile().getAbsolutePath()), this.tableName,
            this.columns, this.content, this.exitReference
        ).getCSVInfo();
    }

    public TableCell getResult() {
        return this.tableCell;
    }
}
