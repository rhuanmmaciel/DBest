package gui.frames.main;
 
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxRectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class teste extends mxGraphComponent {
    private mxRectangle selectionRectangle = null;
    private Point startPoint = null;

    public teste(mxGraph graph) {
        super(graph);
        initialize();
    }

    private void initialize() {
        // Mouse listeners for rectangle selection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint(); // Store starting point
                selectionRectangle = null; // Reset rectangle
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint != null) {
                    Point endPoint = e.getPoint();
                    selectionRectangle = new mxRectangle(
                            Math.min(startPoint.x, endPoint.x),
                            Math.min(startPoint.y, endPoint.y),
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    );
                    repaint(); // Refresh to show the rectangle
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    Point endPoint = e.getPoint();
                    selectionRectangle = new mxRectangle(
                            Math.min(startPoint.x, endPoint.x),
                            Math.min(startPoint.y, endPoint.y),
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    );
                    repaint(); // Refresh while dragging
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Ensure the component is rendered first
        if (selectionRectangle != null) {
            // Draw the selection rectangle
            g.setColor(new Color(0, 0, 255, 100)); // Semi-transparent blue
            g.fillRect((int) selectionRectangle.getX(), (int) selectionRectangle.getY(),
                       (int) selectionRectangle.getWidth(), (int) selectionRectangle.getHeight());
            g.setColor(Color.BLUE); // Border color
            g.drawRect((int) selectionRectangle.getX(), (int) selectionRectangle.getY(),
                       (int) selectionRectangle.getWidth(), (int) selectionRectangle.getHeight()); // Draw border
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mxGraph graph = new mxGraph();
            Object parent = graph.getDefaultParent();

            // Add some sample cells to the graph
            graph.getModel().beginUpdate();
            try {
                graph.insertVertex(parent, null, "Node 1", 50, 50, 80, 30);
                graph.insertVertex(parent, null, "Node 2", 200, 150, 80, 30);
                graph.insertVertex(parent, null, "Node 3", 100, 100, 80, 30);
            } finally {
                graph.getModel().endUpdate();
            }

            JFrame frame = new JFrame("Persistent Selection Example with mxRectangle");
            teste graphComponent = new teste(graph);
            frame.add(graphComponent);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
