package sources.xml;

import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidXMLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for XMLRecognizer
 */
public class XMLRecognizerTest {

    @TempDir
    Path tempDir;

    private Path xmlFile;

    @BeforeEach
    void setUp() throws IOException {
        xmlFile = tempDir.resolve("test.xml");
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<employees>\n" +
            "    <employee id=\"1\">\n" +
            "        <name>João Silva</name>\n" +
            "        <age>30</age>\n" +
            "        <department>TI</department>\n" +
            "        <salary>5000.00</salary>\n" +
            "    </employee>\n" +
            "    <employee id=\"2\">\n" +
            "        <name>Maria Santos</name>\n" +
            "        <age>25</age>\n" +
            "        <department>RH</department>\n" +
            "        <salary>4500.00</salary>\n" +
            "    </employee>\n" +
            "</employees>";
        Files.writeString(xmlFile, xmlContent);
    }

    @Test
    void testAnalyzeStructure() throws InvalidXMLException {
        XMLRecognizer recognizer = new XMLRecognizer(xmlFile.toString());
        XMLAnalysisResult result = recognizer.analyzeStructure();

        assertNotNull(result);
        assertEquals("employees", result.getRootElement());
        assertEquals("employee", result.getRecordElement());
        assertEquals(XMLRecognizer.FlatteningStrategy.NESTED_COLUMNS, result.getStrategy());
        assertEquals(2, result.getTotalRecords());

        List<Column> columns = result.getColumns();
        assertNotNull(columns);
        assertTrue(columns.size() >= 4); // id, name, age, department, salary

        // Check if expected columns exist
        boolean hasId = columns.stream().anyMatch(col -> col.NAME.equals("@id"));
        boolean hasName = columns.stream().anyMatch(col -> col.NAME.equals("name"));
        boolean hasAge = columns.stream().anyMatch(col -> col.NAME.equals("age"));
        boolean hasDepartment = columns.stream().anyMatch(col -> col.NAME.equals("department"));
        boolean hasSalary = columns.stream().anyMatch(col -> col.NAME.equals("salary"));

        assertTrue(hasId, "Should have '@id' column");
        assertTrue(hasName, "Should have 'name' column");
        assertTrue(hasAge, "Should have 'age' column");
        assertTrue(hasDepartment, "Should have 'department' column");
        assertTrue(hasSalary, "Should have 'salary' column");
    }

    @Test
    void testExtractData() throws InvalidXMLException {
        XMLRecognizer recognizer = new XMLRecognizer(xmlFile.toString());
        XMLData data = recognizer.extractData();

        assertNotNull(data);
        assertEquals("employees", data.getRootElement());
        assertEquals("employee", data.getRecordElement());
        assertEquals(2, data.getRecordCount());
        assertTrue(data.getColumnCount() >= 4);

        List<Map<String, String>> records = data.getData();
        assertNotNull(records);
        assertEquals(2, records.size());

        // Check first record
        Map<String, String> firstRecord = records.get(0);
        assertEquals("1", firstRecord.get("@id"));
        assertEquals("João Silva", firstRecord.get("name"));
        assertEquals("30", firstRecord.get("age"));
        assertEquals("TI", firstRecord.get("department"));
        assertEquals("5000.00", firstRecord.get("salary"));

        // Check second record
        Map<String, String> secondRecord = records.get(1);
        assertEquals("2", secondRecord.get("@id"));
        assertEquals("Maria Santos", secondRecord.get("name"));
        assertEquals("25", secondRecord.get("age"));
        assertEquals("RH", secondRecord.get("department"));
        assertEquals("4500.00", secondRecord.get("salary"));
    }

    @Test
    void testWithSpecificElements() throws InvalidXMLException {
        XMLRecognizer recognizer = new XMLRecognizer(xmlFile.toString(), "employees", "employee", XMLRecognizer.FlatteningStrategy.NESTED_COLUMNS);
        XMLAnalysisResult result = recognizer.analyzeStructure();

        assertNotNull(result);
        assertEquals("employees", result.getRootElement());
        assertEquals("employee", result.getRecordElement());
        assertEquals(2, result.getTotalRecords());
    }

    @Test
    void testInvalidFile() {
        assertThrows(InvalidXMLException.class, () -> {
            new XMLRecognizer("nonexistent.xml");
        });
    }

    @Test
    void testDataTypeInference() throws InvalidXMLException {
        XMLRecognizer recognizer = new XMLRecognizer(xmlFile.toString());
        XMLAnalysisResult result = recognizer.analyzeStructure();

        List<Column> columns = result.getColumns();

        // Find specific columns and check their inferred types
        Column ageColumn = columns.stream()
            .filter(col -> col.NAME.equals("age"))
            .findFirst()
            .orElse(null);

        Column salaryColumn = columns.stream()
            .filter(col -> col.NAME.equals("salary"))
            .findFirst()
            .orElse(null);

        Column nameColumn = columns.stream()
            .filter(col -> col.NAME.equals("name"))
            .findFirst()
            .orElse(null);

        if (ageColumn != null) {
            assertTrue(ageColumn.DATA_TYPE == ColumnDataType.INTEGER || ageColumn.DATA_TYPE == ColumnDataType.LONG);
        }

        if (salaryColumn != null) {
            assertTrue(salaryColumn.DATA_TYPE == ColumnDataType.DOUBLE || salaryColumn.DATA_TYPE == ColumnDataType.FLOAT);
        }

        if (nameColumn != null) {
            assertEquals(ColumnDataType.STRING, nameColumn.DATA_TYPE);
        }
    }
}
