package entities.cells;

import com.mxgraph.model.mxCell;
import entities.Column;
import ibd.table.Table;

import java.util.List;
import ibd.table.prototype.Prototype;

public final class MemoryTableCell extends TableCell {

    /**
     * Construtor para quando o usuário cria a MemoryTable
     *
     * @param jCell Célula do jGraphX
     * @param name Nome da TableCell
     * @param columns Colunas
     * @param table Table do fyi-database
     * @param prototype Prototype do fyi-database
     */
    public MemoryTableCell(mxCell jCell, String name, List<Column> columns, Table table, Prototype prototype) {
        super(jCell, name, columns, table, prototype, null);
    }

    /**
     * Construtor para quando o usuário arrasta a célula para a tela
     *
     * @param tableCell TableCell que já está salva no software
     * @param jCell Célula do jGraphX
     */
    public MemoryTableCell(MemoryTableCell tableCell, mxCell jCell){
        super(jCell, tableCell.getName(), tableCell.getTable(), null);
    }
    
    @Override
    public Cell copy() {
        mxCell newCell;
        try {
            newCell = (mxCell) this.jCell.clone();
        } catch (CloneNotSupportedException e) {
            newCell = new mxCell(this.jCell.getValue(), this.jCell.getGeometry(), this.jCell.getStyle());
        }
        
        MemoryTableCell copy = new MemoryTableCell(this, newCell);
        copy.alias = this.alias;
        return copy;
    }
}
