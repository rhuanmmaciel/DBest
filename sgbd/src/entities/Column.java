package entities;

import entities.cells.Cell;
import enums.ColumnDataType;

import java.util.List;

public class Column{

	private final String name;
	private final String source;
	private ColumnDataType type;
	private final Boolean pk;
	
	public Column(String name, String tableName, ColumnDataType type, boolean pk) {
		
		this.name = name;
		this.source = tableName;
		this.type = type;
		this.pk = pk;
		
	}

	public Column(String name, String tableName){

		this.pk = false;
		this.name = name;
		this.source = tableName;

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
		if(!hasSource(txt)) return txt;
		return txt.substring(txt.indexOf(".")+1);
	}

	public static String removeName(String txt){
		if(!hasSource(txt)) return txt;
		return txt.substring(0, txt.indexOf("."));
	}

	public static List<String> sourceAndNameTogether(List<Column> columns){
		return columns.stream().map(Column::getSourceAndName).toList();
	}

	public static boolean columnEquals(String columnAndSource, String column, String source){

		return removeName(columnAndSource).equals(source) && removeSource(columnAndSource).equals(column);

	}

	public static boolean columnEquals(Column c, String column, String source){

		return c.getSource().equals(source) && c.getName().equals(column);

	}

	public static String putSource(String columnName, String sourceName){
		if(hasSource(columnName))
			return columnName;
		return sourceName+"."+columnName;
	}

	public static List<String> putSource(List<String> args, Cell parentCell){

		return args.stream().map(x -> Column.putSource(x, parentCell.getSourceTableNameByColumn(x))).toList();

	}

	public static boolean hasSource(String txt){
		return txt.contains(".") && txt.indexOf(".") > 0 && txt.indexOf(".") < txt.length() - 1;
	}

	@Override
	public boolean equals(Object obj) {

		if(obj instanceof Column columnObj)
			return getSource().equals(columnObj.getSource()) && getName().equals(columnObj.getName());

		return super.equals(obj);
	}

	@Override
	public String toString(){
		return "Nome:" + getName() + "--" +
				"Source:" + getSource() + "--" +
				"Tipo:" + getType() + "--" +
				"PK:" + isPK();
	}

}