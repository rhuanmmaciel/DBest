package entities.cells;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import controllers.ConstantController;
import entities.Column;
import ibd.table.Table;
import ibd.table.prototype.Prototype;
import ibd.table.xml.XMLTable;

import java.io.File;
import java.util.List;

/**
 * XMLTableCell represents an XML table in the visual editor.
 * It extends TableCell to provide XML-specific functionality.
 */
public final class XMLTableCell extends TableCell {

    private String rootElement;
    private String recordElement;
    private String flatteningStrategy;

    /**
     * Constructor for creating XMLTableCell with mxCell and columns
     */
    public XMLTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File headerFile) {
        super(jCell, name, columns, table, prototype, headerFile);
        initializeXMLProperties(table);
    }

    /**
     * Constructor for creating XMLTableCell with mxCell
     */
    public XMLTableCell(mxCell jCell, String name, Table table, File headerFile) {
        super(jCell, name, table, headerFile);
        initializeXMLProperties(table);
    }

    /**
     * Constructor for creating XMLTableCell without mxCell
     */
    public XMLTableCell(String name, Table table, File headerFile) {
        super(new mxCell(name, new mxGeometry(), ConstantController.J_CELL_FYI_TABLE_STYLE), name, table, headerFile);
        initializeXMLProperties(table);
    }

    /**
     * Copy constructor for XMLTableCell
     */
    public XMLTableCell(XMLTableCell xmlTableCell, mxCell jCell) {
        super(jCell, xmlTableCell.getName(), xmlTableCell.getTable(), xmlTableCell.getHeaderFile());
        this.rootElement = xmlTableCell.rootElement;
        this.recordElement = xmlTableCell.recordElement;
        this.flatteningStrategy = xmlTableCell.flatteningStrategy;
    }

    private void initializeXMLProperties(Table table) {
        if (table instanceof XMLTable) {
            XMLTable xmlTable = (XMLTable) table;
            this.rootElement = xmlTable.getRootElement();
            this.recordElement = xmlTable.getRecordElement();
            this.flatteningStrategy = xmlTable.getStrategy().name();
        }
    }

    /**
     * Get the root element name from the XML
     */
    public String getRootElement() {
        return rootElement;
    }

    /**
     * Set the root element name
     */
    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Get the record element name from the XML
     */
    public String getRecordElement() {
        return recordElement;
    }

    /**
     * Set the record element name
     */
    public void setRecordElement(String recordElement) {
        this.recordElement = recordElement;
    }

    /**
     * Get the flattening strategy used
     */
    public String getFlatteningStrategy() {
        return flatteningStrategy;
    }

    /**
     * Set the flattening strategy
     */
    public void setFlatteningStrategy(String flatteningStrategy) {
        this.flatteningStrategy = flatteningStrategy;
    }

    /**
     * Get the XMLTable instance
     */
    public XMLTable getXMLTable() {
        if (getTable() instanceof XMLTable) {
            return (XMLTable) getTable();
        }
        return null;
    }

    @Override
    public void setOperator(ibd.query.Operation operator) {
        this.operator = operator;
    }

    @Override
    public Cell copy() {
        mxCell newCell;
        try {
            newCell = (mxCell) this.jCell.clone();
        } catch (CloneNotSupportedException e) {
            newCell = new mxCell(this.jCell.getValue(), this.jCell.getGeometry(), this.jCell.getStyle());
        }

        XMLTableCell copy = new XMLTableCell(newCell, this.getName(), this.getTable(), this.getHeaderFile());
        copy.alias = this.alias;
        return copy;
    }

    @Override
    public String toString() {
        return String.format("XMLTableCell{name='%s', rootElement='%s', recordElement='%s', strategy='%s'}",
                           getName(), rootElement, recordElement, flatteningStrategy);
    }

    /**
     * Get additional information about the XML table for display
     */
    public String getXMLInfo() {
        XMLTable xmlTable = getXMLTable();
        if (xmlTable != null && xmlTable.getXMLData() != null) {
            return String.format("XML Table: %d records, %d columns\nRoot: %s\nRecord: %s\nStrategy: %s",
                               xmlTable.getXMLData().getRecordCount(),
                               xmlTable.getXMLData().getColumnCount(),
                               rootElement != null ? rootElement : "auto-detected",
                               recordElement != null ? recordElement : "auto-detected",
                               flatteningStrategy);
        }
        return "XML Table (not loaded)";
    }
}
