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
import entities.Column;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.TableCell;
import entities.cells.XMLTableCell;
import enums.CellType;
import enums.FileType;
import static enums.FileType.CSV;
import static enums.FileType.EXCEL;
import static enums.FileType.HEADER;
import static enums.FileType.SQL;
import static enums.FileType.TXT;
import exceptions.dsl.InputException;
import files.csv.CSVInfo;
import gui.frames.ErrorFrame;
import gui.frames.FileTransferHandler;
import gui.frames.forms.importexport.CSVRecognizerForm;
import gui.frames.main.MainFrame;
import ibd.table.Table;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    public ImportFile(FileType fileType, AtomicReference<Boolean> exitReference) throws Exception {
        this.exitReference = exitReference;
        this.fileType = fileType;
        this.fileUpload.setCurrentDirectory(MainController.getLastDirectory());

        this.importFile();
    }

    private void importFile() throws Exception {

        try {
            FileFilter filter = getFileNameExtensionFilter();

            this.fileUpload.setFileFilter(filter);

            if (this.fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                MainController.setLastDirectory(new File(this.fileUpload.getCurrentDirectory().getAbsolutePath()));

                switch (this.fileType) {
                    case CSV -> {

                        if (!this.exitReference.get()) {
//                            CSVInfo info = this.csv(this.fileUpload.getSelectedFile());
//                            assert info != null;
//                            try {
//
//                                this.tableCell = TableCreator.createCSVTable(
//                                        this.tableName.toString(), this.columns, info, false
//                                );
//                            } catch (Exception e) {
//                                this.exitReference.set(true);
//                                new ErrorFrame(e.getMessage());
//                            }
                        FileTransferHandler.openCSVFile(this.fileUpload.getSelectedFile());
                        }
                    }
                    case EXCEL ->
                        this.excel();
                    case HEADER -> {
                        if (!exitReference.get()) {
                            if (!this.fileUpload.getSelectedFile().getName().toLowerCase().endsWith(FileType.HEADER.extension)) {
                                JOptionPane.showMessageDialog(null, String.format("%s %s", ConstantController.getString("file.error.selectRightExtension"), FileType.HEADER.extension));
                                this.exitReference.set(true);
                            } else {


                                FileTransferHandler.openHeadFile(this.fileUpload.getSelectedFile());

                                //String file = this.fileUpload.getSelectedFile().getAbsolutePath();
                                //Table table = TableCreator.loadFromHeader(file);
                                //CellType cellType = getCellType(fileUpload.getSelectedFile());
                                //this.tableCell = importHeaderFile(table, cellType, fileUpload.getSelectedFile());
                            }
                        }
                    }
                    case DAT -> {
                        if (!exitReference.get()) {
                            if (!this.fileUpload.getSelectedFile().getName().toLowerCase().endsWith(FileType.DAT.extension)) {
                                JOptionPane.showMessageDialog(null, String.format("%s %s", ConstantController.getString("file.error.selectRightExtension"), FileType.DAT.extension));
                                this.exitReference.set(true);
                            } else {
                                FileTransferHandler.openDataFile(this.fileUpload.getSelectedFile());
                            }
                        }
                    }
                    case TXT -> {

                        File fileName = fileUpload.getSelectedFile();
                        boolean status = importQuery(fileName);
                        if (status) {
                            this.exitReference.set(true);
                        }
                    }
                    case XML -> {
                        if (!this.exitReference.get()) {
                            FileTransferHandler.openXMLFile(this.fileUpload.getSelectedFile());
                        }
                    }
                    case SQL ->
                        throw new UnsupportedOperationException(String.format("Unimplemented case: %s", this.fileType));
                    default ->
                        throw new IllegalArgumentException(String.format("Unexpected value: %s", this.fileType));
                }
            } else {
                this.exitReference.set(true);
            }
        } catch (FileNotFoundException | IllegalArgumentException e) {

            new ErrorFrame(e.getMessage());
            exitReference.set(true);

        }
    }

    public static CellType getCellType(File file) throws FileNotFoundException {

        JsonObject headerFile = new Gson().fromJson(new FileReader(file), JsonObject.class);
        return headerFile.getAsJsonObject("information").get("file-path").getAsString()
                .replaceAll("' | \"", "").endsWith(".dat")
                ? CellType.FYI_TABLE : CellType.CSV_TABLE;
    }

    private static String readTXT1(File fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        return content.toString();
    }

    private static String readQuery(File fileName) throws IOException {
        return Files.readString(fileName.toPath(), StandardCharsets.UTF_8);
    }

    public static boolean importQuery(File fileName) {
        String content = "";
        try {
            content = readQuery(fileName);
        } catch (IOException e) {
            new ErrorFrame(e.getMessage());
        }

        RelAlgebraParser parser = new RelAlgebraParser(new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(content))));

        parser.removeErrorListeners();

        DslErrorListener errorListener = new DslErrorListener();
        parser.addErrorListener(errorListener);

        ParseTreeWalker walker = new ParseTreeWalker();

        AntlrController listener = new AntlrController();

        walker.walk(listener, parser.command());
//
//                        if (!DslErrorListener.getErrors().isEmpty()) {
//                            new ErrorFrame(DslErrorListener.getErrors().toString());
//                            return;
//                        }

        try {
            DslController.parser(content);
        } catch (InputException exception) {
            DslController.reset();
            new ErrorFrame(exception.getMessage());
            return true;
        }
        return false;
    }

    public static TableCell importHeaderFile(Table table,
            CellType cellType, File file) throws FileNotFoundException, Exception {

        String selectedFileName = file.getName();

        String tableName = selectedFileName.substring(0, selectedFileName.indexOf("."));

        mxCell jCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                        MainFrame.getGraph().getDefaultParent(), null,
                        tableName, 0, 0, 80, 30, cellType.id
                );

        table.open();

        return switch (cellType) {
            case CSV_TABLE ->
                new CSVTableCell(jCell, tableName, table, file);
            case FYI_TABLE ->
                new FYITableCell(jCell, tableName, table, file);
            case XML_TABLE ->
                new XMLTableCell(jCell, tableName, table, file);
            default ->
                throw new IllegalStateException("Unexpected value: " + cellType);
        };
    }

    @NotNull
    private FileNameExtensionFilter getFileNameExtensionFilter() {

        return getFileNameExtensionFilter (this.fileType);
    }

    public static FileNameExtensionFilter getFileNameExtensionFilter(FileType fileType) {

        return switch (fileType) {
            case CSV ->
                new FileNameExtensionFilter("CSV files", "csv");
            case EXCEL ->
                new FileNameExtensionFilter("Sheets files", "xlsx", "xls", "ods");
            case HEADER ->
                new FileNameExtensionFilter("Headers files", "head");
            case DAT ->
                new FileNameExtensionFilter("Dat files", "dat");
            case XML ->
                new FileNameExtensionFilter("XML files", "xml");
            case SQL -> null;
                //throw new UnsupportedOperationException(String.format("Unimplemented case: %s", this.fileType));
            case TXT ->
                new FileNameExtensionFilter("Query files", "txt");
            default -> null;
                //throw new IllegalArgumentException(String.format("Unexpected value: %s", this.fileType));
        };
    }

    private void importTable(AtomicReference<Table> table) throws Exception {

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

    private CSVInfo csv(File file) {
        if (!file.getName().toLowerCase().endsWith(FileType.CSV.extension)) {
            JOptionPane.showMessageDialog(null, ConstantController.getString("file.error.selectRightExtension") + " "
                    + FileType.CSV.extension);

            this.exitReference.set(true);

            return null;
        }

        return new CSVRecognizerForm(
                Path.of(file.getAbsolutePath()), this.tableName,
                this.columns, this.content, this.exitReference
        ).getCSVInfo();
    }

    public TableCell getResult() {
        return this.tableCell;
    }
}
