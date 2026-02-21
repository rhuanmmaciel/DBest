package gui.frames;

import controllers.ConstantController;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.CellStats;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import files.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComparatorFrame extends JFrame implements ActionListener {

    private final JTable jTable = new JTable();

    private final JButton btnNext = new JButton(">");
    private final JButton btnAllNext = new JButton(">>");

    private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JLabel lblTuplesLoaded = new JLabel();

    private final Map<Cell, CellStats> allCellStats = new HashMap<>();
    private final Map<Cell, Boolean> tuplesDone = new HashMap<>();
    private final Map<Cell, Integer> totalTuplesLoaded = new HashMap<>();    private final List<Cell> markedCells;
    
    CellStats emptyStats = CellStats.getEmptyStats();    int tuplesLoaded = 0;
    private boolean totalTuplesKnown = false;
    private int totalTuples = 0;    private SwingWorker<Void, Void> tupleLoaderWorker;
    private JDialog cancelDialog;
    private final DecimalFormat numberFormatter = new DecimalFormat("#,###");

    public ComparatorFrame() {

        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        } catch (Exception ignored) {
        }

        this.markedCells = CellUtils
                .getActiveCells()
                .values()
                .stream()
                .filter(Cell::isMarked)
                .filter(x -> {
                    if (x instanceof OperationCell opCell) {
                        return opCell.hasBeenInitialized() && !opCell.hasError();
                    }
                    return true;
                }).toList();

        CellStats.reset();
        CellStats prevStats = null;
        for (Cell cell : markedCells) {

            try {
                cell.openOperator();
            } catch (Exception ex) {
                Logger.getLogger(ComparatorFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            tuplesDone.put(cell, false);
            totalTuplesLoaded.put(cell, 0);
            CellStats stats = CellStats.getTotalCurrentStats();
            if (prevStats != null) {
                stats = CellStats.getTotalCurrentStats().getDiff(prevStats);
            }
            allCellStats.put(cell, stats);
            prevStats = stats;
        };

        updateJTable(true);
        initGUI();

    }

    private void initGUI() {
        this.setMaximumSize(ConstantController.SCREEN_SIZE);

        this.setLayout(new BorderLayout());

        this.add(new JScrollPane(jTable), BorderLayout.CENTER);

        jTable.setEnabled(false);        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (tupleLoaderWorker != null && !tupleLoaderWorker.isDone()) {
                    tupleLoaderWorker.cancel(true);
                }
                MainController.comparatorFrame = null;
                markedCells.forEach(Cell::closeOperator);
            }
        });

        JPanel southPane = new JPanel(new FlowLayout());
        southPane.add(spinner);
        southPane.add(btnNext);
        southPane.add(btnAllNext);

        btnNext.addActionListener(this);
        btnAllNext.addActionListener(this);

        JPanel northPane = new JPanel(new FlowLayout());
        northPane.add(lblTuplesLoaded);
        updateLblText();

        this.add(northPane, BorderLayout.NORTH);
        this.add(southPane, BorderLayout.SOUTH);

        this.requestFocus(true);
        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void updateLblText() {
        String formattedTuplesLoaded = numberFormatter.format(tuplesLoaded);
        String totalText = totalTuplesKnown ? numberFormatter.format(totalTuples) : "???";
        lblTuplesLoaded.setText(ConstantController.getString("comparator.totalTuplesLoaded") + ": " + formattedTuplesLoaded + "/" + totalText);
    }

    private void verifyIfTuplesAreDone() {
        if (tuplesDone.values().stream().allMatch(x -> x)) {
            btnNext.setEnabled(false);
            btnAllNext.setEnabled(false);
        }
    }
    
    private void updateJTable(boolean first) {

        Vector<Vector<String>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("");

        Map<String, List<Long>> cellStats = new TreeMap<>();

        Vector<String> totalTuplesPerCell = new Vector<>(List.of(ConstantController.getString("comparator.tuplesLoaded")));

        int tuplesToRead = (int) spinner.getValue();
        if (first) {
            tuplesToRead = 0;
            tuplesLoaded = 0;
        }
        for (Cell cell : markedCells) {
            
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            if (cell.getAlias().isBlank())
                columnNames.add(cell.getName());
            else columnNames.add(cell.getAlias());
            Pair<Integer, CellStats> currentStats = tuplesDone.get(cell) ? Pair.of(0, emptyStats) : cell.getCellStats(tuplesToRead, CellStats.getTotalCurrentStats());
            
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            
            if (!first) {
                if (!tuplesDone.get(cell) && (int) spinner.getValue() != currentStats.getLeft()) {
                    tuplesDone.put(cell, true);
                }
                
                totalTuplesLoaded.put(cell, totalTuplesLoaded.get(cell) + currentStats.getLeft());
            }

            totalTuplesPerCell.add(String.valueOf(totalTuplesLoaded.get(cell)));            CellStats stats = null;
            if (first) {
                stats = allCellStats.get(cell);
            }
            else {
                stats = allCellStats.containsKey(cell) ? currentStats.getRight().getSum(allCellStats.get(cell)) : currentStats.getRight();
                allCellStats.put(cell, stats);
                tuplesLoaded += currentStats.getLeft();
            }

            for (Map.Entry<String, Long> c : stats.toMap().entrySet()) {
                if (cellStats.containsKey(c.getKey())) {
                    cellStats.get(c.getKey()).add(c.getValue());
                    continue;
                }
                cellStats.put(c.getKey(), new ArrayList<>(List.of(c.getValue())));
            }
        }

        data.add(totalTuplesPerCell);

        for (String parameterName : cellStats.keySet()) {
            Vector<String> row = new Vector<>();
            row.add(ConstantController.getString(parameterName));
            for (Long l : cellStats.get(parameterName)) {
                row.add(String.valueOf(l));
            }
            data.add(row);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        jTable.setModel(model);

        TableColumn firstColumn = jTable.getColumnModel().getColumn(0);

        int maxWidth = 0;
        for (int row = 0; row < jTable.getRowCount(); row++) {
            TableCellRenderer cellRenderer = jTable.getCellRenderer(row, 0);
            Object value = jTable.getValueAt(row, 0);
            Component rendererComponent = cellRenderer.getTableCellRendererComponent(jTable, value, false, false, row, 0);
            maxWidth = Math.max(maxWidth, rendererComponent.getPreferredSize().width);
        }

        firstColumn.setPreferredWidth(maxWidth);
        jTable.revalidate();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        if (btnNext == e.getSource()) {
            loadSinglePage();
        }

        if (btnAllNext == e.getSource()) {
            loadAllTuples();
        }

    }
    
    private static class MovingSquareBar extends JPanel {
        private int x = 0;
        private final int squareSize = 18;
        private final int barWidth = 300;
        private final int barHeight = 15;
        private final int step = 6;
        private final Color squareColor = new Color(0, 180, 0);
        private javax.swing.Timer timer;

        public MovingSquareBar() {
            setPreferredSize(new Dimension(barWidth, barHeight));
            setMinimumSize(new Dimension(barWidth, barHeight));
            setMaximumSize(new Dimension(barWidth, barHeight));
            setBorder(BorderFactory.createLoweredBevelBorder());
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            startAnimation();
        }

        private void startAnimation() {
            timer = new javax.swing.Timer(30, e -> {
                x += step;
                if (x > barWidth - squareSize) {
                    x = 0;
                }
                repaint();
            });
            timer.start();
        }

        public void stopAnimation() {
            if (timer != null) timer.stop();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(squareColor);
            g.fillRoundRect(x, (barHeight - squareSize) / 2, squareSize, squareSize, 6, 6);
        }
    }

    private void loadAllTuples() {
        cancelDialog = new JDialog(this, "Loading All Tuples", true);
        JButton cancelButton = new JButton("Cancel");
        MovingSquareBar movingBar = new MovingSquareBar();

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel("Loading all tuples. You can cancel the operation.");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        movingBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        dialogPanel.add(messageLabel);
        dialogPanel.add(Box.createVerticalStrut(15));
        dialogPanel.add(movingBar);
        dialogPanel.add(Box.createVerticalStrut(15));
        dialogPanel.add(cancelButton);

        cancelDialog.setContentPane(dialogPanel);
        cancelDialog.setSize(400, 150);
        cancelDialog.setLocationRelativeTo(this);
        cancelDialog.setResizable(false);

        tupleLoaderWorker = new SwingWorker<>() {            @Override
            protected Void doInBackground() throws Exception {
                int batchSize = 100;
                
                while (btnAllNext.isEnabled() && !isCancelled()) {
                    try {
                        if (isCancelled() || Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        
                        SwingUtilities.invokeAndWait(() -> spinner.setValue(batchSize));
                        
                        updateJTable(false);
                        
                        verifyIfTuplesAreDone();
                        
                        SwingUtilities.invokeLater(() -> updateLblText());
                        
                        if (isCancelled() || Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        
                        Thread.yield();
                        
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception ex) {
                        Logger.getLogger(ComparatorFrame.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
                return null;
            }            @Override
            protected void done() {
                movingBar.stopAnimation();
                cancelDialog.dispose();
                
                if (isCancelled()) {
                    SwingUtilities.invokeLater(() -> {
                        totalTuplesKnown = false;
                        updateLblText();
                        btnNext.setEnabled(true);
                        btnAllNext.setEnabled(true);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        totalTuplesKnown = true;
                        totalTuples = tuplesLoaded;
                        updateLblText();
                    });
                }
            }
        };

        cancelButton.addActionListener(e -> {
            movingBar.stopAnimation();
            tupleLoaderWorker.cancel(true);
        });

        tupleLoaderWorker.execute();
        cancelDialog.setVisible(true);
    }
    
    private void loadSinglePage() {
        final int backupTuplesLoaded = tuplesLoaded;
        final boolean backupTotalTuplesKnown = totalTuplesKnown;
        final int backupTotalTuples = totalTuples;
        final Map<Cell, CellStats> backupAllCellStats = new HashMap<>();
        final Map<Cell, Boolean> backupTuplesDone = new HashMap<>();
        final Map<Cell, Integer> backupTotalTuplesLoaded = new HashMap<>();
        
        for (Cell cell : markedCells) {
            if (allCellStats.containsKey(cell)) {
                backupAllCellStats.put(cell, allCellStats.get(cell));
            }
            if (tuplesDone.containsKey(cell)) {
                backupTuplesDone.put(cell, tuplesDone.get(cell));
            }
            if (totalTuplesLoaded.containsKey(cell)) {
                backupTotalTuplesLoaded.put(cell, totalTuplesLoaded.get(cell));
            }
        }
        
        final DefaultTableModel backupTableModel = (DefaultTableModel) jTable.getModel();
        
        cancelDialog = new JDialog(this, "Loading Tuples", true);
        JButton cancelButton = new JButton("Cancel");
        MovingSquareBar movingBar = new MovingSquareBar();

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel("Loading tuples. You can cancel the operation.");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        movingBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        dialogPanel.add(messageLabel);
        dialogPanel.add(Box.createVerticalStrut(15));
        dialogPanel.add(movingBar);
        dialogPanel.add(Box.createVerticalStrut(15));
        dialogPanel.add(cancelButton);

        cancelDialog.setContentPane(dialogPanel);
        cancelDialog.setSize(400, 150);
        cancelDialog.setLocationRelativeTo(this);
        cancelDialog.setResizable(false);        tupleLoaderWorker = new SwingWorker<>() {            @Override
            protected Void doInBackground() throws Exception {
                int targetTuples = (int) spinner.getValue();
                int batchSize = Math.min(100, targetTuples);
                int processedSoFar = 0;
                
                final int originalSpinnerValue = targetTuples;
                
                while (processedSoFar < targetTuples && !isCancelled()) {
                    try {
                        int remainingTuples = targetTuples - processedSoFar;
                        int currentBatchSize = Math.min(batchSize, remainingTuples);
                        
                        SwingUtilities.invokeAndWait(() -> spinner.setValue(currentBatchSize));
                        
                        if (isCancelled() || Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        
                        updateJTable(false);
                        processedSoFar += currentBatchSize;
                        
                        if (isCancelled() || Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        
                        Thread.yield();
                        
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception ex) {
                        Logger.getLogger(ComparatorFrame.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
                
                if (!isCancelled()) {
                    SwingUtilities.invokeAndWait(() -> spinner.setValue(originalSpinnerValue));
                } else {
                    SwingUtilities.invokeLater(() -> spinner.setValue(originalSpinnerValue));
                }
                
                return null;
            }            @Override
            protected void done() {
                movingBar.stopAnimation();
                cancelDialog.dispose();
                
                if (isCancelled()) {
                    tuplesLoaded = backupTuplesLoaded;
                    totalTuplesKnown = backupTotalTuplesKnown;
                    totalTuples = backupTotalTuples;
                    
                    allCellStats.clear();
                    tuplesDone.clear();  
                    totalTuplesLoaded.clear();
                    
                    allCellStats.putAll(backupAllCellStats);
                    tuplesDone.putAll(backupTuplesDone);
                    totalTuplesLoaded.putAll(backupTotalTuplesLoaded);
                    
                    SwingUtilities.invokeLater(() -> {
                        jTable.setModel(backupTableModel);
                        jTable.revalidate();
                        jTable.repaint();
                        updateLblText();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        verifyIfTuplesAreDone();
                        updateLblText();
                    });
                }
            }
        };

        cancelButton.addActionListener(e -> {
            movingBar.stopAnimation();
            tupleLoaderWorker.cancel(true);
        });

        tupleLoaderWorker.execute();
        cancelDialog.setVisible(true);
    }
}
