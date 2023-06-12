package entities;

import enums.ColumnDataType;

import java.util.List;

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

	public String getSourceAndName(){
		return source+"."+name;
	}
	
	public ColumnDataType getType() {
		return type;
	}
	
	public Boolean isPK() {
		return pk;
	}

	public static String removeSource(String txt){
		return txt.substring(txt.indexOf(".")+1);
	}

	public static String removeName(String txt){
		return txt.substring(0, txt.indexOf("."));
	}

	public static List<String> sourceAndNameTogether(List<Column> columns){
		return columns.stream().map(Column::getSourceAndName).toList();
	}

	@Override
	public String toString(){
		return getSourceAndName();
	}

}