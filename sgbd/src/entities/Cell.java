package entities;

import java.util.ArrayList;
import java.util.List;

import entities.util.FindRoots;
import sgbd.query.Operator;

public abstract class Cell {
	
	protected List<Column> columns;
	private String style;
	private String name;
	private Object cell;
	protected List<Cell> parents;
	private Cell child;
	
	public Cell(String name, String style, Object cell) {
		
		this.columns = new ArrayList<>();
		this.parents = new ArrayList<>();
		this.style = style;
		this.name = name;
		this.cell = cell;
		this.child= null;
		
	}
	
	public void setJGraphCell(Object cell) {
		this.cell = cell;
	}
	
	public Cell getChild() {
		return child;
	}

	public void setChild(Cell child) {
		this.child = child;
	}

	public List<Cell> getParents() {
		return parents;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getStyle() {
		return style;
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
	
	public Object getCell() {
		return cell;
	}
	
	public void addParent(Cell parent) {
		parents.add(parent);
	}
	
	public String getSourceTableName(String columnName) {
		
		for(Cell cell : FindRoots.getRoots(this)) {
			
			if(cell.getColumnsName().contains(columnName)) 
				return cell.getName();
		
		}
		
		return null;
		
	}

	public abstract Operator getData();
	public abstract List<String> getColumnsName();
	public abstract List<List<String>> getContent();
	
}
