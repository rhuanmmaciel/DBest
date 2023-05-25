package entities.cells;

import java.awt.Image;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxCellOverlay;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;

import controller.MainController;
import entities.Column;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IOperator;
import gui.frames.main.MainFrame;

public final class OperationCell extends Cell {

	private OperationType type;
	private List<Cell> parents = new ArrayList<>();
	private OperationArity arity;
	private Class<? extends IOperator> form = null;
	private List<String> arguments = null;
	private Boolean error = false;

	public OperationCell(mxCell jCell, OperationType type) {

		super(type.getDisplayNameAndSymbol(), type.getDisplayName(), jCell, 80, 30);
		initializeCell(jCell, type);

	}

	public OperationCell(mxCell jCell, OperationType type, List<Cell> parents, List<String> arguments) {

		super(type.getDisplayNameAndSymbol(), type.getDisplayName(), jCell, 80, 30);
		initializeCell(jCell, type);

		this.arguments = arguments;
		
		if (parents != null && !parents.isEmpty()) {
			
			this.parents = parents;
			parents.forEach(parent -> {

				parent.setChild(this);
				MainFrame.getGraph().insertEdge(parent.getJGraphCell(), null, "", parent.getJGraphCell(), jCell);

			});
			this.form = type.getForm();
			updateOperation();
			
		}
	}
	
	private void initializeCell(mxCell jCell, OperationType type) {
		
		this.type = type;
		arity = type.getArity();
		
	}

	public IOperator editOperation(mxCell jCell) {

		if (hasForm()) {

			try {

				Constructor<? extends IOperator> constructor = form.getDeclaredConstructor(mxCell.class);
				return constructor.newInstance(jCell);

			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					| InvocationTargetException e) {

				e.printStackTrace();

			}

		}

		return null;
	}

	public void setForm(Class<? extends IOperator> form) {
		this.form = form;
	}

	public boolean hasForm() {
		return form != null;
	}

	public void updateOperation() {

		if (hasForm()) {

			try {

				Constructor<? extends IOperator> constructor = form.getDeclaredConstructor();
				IOperator operation = constructor.newInstance();
				operation.executeOperation(getJGraphCell(), getData());

			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	public void setData(List<String> data) {
		this.arguments = new ArrayList<>(data);
	}

	public List<String> getData() {
		return arguments;
	}

	public OperationType getType() {

		return type;

	}

	public OperationArity getArity() {

		return arity;

	}

	public List<Cell> getParents() {

		return parents;

	}

	public void addParent(Cell cell) {
		parents.add(cell);
	}

	public void removeParent(Cell cell) {
		parents.remove(cell);
	}

	public void clearParents() {
		parents.clear();
	}

	public boolean hasParents() {
		return parents.size() != 0;
	}

	public Boolean hasTree() {
		return getOperator() != null;
	}

	public void setError() {

		ImageIcon icon = new ImageIcon("red_exclamation.png");
		Image image = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(image);

		mxCellOverlay overlay = new mxCellOverlay(scaledIcon, "Error");
		MainController.getGraphComponent().addCellOverlay(getJGraphCell(), overlay);
		String style = getJGraphCell().getStyle();
		style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKECOLOR, "red");
		style = mxStyleUtils.setStyle(style, mxConstants.STYLE_FONTCOLOR, "red");
		MainController.getGraph().getModel().setStyle(getJGraphCell(), style);

		error = true;

	}

	public void removeError() {

		MainController.getGraph().getModel().setStyle(getJGraphCell(), getStyle());
		MainController.getGraphComponent().clearCellOverlays();
		;

		error = false;

	}

	public boolean hasError() {
		return error;
	}

	public void setColumns(List<List<Column>> parentColumns, Collection<List<String>> cellColumnsName) {

		List<Column> cellColumns = new ArrayList<>();

		for (List<String> columnsName : cellColumnsName) {

			for (List<Column> columns : parentColumns) {

				for (Column column : columns) {

					if (columnsName.contains(column.getName())) {

						cellColumns.add(column);

					}

				}

			}

		}

		this.columns = cellColumns;

	}

	@Override
	public boolean hasParentErrors() {

		boolean error = false;

		for (Cell cell : getParents()) {

			if (cell.hasError())
				error = true;

		}

		return error;
	}

}
