package entities.cells;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.Coordinates;
import entities.Tree;
import entities.utils.FindRoots;
import entities.utils.TreeUtils;
import sgbd.query.Operator;

import java.util.*;

public abstract sealed class Cell permits TableCell, OperationCell {

	protected Operator operator;
	protected List<Column> columns;
	private final String style;
	private String name;
	private final mxCell jCell;
	private OperationCell child;
	private final int height;
	private final int width;
	private Tree tree;
	protected static Map<mxCell, Cell> cells = new HashMap<>();

	public Cell(String name, mxCell jCell, int width, int height) {

		this.columns = new ArrayList<>();
		this.name = name;
		this.jCell = jCell;
		this.child = null;
		this.height = height;
		this.width = width;
		this.operator = null;
		this.tree = new Tree();
		cells.put(jCell, this);
		if(jCell == null) throw new RuntimeException();

		this.style = jCell != null ? jCell.getStyle()
				: isCsvTableCell() ? "csv"
				: isFyiTableCell() ? "fyi"
				: isOperationCell() ? "operation"
				: "";

	}

	public Coordinates getUpperLeftPosition() {

		return new Coordinates((int) jCell.getGeometry().getX(), (int) jCell.getGeometry().getY());

	}

	public Coordinates getLowerRightPosition() {

		return new Coordinates((int) jCell.getGeometry().getX() + (int) jCell.getGeometry().getWidth(),
				(int) jCell.getGeometry().getY() + (int) jCell.getGeometry().getHeight());

	}

	public Tree getTree() {
		return tree;
	}

	public void setAllNewTrees() {

		TreeUtils.updateTree(this);

	}

	public void setNewTree(Tree newTree) {

		this.tree = newTree;

	}

	public mxCell getJGraphCell() {
		return jCell;
	}

	public abstract void setOperator(Operator operator);

	public Operator getOperator() {
		return operator;

	}

	public OperationCell getChild() {
		return child;
	}

	public void setChild(OperationCell child) {
		this.child = child;
	}

	public Boolean hasChild() {
		return child != null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<String> getColumnNames() {

		return getColumns().stream().map(Column::getName).toList();

	}

	public List<String> getColumnSourceNames(){

		return getColumns().stream().map(x -> Column.putSource(x.getName(), x.getSource())).toList();

	}

	public String getSourceTableNameByColumn(String columnName) {

		for (Cell cell : FindRoots.getRoots(this)) {

			if (cell.getColumnNames().contains(columnName))
				return cell.getName();

		}

		return null;

	}

	public Cell getSourceTableByName(String tableName){

		for (Cell cell : FindRoots.getRoots(this))
			if (cell.getName().equals(tableName))
				return cell;

		return null;

	}

	public List<Cell> getAllSourceTables() {

		return FindRoots.getRoots(this);

	}

	public static Map<mxCell, Cell> getCells() {

		return Collections.unmodifiableMap(cells);

	}

	public static void removeFromCells(mxCell cell) {

		cells.remove(cell);

	}

	public static void clearCells() {

		cells.clear();

	}

	public String getStyle() {
		return style;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean isFyiTableCell(){
		return this instanceof FyiTableCell;
	}

	public boolean isCsvTableCell(){
		return this instanceof CsvTableCell;
	}

	public boolean isTableCell(){
		return this instanceof TableCell;
	}

	public boolean isOperationCell(){
		return this instanceof OperationCell;
	}

	@Override
	public String toString() {

		return getName();

	}

	public abstract boolean hasParents();

	public abstract List<Cell> getParents();

	public abstract boolean hasError();

	public abstract boolean hasParentErrors();

}
