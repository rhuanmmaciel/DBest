package entities.cells;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import enums.ColumnDataType;
import static entities.utils.cells.CellUtils.changeCellName;
import ibd.query.sourceop.IndexScan;
import ibd.query.sourceop.SourceOperation;
import ibd.table.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.Prototype;

public abstract sealed class TableCell extends Cell permits CSVTableCell, FYITableCell, JDBCTableCell, MemoryTableCell, XMLTableCell {

    private final String firstName;

    private final Table table;

    private final SourceOperation sourceOperator;

    private final Prototype prototype;

    private final File headerFile;

    protected TableCell(mxCell jCell, String name, Table table, Prototype prototype, File headerFile) {
        super(name, jCell, ConstantController.TABLE_CELL_HEIGHT);
        this.firstName = name;
        this.headerFile = headerFile;
        this.table = table;
        this.prototype = prototype;

        //this.sourceOperator = new TableScan(table);
        this.sourceOperator = new IndexScan(name, table);

        this.setOperator(sourceOperator);
    }

    protected TableCell(
        mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype, File headerFile
    ) {

        this(jCell, name, table, prototype, headerFile);

        this.setColumns(columns);
    }

    protected TableCell(mxCell jCell, String name, Table table, File headerFile) {
        this(jCell, name, table, table.getHeader().getPrototype(), headerFile);

        this.setColumns();
    }

    public Table getTable() {
        return this.table;
    }

    public Prototype getPrototype() {
        return this.prototype;
    }

    public File getHeaderFile() {
        return this.headerFile;
    }

    private void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public boolean hasParents() {
        return false;
    }

    @Override
    public List<Cell> getParents() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasError() {
        return false;
    }

    public void asOperator(String newName){
        sourceOperator.setDataSourceAlias(newName);
        this.alias = newName;
        jCell.setValue(newName);
        adjustWidthSize();
        changeSourceNames(newName);
        TreeUtils.updateTreeBelow(this);
    }

    public void adjustWidthSize(){
        this.width = Math.max(CellUtils.getCellWidth(jCell), ConstantController.TABLE_CELL_WIDTH);
        String a = alias;
        if (!a.isBlank())
            a = ":"+a;
        String formattedName = this.getName()+a;
        changeCellName(jCell, formattedName, ConstantController.TABLE_CELL_WIDTH);

    }

    public void adjustWidthSize_(){
        this.width = Math.max(CellUtils.getCellWidth(jCell), ConstantController.TABLE_CELL_WIDTH);
        changeCellName(jCell, firstName+":"+name, ConstantController.TABLE_CELL_WIDTH);

    }
    private void changeSourceNames(String newName){

        List<Column> newColumns = new ArrayList<>();

        for (Column c : columns)
            newColumns.add(Column.changeSourceColumn(c, newName));

        columns = newColumns;

    }


    public void setColumns() {
        List<ibd.table.prototype.column.Column> prototypeColumns = this.table
            .getPrototype()
            .getColumns()
            .stream()
            .filter(column -> (!(this instanceof CSVTableCell) || !column.getName().equals(ConstantController.PRIMARY_KEY_CSV_TABLE_NAME)))
            .toList();

        List<Column> columns = new ArrayList<>();

        for (ibd.table.prototype.column.Column prototypeColumn : prototypeColumns) {

            ColumnDataType dataType = switch (prototypeColumn.getType()) {
                        case ibd.table.prototype.column.Column.INTEGER_TYPE ->
                            ColumnDataType.INTEGER;
                        case ibd.table.prototype.column.Column.LONG_TYPE ->
                            ColumnDataType.LONG;
                        case ibd.table.prototype.column.Column.FLOAT_TYPE ->
                            ColumnDataType.FLOAT;
                        case ibd.table.prototype.column.Column.DOUBLE_TYPE ->
                            ColumnDataType.DOUBLE;
                        case ibd.table.prototype.column.Column.BOOLEAN_TYPE ->
                            ColumnDataType.BOOLEAN;
                        case ibd.table.prototype.column.Column.STRING_TYPE ->
                            ColumnDataType.STRING;
                        default -> ColumnDataType.NONE;
                    };

            columns.add(new Column(prototypeColumn.getName(), this.getName(), dataType, prototypeColumn.isPrimaryKey()));
        }

        this.setColumns(columns);
    }

    @Override
    public boolean hasParentErrors() {
        return false;
    }
}
