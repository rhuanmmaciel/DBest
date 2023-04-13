package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;

import entities.util.FindRoots;
import entities.util.TableFormat;
import sgbd.query.Operator;

public abstract class Cell {
	
	private Operator operator;
	protected List<Column> columns;
	private String style;
	private String name;
	private mxCell jCell;
	private OperationCell child;
	private int length;
	private int width;
	protected Map<Integer, Map<String, String>> content;
	
	public Cell(String name, String style, mxCell jCell, int length, int width) {
		
		this.columns = new ArrayList<>();
		this.style = style;
		this.name = name;
		this.jCell = jCell;
		this.child= null;
		this.length = length;
		this.width = width;
		this.operator = null;
		
	}
	
	public mxCell getJGraphCell() {
		return jCell;
	}
	
	public void setJGraphCell(mxCell jCell) {
		this.jCell = jCell;
	}
	
	public void setOperator(Operator operator) {
		
		operator.close();
		operator.open();
		this.operator = operator;
		this.content = TableFormat.getRows(operator);
		
	}
	
	public Operator getOperator() {
		
		if(operator == null) return null;
		
		operator.close();
		operator.open();
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
	
	public List<Column> getColumns(){
		return columns;
	}
	
	public List<String> getColumnsName(){
		
		List<String> names = new ArrayList<>();
		
		columns.forEach(x -> names.add(x.getName()));
		
		return names;
		
	}
	
	public List<String> getOnlyColumnsName(){
		
		List<String> names = new ArrayList<>();
		
		columns.forEach(x -> names.add(x.getName().substring(x.getName().indexOf("_")+1)));
		
		return names;
		
	}
	
	public Map<Integer, Map<String, String>> getMapContent(){
		
		return content;
		
	}
	
	public List<List<String>> getListContent() {
		
	    List<List<String>> result = new ArrayList<>();
	    for (Map.Entry<Integer, Map<String, String>> entry : content.entrySet()) {
	        List<String> row = new ArrayList<>();
	        for (String value : entry.getValue().values()) {
	            row.add(value);
	        }
	        result.add(row);
	    }
	    return result;
	    
	}

	public String getSourceTableName(String columnName) {
		
		for(Cell cell : FindRoots.getRoots(this)) {
			
			if(cell.getColumnsName().contains(columnName)) 
				return cell.getName();
		
		}
		
		return null;
		
	}
	
	public List<Cell> getAllSourceTables(){
		
		return FindRoots.getRoots(this);
		
	}
	
	
	
	//jgraph métodos
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getStyle() {
		return style;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getWidth() {
		return width;
	}
	

	@Override
	public String toString() {
		
		return getName();
		
	}

	public abstract boolean hasParents();
	public abstract boolean hasError();
	public abstract boolean hasParentErrors();
	
}
