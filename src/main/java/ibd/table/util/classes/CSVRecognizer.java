package ibd.table.util.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CSVRecognizer implements Iterable<String[]>{
    private File file;
    private String[] columnsNameArray;
    private char separator, stringDelimiter;
    private int beginIndex;

    public CSVRecognizer(String path, char separator, char stringDelimiter, int beginIndex) throws InvalidCsvException {
        this.file = new File(path);
        this.separator = separator;
        this.stringDelimiter = stringDelimiter;
        this.beginIndex = beginIndex;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            List<String> columnsNameList = new ArrayList<>();
            for (int i = 1; i < beginIndex; i++)
                br.readLine();
            String line = br.readLine();

            isLineNull(line, "O csv não possui nenhuma informação");
            recognizeItems(line, columnsNameList, stringDelimiter, separator);

            isColumnEmpty(columnsNameList, "O csv deve possuir nome para todas as colunas");
            line = br.readLine();

            isLineNull(line, "O csv possui apenas uma linha");
            columnsNameArray = columnsNameList.stream().toArray(String[]::new);

        }catch (IOException e){}
    }


    @Override
    public Iterator<String[]> iterator() {
        return new Iterator<String[]>() {
            List<String> buffer = null;
            BufferedReader br;

            {
                try {
                    br = new BufferedReader(new FileReader(file));
                    // set beginIndex and ignore header
                    for (int i = 1; i < beginIndex + 1; i++)
                        br.readLine();

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private List<String> loadBuffer() throws IOException, InvalidCsvException {
                if(buffer!=null)return buffer;
                long current = System.currentTimeMillis();
                String line = br.readLine();
                //Parameters.IO_READ_TIME += System.currentTimeMillis() - current;
                while (line != null) {
                    if (!line.isBlank() || !line.isEmpty()) {
                        List<String> tuple = new ArrayList<>();

                        recognizeItems(line, tuple, stringDelimiter, separator);

                        while (tuple.size() > columnsNameArray.length)
                            tuple.remove(tuple.size() - 1);
                        while (tuple.size() < columnsNameArray.length)
                            tuple.add(null);
                        buffer = tuple;
                        return buffer;
                    }
                    current = System.currentTimeMillis();
                    line = br.readLine();
                    //Parameters.IO_READ_TIME += System.currentTimeMillis() - current;
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                try {
                    return loadBuffer()!=null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvalidCsvException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String[] next() {
                try {
                    List<String> val = loadBuffer();
                    if(val==null)return null;
                    return val.stream().toArray(String[]::new);
                } catch (InvalidCsvException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    buffer = null;
                }
            }
        };
    }

    public String[] getColumnNames(){
        return columnsNameArray;
    }
    private static void recognizeItems(String line, List<String> tuple, char stringDelimiter, char separator) throws InvalidCsvException {

        boolean stringDelimiterFound = false;

        StringBuilder data = new StringBuilder();

        for (char c : line.toCharArray()) {

            stringDelimiterFound = c == stringDelimiter ? !stringDelimiterFound : stringDelimiterFound;

            if (c != separator || stringDelimiterFound)
                data.append(c);

            else {

                String inf = data.toString();

                if (isString(inf, stringDelimiter))
                    inf = inf.substring(1, inf.length() - 1);

                tuple.add(inf);
                data = new StringBuilder();

                isStringDelimiterWrong(inf, stringDelimiter);

            }

        }

        if (!data.isEmpty()) {

            String inf = data.toString();

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

}
