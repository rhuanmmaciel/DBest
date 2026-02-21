package gui.frames;

import controllers.ConstantController;
import database.TuplesExtractor;
import engine.info.Parameters;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationType;
import files.FileUtils;
import gui.utils.JTableUtils;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;
import ibd.query.Operation;
import ibd.query.QueryStats;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataFrame extends JDialog implements ActionListener {    private final JLabel lblText = new JLabel();

    private final JLabel lblPages = new JLabel();

    private final JLabel lblTuplesLoaded = new JLabel();

    private final JTable table = new JTable();

    private final JButton btnLeft = new JButton();

    private final JButton btnRight = new JButton();

    private final JButton btnAllLeft = new JButton();

    private final JButton btnAllRight = new JButton();

    private final JButton btnStats = new JButton();

    private final JPanel tablePanel = new JPanel(new BorderLayout());

    private JScrollPane scrollPanel;

    private final JTextPane textPane = new JTextPane();

    private FontIcon iconStats;

    private final long INITIAL_PK_SEARCH = QueryStats.PK_SEARCH;

    private final long INITIAL_SORT_TUPLES = QueryStats.SORT_TUPLES;

    private final long INITIAL_COMPARE_FILTER = QueryStats.COMPARE_FILTER;

    private final long INITIAL_RECORDS_READ = Parameters.RECORDS_READ;

    private final long INITIAL_NEXT_CALLS = QueryStats.NEXT_CALLS;

    private final long INITIAL_MEMORY_USAGE = QueryStats.MEMORY_USED;

    private final long INITIAL_BLOCKS_ACCESSED = Parameters.BLOCKS_ACCESSED;

    private final long INITIAL_BLOCKS_LOADED = Parameters.BLOCKS_LOADED;

    private final List<Tuple> rows;

    private final List<String> columnsName;

    private int currentIndex;

    private final Cell cell;

    private Integer lastPage = null;

    private int currentLastPage = -1;

    private int largestElement = -1;

    private SwingWorker<Void, Tuple> tupleLoaderWorker;
    private JDialog cancelDialog;
    
    // Static flag to allow external cancellation (e.g., from OpenDataFrame)
    private static volatile boolean externalCancellationRequested = false;
    
    public static void requestCancellation() {
        externalCancellationRequested = true;
    }
    
    public static void clearCancellationRequest() {
        externalCancellationRequested = false;
    }
    
    public DataFrame(Cell cell) throws Exception {

        super((Window) null, ConstantController.getString("dataframe"));
        this.setModal(true);
        
        // Clear any previous cancellation request
        clearCancellationRequest();

        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        } catch (Exception ignored) {
        }

        if (cell instanceof OperationCell) {
            OperationCell operationCell = (OperationCell) cell;
            this.lblText.setText(operationCell.getType().displayName + ":");
        } else {
            this.lblText.setText(cell.getName() + ":");
        }

        this.cell = cell;

        cell.openOperator();

        this.columnsName = cell.getColumnSourcesAndNames();

        this.rows = new ArrayList<>();

        firstExecution();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                DataFrame.this.closeWindow();
            }
        });

        this.initializeGUI();
        
        // Don't show the dialog if cancellation was requested during processing
        if (externalCancellationRequested) {
            this.dispose();
        }
    }

    private void firstExecution() throws Exception {

        if (cell instanceof OperationCell) {
            OperationCell operationCell = (OperationCell) cell;
            if (operationCell.getType().isSetBasedProcessing) {
                // For set-based operations, directly process all tuples without showing dialog
                // since OpenDataFrame already showed the loading dialog
                currentIndex = 0;
                while (cell.getOperator().hasNext() && !externalCancellationRequested) {
                    ibd.query.Tuple tuple = cell.getOperator().next();
                    rows.add(tuple);
                    largestElement++;
                    
                    // Allow cancellation and UI updates
                    if (Thread.currentThread().isInterrupted() || externalCancellationRequested) {
                        break;
                    }
                }
                
                if (externalCancellationRequested) {
                    // If cancelled, clean up and close this dialog
                    try {
                        // Clear DataFrame data structures
                        if (rows != null) {
                            rows.clear();
                        }
                        this.cell.freeOperatorResources();
                    } catch (Exception e) {
                        // Ignore cleanup errors during cancellation
                    }
                    this.dispose();
                    return;
                }
                
                if (largestElement >= 0) {
                    lastPage = largestElement / 15;
                    currentIndex = 0; // Start at the beginning, not the end
                    this.updateTable(currentIndex);
                } else {
                    this.updateTable(0);
                }
            } else {
                // For non-set-based operations, load only the first page
                currentIndex = 0;
                this.getTuples(currentIndex);
                this.updateTable(currentIndex);
            }
        } else {
            // For tables (non-OperationCell), load only the first page
            currentIndex = 0;
            this.getTuples(currentIndex);
            this.updateTable(currentIndex);
        }
        
        this.updateStats();
        this.verifyButtons();
    }

    private void setIcons() {
        int buttonsSize = 15;

        FontIcon iconLeft = FontIcon.of(Dashicons.CONTROLS_BACK);
        iconLeft.setIconSize(buttonsSize);
        this.btnLeft.setIcon(iconLeft);

        FontIcon iconRight = FontIcon.of(Dashicons.CONTROLS_FORWARD);
        iconRight.setIconSize(buttonsSize);
        this.btnRight.setIcon(iconRight);

        FontIcon iconAllLeft = FontIcon.of(Dashicons.CONTROLS_SKIPBACK);
        iconAllLeft.setIconSize(buttonsSize);
        this.btnAllLeft.setIcon(iconAllLeft);

        FontIcon iconAllRight = FontIcon.of(Dashicons.CONTROLS_SKIPFORWARD);
        iconAllRight.setIconSize(buttonsSize);
        this.btnAllRight.setIcon(iconAllRight);

        this.iconStats = FontIcon.of(Dashicons.BOOK);
        this.iconStats.setIconSize(buttonsSize);
        this.btnStats.setIcon(this.iconStats);
    }

    private void updateTable(int page) throws Exception {
        int firstElement = page * 15;
        int lastElement = page * 15 + 14;

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("");

        //this.getTuples(firstElement, page, lastElement);
        this.currentLastPage = Math.max(this.currentLastPage, page);

        if (!this.rows.isEmpty()) {

            ReferedDataSource sources[] = this.cell.getOperator().getExposedDataSources();
            for (ReferedDataSource source : sources) {
                List<ibd.table.prototype.column.Column> columns = source.prototype.getColumns();
                for (ibd.table.prototype.column.Column column : columns) {
                    model.addColumn(Column.composeSourceAndName(source.alias, column.getName()));
                }
            }

            int i = page * 15 + 1;
            int endOfList = Math.min(lastElement + 1, this.rows.size());

            for (Tuple tuple : this.rows.subList(firstElement, endOfList)) {
                Object[] line = new Object[this.rows.get(firstElement).size() + 1];

                line[0] = i++;

                Map<String, String> currentRow = TuplesExtractor.getRow_(tuple, this.cell.getOperator(), true);
                for (int j = 0; j < currentRow.size(); j++) {
                    line[j + 1] = currentRow.get(model.getColumnName(j + 1));
                }

                model.addRow(line);
            }
        } else {
            model.setColumnIdentifiers(this.columnsName.toArray());
        }

        this.table.setModel(model);

        JTableUtils.preferredColumnWidthByValues(this.table, 0);

        for (int i = 1; i < this.table.getColumnCount(); i++) {
            JTableUtils.preferredColumnWidthByColumnName(this.table, i);
        }

        this.table.getColumnModel().getColumn(0).setResizable(false);

        JTableUtils.setColumnBold(this.table, 0);
        JTableUtils.setNullInRed(this.table);

        this.table.setEnabled(false);
        this.table.setFillsViewportHeight(true);
        this.table.repaint();
    }

    private void getTuples(int page) throws Exception {
        int lastElement = page * 15 + 14;

        if (page > this.currentLastPage) {
            //Map<String, String> row = TuplesExtractor.getRow_(this.cell.getOperator(), true);
            while (this.cell.getOperator().hasNext() && largestElement < lastElement) {
                Tuple tuple = this.cell.getOperator().next();
                this.rows.add(tuple);
                largestElement++;
            }
            
            if (!this.cell.getOperator().hasNext())
                this.lastPage = largestElement / 15;
        }
    }

    private void getAllTuples() throws Exception {
        // Determine dialog title and message based on operation type
        String dialogTitle = "Loading All Tuples";
        String messageText = "Loading all tuples. You can cancel the operation.";
        
        if (cell instanceof OperationCell) {
            OperationCell operationCell = (OperationCell) cell;
            if (operationCell.getType().isSetBasedProcessing) {
                dialogTitle = "Generating " + operationCell.getType().displayName;
                messageText = "Generating " + operationCell.getType().displayName.toLowerCase() + ". You can cancel the operation.";
            }
        }
        
        cancelDialog = new JDialog(this, dialogTitle, true);
        JButton cancelButton = new JButton("Cancel");
        MovingSquareBar movingBar = new MovingSquareBar();

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(messageText);
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
        tupleLoaderWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                while (cell.getOperator().hasNext() && !isCancelled()) {
                    Tuple tuple = cell.getOperator().next();
                    rows.add(tuple);
                    largestElement++;
                }
                return null;
            }

            @Override
            protected void done() {
                movingBar.stopAnimation();
                cancelDialog.dispose();
                
                if (isCancelled()) {
                    // Clean up memory resources when cancelled
                    try {
                        cell.freeOperatorResources();
                    } catch (Exception e) {
                        // Ignore cleanup errors during cancellation
                    }
                    
                    // Set current index to the last page that contains loaded data
                    if (largestElement >= 0) {
                        currentIndex = largestElement / 15;
                    } else {
                        currentIndex = 0;
                    }
                    // Set lastPage to null to show "???" in the display
                    lastPage = null;
                    
                    // Update lblText to show cancellation format with operation name
                    String operationName = "";
                    if (cell instanceof OperationCell) {
                        operationName = ((OperationCell) cell).getType().displayName;
                    } else {
                        operationName = cell.getName();
                    }
                    lblText.setText(operationName + ":");
                    try {
                        updateTable(currentIndex);
                        updateTuplesLoaded();
                        verifyButtons();
                    } catch (Exception ex) {
                        Logger.getLogger(DataFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    lastPage = largestElement / 15;
                    currentIndex = lastPage;
                    try {
                        updateTable(currentIndex);
                        updateTuplesLoaded();
                        verifyButtons();
                    } catch (Exception ex) {
                        Logger.getLogger(DataFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };

        cancelButton.addActionListener(e -> {
            movingBar.stopAnimation();
            tupleLoaderWorker.cancel(true);
            // Also trigger immediate cleanup to free any partially built hash
            try {
                cell.freeOperatorResources();
            } catch (Exception ex) {
                // Ignore cleanup errors during cancellation
            }
        });

        tupleLoaderWorker.execute();
        cancelDialog.setVisible(true);
    }

    private void initializeGUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.setContentPane(contentPane);
        this.setIcons();
        
        this.btnLeft.addActionListener(this);
        this.btnAllLeft.addActionListener(this);
        this.btnRight.addActionListener(this);
        this.btnAllRight.addActionListener(this);
        this.btnStats.addActionListener(this);

        JPanel northPane = new JPanel(new FlowLayout());
        northPane.add(this.lblText);
        northPane.add(this.lblPages);
        northPane.add(this.lblTuplesLoaded);
        northPane.add(this.btnStats);

        this.tablePanel.add(this.table.getTableHeader(), BorderLayout.NORTH);
        this.tablePanel.add(this.table, BorderLayout.CENTER);
        this.scrollPanel = new JScrollPane(this.tablePanel);
        this.scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        this.textPane.setEditable(false);

        JPanel southPane = new JPanel(new FlowLayout());
        southPane.add(this.btnAllLeft);
        southPane.add(this.btnLeft);
        southPane.add(this.btnRight);
        southPane.add(this.btnAllRight);

        contentPane.add(northPane, BorderLayout.NORTH);
        contentPane.add(this.scrollPanel, BorderLayout.CENTER);
        contentPane.add(southPane, BorderLayout.SOUTH);

        this.verifyButtons();

        if (this.table.getRowCount() == 0) {
            this.lblPages.setText("0/0");
        }
        updateTuplesLoaded();

        this.resize();
        this.setLocationRelativeTo(null);
        
        // Only show the dialog if cancellation was not requested
        if (!externalCancellationRequested) {
            this.setVisible(true);
        }
    }

    private void updateTuplesLoaded() {
        lblTuplesLoaded.setText(ConstantController.getString("dataframe.tuplesLoaded") + ":" + rows.size());
    }

    private void resize() {
        this.pack();

        if (this.getWidth() > ConstantController.UI_SCREEN_WIDTH) {
            int height = this.getHeight();
            this.setSize((int) (ConstantController.UI_SCREEN_WIDTH * 0.95), height);
        }
    }

    private void verifyButtons() {
        this.btnLeft.setEnabled(this.currentIndex != 0);
        this.btnAllLeft.setEnabled(this.currentIndex != 0);
        this.btnRight.setEnabled(this.lastPage == null || this.lastPage != this.currentIndex);
        this.btnAllRight.setEnabled(this.lastPage == null || this.lastPage != this.currentIndex);
        this.lblPages.setText(this.currentIndex + 1 + "/" + (this.lastPage == null ? " ???" : this.lastPage + 1));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            if (event.getSource() == this.btnRight) {
                this.currentIndex++;
                this.getTuples(this.currentIndex);
                this.updateTable(this.currentIndex);
            }
            else if (event.getSource() == this.btnLeft) {
                this.currentIndex--;
                this.getTuples(this.currentIndex);
                this.updateTable(this.currentIndex);
            }
            
            if (event.getSource() == this.btnAllLeft) {
                this.currentIndex = 0;
                this.getTuples(this.currentIndex);
                this.updateTable(this.currentIndex);
            }
            else if (event.getSource() == this.btnAllRight) {
                this.getAllTuples();
                if (this.lastPage != null) {
                    this.updateTable(lastPage);
                }
            }
            else if (event.getSource() == this.btnStats) {
                this.alternateScreen();
            }

            this.updateTuplesLoaded();
            this.updateStats();
            this.verifyButtons();
        } catch (Exception ex) {
            Logger.getLogger(DataFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateStats() {

        StringBuilder sb = new StringBuilder();

        sb.append(ConstantController.getString("dataframe.query"))
                .append(":\n")
                .append(ConstantController.getString("PK_SEARCH"))
                .append(" = ")
                .append(QueryStats.PK_SEARCH - this.INITIAL_PK_SEARCH)
                .append("\n")
                .append(ConstantController.getString("SORT_TUPLES"))
                .append(" = ")
                .append(QueryStats.SORT_TUPLES - this.INITIAL_SORT_TUPLES)
                .append("\n")
                .append(ConstantController.getString("COMPARE_FILTER"))
                .append(" = ")
                .append(QueryStats.COMPARE_FILTER - this.INITIAL_COMPARE_FILTER)
                .append("\n")
                .append(ConstantController.getString("NEXT_CALLS"))
                .append(" = ")
                .append(QueryStats.NEXT_CALLS - this.INITIAL_NEXT_CALLS)
                .append("\n")
                .append(ConstantController.getString("MEMORY_USED"))
                .append(" = ")
                .append(QueryStats.MEMORY_USED - this.INITIAL_MEMORY_USAGE)
                .append("\n\n")
                .append(ConstantController.getString("dataframe.disk"))
                .append(":")
                .append("\n")
                .append(ConstantController.getString("RECORDS_READ"))
                .append(" = ")
                .append(Parameters.RECORDS_READ - this.INITIAL_RECORDS_READ)
                .append("\n")
                .append(ConstantController.getString("BLOCKS_LOADED"))
                .append(" = ")
                .append(Parameters.BLOCKS_LOADED - this.INITIAL_BLOCKS_LOADED)
                .append("\n")
                .append(ConstantController.getString("BLOCKS_ACCESSED"))
                .append(" = ")
                .append(Parameters.BLOCKS_ACCESSED - this.INITIAL_BLOCKS_ACCESSED)
                .append("\n")
                .append("\n");

        this.textPane.setText(sb.toString());

        this.revalidate();
    }

    private void alternateScreen() {
        if (this.scrollPanel.isAncestorOf(this.tablePanel)) {
            this.scrollPanel.setViewportView(null);
            this.scrollPanel.setViewportView(this.textPane);
            this.iconStats.setIkon(Dashicons.EDITOR_TABLE);

            this.revalidate();
            this.repaint();

            return;
        }

        this.scrollPanel.setViewportView(null);
        this.scrollPanel.setViewportView(this.tablePanel);
        this.iconStats.setIkon(Dashicons.BOOK);

        this.revalidate();
        this.repaint();
    }
    
    private void closeWindow() {
        if (tupleLoaderWorker != null && !tupleLoaderWorker.isDone()) {
            tupleLoaderWorker.cancel(true);
        }
        
        // Clear DataFrame data structures
        if (rows != null) {
            rows.clear();
        }
        
        this.cell.closeOperator();
        this.cell.freeOperatorResources();
        this.dispose();
    }

    private static class MovingSquareBar extends JPanel {
        private int x = 0;
        private final int squareSize = 18;
        private final int barWidth = 300;
        private final int barHeight = 15;
        private final int step = 6;
        private final Color squareColor = new Color(0, 180, 0);
        private Timer timer;

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
            timer = new Timer(30, e -> {
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
}
