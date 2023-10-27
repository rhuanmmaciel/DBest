package entities.cells;

import com.mxgraph.model.mxCell;
import database.TuplesExtractor;
import entities.Column;
import entities.Coordinates;
import entities.Tree;
import entities.utils.RootFinder;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import org.apache.commons.lang3.tuple.Pair;
import sgbd.query.Operator;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class Cell permits TableCell, OperationCell {

    protected Operator operator;

    protected List<Column> columns;

    protected final String style;

    protected String name;

    protected final mxCell jCell;

    protected OperationCell child;

    protected final int height;

    protected final int width;

    protected Tree tree;

    private boolean isMarked = false;

    protected Cell(String name, mxCell jCell, int width, int height) {
        this.columns = new ArrayList<>();
        this.name = name;
        this.jCell = jCell;
        this.width = width;
        this.height = height;
        this.child = null;
        this.operator = null;
        this.tree = new Tree();

        this.style = jCell != null
            ? jCell.getStyle() : this.isCSVTableCell()
            ? "csv" : this.isFYITableCell()
            ? "fyi" : this.isOperationCell()
            ? "operation" : "";

        CellUtils.addCell(jCell, this);

        if (jCell == null) throw new RuntimeException();
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

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return this.operator;
    }

    public OperationCell getChild() {
        return this.child;
    }

    public void setChild(OperationCell child) {
        this.child = child;
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

    public void openOperator(){
        operator.open();
    }

    public void closeOperator(){
        operator.close();
    }

    public void freeOperatorResources(){
        operator.freeResources();
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

    public boolean isTableCell(){
        return this instanceof TableCell;
    }

    public boolean isOperationCell(){
        return this instanceof OperationCell;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public abstract boolean hasParents();

    public abstract List<Cell> getParents();

    public abstract boolean hasError();

    public abstract boolean hasParentErrors();
}
