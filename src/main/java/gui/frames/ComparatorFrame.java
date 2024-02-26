package gui.frames;

import controllers.ConstantController;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.CellStats;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.*;

public class ComparatorFrame extends JFrame implements ActionListener {

    private final JTable jTable = new JTable();

    private final JButton btnNext = new JButton(">");
    private final JButton btnAllNext = new JButton(">>");

    private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JLabel lblTuplesLoaded = new JLabel();

    private final Map<Cell, CellStats> allCellStats = new HashMap<>();
    private final Map<Cell, Boolean> tuplesDone = new HashMap<>();
    private final Map<Cell, Integer> totalTuplesLoaded = new HashMap<>();

    private final List<Cell> markedCells;

    int tuplesLoaded = 0;

    public ComparatorFrame(){

        this.markedCells = CellUtils
                .getActiveCells()
                .values()
                .stream()
                .filter(Cell::isMarked)
                .filter(x -> {
                    if(x instanceof OperationCell opCell)
                        return opCell.hasBeenInitialized() && !opCell.hasError();
                    return true;
                }).toList();

        markedCells.forEach(cell -> {
            cell.openOperator();
            tuplesDone.put(cell, false);
            totalTuplesLoaded.put(cell, 0);
        });

        updateJTable();
        initGUI();

    }

    private void initGUI() {
        this.setMaximumSize(ConstantController.SCREEN_SIZE);

        this.setLayout(new BorderLayout());

        this.add(new JScrollPane(jTable), BorderLayout.CENTER);

        jTable.setEnabled(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
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

    private void updateLblText(){
        lblTuplesLoaded.setText(ConstantController.getString("comparator.totalTuplesLoaded")+": " + tuplesLoaded);
    }

    private void verifyIfTuplesAreDone(){

        if(tuplesDone.values().stream().allMatch(x->x)) {
            btnNext.setEnabled(false);
            btnAllNext.setEnabled(false);
        }

    }

    private void updateJTable(){

        Vector<Vector<String>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("");

        Map<String, List<Long>> cellStats = new TreeMap<>();

        Vector<String> totalTuplesPerCell = new Vector<>(List.of(ConstantController.getString("comparator.tuplesLoaded")));

        for(Cell cell : markedCells){

            columnNames.add(cell.getName());

            Pair<Integer, CellStats> currentStats = tuplesDone.get(cell) ? Pair.of(0, allCellStats.get(cell)) : cell.getCellStats((int)spinner.getValue(), CellStats.getTotalCurrentStats());

            if(!tuplesDone.get(cell) && (int)spinner.getValue() != currentStats.getLeft())
                tuplesDone.put(cell, true);

            totalTuplesLoaded.put(cell, totalTuplesLoaded.get(cell) + currentStats.getLeft());

            totalTuplesPerCell.add(String.valueOf(totalTuplesLoaded.get(cell)));

            tuplesLoaded += currentStats.getLeft();

            CellStats stats = allCellStats.containsKey(cell) ? currentStats.getRight().getSum(allCellStats.get(cell)) : currentStats.getRight();

            allCellStats.put(cell, stats);

            for(Map.Entry<String, Long> c : stats.toMap().entrySet()){

                if(cellStats.containsKey(c.getKey())) {
                    cellStats.get(c.getKey()).add(c.getValue());
                    continue;
                }

                cellStats.put(c.getKey(), new ArrayList<>(List.of(c.getValue())));

            }

        }

        data.add(totalTuplesPerCell);

        for(String parameterName : cellStats.keySet()){

            Vector<String> row = new Vector<>();

            row.add(ConstantController.getString(parameterName));

            for(Long l : cellStats.get(parameterName))
                row.add(String.valueOf(l));

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

        if(btnNext == e.getSource()){
            updateJTable();
            verifyIfTuplesAreDone();
            updateLblText();
        }

        if(btnAllNext == e.getSource()){
            while (btnAllNext.isEnabled()){
                updateJTable();
                verifyIfTuplesAreDone();
                updateLblText();
            }
        }

    }
}
