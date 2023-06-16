package files.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import exceptions.InvalidCsvException;

public class Recognizer {

	public static CsvData importCsv(String path, char separator, char stringDelimiter, int beginIndex)
			throws InvalidCsvException {

		List<List<String>> dataList = new ArrayList<>();
		List<String> columnsNameList = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			for (int i = 1; i < beginIndex; i++)
				br.readLine();

			String line = br.readLine();

			isLineNull(line, "O csv não possui nenhuma informação");
			
			recognizeItems(line, columnsNameList, stringDelimiter, separator);

			isColumnEmpty(columnsNameList, "O csv deve possuir nome para todas as colunas");

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
						tuple.add("null");

					dataList.add(tuple);

				}
				line = br.readLine();

			}

		} catch (IOException e) {

		}

		Vector<Vector<Object>> dataArray = new Vector<>();
		
		for(List<String> row : dataList) {
			
			Vector<Object> rowVector = new Vector<>();
			
			for(String data : row)
				rowVector.add(data);
			
			dataArray.add(rowVector);
			
		}

		return new CsvData(dataList, columnsNameList, dataArray, new Vector<>(columnsNameList));

	}
	
	private static void recognizeItems(String line, List<String> tuple, char stringDelimiter, char separator) throws InvalidCsvException {
		
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

				isStringDelimiterWrong(inf, stringDelimiter);

			}

		}

		if (!data.isEmpty()) {

			String inf = data.toString().strip();

			if (isString(inf, stringDelimiter))
				inf = inf.substring(1, inf.length() - 1);

			tuple.add(inf);

			isStringDelimiterWrong(inf, stringDelimiter);

		}
		
	}

	private static boolean isString(String data, char stringDelimiter) {
		return data.startsWith(String.valueOf(stringDelimiter)) && data.endsWith(String.valueOf(stringDelimiter))
				&& data.length() > 1;
	}

	private static void isStringDelimiterWrong(String data, char stringDelimiter) throws InvalidCsvException {
		int i = 0;

		for (char c : data.toCharArray())
			if (c == stringDelimiter)
				i++;

		if (i > 2)
			throw new InvalidCsvException("Não é possível ter mais de dois delimitadores de String: \n" + data);

		if (i == 1)
			throw new InvalidCsvException("Não é possível ter apenas um delimitador de String: \n" + data);

		if (i == 2 && (!data.strip().startsWith(String.valueOf(stringDelimiter))
				|| !data.strip().endsWith(String.valueOf(stringDelimiter))))
			throw new InvalidCsvException(
					"Os delimitadores de String precisam estar no começo e no final do dado: \n" + data);

	}

	private static void isLineNull(String line, String txt) throws InvalidCsvException {
		if (line == null)
			throw new InvalidCsvException(txt);
	}

	private static void isColumnEmpty(List<String> columns, String txt) throws InvalidCsvException {
		if (columns.contains("") || columns.contains(null))
			throw new InvalidCsvException(txt);
	}

	public record CsvData(List<List<String>> dataList, List<String> columnsNameList, Vector<Vector<Object>> dataArray,
			Vector<String> columnsNameArray) {

	}

}
