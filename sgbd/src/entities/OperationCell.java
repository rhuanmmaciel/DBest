package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;

import enums.OperationArity;
import enums.OperationType;

public class OperationCell extends Cell{

	private OperationType type;
	private List<Cell> parents;
	private OperationArity arity;
	
	public OperationCell(String name, String style, mxCell jCell, OperationType type, List<Cell> parents, int x, int y, int length, int width) {
	
		super(name, style, jCell, length, width);
		this.type = type;
		this.parents = new ArrayList<>();
		
		switch(type) {
		
			case SELECTION:
			case PROJECTION:
			case AGGREGATION:
			case RENAME:
				arity = OperationArity.UNARY;
			break;
			
			case UNION:
			case JOIN:
			case LEFTJOIN:
			case CARTESIANPRODUCT:
			case DIFFERENCE:
				arity = OperationArity.BINARY;
		
		}
		
	}
	public OperationType getType() {
		
		return type;
		
	}
	
	public OperationArity getArity() {
		
		return arity;
		
	}
	
	public List<Cell> getParents(){
		
		return parents;
		
	}
	
	public void addParent(Cell cell) {
		parents.add(cell);
	}
	
	public void clearParents() {
		parents = new ArrayList<>();
	}
	
	public Boolean hasParents() {
		return parents.size() != 0;
	}
	
	public Boolean hasTree() {
		return getOperator() != null;
	}
	
	public void setColumns(List<List<Column>> parentColumns, Collection<List<String>> cellColumnsName) {
		
		List<Column> cellColumns = new ArrayList<>();
		
		for(List<String> columnsName : cellColumnsName) {
			
			for(List<Column> columns : parentColumns) {
				
				for(Column column : columns) {
					
					if(columnsName.contains(column.getName())) {
						
						cellColumns.add(column);
						
					}
					
				}
				
			}
			
		}
		
		this.columns = cellColumns;
		
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
	
}
