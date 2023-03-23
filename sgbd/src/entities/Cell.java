package entities;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import entities.util.FindRoots;
import enums.OperationTypeEnums;
import sgbd.query.Operator;

public abstract class Cell {
	
	protected List<Column> columns;
	private String style;
	private String name;
	private Object cell;
	protected List<Cell> parents;
	private Cell child;
	private int x;
	private int y;
	private int length;
	private int width;
	protected List<List<String>> content;
	
	public Cell(String name, String style, Object cell, int x, int y, int length, int width) {
		
		this.columns = new ArrayList<>();
		this.parents = new ArrayList<>();
		this.style = style;
		this.name = name;
		this.cell = cell;
		this.child= null;
		this.x = x;
		this.y = y;
		this.length = length;
		this.width = width;
		
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
	
	public List<String> getColumnsName(){
		
		List<String> names = new ArrayList<>();
		
		columns.forEach(x -> names.add(x.getName()));
		
		return names;
		
	}
	
	public List<List<String>> getContent(){
		
		return content;
		
	}
	
	public Object getCell() {
		return cell;
	}
	
	public void addParent(Cell parent) {
		parents.add(parent);
	}
	
	public int getX() {
		return x;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void setLength(int newLength) {
		this.length = newLength;
	}
	
	public void setWidth(int newWidth) {
		this.width = newWidth;
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
	
	public Boolean checkRules(OperationTypeEnums type) {
	
		if(type == OperationTypeEnums.UNARY) {
			
			if(this.getParents().size() != 1) {
				
				JOptionPane.showMessageDialog(null, "Operacao unaria", "Erro", JOptionPane.ERROR_MESSAGE);
				return false;
			
			}
			
		}else if(type == OperationTypeEnums.BINARY){
			
			if(this.getParents().size() > 2) {
				
				JOptionPane.showMessageDialog(null, "Operacao binaria", "Erro", JOptionPane.ERROR_MESSAGE);
				return false;
				
			}else if(this.getParents().size() < 2) {
				
				return false;
				
			}
		}
		
		return true;
	
	}
	
	public abstract Operator getData();
	
}
