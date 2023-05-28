package entities.cells;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;

import controller.MainController;
import entities.Column;
import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IOperator;
import gui.frames.main.MainFrame;
import operations.OperationErrorVerifier.ErrorMessage;

public final class OperationCell extends Cell {

	private OperationType type;
	private List<Cell> parents = new ArrayList<>();
	private OperationArity arity;
	private Class<? extends FormFrameOperation> form = null;
	private List<String> arguments = null;
	private Boolean error = false;
	private String errorMessage = null;
	private Class<? extends IOperator> operator = null;

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
			this.operator = type.getOperator();
			updateOperation();

		}
	}

	private void initializeCell(mxCell jCell, OperationType type) {

		this.type = type;
		arity = type.getArity();

	}

	public FormFrameOperation editOperation(mxCell jCell) {

		if (hasForm()) {

			try {

				Constructor<? extends FormFrameOperation> constructor = form.getDeclaredConstructor(mxCell.class);
				return constructor.newInstance(jCell);

			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					| InvocationTargetException e) {

				e.printStackTrace();

			}

		}

		return null;
	}

	public void setForm() {
		this.form = type.getForm();
		this.operator = type.getOperator();
	}

	public boolean hasForm() {
		return form != null;
	}

	public boolean hasOperator() {
		return operator != null;
	}

	public void updateOperation() {

		if (hasOperator()) {

			try {

				Constructor<? extends IOperator> constructor = operator.getDeclaredConstructor();
				IOperator operation = constructor.newInstance();
				operation.executeOperation(getJGraphCell(), getData());

			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	public void setArguments(List<String> arguments) {

		if (arguments != null && this.arguments != null)
			this.arguments = new ArrayList<>(arguments);

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

	public void setError(ErrorMessage message) {

		String style = getJGraphCell().getStyle();
		style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKECOLOR, "red");
		style = mxStyleUtils.setStyle(style, mxConstants.STYLE_FONTCOLOR, "red");
		MainController.getGraph().getModel().setStyle(getJGraphCell(), style);

		error = true;

		errorMessage = switch (message) {

		case NO_ONE_ARGUMENT -> "O parâmetro passado possui erros";
		case NO_ONE_PARENT -> "A operação não possui apenas 1 pai";
		case NO_PARENT -> "A operação não possui pai";
		case NULL_ARGUMENT -> "Parâmetro passado é nulo";
		case PARENT_ERROR -> "Erro(s) em células anteriores";
		case PARENT_WITHOUT_COLUMN -> "Alguma coluna fornecida não existe no pai";
		case NO_TWO_PARENTS -> "A operação não possui 2 pais";
		case NO_TWO_ARGUMENTS -> "Alguma coluna fornecida não existe no respectivo pai";

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
