package entities.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.Util;

public class TableFormat {

	public static List<List<String>> getRows(Operator operator) {
		
		Operator aux = operator;
		aux.open();
		
		List<List<String>> rows = new ArrayList<>();

		Tuple tuple = aux.hasNext() ? aux.next() : null;
		
		while (aux.hasNext() || tuple != null) {
			Tuple t = tuple == null ? aux.next() : tuple;

			List<String> row = new ArrayList<>();
			for (Map.Entry<String, ComplexRowData> line : t) {
				for (Map.Entry<String, byte[]> data : line.getValue()) {
					
					switch(Util.typeOfColumn(line.getValue().getMeta(data.getKey()))){
					
						case "int":
	
							row.add(line.getValue().getInt(data.getKey()).toString());
							break;
							
						case "float":
						
							row.add(line.getValue().getFloat(data.getKey()).toString());
							break;
							
						case "string":
						default:
							row.add(line.getValue().getString(data.getKey()));
						
					}

				}

			}

			rows.add(row);
			tuple = null;

		}

		aux.close();

		return rows;

	}

}
