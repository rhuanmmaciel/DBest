package entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import enums.OperationArity;
import enums.OperationType;
import gui.frames.forms.operations.IOperator;

public class OperationCell extends Cell{

	private OperationType type;
	private List<Cell> parents;
	private OperationArity arity;
	private Class<? extends IOperator> form;
	private List<String> data;
	
	public OperationCell(String name, String style, mxCell jCell, OperationType type, List<Cell> parents, int x, int y,
			int length, int width) {

		super(name, style, jCell, length, width);
		this.type = type;
		this.parents = new ArrayList<>();
		this.data = null;
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
	    try {
	        Constructor<? extends IOperator> constructor = form.getDeclaredConstructor(mxCell.class, AtomicReference.class);
	        return constructor.newInstance(jCell, exitReference);
	    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	public void setForm(Class<? extends IOperator> form) {
		this.form = form;
	}
	
	public void setData(List<String> data) {
		this.data = new ArrayList<>(data);
	}

	public List<String> getData(){
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

	public void clearParents() {
		parents = new ArrayList<>();
	}

	public Boolean hasParents() {
		return parents.size() != 0;
	}

	public Boolean hasTree() {
		return getOperator() != null;
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

}
