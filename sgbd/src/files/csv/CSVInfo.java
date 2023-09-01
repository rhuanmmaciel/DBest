package files.csv;

import java.nio.file.Path;

public record CsvInfo(char separator, char stringDelimiter, int beginIndex, Path path) {
}
