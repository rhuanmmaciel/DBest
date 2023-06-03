package enums;

import enums.interfaces.IColumnDataType;

public enum ColumnDataType implements IColumnDataType{
	
	INTEGER{

		@Override
		public String getTypeName() {
			return "integer";
		}
	
	},
	FLOAT{

		@Override
		public String getTypeName() {
			return "float";
		}
		
	},
	CHARACTER{

		@Override
		public String getTypeName() {
			return "char";
		}
		
	},
	STRING{

		@Override
		public String getTypeName() {
			return "string";
		}
		
	},
	BOOLEAN{

		@Override
		public String getTypeName() {
			return "bool";
		}
		
	},
	NONE{

		@Override
		public String getTypeName() {
			return "none";
		}
		
	};


	
}