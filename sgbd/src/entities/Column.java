package entities;

import enums.ColumnDataType;

public class Column{

	private String name;
	private String table;
	private ColumnDataType type;
	private Boolean pk;
	
	public Column(String name, ColumnDataType type) {
		
		this.name = name.replaceAll("[^a-zA-Z0-9]", "");
		this.type = type;
		this.pk = false;
		this.table = null;
		
	}
	
	public Column(String name, String tableName, ColumnDataType type, boolean pk, boolean isTableNameAlreadyAdded) {
		
		name.replaceAll("[^a-zA-Z0-9]", "");
		tableName.replaceAll("[^a-zA-Z0-9]", "");
		this.name = isTableNameAlreadyAdded ? name : tableName + "_" + name;
		this.table = tableName;
		this.type = type;
		this.pk = pk;
		
	}
	
	public String getTable() {
		return table;
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