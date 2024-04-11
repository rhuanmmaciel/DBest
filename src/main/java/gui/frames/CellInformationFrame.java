package gui.frames;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class CellInformationFrame extends JDialog {

    private final mxCell jCell;

    private Cell cell;

    private final JPanel mainPane = new JPanel();

    private final GridBagConstraints gbc = new GridBagConstraints();


    public CellInformationFrame(mxCell jCell) {
        super((Window) null);
        setModal(true);

        this.jCell = jCell;

        initGUI();
    }

    private void initGUI() {
        setModal(true);

        mainPane.setLayout(new GridBagLayout());

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addCellInfo();

        if (cell.hasParents()) {

            addParentInfo();

        }

        if (cell.hasChild()) {

            addChildInfo();

        }

        if (cell.hasError()) {

            addCellError();

        }

        getContentPane().add(mainPane, BorderLayout.CENTER);
        pack();

        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void addCellInfo(){

        JPanel cellGroupPanel = new JPanel();
        cellGroupPanel.setLayout(new GridBagLayout());
        cellGroupPanel.setBackground(Color.WHITE);
        cellGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblCellGroupTitle = new JLabel(ConstantController.getString("cellInformation.cell") +":");
        gbc.gridx = 0;
        gbc.gridy = 0;
        cellGroupPanel.add(lblCellGroupTitle, gbc);

        JLabel lblCellId = new JLabel(ConstantController.getString("cellInformation.id") +":");
        JLabel lblCellIdInf = new JLabel(jCell.getId());

        gbc.gridx = 0;
        gbc.gridy = 1;
        cellGroupPanel.add(lblCellId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        cellGroupPanel.add(lblCellIdInf, gbc);

        Optional<Cell> optionalCell = CellUtils.getActiveCell(this.jCell);

        if (optionalCell.isEmpty()) return;

        cell = optionalCell.get();

        JLabel lblCellName = new JLabel(ConstantController.getString("cellInformation.name") +":");
        JLabel lblCellNameInf = new JLabel(cell.getName());

        gbc.gridx = 0;
        gbc.gridy = 2;
        cellGroupPanel.add(lblCellName, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        cellGroupPanel.add(lblCellNameInf, gbc);

        JLabel lblCellPosition = new JLabel(ConstantController.getString("cellInformation.position")+":");
        JLabel lblCellPositionInf = new JLabel(cell.getUpperLeftPosition().toString());

        gbc.gridx = 0;
        gbc.gridy = 3;

        cellGroupPanel.add(lblCellPosition, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;

        cellGroupPanel.add(lblCellPositionInf, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPane.add(cellGroupPanel, gbc);

    }

    private void addParentInfo(){

        JPanel parentGroupPanel = new JPanel();
        parentGroupPanel.setLayout(new GridBagLayout());
        parentGroupPanel.setBackground(Color.WHITE);
        parentGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblParentGroupTitle = new JLabel(ConstantController.getString("cellInformation.parent") +":");
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentGroupPanel.add(lblParentGroupTitle, gbc);


        JLabel lblParentId = new JLabel(ConstantController.getString("cellInformation.id") +":");
        JLabel lblParentIdInf = new JLabel( cell.getParents().get(0).getJCell().getId());

        gbc.gridx = 0;
        gbc.gridy = 1;
        parentGroupPanel.add(lblParentId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        parentGroupPanel.add(lblParentIdInf, gbc);

        JLabel lblParent = new JLabel(ConstantController.getString("cellInformation.name") +":");
        JLabel lblParentInf = new JLabel(cell.getParents().get(0).getName());

        gbc.gridx = 0;
        gbc.gridy = 2;
        parentGroupPanel.add(lblParent, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        parentGroupPanel.add(lblParentInf, gbc);

        JLabel lblParentPosition = new JLabel(ConstantController.getString("cellInformation.position")+":");
        JLabel lblParentPositionInf = new JLabel(cell.getParents().get(0).getUpperLeftPosition().toString());

        gbc.gridx = 0;
        gbc.gridy = 3;

        parentGroupPanel.add(lblParentPosition, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;

        parentGroupPanel.add(lblParentPositionInf, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPane.add(parentGroupPanel, gbc);

        if (cell.getParents().size() == 2) {

            addSecondParentInfo(lblParentGroupTitle);

        }

    }

    private void addSecondParentInfo(JLabel lblParentGroupTitle){

        JPanel secondParentGroupPanel = new JPanel();
        secondParentGroupPanel.setLayout(new GridBagLayout());
        secondParentGroupPanel.setBackground(Color.WHITE);
        secondParentGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblSecondParentGroupTitle = new JLabel("2º " + ConstantController.getString("cellInformation.parent") +":");
        lblParentGroupTitle.setText("1º " + ConstantController.getString("cellInformation.parent") +":");
        gbc.gridx = 0;
        gbc.gridy = 0;
        secondParentGroupPanel.add(lblSecondParentGroupTitle, gbc);

        JLabel lblSecondParentId = new JLabel(ConstantController.getString("cellInformation.id") +":");
        JLabel lblSecondParentIdInf = new JLabel(cell.getParents().get(1).getJCell().getId());

        gbc.gridx = 0;
        gbc.gridy = 1;
        secondParentGroupPanel.add(lblSecondParentId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        secondParentGroupPanel.add(lblSecondParentIdInf, gbc);

        JLabel lblSecondParent = new JLabel(ConstantController.getString("cellInformation.name") +":");
        JLabel lblSecondParentInf = new JLabel(cell.getParents().get(1).getName());

        gbc.gridx = 0;
        gbc.gridy = 2;
        secondParentGroupPanel.add(lblSecondParent, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        secondParentGroupPanel.add(lblSecondParentInf, gbc);

        JLabel lblSecondParentPosition = new JLabel(ConstantController.getString("cellInformation.position")+":");
        JLabel lblSecondParentPositionInf = new JLabel(cell.getParents().get(1).getUpperLeftPosition().toString());

        gbc.gridx = 0;
        gbc.gridy = 3;
        secondParentGroupPanel.add(lblSecondParentPosition, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        secondParentGroupPanel.add(lblSecondParentPositionInf, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPane.add(secondParentGroupPanel, gbc);

    }

    private void addChildInfo(){

        JPanel childGroupPanel = new JPanel();
        childGroupPanel.setLayout(new GridBagLayout());
        childGroupPanel.setBackground(Color.WHITE);
        childGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblChildGroupTitle = new JLabel(ConstantController.getString("cellInformation.child") +":");
        gbc.gridx = 0;
        gbc.gridy = 0;
        childGroupPanel.add(lblChildGroupTitle, gbc);


        JLabel lblChildId = new JLabel(ConstantController.getString("cellInformation.id") +":");
        JLabel lblChildIdInf = new JLabel(cell.getChild().getJCell().getId());

        gbc.gridx = 0;
        gbc.gridy = 1;
        childGroupPanel.add(lblChildId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        childGroupPanel.add(lblChildIdInf, gbc);

        JLabel lblChild = new JLabel(ConstantController.getString("cellInformation.name") +":");
        JLabel lblChildInf = new JLabel(cell.getChild().getName());

        gbc.gridx = 0;
        gbc.gridy = 2;
        childGroupPanel.add(lblChild, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        childGroupPanel.add(lblChildInf, gbc);

        JLabel lblChildPosition = new JLabel(ConstantController.getString("cellInformation.position")+":");
        JLabel lblChildPositionInf = new JLabel(cell.getChild().getUpperLeftPosition().toString());

        gbc.gridx = 0;
        gbc.gridy = 3;
        childGroupPanel.add(lblChildPosition, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        childGroupPanel.add(lblChildPositionInf, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPane.add(childGroupPanel, gbc);

    }

    private void addCellError(){

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

}
