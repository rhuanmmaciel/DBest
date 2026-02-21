package files.xml;

import entities.Column;
import sources.xml.XMLRecognizer;
import java.nio.file.Path;
import java.util.List;

/**
 * Record containing XML import configuration and metadata
 */
public record XMLInfo(
    String rootElement, 
    String recordElement, 
    XMLRecognizer.FlatteningStrategy strategy, 
    Path path, 
    String tableName, 
    List<Column> columns
) {
    
    /**
     * Creates XMLInfo with default flattening strategy
     */
    public static XMLInfo create(String rootElement, String recordElement, Path path, String tableName, List<Column> columns) {
        return new XMLInfo(rootElement, recordElement, XMLRecognizer.FlatteningStrategy.NESTED_COLUMNS, path, tableName, columns);
    }
    
    /**
     * Creates XMLInfo with auto-detected elements
     */
    public static XMLInfo createAutoDetect(Path path, String tableName, List<Column> columns) {
        return new XMLInfo(null, null, XMLRecognizer.FlatteningStrategy.NESTED_COLUMNS, path, tableName, columns);
    }
}
