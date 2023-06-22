package enums;

public enum ColumnDataType{
	
	INTEGER("integer"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),
	CHARACTER("char"),
	STRING("string"),
	BOOLEAN("bool"),
	NONE("none"),
	UNDEFINED("undefined");

	private final String typeName;

	ColumnDataType(String typeName){

		this.typeName = typeName;

	}

	public String getTypeName() {
		return typeName;
	}
}