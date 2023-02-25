package entities;

import enums.ColumnDataType;

public class Column{

	private String name;
	private ColumnDataType type;
	
	public Column(String name, ColumnDataType type) {
		
		this.name = name;
		this.type = type;
		
	}

	public String getName() {
		return name;
	}

	public ColumnDataType getType() {
		return type;
	}
	
}
