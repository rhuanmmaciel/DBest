package entities.cells;

import com.mxgraph.model.mxCell;
import entities.Column;
import sgbd.prototype.Prototype;
import sgbd.source.table.Table;

import java.io.File;
import java.util.List;

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

}
