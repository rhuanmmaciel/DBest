package files.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import controllers.ConstantController;

import exceptions.InvalidCSVException;

public class CSVRecognizer {

    public static CSVData importCSV(Path path, char separator, char stringDelimiter, int beginIndex)
        throws InvalidCSVException {

        List<List<String>> rows = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            for (int i = 1; i < beginIndex; i++) {
                reader.readLine();
            }

            String line = reader.readLine();

            isLineNull(line, ConstantController.getString("csvRecognizer.error.noInformation"));

            recognizeItems(line, columnNames, stringDelimiter, separator);

            isColumnEmpty(columnNames);

            line = reader.readLine();

            isLineNull(line, ConstantController.getString("csvRecognizer.error.justOneLine"));

            int size = columnNames.size();

            while (line != null) {
                if (!line.isBlank() || !line.isEmpty()) {
                    List<String> tuple = new ArrayList<>();

                    recognizeItems(line, tuple, stringDelimiter, separator);

                    while (tuple.size() > size) {
                        tuple.remove(tuple.size() - 1);
                    }

                    while (tuple.size() < size) {
                        tuple.add(ConstantController.NULL);
                    }

                    rows.add(tuple);
                }

                line = reader.readLine();
            }
        } catch (IOException exception) {
            throw new InvalidCSVException(ConstantController.getString("csvRecognizer.error.notPossibleToRead"));
        }

        Vector<Vector<Object>> dataArray = new Vector<>();

        for (List<String> row : rows) {
            Vector<Object> rowVector = new Vector<>(row);

            dataArray.add(rowVector);
        }

        return new CSVData(rows, columnNames, dataArray, new Vector<>(columnNames));
    }

    private static void recognizeItems(String line, List<String> tuple, char stringDelimiter, char separator) {
        boolean stringDelimiterFound = false;

        StringBuilder data = new StringBuilder();

        for (char character : line.toCharArray()) {
            stringDelimiterFound = (character == stringDelimiter) != stringDelimiterFound;

            if (character != separator || stringDelimiterFound) {
                data.append(character);
            } else {
                String inf = data.toString().strip();

                if (isString(inf, stringDelimiter)) {
                    inf = inf.substring(1, inf.length() - 1);
                }

                tuple.add(inf);

                data = new StringBuilder();
            }
        }

        if (!data.isEmpty()) {
            String inf = data.toString().strip();

            if (isString(inf, stringDelimiter)) {
                inf = inf.substring(1, inf.length() - 1);
            }

            tuple.add(inf);
        }
    }

    private static boolean isString(String data, char stringDelimiter) {
        return
            data.startsWith(String.valueOf(stringDelimiter)) &&
            data.endsWith(String.valueOf(stringDelimiter)) &&
            data.length() > 1;
    }

    private static void isLineNull(String line, String txt) throws InvalidCSVException {
        if (line == null) {
            throw new InvalidCSVException(txt);
        }
    }

    private static void isColumnEmpty(List<String> columns) throws InvalidCSVException {
        if (columns.contains("") || columns.contains(null)) {
            throw new InvalidCSVException(ConstantController.getString("csvRecognizer.error.columnNames"));
        }
    }

    public record CSVData(
        List<List<String>> dataList, List<String> columnNamesList,
        Vector<Vector<Object>> dataArray, Vector<String> columnNamesArray
    ) {

    }
}
