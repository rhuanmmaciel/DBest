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

		case NO_ONE_ARGUMENT -> "O parâmetro passado possui erros";
		case NO_ONE_PARENT -> "A operação não possui apenas 1 célula pai";
		case NO_PARENT -> "A operação não possui célula pai";
		case NULL_ARGUMENT -> "Parâmetro passado é nulo";
		case PARENT_ERROR -> "Erro(s) em células anteriores";
		case PARENT_WITHOUT_COLUMN -> "Alguma coluna fornecida não existe na célula pai";
		case NO_TWO_PARENTS -> "A operação não possui 2 células pais";
		case NO_TWO_ARGUMENTS -> "Alguma coluna fornecida não existe na respectiva célula pai";
		case EMPTY_ARGUMENT -> "Não foi passado parâmetro";
		case NO_PREFIX -> "Algum parâmetro não possui prefixo";
		case SAME_SOURCE -> "Existem colunas com a mesma fonte. É necessário a renomeação.";
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
		return "Sem erros";
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
