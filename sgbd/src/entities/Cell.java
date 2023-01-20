package entities;

import java.util.ArrayList;
import java.util.List;

import sgbd.query.Operator;

public abstract class Cell {
	
	private String style;
	private String name;
	private Object cell;
	private List<Cell> parents;
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

	public abstract Operator getData();
	public abstract List<String> getColumns();
	public abstract List<List<String>> getContent();
	
}
