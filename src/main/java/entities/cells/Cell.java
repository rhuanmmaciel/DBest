package entities.cells;

import com.mxgraph.model.mxCell;


import controllers.ConstantController;
import database.TuplesExtractor;
import entities.Column;
import entities.Coordinates;
import entities.Tree;
import entities.utils.RootFinder;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import ibd.query.Operation;
import ibd.query.SingleSource;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract sealed class Cell permits TableCell, OperationCell {

    protected Operation operator;

    protected List<Column> columns;

    protected final String style;

    protected String name;

    protected String alias="";

    protected final mxCell jCell;

    protected OperationCell child;

    protected final int height;

    protected int width;

    protected Tree tree;

    private boolean isMarked = false;

    protected Cell(String name, mxCell jCell, int height) {

        this.columns = new ArrayList<>();
        this.name = name.trim();
        this.jCell = jCell;
        this.width = Math.max(CellUtils.getCellWidth(jCell), ConstantController.TABLE_CELL_WIDTH);
        this.height = height;
        this.child = null;
        this.operator = null;
        this.tree = new Tree();

        this.style =  this.isCSVTableCell()
            ? "csv" : this.isFYITableCell()
            ? "fyi" : this.isXMLTableCell()
            ? "xml" : this.isOperationCell()
            ? "operation" : jCell.getStyle();

        CellUtils.addCell(jCell, this);

    }

    public Coordinates getUpperLeftPosition() {
        return new Coordinates(
            (int) this.jCell.getGeometry().getX(),
            (int) this.jCell.getGeometry().getY()
        );
    }

    public Coordinates getLowerRightPosition() {
        return new Coordinates(
            (int) this.jCell.getGeometry().getX() + (int) this.jCell.getGeometry().getWidth(),
            (int) this.jCell.getGeometry().getY() + (int) this.jCell.getGeometry().getHeight()
        );
    }

    public void markCell(){
        this.isMarked = true;
    }

    public void unmarkCell(){
        this.isMarked = false;
    }

    public boolean isMarked(){
        return isMarked;
    }

    public Tree getTree() {
        return this.tree;
    }

    public void setAllNewTrees() {
        TreeUtils.updateTree(this);
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public mxCell getJCell() {
        return this.jCell;
    }

    public void setOperator(Operation operator) {
        this.operator = operator;
    }

    public Operation getOperator() {
        return this.operator;
    }

    public OperationCell getChild() {
        return this.child;
    }

    public void setChild(OperationCell child) {
        this.child = child;
    }

    public void removeChild() {
        this.child = null;
    }

    public Boolean hasChild() {
        return this.child != null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public List<String> getColumnNames() {
        return this.getColumns().stream().map(x -> x.NAME).toList();
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean canBeParent(){

        if(this instanceof TableCell) return true;

        OperationCell operationCell = (OperationCell) this;

        return operationCell.getType().allwaysAllowConnections || (operationCell.hasBeenInitialized() && !operationCell.hasError() && !operationCell.hasParentErrors());

    }

    public boolean canBeChild(){
        return this instanceof OperationCell;
    }

    public List<String> getColumnSourcesAndNames() {
        return this
            .getColumns()
            .stream()
            .map(column -> Column.composeSourceAndName(column.SOURCE, column.NAME))
            .toList();
    }

    public String getSourceNameByColumnName(String columnName) {
        for (Cell cell : RootFinder.findRoots(this)) {
            if (cell.getColumnNames().contains(columnName)) {
                return cell.name;
            }
        }

        return null;
    }

    public Cell getSourceByTableName(String tableName) {
        for (Cell cell : RootFinder.findRoots(this)) {
            if (cell.getName().equals(tableName)) {
                return cell;
            }
        }

        return null;
    }

    public void openOperator() throws Exception{
            operator.open();
    }

    public void closeOperator(){
        try {
            operator.close();
        } catch (Exception ex) {
            Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void freeOperatorResources(){
        // Simple memory cleanup - call freeResources if available
        if (operator != null) {
            try {
                // Try to clean up hash tables and other data structures
                operator.close();
                
                //operator is reopen in case the processing continues
                openOperator();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    public Pair<Integer, CellStats> getCellStats(int amountOfTuples, CellStats initialCellStats){

        return Pair.of(TuplesExtractor.getRows(operator, true, amountOfTuples).size(), CellStats.getTotalCurrentStats().getDiff(initialCellStats));

    }

    public List<Cell> getSources() {
        return RootFinder.findRoots(this);
    }

    public String getStyle() {
        return this.style;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isFYITableCell(){
        return this instanceof FYITableCell;
    }

    public boolean isCSVTableCell(){
        return this instanceof CSVTableCell;
    }

    public boolean isXMLTableCell(){
        return this instanceof XMLTableCell;
    }

    public boolean isTableCell(){
        return this instanceof TableCell;
    }

    public boolean isOperationCell(){
        return this instanceof OperationCell;
    }

    public boolean alwaysAllowConnections(){
        return false;
    }

    public boolean hasSingleSource(){
        if (operator==null) return false;
        if (operator instanceof SingleSource) return true;

        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public abstract Cell copy();

    public abstract boolean hasParents();

    public abstract List<Cell> getParents();

    public abstract boolean hasError();

    public abstract boolean hasParentErrors();
}
