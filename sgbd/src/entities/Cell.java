package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import entities.util.FindRoots;
import enums.OperationArity;
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
	protected Map<Integer, Map<String, String>> content;
	
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
	
	public Boolean checkRules(OperationArity type) {
	
		if(type == OperationArity.UNARY) {
			
			if(this.getParents().size() != 1) {
				
				JOptionPane.showMessageDialog(null, "Operacao unaria", "Erro", JOptionPane.ERROR_MESSAGE);
				return false;
			
			}
			
		}else if(type == OperationArity.BINARY){
			
			if(this.getParents().size() > 2) {
				
				JOptionPane.showMessageDialog(null, "Operacao binaria", "Erro", JOptionPane.ERROR_MESSAGE);
				return false;
				
			}else if(this.getParents().size() < 2) {
				
				return false;
				
			}
		}
		
		return true;
	
	}
	
	
	public String console() {
		/*
	    List<Integer> columnWidths = new ArrayList<>();
	    for (String column : getColumnsName()) {
	        int maxWidth = column.length();
	        for (List<String> row : getContent()) {
	            int width = row.get(getColumnsName().indexOf(column)).length();
	            maxWidth = Math.max(maxWidth, width);
	        }
	        columnWidths.add(maxWidth);
	    }

	    StringBuilder tableFormatted = new StringBuilder();

	    for (int i = 0; i < columnWidths.size(); i++) {
	        tableFormatted.append("+");
	        for (int j = 0; j < columnWidths.get(i) + 2; j++) {
	            tableFormatted.append("-");
	        }
	    }
	    tableFormatted.append("+\n");

	    for (int i = 0; i < getColumnsName().size(); i++) {
	        String column = getColumnsName().get(i);
	        int width = columnWidths.get(i);
	        tableFormatted.append("| ");
	        tableFormatted.append(String.format("%-" + width + "s", column));
	        tableFormatted.append(" ");
	    }
	    tableFormatted.append("|\n");

	    for (int i = 0; i < columnWidths.size(); i++) {
	        tableFormatted.append("+");
	        for (int j = 0; j < columnWidths.get(i) + 2; j++) {
	            tableFormatted.append("-");
	        }
	    }
	    tableFormatted.append("+\n");

	    for (List<String> row : getContent()) {
	        for (int i = 0; i < row.size(); i++) {
	            String value = row.get(i);
	            int width = columnWidths.get(i);
	            tableFormatted.append("| ");
	            tableFormatted.append(String.format("%-" + width + "s", value));
	            tableFormatted.append(" ");
	        }
	        tableFormatted.append("|\n");
	    }

	    for (int i = 0; i < columnWidths.size(); i++) {
	        tableFormatted.append("+");
	        for (int j = 0; j < columnWidths.get(i) + 2; j++) {
	            tableFormatted.append("-");
	        }
	    }
	    tableFormatted.append("+\n");

	    return tableFormatted.toString();
		 */
		return "";
	}

	
	public abstract Operator getData();
	
}
