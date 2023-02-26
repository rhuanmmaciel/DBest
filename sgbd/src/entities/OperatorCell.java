package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import enums.OperationType;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;

public class OperatorCell extends Cell{

	private Operator operator;
	private List<List<String>> content;
	private OperationType type;
	
	public OperatorCell(String name, String style, Object cell, OperationType type) {
	
		super(name, style, cell);
		this.operator = null;
		this.type = type;
		
	}
	
	public void setOperator(Operator operator, List<List<String>> result) {
		
		this.operator = operator;
		this.content = result;
		
	}
	
	public List<List<String>> getContent() {

		return content;
	
	}
	
	public List<String> getColumnsName(){
		
		List<String> columnsName = new ArrayList<>();
		getColumns().forEach(x -> columnsName.add(x.getName()));
		return columnsName;
		
	}
	
	public Operator getData() {
		
		return operator;
		
	}
	
	public OperationType getType() {
		
		return type;
		
	}

	public void setColumns() {
		
		Operator aux = operator;
		aux.open();
		List<String> columnsName = new ArrayList<>();
		
		if(aux.hasNext()) {
		
			Tuple t = aux.next();
			for (Map.Entry<String, ComplexRowData> row: t){
	             for(Map.Entry<String,byte[]> data:row.getValue()) {
	            	 
	            	 columnsName.add(data.getKey().toLowerCase());
	            	 
	             }
			 }
		
		}
		aux.close();
		
		List<Column> columns = new ArrayList<>();
		
		getParents().forEach(x -> columns.addAll(x.getColumns()));
		
		for(Cell cell : getParents()) {
			
			for(Column column : cell.getColumns()) {
				
				if(!columnsName.contains(column.getName().toLowerCase())) {
					
					columns.remove(columns.indexOf(column));
					
				}
				
			}
			
		}

		
		this.columns = columns;
		
	}
	
}
