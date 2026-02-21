package sources.xml;

import entities.Column;
import java.util.List;
import java.util.Map;

/**
 * Result of XML structure analysis containing metadata about the XML file
 */
public class XMLAnalysisResult {
    
    private final String rootElement;
    private final String recordElement;
    private final List<Column> columns;
    private final List<Map<String, String>> sampleData;
    private final XMLRecognizer.FlatteningStrategy strategy;
    private final int totalRecords;
    
    public XMLAnalysisResult(String rootElement, String recordElement, List<Column> columns, 
                           List<Map<String, String>> sampleData, XMLRecognizer.FlatteningStrategy strategy, 
                           int totalRecords) {
        this.rootElement = rootElement;
        this.recordElement = recordElement;
        this.columns = columns;
        this.sampleData = sampleData;
        this.strategy = strategy;
        this.totalRecords = totalRecords;
    }
    
    public String getRootElement() {
        return rootElement;
    }
    
    public String getRecordElement() {
        return recordElement;
    }
    
    public List<Column> getColumns() {
        return columns;
    }
    
    public List<Map<String, String>> getSampleData() {
        return sampleData;
    }
    
    public XMLRecognizer.FlatteningStrategy getStrategy() {
        return strategy;
    }
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    @Override
    public String toString() {
        return String.format("XMLAnalysisResult{rootElement='%s', recordElement='%s', columns=%d, totalRecords=%d, strategy=%s}", 
                           rootElement, recordElement, columns.size(), totalRecords, strategy);
    }
}
