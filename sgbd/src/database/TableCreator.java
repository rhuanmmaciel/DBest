package database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import controllers.ConstantController;
import controllers.MainController;
import dsl.utils.DslUtils;
import entities.cells.CSVTableCell;
import entities.cells.FYITableCell;
import entities.cells.MemoryTableCell;
import entities.cells.TableCell;
import enums.CellType;
import enums.FileType;
import files.FileUtils;
import files.csv.CSVInfo;
import gui.frames.main.MainFrame;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.metadata.Metadata;
import sgbd.source.components.Header;
import sgbd.source.table.CSVTable;
import sgbd.source.table.MemoryTable;
import sgbd.source.table.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TableCreator {

    public static TableCell createTable(File file) throws FileNotFoundException {

        if(!file.isFile()) throw new IllegalArgumentException(ConstantController.getString("file.error.notAFile"));

        if(!file.getName().endsWith(FileType.HEADER.extension)) throw new IllegalArgumentException(ConstantController.getString("file.error.wrongExtension"));


        JsonObject headerFile = new Gson().fromJson(new FileReader(file), JsonObject.class);
        CellType cellType = headerFile.getAsJsonObject("information").get("file-path").getAsString()
                .replaceAll("' | \"", "").endsWith(".dat")
                ? CellType.FYI_TABLE : CellType.CSV_TABLE;

        String path = file.getAbsolutePath();

        Table table = Table.loadFromHeader(path);
        table.open();

        String tableName = headerFile.getAsJsonObject("information").get("tablename").getAsString();

        return switch (cellType){

            case MEMORY_TABLE, OPERATION -> throw new IllegalArgumentException();
            case CSV_TABLE -> new CSVTableCell(tableName,
                    table, new File(path));
            case FYI_TABLE -> new FYITableCell(tableName,
                    table, new File(path));

        };

    }

    public static CSVTableCell createCSVTable(
            String tableName, List<entities.Column> columns, CSVInfo csvInfo, boolean mustExport
    ) {
        Prototype prototype = createPrototype(columns);

        Header header = new Header(prototype, tableName);
        header.set(Header.FILE_PATH, csvInfo.path().toString());

        String headerFileName = String.format("%s%s", tableName, FileType.HEADER.extension);

        CSVTable table = new CSVTable(header, csvInfo.separator(), csvInfo.stringDelimiter(), csvInfo.beginIndex());
        table.open();
        table.saveHeader(headerFileName);

        File headerFile = FileUtils.getFile(headerFileName);

        FileUtils.moveToTempDirectory(headerFile);
        headerFile = FileUtils.getFileFromTempDirectory(headerFileName).get();

        if (mustExport)
            return new CSVTableCell(new mxCell(null, new mxGeometry(), ConstantController.J_CELL_CSV_TABLE_STYLE), tableName, columns, table, prototype, headerFile);

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.CSV_TABLE.id
            );

         return new CSVTableCell(jCell, tableName, columns, table, prototype, headerFile);
    }

    public static FYITableCell createFYITable(
            String tableName, List<entities.Column> columns, Map<Integer, Map<String, String>> data, File headerFile, boolean mustExport
    ) {
        List<RowData> rows = new ArrayList<>(getRowData(columns, data));

        Prototype prototype = createPrototype(columns);

        Table table = Table.openTable(new Header(prototype, tableName));
        table.open();
        table.insert(rows);
        table.saveHeader(String.format("%s%s", tableName, FileType.HEADER.extension));

        if (mustExport)
            return  new FYITableCell(new mxCell(null, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), tableName, columns, table, prototype, headerFile);

        mxCell jCell = (mxCell) MainFrame
            .getGraph()
            .insertVertex(
                MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.FYI_TABLE.id
            );

        return new FYITableCell(jCell, tableName, columns, table, prototype, headerFile);
    }

    public static MemoryTableCell createMemoryTable(
            String tableName, List<entities.Column> columns, Map<Integer, Map<String, String>> data
    ) {

        List<RowData> rows = new ArrayList<>(getRowData(columns, data));

        Prototype prototype = createPrototype(columns);

        Header h = new Header(prototype, tableName);
        h.set(Header.TABLE_TYPE, "MemoryTable");

        Table table = Table.openTable(h);
        table.open();
        table.insert(rows);

        mxCell jCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                        MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                        ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.MEMORY_TABLE.id
                );

        return new MemoryTableCell(jCell, tableName, columns, table, prototype);
    }

    public static Prototype createPrototype(List<entities.Column> columns) {
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

    private static List<RowData> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {
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

}
