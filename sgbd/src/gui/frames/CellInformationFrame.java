package gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

public class CellInformationFrame extends JDialog {

    private mxCell jCell;

    public CellInformationFrame(mxCell jCell) {
        super((Window) null);
        setModal(true);

        this.jCell = jCell;

        initGUI();
    }

    private void initGUI() {
        setModal(true);

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel cellGroupPanel = new JPanel();
        cellGroupPanel.setLayout(new GridBagLayout());
        cellGroupPanel.setBackground(Color.WHITE);
        cellGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblCellGroupTitle = new JLabel("Célula:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        cellGroupPanel.add(lblCellGroupTitle, gbc);

        JLabel lblCellId = new JLabel("Id: ");
        JLabel lblCellIdInf = new JLabel(jCell.getId());

        gbc.gridx = 0;
        gbc.gridy = 1;
        cellGroupPanel.add(lblCellId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        cellGroupPanel.add(lblCellIdInf, gbc);

        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.jCell);

        if (optionalCell.isEmpty()) return;

        Cell cell = optionalCell.get();

        JLabel lblCellName = new JLabel("Nome: ");
        JLabel lblCellNameInf = new JLabel(cell.getName());

        gbc.gridx = 0;
        gbc.gridy = 2;
        cellGroupPanel.add(lblCellName, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        cellGroupPanel.add(lblCellNameInf, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPane.add(cellGroupPanel, gbc);

        if (cell.hasParents()) {

            JPanel parentGroupPanel = new JPanel();
            parentGroupPanel.setLayout(new GridBagLayout());
            parentGroupPanel.setBackground(Color.WHITE);
            parentGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel lblParentGroupTitle = new JLabel("Pai:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            parentGroupPanel.add(lblParentGroupTitle, gbc);

            JLabel lblParent = new JLabel("Nome: ");
            JLabel lblParentInf = new JLabel(cell.getParents().get(0).getName());

            gbc.gridx = 0;
            gbc.gridy = 1;
            parentGroupPanel.add(lblParent, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            parentGroupPanel.add(lblParentInf, gbc);

            JLabel lblParentId = new JLabel("ID: " + cell.getParents().get(0).getJCell().getId());

            gbc.gridx = 0;
            gbc.gridy = 2;
            parentGroupPanel.add(lblParentId, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            mainPane.add(parentGroupPanel, gbc);

            if (cell.getParents().size() == 2) {

                JPanel otherParentGroupPanel = new JPanel();
                otherParentGroupPanel.setLayout(new GridBagLayout());
                otherParentGroupPanel.setBackground(Color.WHITE);
                otherParentGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                JLabel lblOtherParentGroupTitle = new JLabel("2º Pai:");
                lblParentGroupTitle.setText("1º Pai");
                gbc.gridx = 0;
                gbc.gridy = 0;
                otherParentGroupPanel.add(lblOtherParentGroupTitle, gbc);

                JLabel lblOtherParent = new JLabel("Nome: ");
                JLabel lblOtherParentInf = new JLabel(cell.getParents().get(1).getName());

                gbc.gridx = 0;
                gbc.gridy = 1;
                otherParentGroupPanel.add(lblOtherParent, gbc);

                gbc.gridx = 1;
                gbc.gridy = 1;
                otherParentGroupPanel.add(lblOtherParentInf, gbc);

                JLabel lblOtherParentId = new JLabel("ID: " + cell.getParents().get(1).getJCell().getId());

                gbc.gridx = 0;
                gbc.gridy = 2;
                otherParentGroupPanel.add(lblOtherParentId, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                mainPane.add(otherParentGroupPanel, gbc);
            }
        }

        if (cell.hasChild()) {

            JPanel childGroupPanel = new JPanel();
            childGroupPanel.setLayout(new GridBagLayout());
            childGroupPanel.setBackground(Color.WHITE);
            childGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel lblChildGroupTitle = new JLabel("Filho:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            childGroupPanel.add(lblChildGroupTitle, gbc);

            JLabel lblChild = new JLabel("Nome: ");
            JLabel lblChildInf = new JLabel(cell.getChild().getName());

            gbc.gridx = 0;
            gbc.gridy = 1;
            childGroupPanel.add(lblChild, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            childGroupPanel.add(lblChildInf, gbc);

            JLabel lblChildId = new JLabel("ID: " + cell.getChild().getJCell().getId());

            gbc.gridx = 0;
            gbc.gridy = 2;
            childGroupPanel.add(lblChildId, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            mainPane.add(childGroupPanel, gbc);
        }

        if (cell.hasError()) {

            JPanel errorPanel = new JPanel();
            errorPanel.setLayout(new GridBagLayout());
            errorPanel.setBackground(Color.WHITE);
            errorPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

            JLabel lblErrorMessage = new JLabel(((OperationCell) cell).getErrorMessage());
            lblErrorMessage.setForeground(Color.RED);

            gbc.gridx = 0;
            gbc.gridy = 0;
            errorPanel.add(lblErrorMessage, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            mainPane.add(errorPanel, gbc);

        }

        getContentPane().add(mainPane, BorderLayout.CENTER);
        pack();

        setLocationRelativeTo(null);

        setVisible(true);
    }
}
