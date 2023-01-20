package util;

import java.util.ArrayList;
import java.util.List;

import sgbd.prototype.Column;

public class Columns {
	
	public static List<String> getColumns(List<Column> columns){
		
		List<String> names = new ArrayList<>();
		columns.forEach(x -> names.add(x.getName()));
		return names;
		
	}
	
}
