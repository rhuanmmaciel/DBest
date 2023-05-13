package entities;

import com.mxgraph.model.mxCell;

import entities.cells.TableCell;
import enums.OperationType;

public class Action {

	public static class CurrentAction {

		public enum ActionType {
			EDGE, CREATE_OPERATOR_CELL, DELETE_CELL, DELETE_ALL, SAVE_CELL, SHOW_CELL, IMPORT_FILE, CREATE_TABLE,
			OPEN_CONSOLE
		}

		private ActionType action;

		public CurrentAction(ActionType action) {
			this.action = action;
		}

		public ActionType getType() {
			return action;
		}

	}

	public static class CreateCellAction extends CurrentAction {

		private final String name;
		private final String style;

		public CreateCellAction(ActionType action, String name, String style) {

			super(action);

			if (action != ActionType.CREATE_OPERATOR_CELL && action != ActionType.CREATE_TABLE
					&& action != ActionType.IMPORT_FILE)
				throw new IllegalArgumentException("ActionType:" + action + " inválido para um CreateCellAction");

			this.name = name;
			this.style = style;

		}

		public String getName() {
			return name;
		}

		public String getStyle() {
			return style;
		}

	}

	public static class CreateOperationAction extends CreateCellAction {

		private OperationType type;
		private mxCell parent;

		public CreateOperationAction(ActionType action, String name, String style, OperationType type) {

			super(action, name, style);
			this.type = type;
			parent = null;

		}

		public OperationType getOperationType() {
			return type;
		}

		public void setParent(mxCell parent) {
			this.parent = parent;
		}

		public mxCell getParent() {

			if (hasParent())
				return parent;
			return null;
		}

		public boolean hasParent() {
			return parent != null;
		}

	}

	public static class CreateTableAction extends CreateCellAction {

		private TableCell tableCell;

		public CreateTableAction(ActionType action, String name, String style, TableCell tableCell) {

			super(action, name, style);
			this.tableCell = tableCell;

		}

		public TableCell getTableCell() {
			return tableCell;
		}

	}

}
