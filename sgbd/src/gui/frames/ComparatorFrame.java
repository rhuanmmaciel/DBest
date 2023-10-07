package gui.frames;

import controllers.ConstantController;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class ComparatorFrame extends JFrame implements ActionListener {

    private JTable jTable = new JTable();

    public ComparatorFrame(){

        populateJTable();
        initGUI();

    }

    private void initGUI() {
        this.setMaximumSize(ConstantController.SCREEN_SIZE);

        this.add(new JScrollPane(jTable));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                MainController.comparatorFrame = null;
            }
        });

        this.requestFocus(true);
        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void populateJTable(){

        List<Cell> markedCells = CellUtils
                .getActiveCells()
                .values()
                .stream()
                .filter(Cell::isMarked)
                .filter(x -> {
                    if(x instanceof OperationCell opCell)
                        return opCell.hasBeenInitialized() && !opCell.hasError();
                    return true;
                }).toList();

        Vector<Vector<String>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("");

        Map<String, List<Long>> cellStats = new TreeMap<>();

        for(Cell cell : markedCells){

            columnNames.add(cell.getName());

            for(Map.Entry<String, Long> c : cell.getCellStats().toMap().entrySet()){

                if(cellStats.containsKey(c.getKey())) {
                    cellStats.get(c.getKey()).add(c.getValue());
                    continue;
                }

                cellStats.put(c.getKey(), new ArrayList<>(List.of(c.getValue())));

            }

        }

        for(String parameterName : cellStats.keySet()){

            Vector<String> row = new Vector<>();

            row.add((parameterName));

            for(Long l : cellStats.get(parameterName))
                row.add(String.valueOf(l));

            data.add(row);

        }

        jTable = new JTable(data, columnNames);

    }

    @Override
    public void actionPerformed(ActionEvent e) {



    }
}
