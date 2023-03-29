package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;

import entities.util.FindRoots;
import enums.OperationArity;
import sgbd.query.Operator;

public abstract class Cell {
	
	protected List<Column> columns;
	private String style;
	private String name;
	private mxCell jCell;
	protected List<Cell> parents;
	private Cell child;
	private int x;
	private int y;
	private int length;
	private int width;
	protected Map<Integer, Map<String, String>> content;
	
	public Cell(String name, String style, mxCell jCell, int x, int y, int length, int width) {
		
		this.columns = new ArrayList<>();
		this.parents = new ArrayList<>();
		this.style = style;
		this.name = name;
		this.jCell = jCell;
		this.child= null;
		this.x = x;
		this.y = y;
		this.length = length;
		this.width = width;
		
	}
	
	
	public mxCell getJGraphCell() {
		return jCell;
	}
	
	public void setJGraphCell(mxCell jCell) {
		this.jCell = jCell;
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
		
	    Map<Integer, Map<String, String>> content = getMapContent();

	    Map<String, Integer> columnWidths = new HashMap<>();
	    for (Map<String, String> row : content.values()) {
	        for (Map.Entry<String, String> entry : row.entrySet()) {
	            String column = entry.getKey();
	            String value = entry.getValue();
	            int width = Math.max(columnWidths.getOrDefault(column, 0), value.length());
	            columnWidths.put(column, width);
	        }
	    }

	    StringBuilder tableFormatted = new StringBuilder();
	    tableFormatted.append("+");
	    for (String column : getColumnsName()) {
	        int width = columnWidths.get(column);
	        tableFormatted.append("-");
	        for (int i = 0; i < width + 2; i++) {
	            tableFormatted.append("-");
	        }
	        tableFormatted.append("+");
	    }
	    tableFormatted.append("\n");

	    tableFormatted.append("|");
	    for (String column : getColumnsName()) {
	        int width = columnWidths.get(column);
	        tableFormatted.append(" ");
	        tableFormatted.append(String.format("%-" + width + "s", column));
	        tableFormatted.append(" |");
	    }
	    tableFormatted.append("\n");

	    tableFormatted.append("+");
	    for (String column : getColumnsName()) {
	        int width = columnWidths.get(column);
	        tableFormatted.append("-");
	        for (int i = 0; i < width + 2; i++) {
	            tableFormatted.append("-");
	        }
	        tableFormatted.append("+");
	    }
	    tableFormatted.append("\n");

	    for (Map<String, String> row : content.values()) {
	        tableFormatted.append("|");
	        for (String column : getColumnsName()) {
	            String value = row.getOrDefault(column, "");
	            int width = columnWidths.get(column);
	            tableFormatted.append(" ");
	            tableFormatted.append(String.format("%-" + width + "s", value));
	            tableFormatted.append(" |");
	        }
	        tableFormatted.append("\n");
	    }

	    tableFormatted.append("+");
	    for (String column : getColumnsName()) {
	        int width = columnWidths.get(column);
	        tableFormatted.append("-");
	        for (int i = 0; i < width + 2; i++) {
	            tableFormatted.append("-");
	        }
	        tableFormatted.append("+");
	    }
	    tableFormatted.append("\n");

	    return tableFormatted.toString();
	}


	
	public abstract Operator getData();
	
}
