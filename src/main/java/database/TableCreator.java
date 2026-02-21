package database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import controllers.ConstantController;
import controllers.MainController;
import database.jdbc.UniversalConnectionConfig;
import entities.Column;
import entities.cells.CSVTableCell;
import entities.cells.Cell;
import entities.cells.FYITableCell;
import entities.cells.JDBCTableCell;
import entities.cells.MemoryTableCell;
import entities.cells.TableCell;
import entities.cells.XMLTableCell;
import enums.CellType;
import enums.ColumnDataType;
import static enums.ColumnDataType.CHARACTER;
import static enums.ColumnDataType.DOUBLE;
import static enums.ColumnDataType.FLOAT;
import static enums.ColumnDataType.INTEGER;
import static enums.ColumnDataType.LONG;
import static enums.ColumnDataType.STRING;
import enums.DatabaseType;
import enums.FileType;
import files.csv.CSVInfo;
import files.xml.XMLInfo;
import gui.frames.main.MainFrame;
import ibd.table.btree.BTreeTable;
import ibd.table.csv.CSVTable;
import ibd.table.Table;
import ibd.table.jdbc.JDBCTable;
import ibd.table.memory.MemoryTable;
import ibd.table.xml.XMLTable;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.DoubleColumn;
import ibd.table.prototype.column.FloatColumn;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.LongColumn;
import ibd.table.prototype.column.StringColumn;
import ibd.table.prototype.metadata.Metadata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TableCreator {

    public static int cacheSize = 5000000;

    public static TableCell createTable(File file) throws Exception {

        if (!file.isFile()) {
            throw new IllegalArgumentException(ConstantController.getString("file.error.notAFile"));
        }

        if (!file.getName().endsWith(FileType.HEADER.extension)) {
            throw new IllegalArgumentException(ConstantController.getString("file.error.wrongExtension"));
        }

        JsonObject headerFile = new Gson().fromJson(new FileReader(file), JsonObject.class);
        CellType cellType = headerFile.getAsJsonObject("information").get("file-path").getAsString()
                .replaceAll("' | \"", "").endsWith(".dat")
                ? CellType.FYI_TABLE : CellType.CSV_TABLE;

        String path = file.getAbsolutePath();

        Table table = loadFromHeader(path);
        table.open();

        String tableName = headerFile.getAsJsonObject("information").get("tablename").getAsString();
        tableName = MainController.resolveTableNameConflict(tableName);

        return switch (cellType) {

            case MEMORY_TABLE, OPERATION ->
                throw new IllegalArgumentException();
            case CSV_TABLE ->
                new CSVTableCell(tableName,
                table, new File(path));
            case FYI_TABLE ->
                new FYITableCell(tableName,
                table, new File(path));
            case XML_TABLE ->
                new XMLTableCell(tableName,
                table, new File(path));
            // TODO: Review unreachable statement
            case JDBC_TABLE ->
                new JDBCTableCell(tableName,
                table, new File(path));
        };

    }

    public static Table loadFromHeader(String path) throws IOException, Exception {
        Header header = Header.load(path);
        return openTable(header, false);
    }

    private static Table openTable(Header header, boolean clear) throws IOException, Exception {
        header.setBool("clear", clear);

        if (header.get(Header.TABLE_TYPE) == null) {
            return new BTreeTable(header, null, null, cacheSize);
        }

        return switch (header.get(Header.TABLE_TYPE)) {
            case "CSVTable" ->
                new CSVTable(header);
            case "MemoryTable" ->
                new MemoryTable(header);
            case "JDBCTable" ->
                new JDBCTable(header);
            case "XMLTable" ->
                new XMLTable(header);
            default ->
                new BTreeTable(header, null, null, cacheSize);
        };
    }

    public static Table openBTreeTable(String fileName) throws IOException, Exception {
        return new BTreeTable(fileName, cacheSize);
    }

    public static CSVTableCell createCSVTable(
            String tableName, List<entities.Column> columns, CSVInfo csvInfo, boolean mustExport
    ) {
        Prototype prototype = createPrototype(columns);

        Header header = new Header(prototype, tableName);
        header.set(Header.FILE_PATH, csvInfo.path().toString());

        String headerFileName = String.format("%s%s", tableName, FileType.HEADER.extension);

        String headerPath = Header.replaceFileName(csvInfo.path().toString(), headerFileName);

        CSVTable table = new CSVTable(header, csvInfo.separator(), csvInfo.stringDelimiter(), csvInfo.beginIndex());
        mxCell jCell = null;
        File headerFile = new File(headerPath);
        try {
            table.open();
            headerFileName = table.saveHeader(headerPath);

//        File headerFile = FileUtils.getFile(headerPath);
//        FileUtils.moveToTempDirectory(headerFile);
//        headerFile = FileUtils.getFileFromTempDirectory(headerFileName).get();
            if (mustExport) {
                return new CSVTableCell(new mxCell(null, new mxGeometry(), ConstantController.J_CELL_CSV_TABLE_STYLE), tableName, columns, table, prototype, headerFile);
            }

            jCell = (mxCell) MainFrame
                    .getGraph()
                    .insertVertex(
                            MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                            ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.CSV_TABLE.id
                    );
        } catch (Exception ex) {
        }
        return new CSVTableCell(jCell, tableName, columns, table, prototype, headerFile);
    }

    public static FYITableCell createIndex(
            String tableName, List<entities.Column> columns, Cell tableCell, boolean unique) {
        File file = new File(tableName + FileType.HEADER.extension);
        return TableCreator.createIndex(tableName,columns,TuplesExtractor.getAllRowsMap(tableCell.getOperator(),false),file,false, unique);
    }

    public static FYITableCell createIndex(
            String tableName, List<entities.Column> columns, Map<Integer, Map<String, String>> data, File headerFile, boolean mustExport, boolean unique
    ) {
        List<BasicDataRow> rows = new ArrayList<>(getRowData(columns, data));

        Prototype prototype = createPrototype(columns);

        Header header = new Header(prototype, tableName);
        String dataFileName = removeSuffix(headerFile.getPath(), ".head") + ".dat";
        header.set(Header.FILE_PATH, dataFileName);

        Map<ibd.table.prototype.column.Column,Integer> dict = new HashMap<>();

        //needs to walkthrough all rows to adjust the size of string columns
        for(BasicDataRow row: rows){
            for(ibd.table.prototype.column.Column c:prototype.getColumns()){
                if(!c.isString())continue;
                String key = c.getName();
                String s = row.getString(key);
                if(s==null)continue;
                if(dict.containsKey(c))
                    dict.put(c,Math.max(dict.get(c),s.length() + 1));
                else
                    dict.put(c,s.length() + 1);
            }
        }
        for(Map.Entry<ibd.table.prototype.column.Column,Integer> c:dict.entrySet()) {
           prototype.replaceColumn(new StringColumn(c.getKey().getName(), (short) (c.getValue() + 1), c.getKey().getFlags()));
        }


            //String.format("%s%s", tableName, FileType.HEADER.extension)
        String headerFileName = headerFile.getPath();

        mxCell jCell = null;
        Table table = null;

        try {
            header.save(headerFileName);

            ibd.table.prototype.Prototype myPrototype = convertPrototype(prototype);
            table = openTable(header, false);
            table.create(myPrototype, 4096);

            RowConverter converter = new RowConverter();
            for (BasicDataRow row : rows) {
                //BasicDataRow dataRow = converter.convertRow(row);
                table.addRecord(row, unique);
            }
            table.flushDB();

            if (mustExport) {
                return new FYITableCell(new mxCell(tableName, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), tableName, columns, table, prototype, headerFile);
            }

            jCell = (mxCell) MainFrame
                    .getGraph()
                    .insertVertex(
                            MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                            ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.FYI_TABLE.id
                    );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new FYITableCell(jCell, tableName, columns, table, prototype, headerFile);
    }

    public static void createIgnoredPKColumn(List<entities.Column> columns, Map<Integer, Map<String, String>> data, String tableName) {

        String ignoredColumnName = "__IDX__";
        int i = 1;

        while (columns.stream().map(column -> column.NAME).toList().contains(ignoredColumnName)) {
            ignoredColumnName = "__IDX__" + i++;
        }

        columns.add(0,new Column(ignoredColumnName, tableName, ColumnDataType.LONG, true, true));

        HashMap<Integer, Map<String, String>> newData = new HashMap<>();
        for (Map.Entry<Integer, Map<String, String>> rows : data.entrySet()) {

            HashMap<String, String> newValues = new HashMap<>(rows.getValue());
            newValues.put(ignoredColumnName, String.valueOf(rows.getKey()));

            newData.put(rows.getKey(), newValues);

        }

        data.clear();
        data.putAll(newData);

    }

    public static MemoryTableCell createMemoryTable(
            String tableName, List<entities.Column> columns, Map<Integer, Map<String, String>> data
    ) {

// there is no need to create a pk column for a memory table
//        if (columns.stream().noneMatch(column -> column.IS_PRIMARY_KEY)) {
//            createIgnoredPKColumn(columns, data, tableName);
//        }

        List<BasicDataRow> rows = new ArrayList<>(getRowData(columns, data));

        Prototype prototype = createPrototype(columns);

        Header h = new Header(prototype, tableName);
        h.set(Header.TABLE_TYPE, "MemoryTable");

        Table table = null;
        mxCell jCell = null;
        try {
            table = openTable(h, false);

            table.open();

            RowConverter converter = new RowConverter();
            long pk = 0;
            for (BasicDataRow row : rows) {
                //BasicDataRow dataRow = converter.convertRow(row);
                //row.setLong("__IDX__", pk); //only needed if we were to create a pk column
                table.addRecord(row, true);
                pk++;
            }

            jCell = (mxCell) MainFrame
                    .getGraph()
                    .insertVertex(
                            MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                            ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.MEMORY_TABLE.id
                    );
        } catch (Exception ex) {
            Logger.getLogger(TableCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new MemoryTableCell(jCell, tableName, columns, table, prototype);
    }

    public static Prototype createPrototype(List<entities.Column> columns) {
        Prototype prototype = new Prototype();
        for (entities.Column column : columns) {
            short size;
            short flags;

            switch (column.DATA_TYPE) {
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
                    size = 500;
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

            flags |= column.IS_PRIMARY_KEY ? Metadata.PRIMARY_KEY : Metadata.CAN_NULL_COLUMN;

            flags |= column.IS_IGNORED_COLUMN ? Metadata.IGNORE_COLUMN : 0;

            prototype.addColumn(createColumn(column, size, flags));
        }

        return prototype;
    }

    public static JDBCTableCell createJDBCTable(
        String tableName, UniversalConnectionConfig connectionConfig, Boolean mustExport
    ) {
        Header header = new Header(null, tableName);

        header.set(Header.TABLE_TYPE, "JDBCTable");
        header.set("connection-url", connectionConfig.constructConnectionURL());
        header.set("connection-user", connectionConfig.getUsername());
        header.set("connection-password", connectionConfig.getPassword());

        Table table = null;
        mxCell jCell = null;

        try {
            table = openTable(header, false);
            table.open();
            table.saveHeader(String.format("%s%s", tableName, FileType.HEADER.extension));

            if (mustExport) {
                return new JDBCTableCell(new mxCell(tableName, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), tableName, table, null);
            }

            jCell = (mxCell) MainFrame
                .getGraph()
                .insertVertex(
                    MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                    ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.JDBC_TABLE.id
                );
        } catch (Exception e) {
            Logger.getLogger(TableCreator.class.getName()).log(Level.SEVERE, null, e);
        }

        return new JDBCTableCell(jCell, tableName, table, null);
    }

    private static ibd.table.prototype.column.Column createColumn(entities.Column column, int size, short flags) {
        ibd.table.prototype.column.Column newCol;

        switch (column.DATA_TYPE) {
            case INTEGER -> {
                newCol = new IntegerColumn(column.NAME, size, flags);
            }
            case LONG -> {
                newCol = new LongColumn(column.NAME, size, flags);
            }
            case FLOAT -> {
                newCol = new FloatColumn(column.NAME, size, flags);
            }
            case DOUBLE -> {
                newCol = new DoubleColumn(column.NAME, size, flags);
            }
            case STRING -> {
                newCol = new StringColumn(column.NAME, size, flags);
            }
            case CHARACTER -> {
                newCol = new StringColumn(column.NAME, size, flags);
            }
            default -> {
                throw new AssertionError();
            }
        }
        return newCol;
    }

    private static List<BasicDataRow> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {
        List<BasicDataRow> rows = new ArrayList<>();
        for (Map<String, String> line : content.values()) {
            BasicDataRow dataRow = new BasicDataRow();

            for (Map.Entry<String, String> data : line.entrySet()) {
                String key = data.getKey();
                String value = data.getValue();
                //entities.Column column = columns.stream().filter(c -> c.NAME.equals(key)).findFirst().orElseThrow();
                entities.Column column = columns.stream().filter(c -> c.NAME.equals(key)).findFirst().orElse(null);
                if (!(column == null)) {

                    boolean empty = value.isEmpty() || value.equals(ConstantController.NULL);
                    if (empty) {
                        if (column.DATA_TYPE != STRING) {
                            value = "0";
                        } else {
                            value = "";
                        }
                    }
                    switch (column.DATA_TYPE) {
                        case INTEGER:
                            dataRow.setInt(column.NAME, (int) (Double.parseDouble(value.strip())));
                            break;
                        case LONG:
                            dataRow.setLong(column.NAME, (long) (Double.parseDouble(value.strip())));
                            break;
                        case FLOAT:
                            dataRow.setFloat(column.NAME, Float.parseFloat(value.strip()));
                            break;
                        case DOUBLE:
                            dataRow.setDouble(column.NAME, Double.parseDouble(value.strip()));
                            break;
                        default:
                            dataRow.setString(column.NAME, value.strip());
                    }
                }
            }

            rows.add(dataRow);
        }

        return rows;
    }

    private static ibd.table.prototype.Prototype convertPrototype(Prototype prot) {

        if (prot == null) {
            return null;
        }

        ibd.table.prototype.Prototype newProt = new ibd.table.prototype.Prototype();

        for (ibd.table.prototype.column.Column c : prot) {

            newProt.addColumn(convertColumn(c));
        }
        return newProt;
    }

    public static ibd.table.prototype.column.Column convertColumn(ibd.table.prototype.column.Column col) {
        ibd.table.prototype.column.Column newCol;

        switch (col.getType()) {
            case "STRING":
                newCol = new ibd.table.prototype.column.StringColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "INTEGER":
                newCol = new ibd.table.prototype.column.IntegerColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "LONG":
                newCol = new ibd.table.prototype.column.LongColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "FLOAT":
                newCol = new ibd.table.prototype.column.FloatColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "DOUBLE":
                newCol = new ibd.table.prototype.column.DoubleColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "BOOLEAN":
                newCol = new ibd.table.prototype.column.BooleanColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            default:
                throw new AssertionError();
        }

        return newCol;
    }

    public static String removeSuffix(final String s, final String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    /**
     * Creates an XML table from XML file analysis
     */
    public static XMLTableCell createXMLTable(
            String tableName, List<entities.Column> columns, XMLInfo xmlInfo, boolean mustExport
    ) {
        return createXMLTable(tableName, columns, xmlInfo, mustExport, false);
    }

    /**
     * Creates an XML table from XML file analysis with option to skip adding to main graph
     */
    public static XMLTableCell createXMLTable(
            String tableName, List<entities.Column> columns, XMLInfo xmlInfo, boolean mustExport, boolean skipMainGraph
    ) {
        Prototype prototype = createPrototype(columns);

        Header header = new Header(prototype, tableName);
        header.set(Header.FILE_PATH, xmlInfo.path().toString());

        String headerFileName = String.format("%s%s", tableName, FileType.HEADER.extension);
        String headerPath = Header.replaceFileName(xmlInfo.path().toString(), headerFileName);

        XMLTable table = new XMLTable(header, xmlInfo.rootElement(), xmlInfo.recordElement(), xmlInfo.strategy());
        mxCell jCell = null;
        File headerFile = new File(headerPath);

        try {
            table.open();
            headerFileName = table.saveHeader(headerPath);

            if (mustExport || skipMainGraph) {
                return new XMLTableCell(new mxCell(tableName, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), tableName, table, headerFile);
            }

            jCell = (mxCell) MainFrame
                    .getGraph()
                    .insertVertex(
                            MainFrame.getGraph().getDefaultParent(), null, tableName, 0, 0,
                            ConstantController.TABLE_CELL_WIDTH, ConstantController.TABLE_CELL_HEIGHT, CellType.XML_TABLE.id
                    );
        } catch (Exception e) {
            Logger.getLogger(TableCreator.class.getName()).log(Level.SEVERE, null, e);
        }

        return new XMLTableCell(jCell, tableName, table, headerFile);
    }

}
