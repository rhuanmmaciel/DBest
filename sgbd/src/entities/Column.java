package entities;

import enums.ColumnDataType;

public class Column{

	private String name;
	private ColumnDataType type;
	private Boolean pk;
	
	public Column(String name, ColumnDataType type) {
		
		this.name = name.replaceAll("[^a-zA-Z0-9_-]", "");
		this.type = type;
		this.pk = false;
		
	}
	
	public Column(String name, String tableName, ColumnDataType type, Boolean pk) {
		
		name.replaceAll("[^a-zA-Z0-9_-]", "");
		tableName.replaceAll("[^a-zA-Z0-9_-]", "");
		this.name = tableName + "." + name;
		this.type = type;
		this.pk = pk;
		
	}

	public String getName() {
		return name;
	}

	public ColumnDataType getType() {
		return type;
	}
	
	public Boolean isPK() {
		
		return pk;
		
	}
	
}