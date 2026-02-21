package sources.xml;

import entities.Column;
import java.util.List;
import java.util.Map;

/**
 * Container for extracted XML data in tabular format
 */
public class XMLData {
    
    private final List<Column> columns;
    private final List<Map<String, String>> data;
    private final String rootElement;
    private final String recordElement;
    
    public XMLData(List<Column> columns, List<Map<String, String>> data, String rootElement, String recordElement) {
        this.columns = columns;
        this.data = data;
        this.rootElement = rootElement;
        this.recordElement = recordElement;
    }
    
    public List<Column> getColumns() {
        return columns;
    }
    
    public List<Map<String, String>> getData() {
        return data;
    }
    
    public String getRootElement() {
        return rootElement;
    }
    
    public String getRecordElement() {
        return recordElement;
    }
    
    public int getRecordCount() {
        return data.size();
    }
    
    public int getColumnCount() {
        return columns.size();
    }
    
    @Override
    public String toString() {
        return String.format("XMLData{columns=%d, records=%d, rootElement='%s', recordElement='%s'}", 
                           columns.size(), data.size(), rootElement, recordElement);
    }
}
