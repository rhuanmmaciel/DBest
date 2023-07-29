package files.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import controller.ConstantController;
import controller.MainController;
import exceptions.InvalidCsvException;

public class CsvRecognizer {

	public static CsvData importCsv(Path path, char separator, char stringDelimiter, int beginIndex)
			throws InvalidCsvException {

		List<List<String>> dataList = new ArrayList<>();
		List<String> columnsNameList = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {

			for (int i = 1; i < beginIndex; i++)
				br.readLine();

			String line = br.readLine();

			isLineNull(line, "O csv não possui nenhuma informação");
			
			recognizeItems(line, columnsNameList, stringDelimiter, separator);

			isColumnEmpty(columnsNameList);

			line = br.readLine();

			isLineNull(line, "O csv possui apenas uma linha");

			int size = columnsNameList.size();

			while (line != null) {

				if (!line.isBlank() || !line.isEmpty()) {

					List<String> tuple = new ArrayList<>();

					recognizeItems(line, tuple, stringDelimiter, separator);

					while (tuple.size() > size)
						tuple.remove(tuple.size() - 1);

					while (tuple.size() < size)
						tuple.add(ConstantController.NULL);

					dataList.add(tuple);

				}
				line = br.readLine();

			}

		} catch (IOException ignored) {

			throw new InvalidCsvException("Não foi possível ler o csv");

		}

		Vector<Vector<Object>> dataArray = new Vector<>();
		
		for(List<String> row : dataList) {

			Vector<Object> rowVector = new Vector<>(row);
			
			dataArray.add(rowVector);
			
		}

		return new CsvData(dataList, columnsNameList, dataArray, new Vector<>(columnsNameList));

	}
	
	private static void recognizeItems(String line, List<String> tuple, char stringDelimiter, char separator) {
		
		boolean stringDelimiterFound = false;
		
		StringBuilder data = new StringBuilder();
		
		for (char c : line.toCharArray()) {

			stringDelimiterFound = (c == stringDelimiter) != stringDelimiterFound;

			if (c != separator || stringDelimiterFound)
				data.append(c);

			else {

				String inf = data.toString().strip();

				if (isString(inf, stringDelimiter))
					inf = inf.substring(1, inf.length() - 1);

				tuple.add(inf);
				data = new StringBuilder();

			}

		}

		if (!data.isEmpty()) {

			String inf = data.toString().strip();

			if (isString(inf, stringDelimiter))
				inf = inf.substring(1, inf.length() - 1);

			tuple.add(inf);

		}
		
	}

	private static boolean isString(String data, char stringDelimiter) {
		return data.startsWith(String.valueOf(stringDelimiter)) && data.endsWith(String.valueOf(stringDelimiter))
				&& data.length() > 1;
	}

	private static void isLineNull(String line, String txt) throws InvalidCsvException {
		if (line == null)
			throw new InvalidCsvException(txt);
	}

	private static void isColumnEmpty(List<String> columns) throws InvalidCsvException {
		if (columns.contains("") || columns.contains(null))
			throw new InvalidCsvException("O csv deve possuir nome para todas as colunas");
	}

	public record CsvData(List<List<String>> dataList, List<String> columnsNameList, Vector<Vector<Object>> dataArray,
			Vector<String> columnsNameArray) {

	}

}
