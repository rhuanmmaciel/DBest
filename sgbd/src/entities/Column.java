package entities;

import enums.ColumnDataType;

import java.util.List;

public class Column{

	private final String name;
	private final String source;
	private final ColumnDataType type;
	private final Boolean pk;
	
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

	public static String putSource(String columnName, String sourceName){
		if(hasSource(columnName))
			return columnName;
		return sourceName+"."+columnName;
	}

	public static boolean hasSource(String txt){
		return txt.contains(".") && txt.indexOf(".") > 0 && txt.indexOf(".") < txt.length() - 1;
	}

	@Override
	public String toString(){
		return getSourceAndName();
	}

}