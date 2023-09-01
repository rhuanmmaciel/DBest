package entities.utils.edges;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mxgraph.model.mxICell;

import entities.Edge;

public class EdgeRepository {

    /**
     * As arestas que foram adicionadas à interface e estão ativas (ainda não foram removidas).
     */
    private static final Map<Edge, mxICell> ACTIVE_EDGES = new HashMap<>();

    /**
     * As arestas que foram adicionadas à interface e estão inativas (já foram removidas).
     */
    private static final Map<Edge, mxICell> INACTIVE_EDGES = new HashMap<>();

    private EdgeRepository() {

    }

    public static void addEdge(Edge edge, mxICell edgeCell) {
        ACTIVE_EDGES.put(edge, edgeCell);
        INACTIVE_EDGES.remove(edge);
    }

    public static Optional<mxICell> removeEdge(Edge edge) {
        mxICell edgeCell = ACTIVE_EDGES.remove(edge);

        if (edgeCell != null) {
            INACTIVE_EDGES.put(edge, edgeCell);
        }

        return Optional.ofNullable(edgeCell);
    }

    public static Optional<mxICell> getActiveEdge(Edge edge) {
        return Optional.ofNullable(ACTIVE_EDGES.get(edge));
    }

    public static Map<Edge, mxICell> getActiveEdges() {
        return Collections.unmodifiableMap(ACTIVE_EDGES);
    }

    public static Optional<mxICell> getInactiveEdge(Edge edge) {
        return Optional.ofNullable(INACTIVE_EDGES.get(edge));
    }

    public static Map<Edge, mxICell> getInactiveEdges() {
        return Collections.unmodifiableMap(INACTIVE_EDGES);
    }
}
