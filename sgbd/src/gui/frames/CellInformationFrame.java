package gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;

import controller.MainController;

@SuppressWarnings("serial")
public class CellInformationFrame extends JDialog {

	private mxCell jCell;

	public CellInformationFrame(mxCell jCell) {

		super((Window) null);
		setModal(true);

		this.jCell = jCell;

		initGUI();

	}

	private void initGUI() {

	    setLocationRelativeTo(null);

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

	    JLabel lblCellName = new JLabel("Nome: ");
	    JLabel lblCellNameInf = new JLabel(MainController.getCells().get(jCell).getName());

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    cellGroupPanel.add(lblCellName, gbc);

	    gbc.gridx = 1;
	    gbc.gridy = 2;
	    cellGroupPanel.add(lblCellNameInf, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    mainPane.add(cellGroupPanel, gbc);

	    if (MainController.getCells().get(jCell).hasParents()) {

	    	JPanel parentGroupPanel = new JPanel();
	        parentGroupPanel.setLayout(new GridBagLayout());
	        parentGroupPanel.setBackground(Color.WHITE);
	        parentGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	        JLabel lblParentGroupTitle = new JLabel("Pai:");
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        parentGroupPanel.add(lblParentGroupTitle, gbc);

	        JLabel lblParent = new JLabel("Nome: ");
	        JLabel lblParentInf = new JLabel(MainController.getCells().get(jCell).getParents().get(0).getName());

	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        parentGroupPanel.add(lblParent, gbc);

	        gbc.gridx = 1;
	        gbc.gridy = 1;
	        parentGroupPanel.add(lblParentInf, gbc);

	        JLabel lblParentId = new JLabel("ID: " + MainController.getCells().get(jCell).getParents().get(0).getJGraphCell().getId());

	        gbc.gridx = 0;
	        gbc.gridy = 2;
	        parentGroupPanel.add(lblParentId, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        mainPane.add(parentGroupPanel, gbc);

	        if (MainController.getCells().get(jCell).getParents().size() == 2) {

	            JPanel otherParentGroupPanel = new JPanel();
	            otherParentGroupPanel.setLayout(new GridBagLayout());
	            otherParentGroupPanel.setBackground(Color.WHITE);
	            otherParentGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	            JLabel lblOtherParentGroupTitle = new JLabel("Pai:");
	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            otherParentGroupPanel.add(lblOtherParentGroupTitle, gbc);

	            JLabel lblOtherParent = new JLabel("Nome: ");
	            JLabel lblOtherParentInf = new JLabel(MainController.getCells().get(jCell).getParents().get(1).getName());

	            gbc.gridx = 0;
	            gbc.gridy = 1;
	            otherParentGroupPanel.add(lblOtherParent, gbc);

	            gbc.gridx = 1;
	            gbc.gridy = 1;
	            otherParentGroupPanel.add(lblOtherParentInf, gbc);

	            JLabel lblOtherParentId = new JLabel("ID: " + MainController.getCells().get(jCell).getParents().get(1).getJGraphCell().getId());

	            gbc.gridx = 0;
	            gbc.gridy = 2;
	            otherParentGroupPanel.add(lblOtherParentId, gbc);

	            gbc.gridx = 0;
	            gbc.gridy = 2;
	            mainPane.add(otherParentGroupPanel, gbc);
	        }
	    }
	    
	    if (MainController.getCells().get(jCell).hasChild()) {

	    	JPanel childGroupPanel = new JPanel();
	        childGroupPanel.setLayout(new GridBagLayout());
	        childGroupPanel.setBackground(Color.WHITE);
	        childGroupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	        JLabel lblChildGroupTitle = new JLabel("Filho:");
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        childGroupPanel.add(lblChildGroupTitle, gbc);

	        JLabel lblChild = new JLabel("Nome: ");
	        JLabel lblChildInf = new JLabel(MainController.getCells().get(jCell).getChild().getName());

	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        childGroupPanel.add(lblChild, gbc);

	        gbc.gridx = 1;
	        gbc.gridy = 1;
	        childGroupPanel.add(lblChildInf, gbc);

	        JLabel lblChildId = new JLabel("ID: " + MainController.getCells().get(jCell).getChild().getJGraphCell().getId());

	        gbc.gridx = 0;
	        gbc.gridy = 2;
	        childGroupPanel.add(lblChildId, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = 3;
	        mainPane.add(childGroupPanel, gbc);
	    }

	    getContentPane().add(mainPane, BorderLayout.CENTER);
	    pack(); 

	    setVisible(true);
	}




}
