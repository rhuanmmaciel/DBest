package controllers.commands;

import com.mxgraph.view.mxGraph;
import controllers.clipboard.Clipboard;
import gui.frames.main.MainFrame;

/**
 * Command to copy selected cells and edges to clipboard
 */
public class CopyCellsCommand extends BaseCommand {
    
    private final mxGraph graph;
    
    public CopyCellsCommand() {
        this.graph = MainFrame.getGraph();
    }
    
    @Override
    public void execute() {
        Object[] selectedCells = graph.getSelectionCells();
        
        if (selectedCells != null && selectedCells.length > 0) {
            Clipboard.getInstance().copy(selectedCells);
            System.out.println("Copied " + selectedCells.length + " item(s) to clipboard");
        } else {
            System.out.println("No cells selected to copy");
        }
    }
    
    @Override
    public String toString() {
        return "Copy Cells Command";
    }
}
