package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;

public class TableFormat {

	public static List<List<String>> getRows(Operator operator) {
		
		Operator aux = operator;
		aux.open();
		
		List<List<String>> rows = new ArrayList<>();
		List<String> columnsName = new ArrayList<>();
		
		Tuple tuple = aux.hasNext() ? aux.next() : null;
		
		if(tuple != null) {
	        for (Map.Entry<String, ComplexRowData> line : tuple)
	    		for(Map.Entry<String,byte[]> data:line.getValue()) 
	    			columnsName.add(data.getKey());
	    	
	    	rows.add(columnsName);
		}
		
		//System.out.println(columnsName);
		
	    while(aux.hasNext()){
	    	
	        Tuple t = tuple == null ? aux.next() : tuple;

	        List<String> row = new ArrayList<>();
	        
	        for (Map.Entry<String, ComplexRowData> line : t){
	    		
	            for(Map.Entry<String,byte[]> data:line.getValue()) {
	            	
	            	if(data.getKey().contains("Name") || data.getKey().contains("Sex") || data.getKey().contains("Team") ||
	            			data.getKey().contains("Position") || data.getKey().contains("JobTitle") || data.getKey().contains("Dateofbirth") ||
	            			data.getKey().contains("Phone")){
	            		
			         
	            		row.add(line.getValue().getString(data.getKey()));
	            	
	            	}else {
			        
	            		row.add(line.getValue().getInt(data.getKey()).toString());

	            	}
	        
	            }
	    
	        }

	        rows.add(row);
	        tuple = null;
	        
	    }
	    
	    aux.close();
	    
	    //System.out.println("Table Format= " + rows);
	    
	    return rows;
		
	}
	
}
