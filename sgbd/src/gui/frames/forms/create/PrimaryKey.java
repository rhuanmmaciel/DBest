package gui.frames.forms.create;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class PrimaryKey {

	public PrimaryKey(Map<Integer, Map<String, String>> content, StringBuilder pkName, AtomicReference<Boolean> exitReference) {
		
		List<String> columnsName = new LinkedList<>(content.get(0).keySet());
		
		List<String> columnsToRemove = new ArrayList<>();
		
		for(String columnName : columnsName) {
			
			List<String> dataColumn = new ArrayList<>();
			
			for(Map<String, String> inf : content.values()) {
				
				dataColumn.add(inf.get(columnName));
				
			}
			
			Set<String> data = new HashSet<>(dataColumn);
			
			if(dataColumn.contains("") || dataColumn.contains(null) || data.size() != dataColumn.size()) columnsToRemove.add(columnName);
			
		}
		
		columnsName.removeAll(columnsToRemove);
		
		if(columnsName.size() == 0) {
			
			String name = "PrimaryKey";
			int count = 1;
			
			
			while(containName(content, name)) {
				
			    name = "PrimaryKey" + count;
			    count++;
			
			}
			pkName.append(name);
		
		}else
			new FormFramePrimaryKey(content, columnsName, pkName, exitReference);
		
	}
	
	private boolean containName(Map<Integer, Map<String, String>> content, String test) {
		
		String name = test;
		
		return content.get(0).keySet().stream().anyMatch(k -> k.contains(name)); 
		
	}
	
}
