package files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import controllers.MainController;
import database.TableCreator;
import dsl.AntlrController;
import dsl.DslController;
import dsl.DslErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;
import engine.exceptions.DataBaseException;
import entities.Column;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.TableCell;
import enums.CellType;
import enums.FileType;
import exceptions.dsl.InputException;
import files.csv.CSVInfo;
import gui.frames.ErrorFrame;
import gui.frames.forms.importexport.CSVRecognizerForm;
import gui.frames.main.MainFrame;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;
import sgbd.source.table.Table;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ImportFile {

    private final JFileChooser fileUpload = new JFileChooser();

    private final AtomicReference<Boolean> exitReference;

    private final StringBuilder tableName = new StringBuilder();

    private final Map<Integer, Map<String, String>> content = new LinkedHashMap<>();

    private final List<Column> columns = new ArrayList<>();

    private final FileType fileType;

    private TableCell tableCell = null;

    public ImportFile(FileType fileType, AtomicReference<Boolean> exitReference) {
        this.exitReference = exitReference;
        this.fileType = fileType;
        this.fileUpload.setCurrentDirectory(MainController.getLastDirectory());

        this.importFile();
    }

    private void importFile() {

        try
        {
            FileFilter filter = getFileNameExtensionFilter();

            this.fileUpload.setFileFilter(filter);

            if (this.fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                MainController.setLastDirectory(new File(this.fileUpload.getCurrentDirectory().getAbsolutePath()));

                switch (this.fileType) {
                    case CSV -> {
                        CSVInfo info = this.csv();

                        if (!this.exitReference.get()) {
                            assert info != null;
                            try{

                                this.tableCell = TableCreator.createCSVTable(
                                    this.tableName.toString(), this.columns, info, false
                                );
                            }catch (DataBaseException e){
                                this.exitReference.set(true);
                                new ErrorFrame(e.getMessage());
                            }
                        }
                    }
                    case EXCEL -> this.excel();
                    case HEADER -> {

                        AtomicReference<Table> table = new AtomicReference<>();

                        header(table);

                        this.tableCell = importHeaderFile(table, exitReference, fileUpload.getSelectedFile());
                    }
                    case TXT -> {

                        StringBuilder content = new StringBuilder();

                        try (BufferedReader reader = new BufferedReader(new FileReader(fileUpload.getSelectedFile()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                content.append(line).append("\n");
                            }
                        } catch (IOException e) {

                            new ErrorFrame(e.getMessage());
                            this.exitReference.set(true);
                            return;
                        }

                        RelAlgebraParser parser = new RelAlgebraParser(new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(content.toString()))));

                        parser.removeErrorListeners();

                        DslErrorListener errorListener = new DslErrorListener();
                        parser.addErrorListener(errorListener);

                        ParseTreeWalker walker = new ParseTreeWalker();

                        AntlrController listener = new AntlrController();

                        walker.walk(listener, parser.command());

                        if (!DslErrorListener.getErrors().isEmpty()) {
                            new ErrorFrame(DslErrorListener.getErrors().toString());
                            return;
                        }

                        try {
                            DslController.parser();
                        } catch (InputException exception) {
                            new ErrorFrame(exception.getMessage());
                            this.exitReference.set(true);
                        }

                    }
                    case SQL ->
                            throw new UnsupportedOperationException(String.format("Unimplemented case: %s", this.fileType));
                    default -> throw new IllegalArgumentException(String.format("Unexpected value: %s", this.fileType));
                }
            } else {
                this.exitReference.set(true);
            }
        }
        catch (FileNotFoundException | IllegalArgumentException e){

            new ErrorFrame(e.getMessage());
            exitReference.set(true);

        }
    }

    public static TableCell importHeaderFile(AtomicReference<Table> table,
                                        AtomicReference<Boolean> exitReference, File file) throws FileNotFoundException{

        if (!exitReference.get()) {
            String selectedFileName = file.getName();

            String fileName = selectedFileName.endsWith(FileType.HEADER.extension) ?
                selectedFileName.substring(0, selectedFileName.indexOf(".")) :
                selectedFileName;

            JsonObject headerFile = new Gson().fromJson(new FileReader(file), JsonObject.class);
            CellType cellType = headerFile.getAsJsonObject("information").get("file-path").getAsString()
                .replaceAll("' | \"", "").endsWith(".dat")
                ? CellType.FYI_TABLE : CellType.CSV_TABLE;

            mxCell jCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                    MainFrame.getGraph().getDefaultParent(), null,
                    fileName, 0, 0, 80, 30, cellType.id
                );

            table.get().open();

            return switch (cellType){
                case CSV_TABLE -> new CSVTableCell(jCell, fileName, table.get(), file);
                case FYI_TABLE -> new FYITableCell(jCell, fileName, table.get(), file);
                default -> throw new IllegalStateException("Unexpected value: " + cellType);
            };
        }

        return null;
    }

    @NotNull
    private FileNameExtensionFilter getFileNameExtensionFilter() {

        return switch (this.fileType) {
            case CSV -> new FileNameExtensionFilter("CSV files", "csv");
            case EXCEL -> new FileNameExtensionFilter("Sheets files", "xlsx", "xls", "ods");
            case HEADER -> new FileNameExtensionFilter("Headers files", "head");
            case SQL -> throw new UnsupportedOperationException(String.format("Unimplemented case: %s", this.fileType));
            case TXT -> new FileNameExtensionFilter("Txt files", "txt");
            default -> throw new IllegalArgumentException(String.format("Unexpected value: %s", this.fileType));
        };
    }

    private void header(AtomicReference<Table> table) {
        if (!this.fileUpload.getSelectedFile().getName().toLowerCase().endsWith(FileType.HEADER.extension)) {
            JOptionPane.showMessageDialog(null, String.format("%s %s", ConstantController.getString("file.error.selectRightExtension"), FileType.HEADER.extension));
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
        if (!this.fileUpload.getSelectedFile().getName().toLowerCase().endsWith(FileType.CSV.extension)) {
            JOptionPane.showMessageDialog(null, ConstantController.getString("file.error.selectRightExtension")+" "
                    +FileType.CSV.extension);

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
