package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.util.Util;

public class PrintOperator {

	public static void print(Operator operator) {
		
			Operator aux = operator;
		    aux.open();

		    Set<String> possibleKeys = new HashSet<>(); 
		    Map<Integer, Map<String, String>> rows = new HashMap<>();

		    Tuple tuple = aux.hasNext() ? aux.next() : null;
		    int k = 0;
		    while (aux.hasNext() || tuple != null) {
		        Tuple t = tuple == null ? aux.next() : tuple;

		        Map<String, String> row = new HashMap<>();

		        for (Map.Entry<String, ComplexRowData> line : t) {
		            for (Map.Entry<String, byte[]> data : line.getValue()) {
		            	possibleKeys.add(data.getKey());
		            	switch(Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
		                    case "int":
		                        row.put(data.getKey(), line.getValue().getInt(data.getKey()).toString());
		                        break;
		                    case "float":
		                        row.put(data.getKey(), line.getValue().getFloat(data.getKey()).toString());
		                        break;
		                    case "string":
		                    default:
		                        row.put(data.getKey(), line.getValue().getString(data.getKey()));
		                }
		            }
		        }

		        rows.put(k, row);
		        tuple = null;
		        k++;
		    }

		    for (Map<String, String> row : rows.values()) {
		        for (String key : possibleKeys) {
		            if (!row.containsKey(key)) {
		                row.put(key, "");
		            }
		        }
		    }
		    
		    Map<Integer, Map<String, String>> content = rows;

		    Map<String, Integer> columnWidths = new HashMap<>();
		    for (Map<String, String> row : content.values()) {
		        for (Map.Entry<String, String> entry : row.entrySet()) {
		            String column = entry.getKey();
		            String value = entry.getValue();
		            int width = Math.max(columnWidths.getOrDefault(column, 0), value.length());
		            columnWidths.put(column, width);
		        }
		    }

		    StringBuilder tableFormatted = new StringBuilder();
		    tableFormatted.append("+");
		    for (String column : rows.get(0).keySet()) {
		        int width = columnWidths.get(column);
		        tableFormatted.append("-");
		        for (int i = 0; i < width + 2; i++) {
		            tableFormatted.append("-");
		        }
		        tableFormatted.append("+");
		    }
		    tableFormatted.append("\n");

		    tableFormatted.append("|");
		    for (String column : rows.get(0).keySet()) {
		        int width = columnWidths.get(column);
		        tableFormatted.append(" ");
		        tableFormatted.append(String.format("%-" + width + "s", column));
		        tableFormatted.append(" |");
		    }
		    tableFormatted.append("\n");

		    tableFormatted.append("+");
		    for (String column : rows.get(0).keySet()) {
		        int width = columnWidths.get(column);
		        tableFormatted.append("-");
		        for (int i = 0; i < width + 2; i++) {
		            tableFormatted.append("-");
		        }
		        tableFormatted.append("+");
		    }
		    tableFormatted.append("\n");

		    for (Map<String, String> row : content.values()) {
		        tableFormatted.append("|");
		        for (String column : rows.get(0).keySet()) {
		            String value = row.getOrDefault(column, "");
		            int width = columnWidths.get(column);
		            tableFormatted.append(" ");
		            tableFormatted.append(String.format("%-" + width + "s", value));
		            tableFormatted.append(" |");
		        }
		        tableFormatted.append("\n");
		    }

		    tableFormatted.append("+");
		    for (String column : rows.get(0).keySet()) {
		        int width = columnWidths.get(column);
		        tableFormatted.append("-");
		        for (int i = 0; i < width + 2; i++) {
		            tableFormatted.append("-");
		        }
		        tableFormatted.append("+");
		    }
		    tableFormatted.append("\n");

		    System.out.println(tableFormatted.toString());
		
	}
	
}
