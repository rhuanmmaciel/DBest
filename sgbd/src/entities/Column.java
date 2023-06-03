package entities;

import enums.ColumnDataType;

public class Column{

	private String name;
	private String source;
	private ColumnDataType type;
	private Boolean pk;
	
	public Column(String name, String tableName, ColumnDataType type, boolean pk) {
		
		this.name = name;
		this.source = tableName;
		this.type = type;
		this.pk = pk;
		
	}
	
	public String getSource() {
		return source;
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