package files.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidCsvException;

public class Recognizer {

	public static CsvData importCsv(String path, char separator, char stringDelimiter, int beginIndex) throws InvalidCsvException{
		
		List<List<String>> dataList = new ArrayList<>();
		List<String> columnsNameList = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			for(int i = 1; i < beginIndex; i++) br.readLine();
			
			String line = br.readLine();
			
			isLineNull(line, "O csv não possui nenhuma informação");
			
			columnsNameList.addAll(List.of(line.split(String.valueOf(separator))));
			line = br.readLine();
			
			isLineNull(line, "O csv possui apenas uma linha");
			
			while (line != null) {
				
				if(!line.isBlank() || !line.isEmpty())
					dataList.add(List.of(line.split(String.valueOf(separator))));
				line = br.readLine();

			}	

		} catch (IOException e) {

		}
		
		updateRows(dataList, columnsNameList);

		String[][] dataArray = dataList.stream().map(line -> line.toArray(new String[0])).toArray(String[][]::new);
		String[] columnsNameArray = columnsNameList.stream().toArray(String[]::new);
		
		return new CsvData(dataList, columnsNameList, dataArray, columnsNameArray);

	}
	
	private static void updateRows(List<List<String>> dataList, List<String> columnsNameList) {
		
	    int size = columnsNameList.size();
	    
	    List<List<String>> updatedDataList = new ArrayList<>();
	    
	    for (List<String> row : dataList) {
	        List<String> updatedRow = new ArrayList<>(row);
	        
	        while (updatedRow.size() > size)
	            updatedRow.remove(updatedRow.size() - 1);
	        
	        while (updatedRow.size() < size)
	            updatedRow.add("null");
	        
	        updatedDataList.add(updatedRow);
	    }
	    
	    dataList.clear();
	    dataList.addAll(updatedDataList);
	    
	}
	
	private static void isLineNull(String line, String txt) throws InvalidCsvException {
		if(line == null) throw new InvalidCsvException(txt);
	}

	public record CsvData(List<List<String>> dataList, List<String> columnsNameList, String[][] dataArray,
			String[] columnsNameArray) {

	}

}
