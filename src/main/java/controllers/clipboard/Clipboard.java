package controllers.clipboard;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clipboard class to manage copy/paste functionality for cells and edges
 */
public class Clipboard {
    
    private static final Clipboard instance = new Clipboard();
    
    private List<CopiedItem> copiedItems;
    private boolean hasData;
    
    private Clipboard() {
        this.copiedItems = new ArrayList<>();
        this.hasData = false;
    }
    
    public static Clipboard getInstance() {
        return instance;
    }
    
    public void copy(Object[] selectedCells) {
        this.copiedItems.clear();
        this.hasData = false;
        
        if (selectedCells == null || selectedCells.length == 0) {
            return;
        }
        
        Map<mxCell, mxCell> originalToCopiedMap = new HashMap<>();
        
        for (Object cellObj : selectedCells) {
            if (cellObj instanceof mxCell cell) {
                Cell activeCell = entities.utils.cells.CellUtils.getActiveCell(cell).orElse(null);                if (activeCell != null) {
                    try {
                        Cell copiedActiveCell = deepCopyCell(activeCell);
                        mxCell copiedJCell = (mxCell) cell.clone();
                        
                        originalToCopiedMap.put(cell, copiedJCell);
                        
                        CopiedItem item = new CopiedItem(copiedActiveCell, copiedJCell, CopiedItemType.CELL);
                        this.copiedItems.add(item);
                        
                    } catch (Exception e) {
                        System.err.println("Error copying cell: " + e.getMessage());
                    }
                }
            }
        }
        
        for (Object cellObj : selectedCells) {
            if (cellObj instanceof mxCell cell && cell.isEdge()) {
                mxCell source = (mxCell) cell.getSource();
                mxCell target = (mxCell) cell.getTarget();
                if (originalToCopiedMap.containsKey(source) && originalToCopiedMap.containsKey(target)) {
                    try {
                        mxCell copiedEdge = (mxCell) cell.clone();
                        
                        copiedEdge.setSource(originalToCopiedMap.get(source));
                        copiedEdge.setTarget(originalToCopiedMap.get(target));
                        
                        CopiedItem item = new CopiedItem(null, copiedEdge, CopiedItemType.EDGE);
                        this.copiedItems.add(item);
                        
                    } catch (Exception e) {
                        System.err.println("Error copying edge: " + e.getMessage());
                    }
                }
            }
        }
        
        this.hasData = !this.copiedItems.isEmpty();
    }

    private Cell deepCopyCell(Cell cell) {
        return cell.copy();
    }
    
    public List<CopiedItem> getCopiedItems() {
        return new ArrayList<>(this.copiedItems);
    }
    
    public boolean hasData() {
        return this.hasData;
    }
    
    public void clear() {
        this.copiedItems.clear();
        this.hasData = false;
    }
    
    /**
     * Inner class to represent a copied item
     */
    public static class CopiedItem {
        private final Cell activeCell;
        private final mxCell jCell;
        private final CopiedItemType type;
        
        public CopiedItem(Cell activeCell, mxCell jCell, CopiedItemType type) {
            this.activeCell = activeCell;
            this.jCell = jCell;
            this.type = type;
        }
        
        public Cell getActiveCell() {
            return activeCell;
        }
        
        public mxCell getJCell() {
            return jCell;
        }
        
        public CopiedItemType getType() {
            return type;
        }
    }
    
    /**
     * Enum to distinguish between different types of copied items
     */
    public enum CopiedItemType {
        CELL,
        EDGE
    }
}
