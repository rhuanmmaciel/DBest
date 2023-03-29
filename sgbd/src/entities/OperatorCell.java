package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mxgraph.model.mxCell;

import entities.util.TableFormat;
import enums.OperationType;
import sgbd.query.Operator;

public class OperatorCell extends Cell{

	private Operator operator;
	private OperationType type;
	
	public OperatorCell(String name, String style, mxCell jCell, OperationType type, int x,int y,int length,int width) {
	
		super(name, style, jCell, x, y, length, width);
		this.operator = null;
		this.type = type;
		
	}
	
	public void setOperator(Operator operator) {
		
		this.operator = operator;
		this.content = TableFormat.getRows(operator);
		
	}
	
	public Operator getData() {
		
		return operator;
		
	}
	
	public OperationType getType() {
		
		return type;
		
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
	
}
