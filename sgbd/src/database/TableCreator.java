package database;

import java.io.File;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;

import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.TableCell;

import enums.FileType;

import files.FileUtils;
import files.csv.CSVInfo;

import gui.frames.main.MainFrame;

import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.metadata.Metadata;
import sgbd.table.CSVTable;
import sgbd.table.Table;
import sgbd.table.components.Header;

public class TableCreator {

    private TableCell tableCell;

    private final boolean mustExport;

    public TableCreator(boolean mustExport) {
        this.tableCell = null;
        this.mustExport = mustExport;
    }

    public TableCreator(String tableName, List<entities.Column> columns, CSVInfo csvInfo, boolean mustExport) {
        this(mustExport);

        this.createCSVTable(tableName, columns, csvInfo.separator(), csvInfo.stringDelimiter(), csvInfo.beginIndex(), csvInfo.path());
    }

    public TableCreator(
        String tableName, List<entities.Column> columns, Map<Integer,
        Map<String, String>> data, File headerFile, boolean mustExport
    ) {
        this(mustExport);

        this.createFYITable(tableName, columns, data, headerFile);
    }

    private void createCSVTable(
        String tableName, List<entities.Column> columns, char separator,
        char stringDelimiter, int beginIndex, Path path
    ) {
        Prototype prototype = this.createPrototype(columns);

        Header header = new Header(prototype, tableName);
        header.set(Header.FILE_PATH, path.toString());

        String headerFileName = String.format("%s%s", tableName, FileType.HEADER.extension);

        CSVTable table = new CSVTable(header, separator, stringDelimiter, beginIndex);
        table.open();
        table.saveHeader(headerFileName);

        File headerFile = FileUtils.getFile(headerFileName);

        FileUtils.moveToTempDirectory(headerFile);
        headerFile = FileUtils.getFileFromTempDirectory(headerFileName).get();

        if (this.mustExport) {
            this.tableCell = new CSVTableCell(null, tableName, columns, table, prototype, headerFile);
            return;
        }

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, FileType.CSV.id
            );

        this.tableCell = new CSVTableCell(jCell, tableName, columns, table, prototype, headerFile);
    }

    private void createFYITable(
        String tableName, List<entities.Column> columns, Map<Integer, Map<String, String>> data, File headerFile
    ) {
        List<RowData> rows = new ArrayList<>(this.getRowData(columns, data));

        Prototype prototype = this.createPrototype(columns);

        Table table = Table.openTable(new Header(prototype, tableName));
        table.open();
        table.insert(rows);
        table.saveHeader(String.format("%s%s", tableName, FileType.HEADER.extension));

        if (this.mustExport) {
            this.tableCell = new FYITableCell(null, tableName, columns, table, prototype, headerFile);
            return;
        }

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, FileType.FYI.id
            );

        this.tableCell = new FYITableCell(jCell, tableName, columns, table, prototype, headerFile);
    }

    private Prototype createPrototype(List<entities.Column> columns) {
        Prototype prototype = new Prototype();

        for (entities.Column column : columns) {
            short size;
            short flags;

            switch (column.getDataType()) {
                case INTEGER -> {
                    size = 4;
                    flags = Metadata.SIGNED_INTEGER_COLUMN;
                }
                case LONG -> {
                    size = 8;
                    flags = Metadata.SIGNED_INTEGER_COLUMN;
                }
                case FLOAT -> {
                    size = 4;
                    flags = Metadata.FLOATING_POINT;
                }
                case DOUBLE -> {
                    size = 8;
                    flags = Metadata.FLOATING_POINT;
                }
                case STRING -> {
                    size = Short.MAX_VALUE;
                    flags = Metadata.STRING;
                }
                case CHARACTER -> {
                    size = 1;
                    flags = Metadata.STRING;
                }
                default -> {
                    size = 100;
                    flags = Metadata.NONE;
                }
            }

            flags |= column.getIsPrimaryKey() ? Metadata.PRIMARY_KEY : Metadata.CAN_NULL_COLUMN;

            prototype.addColumn(column.getName(), size, flags);
        }

        return prototype;
    }

    private List<RowData> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {
        List<RowData> rows = new ArrayList<>();

        for (Map<String, String> line : content.values()) {
            RowData rowData = new RowData();

            for (Map.Entry<String, String> data : line.entrySet()) {
                String key = data.getKey();
                String value = data.getValue();

                entities.Column column = columns.stream().filter(c -> c.getName().equals(key)).findFirst().orElseThrow();

                if (!value.equals(ConstantController.NULL) && !value.isEmpty()) {
                    switch (column.getDataType()) {
                        case INTEGER -> rowData.setInt(column.getName(), (int) (Double.parseDouble(value.strip())));
                        case LONG -> rowData.setLong(column.getName(), (long) (Double.parseDouble(value.strip())));
                        case FLOAT -> rowData.setFloat(column.getName(), Float.parseFloat(value.strip()));
                        case DOUBLE -> rowData.setDouble(column.getName(), Double.parseDouble(value.strip()));
                        default -> rowData.setString(column.getName(), value.strip());
                    }
                }
            }

            rows.add(rowData);
        }

        return rows;
    }

    public TableCell getTableCell() {
        return this.tableCell;
    }
}
