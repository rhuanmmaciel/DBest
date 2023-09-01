package database;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import entities.cells.CSVTableCell;
import entities.cells.FyiTableCell;
import entities.cells.TableCell;
import enums.FileType;
import files.csv.CSVInfo;
import gui.frames.main.MainFrame;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.column.Column;
import sgbd.table.CSVTable;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
        Map<String, String>> data, boolean mustExport
    ) {
        this(mustExport);

        this.createFyiTable(tableName, columns, data);
    }

    private void createCSVTable(
        String tableName, List<entities.Column> columns, char separator,
        char stringDelimiter, int beginIndex, Path path
    ) {
        Prototype prototype = this.createPrototype(columns);

        Header header = new Header(prototype, tableName);
        header.set(Header.FILE_PATH, path.toString());

        CSVTable table = new CSVTable(header, separator, stringDelimiter, beginIndex);
        table.open();
        table.saveHeader(String.format("%s.head", tableName));

        if (this.mustExport) {
            this.tableCell = new CSVTableCell(null, tableName, FileType.CSV.id, columns, table, prototype);
            return;
        }

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, FileType.CSV.id
            );

        this.tableCell = new CSVTableCell(jCell, tableName, FileType.CSV.id, columns, table, prototype);
    }

    private void createFyiTable(
        String tableName, List<entities.Column> columns, Map<Integer, Map<String, String>> data
    ) {
        List<RowData> rows = new ArrayList<>(this.getRowData(columns, data));

        Prototype prototype = this.createPrototype(columns);

        Table table = SimpleTable.openTable(new Header(prototype, tableName));
        table.open();
        table.insert(rows);
        table.saveHeader(String.format("%s.head", tableName));

        if (this.mustExport) {
            this.tableCell = new FyiTableCell(null, tableName, FileType.FYI.id, columns, table, prototype);
            return;
        }

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, FileType.FYI.id
            );

        this.tableCell = new FyiTableCell(jCell, tableName, FileType.FYI.id, columns, table, prototype);
    }

    private Prototype createPrototype(List<entities.Column> columns) {
        Prototype prototype = new Prototype();

        for (entities.Column column : columns) {
            short size;
            short flags;

            switch (column.getDataType()) {
                case INTEGER -> {
                    size = 4;
                    flags = Column.SIGNED_INTEGER_COLUMN;
                }
                case LONG -> {
                    size = 8;
                    flags = Column.SIGNED_INTEGER_COLUMN;
                }
                case FLOAT -> {
                    size = 4;
                    flags = Column.FLOATING_POINT;
                }
                case DOUBLE -> {
                    size = 8;
                    flags = Column.FLOATING_POINT;
                }
                case STRING -> {
                    size = Short.MAX_VALUE;
                    flags = Column.STRING;
                }
                case CHARACTER -> {
                    size = 1;
                    flags = Column.STRING;
                }
                default -> {
                    size = 100;
                    flags = Column.NONE;
                }
            }

            flags |= column.getIsPrimaryKey() ? Column.PRIMARY_KEY : Column.CAN_NULL_COLUMN;

            prototype.addColumn(column.getName(), size, flags);
        }

        return prototype;
    }

    private List<RowData> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {
        List<RowData> rows = new ArrayList<>();

        for (Map<String, String> line : content.values()) {
            RowData rowData = new RowData();

            for (String data : line.keySet()) {
                entities.Column column = columns.stream().filter(x -> x.getName().equals(data)).findFirst().orElseThrow();

                if (!line.get(data).equals(ConstantController.NULL) && !line.get(data).isEmpty()) {
                    switch (column.getDataType()) {
                        case INTEGER -> rowData.setInt(column.getName(), (int) (Double.parseDouble(line.get(data).strip())));
                        case LONG -> rowData.setLong(column.getName(), (long) (Double.parseDouble(line.get(data).strip())));
                        case FLOAT -> rowData.setFloat(column.getName(), Float.parseFloat(line.get(data).strip()));
                        case DOUBLE -> rowData.setDouble(column.getName(), Double.parseDouble(line.get(data).strip()));
                        default -> rowData.setString(column.getName(), line.get(data).strip());
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
