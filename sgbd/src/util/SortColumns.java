package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;

public class SortColumns {

	public static void array(Operator op, String[] columnsName) {
		
	    Operator aux = op;
	    aux.open();
	    
	    if(aux.hasNext()) {
	    	
		    Tuple tuple = aux.next();
		    List<String> keyOrder = new ArrayList<String>();
		
		    for (Map.Entry<String, ComplexRowData> line : tuple) {
		    	for (Map.Entry<String, byte[]> data : line.getValue()) {
		            keyOrder.add(data.getKey());
		        }
		    }
		    
		    Arrays.sort(columnsName, Comparator.comparingInt(keyOrder::indexOf));
		    
	    }
	    aux.close();
	    
	}
	
	public static void list(Operator op, List<String> columnsName) {
		
	    Operator aux = op;
	    aux.open();
	    
	    if(aux.hasNext()) {
	    	
		    Tuple tuple = aux.next();
		    List<String> keyOrder = new ArrayList<String>();
	
		    for (Map.Entry<String, ComplexRowData> line : tuple) {
		        for (Map.Entry<String, byte[]> data : line.getValue()) {
		            keyOrder.add(data.getKey().substring(data.getKey().indexOf("_")+1));
		        }
		    }
	
		    Map<String, Integer> columnPositions = new HashMap<String, Integer>();
		    for (int i = 0; i < keyOrder.size(); i++) {
		        columnPositions.put(keyOrder.get(i), i);
		    }
	
		    Collections.sort(columnsName, Comparator.comparing(column -> columnPositions.get(column)));
	   
	    }
	    aux.close();
	}

	
}
