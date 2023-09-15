package dsl.entities;

import dsl.utils.DslUtils;

import entities.cells.TableCell;

public final class Relation extends Expression<TableCell> {

    private final String name;

    private TableCell cell = null;

    public Relation(String command) {
        super(command);

        this.name = DslUtils.clearTableName(command);
        this.setCoordinates(command);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public TableCell getCell() {
        return this.cell;
    }

    @Override
    public void setCell(TableCell cell) {
        if (this.cell == null) {
            this.cell = cell;
        }
    }
}
