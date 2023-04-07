package controller;

import entities.TableCell;
import enums.OperationType;

public class CreateAction {

	public static class CurrentAction{
		
		enum ActionType {
			EDGE, TABLECELL, OPERATORCELL
		}
	
		private ActionType action;
	
		public CurrentAction(ActionType action) {
			this.action = action;
		}
	
		public ActionType getType() {
			return action;
		}
		
	}
	
	public static class CreateCellAction extends CurrentAction{

		private String name;
		private String style;
		
		public CreateCellAction(ActionType action, String name, String style) {
			
			super(action);
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

	public static class CreateOperationAction extends CreateCellAction{

		private OperationType type;

		public CreateOperationAction(ActionType action, String name, String style, OperationType type) {

			super(action, name, style);
			this.type = type;

		}

		public OperationType getOperationType() {
			return type;
		}

	
	}
	
	public static class CreateTableAction extends CreateCellAction{

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
