package entities.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;

public class TableFormat {

	public static List<List<String>> getRows(Operator operator, List<Column> columns){
		
		Operator aux = operator;
		aux.open();
		
		List<List<String>> rows = new ArrayList<>();
		
		Tuple tuple = aux.hasNext() ? aux.next() : null;
		
	    while(aux.hasNext() || tuple != null){
	    	
	        Tuple t = tuple == null ? aux.next() : tuple;

	        List<String> row = new ArrayList<>();
	        
	        for (Map.Entry<String, ComplexRowData> line : t){
	    		
	            for(Map.Entry<String,byte[]> data:line.getValue()) {
	            	
	            	Column column = columns.stream().filter(x -> x.getName().toLowerCase().contains(data.getKey().toLowerCase())).findFirst().orElse(null);
	            	
	            	if(column.getType() == ColumnDataType.STRING){
	            		
	            		row.add(line.getValue().getString(data.getKey()));
	            	
	            	}else if(column.getType() == ColumnDataType.INTEGER){
			        
	            		row.add(line.getValue().getInt(data.getKey()).toString());

	            	}else if(column.getType() == ColumnDataType.FLOAT) {
	            		
	            		row.add(line.getValue().getFloat(data.getKey()).toString());
	            		
	            	}else {
	            		
	            		row.add(line.getValue().getString(data.getKey()));
	            		
	            	}
	            		
	            }
	    
	        }
	        
	        rows.add(row);
	        tuple = null;
	        
	    }
	    
	    aux.close();
	    
	    return rows;
		
	}
	
}
