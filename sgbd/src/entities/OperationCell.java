package entities;

import java.awt.Image;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxCellOverlay;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;

import controller.ActionClass;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IOperator;

public class OperationCell extends Cell {

	private OperationType type;
	private List<Cell> parents;
	private OperationArity arity;
	private Class<? extends IOperator> form;
	private List<String> data;
	private Boolean error;

	public OperationCell(String name, String style, mxCell jCell, OperationType type, List<Cell> parents, int x, int y,
			int length, int width) {

		super(name, style, jCell, length, width);
		this.type = type;
		this.parents = new ArrayList<>();
		this.data = null;
		this.error = false;
		this.form = null;
		
		switch (type) {

		case SELECTION:
		case PROJECTION:
		case AGGREGATION:
		case RENAME:
			arity = OperationArity.UNARY;
			break;

		case UNION:
		case JOIN:
		case LEFTJOIN:
		case CARTESIANPRODUCT:
		case DIFFERENCE:
			arity = OperationArity.BINARY;

		}

	}

	public IOperator editOperation(mxCell jCell, AtomicReference<Boolean> exitReference) {
		
		if(hasForm()) {
			
			try {
				
				Constructor<? extends IOperator> constructor = form.getDeclaredConstructor(mxCell.class,
						AtomicReference.class);
				return constructor.newInstance(jCell, exitReference);
				
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
		
		if(hasForm()) {
		
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
		this.data = new ArrayList<>(data);
	}

	public List<String> getData() {
		return data;
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
		;
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
		ActionClass.getGraphComponent().addCellOverlay(getJGraphCell(), overlay);
		String style = getJGraphCell().getStyle();
		style = mxStyleUtils.setStyle(style , mxConstants.STYLE_STROKECOLOR, "red");
		style = mxStyleUtils.setStyle(style , mxConstants.STYLE_FONTCOLOR, "red");
		ActionClass.getGraph().getModel().setStyle(getJGraphCell(), style);
		
		error = true;
		
	}
	
	public void removeError() {
		
		ActionClass.getGraph().getModel().setStyle(getJGraphCell(), getStyle());
		ActionClass.getGraphComponent().clearCellOverlays();;
		
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
		
		for(Cell cell : getParents()) {
			
			if(cell.hasError()) error = true;
			
		}
		
		return error;
	}

}
