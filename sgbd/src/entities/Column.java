package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import entities.cells.Cell;

import enums.ColumnDataType;

public class Column implements Comparable<Column> {

    private final String name;

    private final String source;

    private final ColumnDataType dataType;

    private final Boolean isPrimaryKey;

    public Column(String name, String source, ColumnDataType dataType, boolean isPrimaryKey) {
        this.name = name;
        this.source = source;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
    }

    public Column(String name, String source, ColumnDataType dataType) {
        this(name, source, dataType, false);
    }

    public Column(String name, String source, boolean isPrimaryKey) {
        this(name, source, ColumnDataType.NONE, isPrimaryKey);
    }

    public Column(String name, String source) {
        this(name, source, ColumnDataType.NONE, false);
    }

    public String getName() {
        return this.name;
    }

    public String getSource() {
        return this.source;
    }

    public String getSourceAndName() {
        return String.format("%s.%s", this.source, this.name);
    }

    public ColumnDataType getDataType() {
        return this.dataType;
    }

    public Boolean getIsPrimaryKey() {
        return this.isPrimaryKey;
    }

    public static String removeName(String sourceAndName) {
        if (!hasSource(sourceAndName)) return sourceAndName;

        return sourceAndName.substring(0, sourceAndName.indexOf("."));
    }

    public static String removeSource(String sourceAndName) {
        if (!hasSource(sourceAndName)) return sourceAndName;

        return sourceAndName.substring(sourceAndName.indexOf(".") + 1);
    }

    public static List<String> getSourcesAndNames(List<Column> columns) {
        if (columns == null) return new ArrayList<>();

        return columns.stream().map(Column::getSourceAndName).toList();
    }

    public static boolean equals(String sourceAndName, String source, String name) {
        if (sourceAndName == null || source == null || name == null) return false;

        return removeName(sourceAndName).equals(source) && removeSource(sourceAndName).equals(name);
    }

    public static boolean equals(Column column, String source, String name) {
        if (column == null || source == null || name == null) return false;

        return column.getSource().equals(source) && column.getName().equals(name);
    }

    public static String composeSourceAndName(String source, String name) {
        if (hasSource(name)) return name;

        return String.format("%s.%s", source, name);
    }

    public static List<String> composeSourceAndName(List<String> names, Cell parent) {
        return names
            .stream()
            .map(name -> composeSourceAndName(parent.getSourceNameByColumnName(name), name))
            .toList();
    }

    public static boolean hasSource(String sourceAndName) {
        return
            sourceAndName != null &&
                sourceAndName.contains(".") &&
                sourceAndName.indexOf(".") < sourceAndName.length() - 1;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Column column) {
            return this.getSource().equals(column.getSource()) && this.getName().equals(column.getName());
        }

        return super.equals(object);
    }

    @Override
    public String toString() {
        return String.format(
            "Source: %s -- Nome: %s -- Tipo: %s -- Primary key: %s",
            this.name, this.source, this.dataType, this.isPrimaryKey
        );
    }

    @Override
    public int compareTo(@NotNull Column column) {
        if (Objects.equals(column.getName(), this.name) && Objects.equals(column.getSource(), this.source)) return 0;

        if (Objects.equals(column.getName(), this.name)) return 1;

        return -1;
    }
}
