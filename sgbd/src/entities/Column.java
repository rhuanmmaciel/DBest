package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import entities.cells.Cell;

import enums.ColumnDataType;

public class Column implements Comparable<Column> {

    public final String NAME;

    public final String SOURCE;

    public final ColumnDataType DATA_TYPE;

    public final Boolean IS_PRIMARY_KEY;

    public final Boolean IS_IGNORED_COLUMN;

    public Column(String NAME, String SOURCE, ColumnDataType DATA_TYPE, boolean IS_PRIMARY_KEY, boolean IS_IGNORED_COLUMN) {
        this.NAME = NAME;
        this.SOURCE = SOURCE;
        this.DATA_TYPE = DATA_TYPE;
        this.IS_PRIMARY_KEY = IS_PRIMARY_KEY;
        this.IS_IGNORED_COLUMN = IS_IGNORED_COLUMN;
    }

    public Column(String NAME, String SOURCE, ColumnDataType DATA_TYPE, boolean IS_PRIMARY_KEY) {
        this(NAME, SOURCE, DATA_TYPE, IS_PRIMARY_KEY, false);
    }

    public Column(String NAME, String SOURCE, ColumnDataType DATA_TYPE) {
        this(NAME, SOURCE, DATA_TYPE, false, false);
    }

    public Column(String NAME, String SOURCE, boolean IS_PRIMARY_KEY) {
        this(NAME, SOURCE, ColumnDataType.NONE, IS_PRIMARY_KEY, false);
    }

    public Column(String NAME, String SOURCE) {
        this(NAME, SOURCE, ColumnDataType.NONE, false, false);
    }

    public String getSourceAndName() {
        return String.format("%s.%s", this.SOURCE, this.NAME);
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

        return column.SOURCE.equals(source) && column.NAME.equals(name);
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
            return this.SOURCE.equals(column.SOURCE) && this.NAME.equals(column.NAME);
        }

        return super.equals(object);
    }

    @Override
    public String toString() {
        return String.format(
            "Name: %s -- Source: %s -- Type: %s -- Primary key: %s",
            this.NAME, this.SOURCE, this.DATA_TYPE, this.IS_PRIMARY_KEY
        );
    }

    @Override
    public int compareTo(@NotNull Column column) {
        if (Objects.equals(column.NAME, this.NAME) && Objects.equals(column.SOURCE, this.SOURCE)) return 0;

        if (Objects.equals(column.NAME, this.NAME)) return 1;

        return -1;
    }
}
