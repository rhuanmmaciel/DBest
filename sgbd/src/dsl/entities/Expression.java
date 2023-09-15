package dsl.entities;

import java.util.Optional;

import dsl.utils.DslUtils;

import entities.Coordinates;
import entities.cells.Cell;

public abstract sealed class Expression<T extends Cell> extends Command permits OperationExpression, Relation {

    private Optional<Coordinates> coordinates;

    protected Expression(String command) {
        super(command);
    }

    protected void setCoordinates(String input) {
        this.coordinates = DslUtils.getPosition(input);
    }

    public Optional<Coordinates> getCoordinates() {
        return this.coordinates;
    }

    public abstract T getCell();

    public abstract void setCell(T cell);
}
