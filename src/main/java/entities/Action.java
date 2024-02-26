package entities;

import com.mxgraph.model.mxCell;
import entities.cells.TableCell;
import enums.OperationType;

import java.io.Serializable;

public class Action implements Serializable {

	public static class CurrentAction implements Serializable {

		public enum ActionType {
			CREATE_EDGE, CREATE_OPERATOR_CELL, DELETE_CELL, DELETE_ALL, PRINT_SCREEN, SHOW_CELL,
			IMPORT_FILE, CREATE_TABLE_CELL, OPEN_CONSOLE, OPEN_TEXT_EDITOR, OPEN_COMPARATOR, NONE
		}

		private final ActionType type;

		public CurrentAction(ActionType type) {
			this.type = type;
		}

		public ActionType getType() {
			return this.type;
		}
	}

	public static class CreateCellAction extends CurrentAction {

		private final String name;

		private final String style;

		public CreateCellAction(ActionType actionType, String name, String style) {
			super(actionType);

			if (
				actionType != ActionType.CREATE_OPERATOR_CELL &&
				actionType != ActionType.CREATE_TABLE_CELL &&
				actionType != ActionType.IMPORT_FILE
			) {
				throw new IllegalArgumentException(
					String.format("ActionType: %s inválido para um CreateCellAction", actionType)
				);
			}

			this.name = name;
			this.style = style;
		}

		public String getName() {
			return this.name;
		}

		public String getStyle() {
			return this.style;
		}
	}

	public static class CreateOperationCellAction extends CreateCellAction {

		private final OperationType operationType;

		private mxCell parent;

		public CreateOperationCellAction(OperationType operationType) {
			super(ActionType.CREATE_OPERATOR_CELL, operationType.getFormattedDisplayName(), operationType.displayName);

			this.operationType = operationType;
			this.parent = null;
		}

		public OperationType getOperationType() {
			return this.operationType;
		}

		public void setParent(mxCell parent) {
			this.parent = parent;
		}

		public mxCell getParent() {
			return this.parent;
		}

		public boolean hasParent() {
			return this.parent != null;
		}
	}

	public static class CreateTableCellAction extends CreateCellAction {

		private final TableCell tableCell;

		public CreateTableCellAction(ActionType actionType, String name, String style, TableCell tableCell) {
			super(actionType, name, style);

			this.tableCell = tableCell;
		}

		public TableCell getTableCell() {
			return this.tableCell;
		}
	}
}
