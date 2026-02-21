package ibd.table.xml;

import engine.exceptions.DataBaseException;
import exceptions.InvalidXMLException;
import ibd.query.lookup.LookupFilter;
import ibd.table.ComparisonTypes;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;
import sources.xml.XMLData;
import sources.xml.XMLRecognizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XMLTable provides access to XML data in a tabular format.
 * It converts hierarchical XML data to flat tabular structure using XMLRecognizer.
 */
public class XMLTable extends Table {

    private XMLRecognizer recognizer;
    private XMLData xmlData;
    private String rootElement;
    private String recordElement;
    private XMLRecognizer.FlatteningStrategy strategy;
    private String name;

    /**
     * Constructor for loading existing XML table from header
     */
    public XMLTable(Header header) {
        super(header);
        this.rootElement = header.get("root-element");
        this.recordElement = header.get("record-element");
        String strategyStr = header.get("flattening-strategy");
        this.strategy = strategyStr != null ?
            XMLRecognizer.FlatteningStrategy.valueOf(strategyStr) :
            XMLRecognizer.FlatteningStrategy.NESTED_COLUMNS;

        String path = header.getTablePath();
        Path filePath = Paths.get(path);
        this.name = filePath.getFileName().toString();
    }

    /**
     * Constructor for creating new XML table
     */
    public XMLTable(Header header, String rootElement, String recordElement, XMLRecognizer.FlatteningStrategy strategy) {
        super(header);
        header.set(Header.TABLE_TYPE, "XMLTable");
        header.set("root-element", rootElement != null ? rootElement : "");
        header.set("record-element", recordElement != null ? recordElement : "");
        header.set("flattening-strategy", strategy.name());

        this.rootElement = rootElement;
        this.recordElement = recordElement;
        this.strategy = strategy;

        String path = header.getTablePath();
        Path filePath = Paths.get(path);
        this.name = filePath.getFileName().toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHeaderName() {
        return header.getFileName();
    }

    @Override
    public void create(Prototype prototype, int pageSize) throws Exception {
        // XML tables are read-only, no creation needed
    }

    @Override
    public void open() throws Exception {
        try {
            String xmlPath = header.getTablePath();
            System.out.println("XMLTable->open: Opening XML file at path: " + xmlPath);

            // Check if this is a separate table
            if (recordElement != null && recordElement.startsWith("SEPARATE_TABLE:")) {
                // Parse the special format: "SEPARATE_TABLE:parentElement:childElement"
                String[] parts = recordElement.split(":");
                if (parts.length == 3) {
                    String parentRecordElement = parts[1];
                    String childElementName = parts[2];

                    this.recognizer = new XMLRecognizer(xmlPath, rootElement, parentRecordElement, strategy);
                    this.xmlData = recognizer.extractSeparateTableData(parentRecordElement, childElementName);
                    System.out.println("XMLTable->open: Successfully loaded " + xmlData.getRecordCount() + " records for separate table");
                } else {
                    throw new InvalidXMLException("Invalid separate table format: " + recordElement);
                }
            } else {
                // Normal table extraction
                this.recognizer = new XMLRecognizer(xmlPath, rootElement, recordElement, strategy);
                this.xmlData = recognizer.extractData();
                System.out.println("XMLTable->open: Successfully loaded " + xmlData.getRecordCount() + " records");
            }
        } catch (InvalidXMLException e) {
            System.err.println("XMLTable->open: Error loading XML: " + e.getMessage());
            throw new DataBaseException("XMLTable->open", e.getMessage());
        } catch (Exception e) {
            System.err.println("XMLTable->open: Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void close() {
        this.recognizer = null;
        this.xmlData = null;
    }

    @Override
    public void flushDB() throws Exception {
        // XML tables are read-only, no flushing needed
    }

    @Override
    public LinkedDataRow addRecord(BasicDataRow dataRow, boolean unique) {
        throw new DataBaseException("XMLTable", "XML tables are read-only");
    }

    @Override
    public LinkedDataRow updateRecord(BasicDataRow dataRow) throws Exception {
        throw new DataBaseException("XMLTable", "XML tables are read-only");
    }

    @Override
    public LinkedDataRow updateRecord(LinkedDataRow dataRow) throws Exception {
        throw new DataBaseException("XMLTable", "XML tables are read-only");
    }

    @Override
    public LinkedDataRow removeRecord(BasicDataRow dataRow) throws Exception {
        throw new DataBaseException("XMLTable", "XML tables are read-only");
    }

    @Override
    public List<LinkedDataRow> getAllRecords() throws Exception {
        if (xmlData == null) {
            return new ArrayList<>();
        }

        List<LinkedDataRow> records = new ArrayList<>();

        for (Map<String, String> rowData : xmlData.getData()) {
            BasicDataRow basicRow = createBasicDataRow(rowData);
            LinkedDataRow linkedRow = basicRow.getLinkedDataRow(prototype);
            records.add(linkedRow);
        }

        return records;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List<LinkedDataRow> getRecords(String columnName, Comparable value, int comparisonType) throws Exception {
        List<LinkedDataRow> allRecords = getAllRecords();
        List<LinkedDataRow> filteredRecords = new ArrayList<>();

        Column column = prototype.getColumn(columnName);
        if (column == null) {
            return filteredRecords;
        }

        for (LinkedDataRow record : allRecords) {
            Object recordValue = record.getField(column.index).getValue();

            boolean matches = switch (comparisonType) {
                case ComparisonTypes.EQUAL -> recordValue.equals(value);
                case ComparisonTypes.GREATER_THAN -> recordValue instanceof Comparable &&
                    ((Comparable) recordValue).compareTo(value) > 0;
                case ComparisonTypes.GREATER_EQUAL_THAN -> recordValue instanceof Comparable &&
                    ((Comparable) recordValue).compareTo(value) >= 0;
                case ComparisonTypes.LOWER_THAN -> recordValue instanceof Comparable &&
                    ((Comparable) recordValue).compareTo(value) < 0;
                case ComparisonTypes.LOWER_EQUAL_THAN -> recordValue instanceof Comparable &&
                    ((Comparable) recordValue).compareTo(value) <= 0;
                case ComparisonTypes.DIFF -> !recordValue.equals(value);
                default -> false;
            };

            if (matches) {
                filteredRecords.add(record);
            }
        }

        return filteredRecords;
    }

    @Override
    public List<LinkedDataRow> getRecords(BasicDataRow rowData) throws Exception {
        // For XML tables, this would typically filter by primary key
        // For now, return all records
        return getAllRecords();
    }

    @Override
    public LinkedDataRow getRecord(BasicDataRow rowData) throws Exception {
        List<LinkedDataRow> records = getRecords(rowData);
        return records.isEmpty() ? null : records.get(0);
    }

    @Override
    public LinkedDataRow getRecord(LinkedDataRow rowData) throws Exception {
        // Convert LinkedDataRow to BasicDataRow for filtering
        BasicDataRow basicRow = new BasicDataRow();
        for (Column column : prototype.getColumns()) {
            Object value = rowData.getField(column.index).getValue();
            if (value != null) {
                switch (column.getType()) {
                    case Column.INTEGER_TYPE -> basicRow.setInt(column.getName(), (Integer) value);
                    case Column.LONG_TYPE -> basicRow.setLong(column.getName(), (Long) value);
                    case Column.FLOAT_TYPE -> basicRow.setFloat(column.getName(), (Float) value);
                    case Column.DOUBLE_TYPE -> basicRow.setDouble(column.getName(), (Double) value);
                    case Column.BOOLEAN_TYPE -> basicRow.setBoolean(column.getName(), (Boolean) value);
                    default -> basicRow.setString(column.getName(), value.toString());
                }
            }
        }
        return getRecord(basicRow);
    }

    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow rowData) throws Exception {
        return getRecords(convertLinkedToBasic(rowData));
    }

    @Override
    public boolean contains(LinkedDataRow pkRow) {
        try {
            return getRecord(pkRow) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<LinkedDataRow> getFilteredRecords(LookupFilter filter) throws Exception {
        // For now, return all records and let external filtering handle it
        // TODO: Implement proper XML-level filtering
        return getAllRecords();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Iterator getAllRecordsIterator() throws Exception {
        return getAllRecords().iterator();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Iterator getFilteredRecordsIterator(LookupFilter filter) throws Exception {
        return getFilteredRecords(filter).iterator();
    }

    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter) throws Exception {
        // For now, return all records
        // TODO: Implement proper filtering
        return getAllRecords();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        return getRecords(pkRow, rowFilter).iterator();
    }

    private BasicDataRow convertLinkedToBasic(LinkedDataRow linkedRow) {
        BasicDataRow basicRow = new BasicDataRow();
        for (Column column : prototype.getColumns()) {
            Object value = linkedRow.getField(column.index).getValue();
            if (value != null) {
                switch (column.getType()) {
                    case Column.INTEGER_TYPE -> basicRow.setInt(column.getName(), (Integer) value);
                    case Column.LONG_TYPE -> basicRow.setLong(column.getName(), (Long) value);
                    case Column.FLOAT_TYPE -> basicRow.setFloat(column.getName(), (Float) value);
                    case Column.DOUBLE_TYPE -> basicRow.setDouble(column.getName(), (Double) value);
                    case Column.BOOLEAN_TYPE -> basicRow.setBoolean(column.getName(), (Boolean) value);
                    default -> basicRow.setString(column.getName(), value.toString());
                }
            }
        }
        return basicRow;
    }

    @Override
    public int getRecordsAmount() throws Exception {
        return xmlData != null ? xmlData.getRecordCount() : 0;
    }

    @Override
    public void printStats() throws Exception {
        System.out.println("XML Table: " + getName());
        System.out.println("Total records: " + getRecordsAmount());
        if (xmlData != null) {
            System.out.println("Total columns: " + xmlData.getColumnCount());
            System.out.println("Root element: " + (rootElement != null ? rootElement : "auto-detected"));
            System.out.println("Record element: " + (recordElement != null ? recordElement : "auto-detected"));
            System.out.println("Strategy: " + strategy);
        }
    }

    @Override
    public Prototype getPrototype() {
        return prototype;
    }

    private BasicDataRow createBasicDataRow(Map<String, String> rowData) {
        BasicDataRow row = new BasicDataRow();

        for (Column column : prototype.getColumns()) {
            String value = rowData.get(column.getName());
            if (value != null) {
                // Use the appropriate setter based on column type
                switch (column.getType()) {
                    case Column.INTEGER_TYPE -> {
                        try {
                            row.setInt(column.getName(), Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            row.setString(column.getName(), value);
                        }
                    }
                    case Column.LONG_TYPE -> {
                        try {
                            row.setLong(column.getName(), Long.parseLong(value));
                        } catch (NumberFormatException e) {
                            row.setString(column.getName(), value);
                        }
                    }
                    case Column.FLOAT_TYPE -> {
                        try {
                            row.setFloat(column.getName(), Float.parseFloat(value));
                        } catch (NumberFormatException e) {
                            row.setString(column.getName(), value);
                        }
                    }
                    case Column.DOUBLE_TYPE -> {
                        try {
                            row.setDouble(column.getName(), Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            row.setString(column.getName(), value);
                        }
                    }
                    case Column.BOOLEAN_TYPE -> {
                        row.setBoolean(column.getName(), Boolean.parseBoolean(value));
                    }
                    default -> row.setString(column.getName(), value);
                }
            }
        }

        return row;
    }

    /**
     * Get the XML data object
     */
    public XMLData getXMLData() {
        return xmlData;
    }

    /**
     * Get the root element name
     */
    public String getRootElement() {
        return rootElement;
    }

    /**
     * Get the record element name
     */
    public String getRecordElement() {
        return recordElement;
    }

    /**
     * Get the flattening strategy
     */
    public XMLRecognizer.FlatteningStrategy getStrategy() {
        return strategy;
    }
}
