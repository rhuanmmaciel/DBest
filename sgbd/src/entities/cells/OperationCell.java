package entities.cells;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;

import controllers.ConstantController;

import entities.Column;
import entities.Edge;
import entities.utils.cells.CellUtils;

import enums.ColumnDataType;
import enums.OperationArity;
import enums.OperationErrorType;
import enums.OperationType;

import gui.frames.forms.operations.IOperationForm;
import gui.frames.main.MainFrame;

import operations.IOperator;

import sgbd.query.Operator;

public final class OperationCell extends Cell {

    private final OperationType type;

    private List<Cell> parents;

    private final OperationArity arity;

    private final Class<? extends IOperationForm> form;

    private List<String> arguments;

    private Boolean error;

    private String errorMessage;

    private final Class<? extends IOperator> operatorClass;

    private Boolean hasBeenInitialized;

    public OperationCell(mxCell jCell, OperationType type) {
        super(
            type.getFormattedDisplayName(), jCell,
            ConstantController.OPERATION_CELL_WIDTH, ConstantController.OPERATION_CELL_HEIGHT
        );

        this.parents = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.error = false;
        this.errorMessage = null;
        this.hasBeenInitialized = false;
        this.type = type;
        this.arity = type.arity;
        this.form = type.form;
        this.operatorClass = type.operatorClass;
    }

    public OperationCell(mxCell jCell, OperationType type, List<Cell> parents, List<String> arguments) {
        this(jCell, type);

        this.arguments = arguments;

        if (parents != null && !parents.isEmpty()) {
            this.hasBeenInitialized = true;
            this.parents = parents;

            parents.forEach(parent -> {
                parent.setChild(this);
                MainFrame.getGraph().insertEdge(parent.getJCell(), null, "", parent.getJCell(), jCell);
            });

            this.updateOperation();
        }
    }

    public void editOperation(mxCell jCell) {
        if (!this.hasBeenInitialized) return;

        try {
            Constructor<? extends IOperationForm> constructor = this.form.getDeclaredConstructor(mxCell.class);
            constructor.newInstance(jCell);
        } catch (
            InstantiationException | IllegalAccessException |
            NoSuchMethodException | InvocationTargetException exception
        ) {
            exception.printStackTrace();
        }
    }

    public boolean hasBeenInitialized() {
        return this.hasBeenInitialized;
    }

    public void updateOperation() {
        if (!this.hasBeenInitialized) return;

        try {
            Constructor<? extends IOperator> constructor = this.operatorClass.getDeclaredConstructor();
            IOperator operation = constructor.newInstance();
            operation.executeOperation(this.getJCell(), this.getArguments());
        } catch (
            InstantiationException | IllegalAccessException |
            NoSuchMethodException | InvocationTargetException exception
        ) {
            exception.printStackTrace();
        }
    }

    public void setArguments(List<String> arguments) {
        this.hasBeenInitialized = true;

        if (arguments != null && this.arguments != null) {
            this.arguments = new ArrayList<>(arguments);
        }
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    public OperationType getType() {
        return this.type;
    }

    public OperationArity getArity() {
        return this.arity;
    }

    @Override
    public List<Cell> getParents() {
        return this.parents;
    }

    public void addParent(Cell cell) {
        this.parents.add(cell);
    }

    public void removeParent(Cell cell) {
        this.parents.remove(cell);
    }

    public void removeParent(mxCell jCell) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        optionalCell.ifPresent(this::removeParent);
    }

    public void removeParent(Edge edge) {
        this.removeParent(edge.getParent());
    }

    public void removeParents() {
        this.parents.clear();
    }

    @Override
    public boolean hasParents() {
        return !this.parents.isEmpty();
    }

    public Boolean hasTree() {
        return this.getOperator() != null;
    }

    private void setError() {
        String style = this.getJCell().getStyle();

        style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKECOLOR, "red");
        style = mxStyleUtils.setStyle(style, mxConstants.STYLE_FONTCOLOR, "red");

        MainFrame.getGraph().getModel().setStyle(this.getJCell(), style);

        this.error = true;
    }

    public void setError(OperationErrorType message) {
        String style = this.getJCell().getStyle();

        style = mxStyleUtils.setStyle(style, mxConstants.STYLE_STROKECOLOR, "red");
        style = mxStyleUtils.setStyle(style, mxConstants.STYLE_FONTCOLOR, "red");

        MainFrame.getGraph().getModel().setStyle(this.getJCell(), style);

        this.error = true;

        this.errorMessage = switch (message) {
            case NO_ONE_ARGUMENT -> "O parâmetro passado possui erros";
            case NO_ONE_PARENT -> "A operação possui mais de uma célula pai";
            case NO_PARENT -> "A operação não possui uma célula pai";
            case NULL_ARGUMENT -> "O parâmetro fornecido é nulo";
            case PARENT_ERROR -> "Há erro(s) em células anteriores";
            case PARENT_WITHOUT_COLUMN -> "Alguma coluna fornecida não existe na célula pai";
            case NO_TWO_PARENTS -> "A operação não possui duas células pais";
            case NO_TWO_ARGUMENTS -> "Alguma coluna fornecida não existe na respectiva célula pai";
            case EMPTY_ARGUMENT -> "Não foi fornecido um parâmetro";
            case NO_PREFIX -> "Algum parâmetro não possui prefixo";
            case SAME_SOURCE -> "Existem colunas com a mesma fonte. É necessário a renomeação";
        };
    }

    public void setError(String message) {
        this.setError();
        this.errorMessage = message;
    }

    public void removeError() {
        MainFrame.getGraph().getModel().setStyle(this.getJCell(), this.getStyle());
        MainFrame.getGraphComponent().clearCellOverlays();

        this.error = false;
        this.errorMessage = null;
    }

    public void reset() {
        this.name = this.type.getFormattedDisplayName();
        this.parents.clear();
        this.arguments.clear();
        this.hasBeenInitialized = false;

        this.removeError();

        MainFrame.getGraph().getModel().setValue(this.jCell, this.name);
    }

    public OperationCell copy() {
        OperationCell operationCell =  new OperationCell(
            this.jCell, this.type, new ArrayList<>(this.parents), new ArrayList<>(this.arguments)
        );

        operationCell.name = this.name;

        return operationCell;
    }

    public void updateFrom(OperationCell cell) {
        this.name = cell.name;
        this.parents = cell.parents;
        this.arguments = cell.arguments;
        this.hasBeenInitialized = cell.hasBeenInitialized;
        this.error = cell.error;
        this.errorMessage = cell.errorMessage;
    }

    @Override
    public boolean hasError() {
        return this.error;
    }

    public String getErrorMessage() {
        return this.hasError() ? this.errorMessage : "Sem erros";
    }

    public void setColumns() {
        List<Column> columns = new ArrayList<>();

        for (Map.Entry<String, List<String>> contentInfo : this.getOperator().getContentInfo().entrySet()) {
            for (String columnName : contentInfo.getValue()) {
                Column column = new Column(columnName, contentInfo.getKey(), ColumnDataType.NONE, false);

                for (Cell parent : this.parents) {
                    Column finalColumn = column;
                    column = parent.getColumns().stream().filter(c -> c.equals(finalColumn)).findAny().orElse(column);
                }

                columns.add(column);
            }
        }

        this.columns = columns;
    }

    @Override
    public boolean hasParentErrors() {
        boolean error = false;

        for (Cell cell : this.parents) {
            if (cell.hasError()) {
                error = true;
            }
        }

        return error;
    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = operator;
        this.setColumns();
    }
}
