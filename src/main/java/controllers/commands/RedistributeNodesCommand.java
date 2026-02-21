package controllers.commands;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import entities.Tree;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.main.MainFrame;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Comando para redistribuir nós de uma árvore em layout hierárquico
 * a partir de um nó selecionado como referência.
 */
public class RedistributeNodesCommand implements UndoableRedoableCommand {

    private final mxCell selectedCell;
    private final Map<mxCell, mxGeometry> originalPositions;
    private final Map<mxCell, mxGeometry> newPositions;

    public RedistributeNodesCommand(mxCell selectedCell) {
        this.selectedCell = selectedCell;
        this.originalPositions = new HashMap<>();
        this.newPositions = new HashMap<>();
    }

    @Override
    public void execute() {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(selectedCell);
        if (optionalCell.isEmpty()) {
            return;
        }

        Cell rootCell = optionalCell.get();
        Tree tree = rootCell.getTree();

        if (tree == null) {
            return;
        }

        // Salvar posições originais
        saveOriginalPositions(tree);

        // Calcular novas posições usando layout hierárquico
        calculateNewPositions(rootCell);

        // Aplicar novas posições
        applyNewPositions();
    }

    @Override
    public void undo() {
        // Restaurar posições originais
        mxGraph graph = MainFrame.getGraph();
        mxGraphComponent graphComponent = MainFrame.getGraphComponent();

        graph.getModel().beginUpdate();
        try {
            for (Map.Entry<mxCell, mxGeometry> entry : originalPositions.entrySet()) {
                mxCell cell = entry.getKey();
                mxGeometry originalGeometry = entry.getValue();
                mxGeometry currentGeometry = cell.getGeometry();

                if (currentGeometry != null && originalGeometry != null) {
                    currentGeometry.setX(originalGeometry.getX());
                    currentGeometry.setY(originalGeometry.getY());
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // Refresh do grafo
        graph.refresh();

        // Garantir que os nodos redistribuídos fiquem visíveis na tela
        ensureRedistributedNodesVisible(graphComponent, graph);
    }

    @Override
    public void redo() {
        // Reaplicar novas posições
        applyNewPositions();
    }

    @Override
    public String getName() {
        return "Reorganizar Árvore";
    }

    private void saveOriginalPositions(Tree tree) {
        Set<Cell> cells = tree.getCells();
        for (Cell cell : cells) {
            mxCell jCell = cell.getJCell();
            if (jCell != null && jCell.getGeometry() != null) {
                mxGeometry originalGeometry = (mxGeometry) jCell.getGeometry().clone();
                originalPositions.put(jCell, originalGeometry);
            }
        }
    }

    private void calculateNewPositions(Cell rootCell) {
        // Configurações do layout
        final double VERTICAL_SPACING = 80.0;
        final double MIN_HORIZONTAL_SPACING = 50.0; // Espaçamento mínimo entre nodos

        // Obter posição de referência do nó selecionado
        mxGeometry rootGeometry = rootCell.getJCell().getGeometry();
        double rootX = rootGeometry.getX();
        double rootY = rootGeometry.getY();

        // Organizar nós por níveis hierárquicos
        Map<Integer, List<Cell>> levelMap = organizeCellsByLevels(rootCell);

        // Calcular posições para cada nível
        for (Map.Entry<Integer, List<Cell>> entry : levelMap.entrySet()) {
            int level = entry.getKey();
            List<Cell> cellsInLevel = entry.getValue();

            // Calcular Y baseado no nível
            double levelY = rootY + (level * VERTICAL_SPACING);

            // Calcular espaçamento dinâmico baseado no tamanho dos títulos
            double totalLevelWidth = calculateLevelWidth(cellsInLevel);
            double startX = rootX - (totalLevelWidth / 2.0);

            double currentX = startX;
            for (Cell cell : cellsInLevel) {
                mxCell jCell = cell.getJCell();

                if (jCell != null && jCell.getGeometry() != null) {
                    // Usar a largura real da geometria do nodo (que inclui padding interno)
                    double cellWidth = jCell.getGeometry().getWidth();

                    // Posicionar o nodo
                    double newX = currentX;
                    double newY = levelY;

                    mxGeometry newGeometry = (mxGeometry) jCell.getGeometry().clone();
                    newGeometry.setX(newX);
                    newGeometry.setY(newY);
                    newPositions.put(jCell, newGeometry);

                    // Avançar para a próxima posição
                    currentX += cellWidth + MIN_HORIZONTAL_SPACING;
                }
            }
        }
    }

    /**
     * Calcula a largura total necessária para um nível, considerando
     * a largura real dos nodos e o espaçamento mínimo entre eles.
     */
    private double calculateLevelWidth(List<Cell> cellsInLevel) {
        if (cellsInLevel.isEmpty()) {
            return 0.0;
        }

        final double MIN_HORIZONTAL_SPACING = 50.0;
        double totalWidth = 0.0;

        for (int i = 0; i < cellsInLevel.size(); i++) {
            Cell cell = cellsInLevel.get(i);
            mxCell jCell = cell.getJCell();

            if (jCell != null && jCell.getGeometry() != null) {
                // Usar a largura real da geometria do nodo
                totalWidth += jCell.getGeometry().getWidth();

                // Adicionar espaçamento (exceto para o último nodo)
                if (i < cellsInLevel.size() - 1) {
                    totalWidth += MIN_HORIZONTAL_SPACING;
                }
            }
        }

        return totalWidth;
    }

    private Map<Integer, List<Cell>> organizeCellsByLevels(Cell rootCell) {
        Map<Integer, List<Cell>> levelMap = new HashMap<>();
        Queue<CellLevel> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();

        // Começar com o nó raiz no nível 0
        queue.offer(new CellLevel(rootCell, 0));
        visited.add(rootCell);

        while (!queue.isEmpty()) {
            CellLevel current = queue.poll();
            Cell cell = current.cell;
            int level = current.level;

            // Adicionar célula ao nível correspondente
            levelMap.computeIfAbsent(level, k -> new ArrayList<>()).add(cell);

            // Adicionar filhos (pais na estrutura de árvore de consulta)
            if (cell.hasParents()) {
                for (Cell parent : cell.getParents()) {
                    if (!visited.contains(parent)) {
                        queue.offer(new CellLevel(parent, level + 1));
                        visited.add(parent);
                    }
                }
            }
        }

        return levelMap;
    }

    private void applyNewPositions() {
        mxGraph graph = MainFrame.getGraph();
        mxGraphComponent graphComponent = MainFrame.getGraphComponent();

        graph.getModel().beginUpdate();
        try {
            for (Map.Entry<mxCell, mxGeometry> entry : newPositions.entrySet()) {
                mxCell cell = entry.getKey();
                mxGeometry newGeometry = entry.getValue();
                mxGeometry currentGeometry = cell.getGeometry();

                if (currentGeometry != null) {
                    currentGeometry.setX(newGeometry.getX());
                    currentGeometry.setY(newGeometry.getY());
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // Refresh do grafo
        graph.refresh();

        // Garantir que os nodos redistribuídos fiquem visíveis na tela
        ensureRedistributedNodesVisible(graphComponent, graph);
    }

    /**
     * Garante que os nodos redistribuídos fiquem visíveis na tela após o reposicionamento.
     * Ajusta o zoom e a posição da visualização se necessário, considerando apenas os nodos que foram movidos.
     */
    private void ensureRedistributedNodesVisible(mxGraphComponent graphComponent, mxGraph graph) {
        if (newPositions.isEmpty()) {
            return;
        }

        // Calcular bounds apenas dos nodos redistribuídos
        mxRectangle redistributedBounds = calculateRedistributedNodesBounds();

        if (redistributedBounds != null && redistributedBounds.getWidth() > 0 && redistributedBounds.getHeight() > 0) {
            // Obter dimensões da área visível
            Dimension viewportSize = graphComponent.getViewport().getSize();

            // Calcular zoom necessário para mostrar os nodos redistribuídos
            double scaleX = viewportSize.width / (redistributedBounds.getWidth() + 200); // +200 para margem
            double scaleY = viewportSize.height / (redistributedBounds.getHeight() + 200); // +200 para margem
            double newScale = Math.min(scaleX, scaleY);

            // Limitar o zoom para não ficar muito pequeno ou muito grande
            newScale = Math.max(0.3, Math.min(1.5, newScale));

            // Aplicar o novo zoom apenas se necessário (se os nodos estão fora da tela)
            if (newScale < graph.getView().getScale() || !isAreaVisible(redistributedBounds, graphComponent)) {
                graph.getView().setScale(newScale);
            }

            // Centralizar a visualização nos nodos redistribuídos
            Point centerPoint = new Point(
                (int) (redistributedBounds.getCenterX() * graph.getView().getScale()),
                (int) (redistributedBounds.getCenterY() * graph.getView().getScale())
            );

            // Calcular posição do viewport para centralizar
            Point viewPosition = new Point(
                Math.max(0, centerPoint.x - viewportSize.width / 2),
                Math.max(0, centerPoint.y - viewportSize.height / 2)
            );

            // Aplicar a nova posição do viewport
            graphComponent.getViewport().setViewPosition(viewPosition);

            // Refresh final
            graphComponent.refresh();
            graphComponent.repaint();
        }
    }

    /**
     * Calcula os bounds (área retangular) que engloba todos os nodos redistribuídos.
     */
    private mxRectangle calculateRedistributedNodesBounds() {
        if (newPositions.isEmpty()) {
            return null;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Map.Entry<mxCell, mxGeometry> entry : newPositions.entrySet()) {
            mxGeometry geometry = entry.getValue();

            minX = Math.min(minX, geometry.getX());
            minY = Math.min(minY, geometry.getY());
            maxX = Math.max(maxX, geometry.getX() + geometry.getWidth());
            maxY = Math.max(maxY, geometry.getY() + geometry.getHeight());
        }

        return new mxRectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Verifica se uma área está visível no viewport atual.
     */
    private boolean isAreaVisible(mxRectangle area, mxGraphComponent graphComponent) {
        Point viewPosition = graphComponent.getViewport().getViewPosition();
        Dimension viewportSize = graphComponent.getViewport().getSize();
        double scale = graphComponent.getGraph().getView().getScale();

        // Converter área para coordenadas da tela
        double areaScreenX = area.getX() * scale;
        double areaScreenY = area.getY() * scale;
        double areaScreenWidth = area.getWidth() * scale;
        double areaScreenHeight = area.getHeight() * scale;

        // Verificar se a área está dentro do viewport
        return areaScreenX >= viewPosition.x &&
               areaScreenY >= viewPosition.y &&
               (areaScreenX + areaScreenWidth) <= (viewPosition.x + viewportSize.width) &&
               (areaScreenY + areaScreenHeight) <= (viewPosition.y + viewportSize.height);
    }

    /**
     * Classe auxiliar para armazenar célula e seu nível
     */
    private static class CellLevel {
        final Cell cell;
        final int level;

        CellLevel(Cell cell, int level) {
            this.cell = cell;
            this.level = level;
        }
    }
}
