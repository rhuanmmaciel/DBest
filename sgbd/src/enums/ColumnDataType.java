package enums;

public enum ColumnDataType {

    INTEGER   ("integer"),
    LONG      ("long"),
    FLOAT     ("float"),
    DOUBLE    ("double"),
    CHARACTER ("char"),
    STRING    ("string"),
    BOOLEAN   ("bool"),
    NONE      ("none");

    private final String name;

    ColumnDataType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
