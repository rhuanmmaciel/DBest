package entities;

import java.util.ArrayList;
import java.util.List;

import sgbd.query.Operator;
import util.FindRoots;

public abstract class Cell {
	
	private String style;
	private String name;
	private Object cell;
	protected List<Cell> parents;
	private Cell child;
	
	public Cell(String name, String style, Object cell) {
		
		this.parents = new ArrayList<>();
		this.style = style;
		this.name = name;
		this.cell = cell;
		this.child= null;
		
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

	public String getStyle() {
		return style;
	}

	public String getName() {
		return name;
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
