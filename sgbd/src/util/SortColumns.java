package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Column;
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

	public static void columnsWithMap(List<Column> columns, Map<String, String> firstLine) {
		
	    Comparator<Column> comparator = new Comparator<Column>() {
	        @Override
	        public int compare(Column c1, Column c2) {
	            int index1 = columns.indexOf(c1);
	            int index2 = columns.indexOf(c2);
	            int keyIndex1 = new ArrayList<>(firstLine.keySet()).indexOf(c1.getName());
	            int keyIndex2 = new ArrayList<>(firstLine.keySet()).indexOf(c2.getName());
	            return Integer.compare(index1, index2) - Integer.compare(keyIndex1, keyIndex2);
	        }
	    };
	    
	    Collections.sort(columns, comparator);
	    
	}

	
}
