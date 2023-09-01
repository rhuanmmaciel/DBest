package files.csv;

import java.nio.file.Path;

public record CSVInfo(char separator, char stringDelimiter, int beginIndex, Path path) {

}
