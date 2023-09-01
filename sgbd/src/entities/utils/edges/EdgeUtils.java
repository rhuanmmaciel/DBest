package entities.utils.edges;

import java.util.Optional;

import com.mxgraph.model.mxICell;

import entities.Edge;
import gui.frames.main.MainFrame;

public class EdgeUtils {

    private EdgeUtils() {

    }

    public static mxICell addEdge(Edge edge, mxICell target) {
        MainFrame.getGraph().getModel().beginUpdate();

        try {
            mxICell edgeCell = (mxICell) MainFrame
                .getGraph()
                .insertEdge(
                    edge.getParent(), null, "",
                    edge.getParent(), target
                );

            EdgeRepository.addEdge(edge, edgeCell);

            return edgeCell;
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
            MainFrame.getGraph().refresh();
        }
    }

    public static Optional<mxICell> removeEdge(Edge edge) {
        Optional<mxICell> edgeCellToRemove = EdgeRepository.getActiveEdge(edge);

        if (edgeCellToRemove.isEmpty()) return Optional.empty();

        MainFrame.getGraph().getModel().beginUpdate();

        try {
            MainFrame.getGraph().getModel().remove(edgeCellToRemove.get());
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
            MainFrame.getGraph().refresh();
        }

        return EdgeRepository.removeEdge(edge);
    }

    public static void removeEdges(mxICell jCell) {
        MainFrame.getGraph().getModel().beginUpdate();

        try {
            Object[] edges = MainFrame.getGraph().getEdges(jCell);
            MainFrame.getGraph().removeCells(edges, true);
        } finally {
            MainFrame.getGraph().getModel().endUpdate();
            MainFrame.getGraph().refresh();
        }
    }
}
