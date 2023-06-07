package entities.cells;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import controller.MainController;
import entities.Column;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.main.MainFrame;
import gui.frames.forms.operations.IFormFrameOperation;
import operations.IOperator;
import operations.OperationErrorVerifier.ErrorMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class OperationCell extends Cell {

	private OperationType type;
	private List<Cell> parents = new ArrayList<>();
	private OperationArity arity;
	private Class<? extends IFormFrameOperation> form = null;
	private List<String> arguments = new ArrayList<>();
	private Boolean error = false;
	private String errorMessage = null;
	private Class<? extends IOperator> operator = null;
	private Boolean hasBeenInitialized = false;

	public OperationCell(mxCell jCell, OperationType type) {

		super(type.getDisplayNameAndSymbol(), type.getDisplayName(), jCell, 80, 30);
		initializeInfos(type);

	}

	public OperationCell(mxCell jCell, OperationType type, List<Cell> parents, List<String> arguments) {

		super(type.getDisplayNameAndSymbol(), type.getDisplayName(), jCell, 80, 30);
		initializeInfos(type);

		this.arguments = arguments;

		if (parents != null && !parents.isEmpty()) {

			hasBeenInitialized = true;

			this.parents = parents;
			parents.forEach(parent -> {

				parent.setChild(this);
				MainFrame.getGraph().insertEdge(parent.getJGraphCell(), null, "", parent.getJGraphCell(), jCell);

			});
			this.form = type.getForm();
			this.operator = type.getOperator();
			updateOperation();

		}
	}

	private void initializeInfos(OperationType type) {

		this.type = type;
		arity = type.getArity();
		this.form = type.getForm();
		this.operator = type.getOperator();

	}

	public void editOperation(mxCell jCell) {

		if (hasBeenInitialized) {

			try {

				Constructor<? extends IFormFrameOperation> constructor = form.getDeclaredConstructor(mxCell.class);
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

				Constructor<? extends IOperator> constructor = operator.getDeclaredConstructor();
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
		return parents.size() != 0;
	}

	public Boolean hasTree() {
		return getOperator() != null;
	}

	public void setError(ErrorMessage message) {

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

		};
		
	}

	public void removeError() {
		
		MainController.getGraph().getModel().setStyle(getJGraphCell(), getStyle());
		MainController.getGraphComponent().clearCellOverlays();
		errorMessage = null;
		error = false;

	}

	public boolean hasError() {
		return error;
	}

	public String getErrorMessage() {
		if (hasError())
			return errorMessage;
		return "Sem erros";
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
