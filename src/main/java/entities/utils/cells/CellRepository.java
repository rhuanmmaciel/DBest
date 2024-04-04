package entities.utils.cells;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mxgraph.model.mxICell;

import entities.cells.Cell;

public class CellRepository {

    /**
     * As células que foram adicionadas à interface e estão ativas (ainda não foram removidas).
     */
    private static final Map<mxICell, Cell> ACTIVE_CELLS = new HashMap<>();

    /**
     * As células que foram adicionadas à interface e estão inativas (já foram removidas).
     */
    private static final Map<mxICell, Cell> INACTIVE_CELLS = new HashMap<>();

    private CellRepository() {

    }

    public static void addCell(mxICell jCell, Cell cell) {

        if (jCell != null && cell != null) {
            ACTIVE_CELLS.put(jCell, cell);
            INACTIVE_CELLS.remove(jCell);
        }

    }

    public static Optional<Cell> removeCell(mxICell jCell) {
        if (jCell == null) return Optional.empty();

        Cell cell = ACTIVE_CELLS.remove(jCell);

        if (cell != null) {
            INACTIVE_CELLS.put(jCell, cell);
        }

        return Optional.ofNullable(cell);
    }

    public static Map<mxICell, Cell> removeCells() {
        INACTIVE_CELLS.putAll(ACTIVE_CELLS);
        ACTIVE_CELLS.clear();
        return INACTIVE_CELLS;
    }

    public static boolean activeCellsContainsKey(mxICell jCell) {
        if (jCell == null) return false;

        return ACTIVE_CELLS.containsKey(jCell);
    }

    public static Optional<Cell> getActiveCell(mxICell jCell) {
        if (jCell == null) return Optional.empty();

        return Optional.ofNullable(ACTIVE_CELLS.get(jCell));
    }

    public static Map<mxICell, Cell> getActiveCells() {
        return Collections.unmodifiableMap(ACTIVE_CELLS);
    }

    public static Optional<Cell> getInactiveCell(mxICell jCell) {
        if (jCell == null) return Optional.empty();

        return Optional.ofNullable(INACTIVE_CELLS.get(jCell));
    }

    public static Map<mxICell, Cell> getInactiveCells() {
        return Collections.unmodifiableMap(INACTIVE_CELLS);
    }
}
