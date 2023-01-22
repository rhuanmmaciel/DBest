package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import enums.OperationEnums;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;

public class OperatorCell extends Cell{

	private Operator operator;
	private List<List<String>> content;
	private OperationEnums type;
	
	public OperatorCell(String name, String style, Object cell, OperationEnums type) {
	
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
		
		Operator aux = operator;
		aux.open();
		List<String> columns = new ArrayList<>();
		
		if(aux.hasNext()) {
		
			Tuple t = aux.next();
			for (Map.Entry<String, ComplexRowData> row: t){
	             for(Map.Entry<String,byte[]> data:row.getValue()) {
	            	 
	            	 columns.add(data.getKey());
	            	 
	             }
			 }
		
		}
		aux.close();
		return columns;
		
	}
	
	public Operator getData() {
		
		return operator;
		
	}
	
	public OperationEnums getType() {
		
		return type;
		
	}
	
}
