package entities.cells;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import controller.ConstantController;
import controller.MainController;
import entities.Column;
import enums.ColumnDataType;
import enums.OperationArity;
import enums.OperationErrorType;
import enums.OperationType;
import gui.frames.main.MainFrame;
import gui.frames.forms.operations.IOperationForm;
import operations.IOperator;
import sgbd.query.Operator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class OperationCell extends Cell {

	private OperationType type;
	private List<Cell> parents = new ArrayList<>();
	private OperationArity arity;
	private Class<? extends IOperationForm> form = null;
	private List<String> arguments = new ArrayList<>();
	private Boolean error = false;
	private String errorMessage = null;
	private Class<? extends IOperator> operatorClass = null;
	private Boolean hasBeenInitialized = false;

	public OperationCell(mxCell jCell, OperationType type) {

		super(type.getDisplayNameAndSymbol(), type.DISPLAY_NAME, jCell, ConstantController.OPERATION_CELL_WIDTH,
				ConstantController.OPERATION_CELL_HEIGHT);
		initializeInfos(type);

	}

	public OperationCell(mxCell jCell, OperationType type, List<Cell> parents, List<String> arguments) {

		super(type.getDisplayNameAndSymbol(), type.DISPLAY_NAME, jCell, ConstantController.OPERATION_CELL_WIDTH,
				ConstantController.OPERATION_CELL_HEIGHT);
		initializeInfos(type);

		this.arguments = arguments;

		if (parents != null && !parents.isEmpty()) {

			hasBeenInitialized = true;

			this.parents = parents;
			parents.forEach(parent -> {

				parent.setChild(this);
				MainFrame.getGraph().insertEdge(parent.getJGraphCell(), null, "", parent.getJGraphCell(), jCell);

			});
			updateOperation();

		}
	}

	private void initializeInfos(OperationType type) {

		this.type = type;
		this.arity = type.ARITY;
		this.form = type.FORM;
		this.operatorClass = type.OPERATOR_CLASS;

	}

	public void editOperation(mxCell jCell) {

		if (hasBeenInitialized) {

			try {

				Constructor<? extends IOperationForm> constructor = form.getDeclaredConstructor(mxCell.class);
				constructor.newInstance(jCell);

			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					| InvocationTargetException e) {

				e.printStackTrace();

			}

		}

	}

	public boolean hasBeenInitialized() {
		return hasBeenInitialized;
	}

	public void updateOperation() {

		if (hasBeenInitialized) {
			
			try {

				Constructor<? extends IOperator> constructor = operatorClass.getDeclaredConstructor();
				IOperator operation = constructor.newInstance();
				operation.executeOperation(getJGraphCell(), getArguments());

			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	public void setArguments(List<String> arguments) {

		hasBeenInitialized = true;

		if (arguments != null && this.arguments != null)
			this.arguments = new ArrayList<>(arguments);

	}

	public List<String> getArguments() {
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
		return !parents.isEmpty();
	}

	public Boolean hasTree() {
		return getOperator() != null;
	}

	public void setError(OperationErrorType message) {

		String style = getJGraphCell().getStyle();
		style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKECOLOR, "red");
		style = mxStyleUtils.setStyle(style, mxConstants.STYLE_FONTCOLOR, "red");
		MainController.getGraph().getModel().setStyle(getJGraphCell(), style);

		error = true;

		errorMessage = switch (message) {

		case NO_ONE_ARGUMENT -> ConstantController.getString("cell.operationCell.error.noOneArgument");
		case NO_ONE_PARENT -> ConstantController.getString("cell.operationCell.error.noOneParent");
		case NO_PARENT -> ConstantController.getString("cell.operationCell.error.noParent");
		case NULL_ARGUMENT -> ConstantController.getString("cell.operationCell.error.nullArgument");
		case PARENT_ERROR -> ConstantController.getString("cell.operationCell.error.parentError");
		case PARENT_WITHOUT_COLUMN -> ConstantController.getString("cell.operationCell.error.parentWithoutColumn");
		case NO_TWO_PARENTS ->  ConstantController.getString("cell.operationCell.error.noTwoParents");
		case NO_TWO_ARGUMENTS -> ConstantController.getString("cell.operationCell.error.noTwoArguments");
		case EMPTY_ARGUMENT -> ConstantController.getString("cell.operationCell.error.emptyArgument");
		case NO_PREFIX -> ConstantController.getString("cell.operationCell.error.noPrefix");
		case SAME_SOURCE -> ConstantController.getString("cell.operationCell.error.sameSource");
		};
		
	}

	public void removeError() {
		
		MainController.getGraph().getModel().setStyle(getJGraphCell(), getStyle());
		MainController.getGraphComponent().clearCellOverlays();
		errorMessage = null;
		error = false;

	}

	@Override
	public boolean hasError() {
		return error;
	}

	public String getErrorMessage() {
		if (hasError())
			return errorMessage;
		return ConstantController.getString("cell.operationCell.error.noError");
	}

	public void setColumns() {

		List<Column> cellColumns = new ArrayList<>();

		for (Map.Entry<String, List<String>> columns : getOperator().getContentInfo().entrySet())
			for(String column : columns.getValue()) {

				Column c = new Column(column, columns.getKey(), ColumnDataType.NONE, false);

				for(Cell parent : parents) {
					Column finalC = c;
					c = parent.getColumns().stream().filter(x -> x.equals(finalC)).findAny().orElse(c);
				}

				cellColumns.add(c);

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

	@Override
	public void setOperator(Operator operator){
		this.operator = operator;
		setColumns();
	}

}
