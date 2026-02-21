package files.csv;

import entities.Column;
import java.nio.file.Path;
import java.util.List;

public record CSVInfo(char separator, char stringDelimiter, int beginIndex, Path path, String tableName, List<Column> columns) {

}
