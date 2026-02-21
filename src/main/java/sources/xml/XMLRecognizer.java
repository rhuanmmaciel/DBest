package sources.xml;

import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidXMLException;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * XMLRecognizer is responsible for parsing XML files and converting hierarchical
 * data into a tabular format suitable for DBest.
 */
public class XMLRecognizer {

    private final File xmlFile;
    private final String rootElement;
    private final String recordElement;
    private final FlatteningStrategy strategy;

    public enum FlatteningStrategy {
        NESTED_COLUMNS,    // Convert nested elements to dot-notation columns (parent.child)
        SEPARATE_TABLES,   // Create separate tables for nested structures
        JSON_COLUMNS       // Store complex nested data as JSON strings
    }

    public XMLRecognizer(String filePath, String rootElement, String recordElement, FlatteningStrategy strategy) throws InvalidXMLException {
        this.xmlFile = new File(filePath);
        this.rootElement = rootElement;
        this.recordElement = recordElement;
        this.strategy = strategy != null ? strategy : FlatteningStrategy.NESTED_COLUMNS;

        if (!xmlFile.exists()) {
            throw new InvalidXMLException("XML file not found: " + filePath);
        }
    }

    public XMLRecognizer(String filePath) throws InvalidXMLException {
        this(filePath, null, null, FlatteningStrategy.NESTED_COLUMNS);
    }

    /**
     * Analyzes the XML structure and returns metadata about the file
     */
    public XMLAnalysisResult analyzeStructure() throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            // Auto-detect structure if not specified
            String detectedRoot = rootElement != null ? rootElement : root.getNodeName();
            String detectedRecord = recordElement;

            if (detectedRecord == null) {
                detectedRecord = detectRecordElement(root);
            }

            // Analyze schema
            List<Column> columns = analyzeSchema(document, detectedRecord);
            List<Map<String, String>> sampleData = extractSampleData(document, detectedRecord, 10);

            return new XMLAnalysisResult(
                detectedRoot,
                detectedRecord,
                columns,
                sampleData,
                strategy,
                countRecords(document, detectedRecord)
            );

        } catch (Exception e) {
            throw new InvalidXMLException("Error analyzing XML structure: " + e.getMessage());
        }
    }

    /**
     * Extracts all data from the XML file in tabular format
     */
    public XMLData extractData() throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            XMLAnalysisResult analysis = analyzeStructure();

            List<Map<String, String>> allData = extractAllData(document, analysis.getRecordElement());

            return new XMLData(
                analysis.getColumns(),
                allData,
                analysis.getRootElement(),
                analysis.getRecordElement()
            );

        } catch (Exception e) {
            throw new InvalidXMLException("Error extracting XML data: " + e.getMessage());
        }
    }

    /**
     * Extracts data for a separate table with parent references
     */
    public XMLData extractSeparateTableData(String parentRecordElement, String childElementName) throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            List<Map<String, String>> allData = extractSeparateTableAllData(document, parentRecordElement, childElementName);

            // Create columns for the separate table
            List<Column> columns = new ArrayList<>();
            columns.add(new Column("parent_id", "xml", ColumnDataType.STRING));

            // Analyze structure of the child element to get its columns
            if (!allData.isEmpty()) {
                Map<String, String> firstRow = allData.get(0);
                for (String columnName : firstRow.keySet()) {
                    if (!columnName.equals("parent_id")) {
                        columns.add(new Column(columnName, "xml", ColumnDataType.STRING));
                    }
                }
            }

            return new XMLData(
                columns,
                allData,
                rootElement,
                childElementName
            );

        } catch (Exception e) {
            throw new InvalidXMLException("Error extracting separate table data: " + e.getMessage());
        }
    }

    private String detectRecordElement(Element root) {
        // Find the most common child element that likely represents records
        Map<String, Integer> childCounts = new HashMap<>();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                childCounts.put(nodeName, childCounts.getOrDefault(nodeName, 0) + 1);
            }
        }

        // Return the element with highest count (most likely to be records)
        return childCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(root.getFirstChild() != null ? root.getFirstChild().getNodeName() : "record");
    }

    private List<Column> analyzeSchema(Document document, String recordElement) {
        Set<String> allColumns = new LinkedHashSet<>();
        Map<String, ColumnDataType> columnTypes = new HashMap<>();

        NodeList records = document.getElementsByTagName(recordElement);

        // Analyze first few records to determine schema
        int maxRecordsToAnalyze = Math.min(100, records.getLength());

        for (int i = 0; i < maxRecordsToAnalyze; i++) {
            Element record = (Element) records.item(i);
            Map<String, String> flatRecord = flattenElement(record, "");

            for (Map.Entry<String, String> entry : flatRecord.entrySet()) {
                String columnName = entry.getKey();
                String value = entry.getValue();

                allColumns.add(columnName);

                // Infer data type
                ColumnDataType inferredType = inferDataType(value);
                ColumnDataType existingType = columnTypes.get(columnName);

                if (existingType == null) {
                    columnTypes.put(columnName, inferredType);
                } else if (existingType != inferredType && existingType != ColumnDataType.STRING) {
                    // If types conflict, default to STRING
                    columnTypes.put(columnName, ColumnDataType.STRING);
                }
            }
        }

        // Convert to Column objects
        List<Column> columns = new ArrayList<>();
        for (String columnName : allColumns) {
            ColumnDataType type = columnTypes.getOrDefault(columnName, ColumnDataType.STRING);
            Column column = new Column(columnName, "xml", type, false, false);
            columns.add(column);
        }

        return columns;
    }

    private ColumnDataType inferDataType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ColumnDataType.STRING;
        }

        // Try integer
        try {
            Integer.parseInt(value);
            return ColumnDataType.INTEGER;
        } catch (NumberFormatException ignored) {}

        // Try double
        try {
            Double.parseDouble(value);
            return ColumnDataType.DOUBLE;
        } catch (NumberFormatException ignored) {}

        // Default to string
        return ColumnDataType.STRING;
    }

    private Map<String, String> flattenElement(Element element, String prefix) {
        Map<String, String> result = new HashMap<>();

        // Handle attributes
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            // For separate tables, use attribute name directly (no @ prefix)
            // For nested columns, use @ prefix to distinguish from elements
            String attrName;
            if (strategy == FlatteningStrategy.SEPARATE_TABLES && prefix.isEmpty()) {
                attrName = attr.getNodeName();
            } else {
                attrName = prefix.isEmpty() ? "@" + attr.getNodeName() : prefix + ".@" + attr.getNodeName();
            }
            result.put(attrName, attr.getNodeValue());
        }

        // Handle child elements and text content
        NodeList children = element.getChildNodes();
        boolean hasElementChildren = false;
        StringBuilder textContent = new StringBuilder();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                hasElementChildren = true;
                Element childElement = (Element) child;
                String childPrefix = prefix.isEmpty() ? child.getNodeName() : prefix + "." + child.getNodeName();

                if (strategy == FlatteningStrategy.NESTED_COLUMNS) {
                    result.putAll(flattenElement(childElement, childPrefix));
                } else if (strategy == FlatteningStrategy.SEPARATE_TABLES) {
                    // For SEPARATE_TABLES, check if this element will become a separate table
                    if (willBecomeSeperateTable(element, child.getNodeName())) {
                        // Create a reference ID for linking tables
                        String referenceId = generateReferenceId(element, childElement);
                        System.out.println("DEBUG: Main table - " + child.getNodeName() + "_id = " + referenceId + " (from element: " + element.getAttribute("id") + ")");
                        result.put(child.getNodeName() + "_id", referenceId);
                    } else {
                        // Include as regular column(s)
                        if (hasOnlyTextContent(childElement)) {
                            result.put(child.getNodeName(), childElement.getTextContent());
                        } else {
                            // Flatten complex elements that won't become separate tables
                            result.putAll(flattenElement(childElement, childPrefix));
                        }
                    }
                } else if (strategy == FlatteningStrategy.JSON_COLUMNS) {
                    // For JSON_COLUMNS, store complex structures as JSON strings
                    result.put(child.getNodeName(), elementToJson(childElement));
                } else {
                    // Default fallback
                    result.put(childPrefix, childElement.getTextContent());
                }
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    textContent.append(text);
                }
            }
        }

        // If element has no child elements, use its text content
        if (!hasElementChildren && textContent.length() > 0) {
            // For elements with only text content, use "value" as column name to avoid conflicts
            String columnName = prefix.isEmpty() ? "value" : prefix;
            result.put(columnName, textContent.toString());
        }

        return result;
    }

    private List<Map<String, String>> extractSampleData(Document document, String recordElement, int maxSamples) {
        List<Map<String, String>> sampleData = new ArrayList<>();
        NodeList records = document.getElementsByTagName(recordElement);

        int samplesToExtract = Math.min(maxSamples, records.getLength());

        for (int i = 0; i < samplesToExtract; i++) {
            Element record = (Element) records.item(i);
            Map<String, String> flatRecord = flattenElement(record, "");
            sampleData.add(flatRecord);
        }

        return sampleData;
    }

    private List<Map<String, String>> extractAllData(Document document, String recordElement) {
        List<Map<String, String>> allData = new ArrayList<>();
        NodeList records = document.getElementsByTagName(recordElement);

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            Map<String, String> flatRecord = flattenElement(record, "");
            allData.add(flatRecord);
        }

        return allData;
    }

    /**
     * Extracts data for separate tables with parent references
     */
    private List<Map<String, String>> extractSeparateTableAllData(Document document, String parentRecordElement, String childElementName) {
        List<Map<String, String>> allData = new ArrayList<>();
        NodeList parentRecords = document.getElementsByTagName(parentRecordElement);

        for (int i = 0; i < parentRecords.getLength(); i++) {
            Element parentRecord = (Element) parentRecords.item(i);

            // Generate parent ID for this record
            String parentId = findElementId(parentRecord);
            if (parentId == null) {
                parentId = String.valueOf(Math.abs(parentRecord.toString().hashCode()));
            }
            System.out.println("DEBUG: Separate table - parent_id = " + parentId + " (from element: " + parentRecord.getAttribute("id") + ")");

            // Find all direct child elements with the specified name within this parent
            NodeList childElements = getDirectChildElements(parentRecord, childElementName);

            for (int j = 0; j < childElements.getLength(); j++) {
                Element childElement = (Element) childElements.item(j);

                // Flatten the child element data
                Map<String, String> childData = flattenElement(childElement, "");

                // Add parent reference
                childData.put("parent_id", parentId);

                allData.add(childData);
            }
        }

        return allData;
    }

    private int countRecords(Document document, String recordElement) {
        NodeList records = document.getElementsByTagName(recordElement);
        return records.getLength();
    }

    /**
     * Gets all unique element names in the XML document
     */
    public List<String> getAllElementNames() throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Set<String> elementNames = new LinkedHashSet<>();
            collectElementNames(document.getDocumentElement(), elementNames);

            return new ArrayList<>(elementNames);
        } catch (Exception e) {
            throw new InvalidXMLException("Error reading XML structure: " + e.getMessage());
        }
    }

    /**
     * Recursively collects all element names from the XML tree
     */
    private void collectElementNames(Element element, Set<String> elementNames) {
        elementNames.add(element.getNodeName());

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                collectElementNames((Element) child, elementNames);
            }
        }
    }

    /**
     * Gets potential record elements (elements that appear multiple times)
     */
    public List<String> getPotentialRecordElements() throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Map<String, Integer> elementCounts = new HashMap<>();
            countElements(document.getDocumentElement(), elementCounts);

            // Return elements that appear more than once, sorted by frequency
            return elementCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InvalidXMLException("Error analyzing XML structure: " + e.getMessage());
        }
    }

    /**
     * Recursively counts occurrences of each element
     */
    private void countElements(Element element, Map<String, Integer> elementCounts) {
        String elementName = element.getNodeName();
        elementCounts.put(elementName, elementCounts.getOrDefault(elementName, 0) + 1);

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                countElements((Element) child, elementCounts);
            }
        }
    }

    /**
     * Checks if an element contains only text content (no child elements)
     */
    private boolean hasOnlyTextContent(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts an XML element to a JSON-like string representation
     */
    private String elementToJson(Element element) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // Add attributes
        NamedNodeMap attributes = element.getAttributes();
        boolean hasAttributes = attributes.getLength() > 0;

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            if (i > 0) json.append(",");
            json.append("\"@").append(attr.getNodeName()).append("\":\"")
                .append(escapeJsonString(attr.getNodeValue())).append("\"");
        }

        // Add child elements
        NodeList children = element.getChildNodes();
        Map<String, Integer> childCounts = new HashMap<>();

        // Count child elements
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();
                childCounts.put(name, childCounts.getOrDefault(name, 0) + 1);
            }
        }

        // Process child elements
        Map<String, List<String>> childValues = new HashMap<>();
        StringBuilder textContent = new StringBuilder();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();
                String value = elementToJson((Element) child);
                childValues.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    textContent.append(text);
                }
            }
        }

        // Add child elements to JSON
        boolean needsComma = hasAttributes;
        for (Map.Entry<String, List<String>> entry : childValues.entrySet()) {
            if (needsComma) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");

            List<String> values = entry.getValue();
            if (values.size() == 1) {
                json.append(values.get(0));
            } else {
                json.append("[").append(String.join(",", values)).append("]");
            }
            needsComma = true;
        }

        // Add text content if present and no child elements
        if (textContent.length() > 0 && childValues.isEmpty()) {
            if (needsComma) json.append(",");
            json.append("\"#text\":\"").append(escapeJsonString(textContent.toString())).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Escapes special characters in JSON strings
     */
    private String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Generates a reference ID for linking parent and child tables
     */
    private String generateReferenceId(Element parentElement, Element childElement) {
        // Try to find a unique identifier in the parent element
        String parentId = findElementId(parentElement);
        if (parentId == null) {
            // If no ID found, create one based on element position and content
            parentId = String.valueOf(Math.abs(parentElement.toString().hashCode()));
        }

        // Return just the parent ID - no need to append child position
        // since we want to link the child table back to the parent record
        return parentId;
    }

    /**
     * Finds an ID attribute or unique identifier in an element
     */
    private String findElementId(Element element) {
        // Check common ID attribute names
        String[] idAttributes = {"id", "ID", "Id", "key", "KEY", "Key"};
        for (String attr : idAttributes) {
            if (element.hasAttribute(attr)) {
                return element.getAttribute(attr);
            }
        }

        // Check for child elements that might be IDs
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName().toLowerCase();
                if (nodeName.contains("id") || nodeName.contains("key")) {
                    return child.getTextContent().trim();
                }
            }
        }

        return null;
    }

    /**
     * Checks if a child element will become a separate table
     */
    private boolean willBecomeSeperateTable(Element parentElement, String childElementName) {
        // Count how many times this child element appears within this parent
        int count = 0;
        NodeList children = parentElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childElementName)) {
                count++;
            }
        }

        // Element becomes separate table if it appears multiple times within the same parent
        return count > 1;
    }

    /**
     * Gets direct child elements with the specified name (not descendants)
     */
    private NodeList getDirectChildElements(Element parent, String elementName) {
        List<Element> directChildren = new ArrayList<>();
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                child.getNodeName().equals(elementName)) {
                directChildren.add((Element) child);
            }
        }

        // Return a custom NodeList implementation
        return new NodeList() {
            @Override
            public Node item(int index) {
                return index < directChildren.size() ? directChildren.get(index) : null;
            }

            @Override
            public int getLength() {
                return directChildren.size();
            }
        };
    }



    /**
     * Gets potential separate tables when using SEPARATE_TABLES strategy
     */
    public List<SeparateTableInfo> getPotentialSeparateTables() throws InvalidXMLException {
        if (strategy != FlatteningStrategy.SEPARATE_TABLES) {
            return new ArrayList<>();
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();
            String detectedRecord = recordElement != null ? recordElement : detectRecordElement(root);

            List<SeparateTableInfo> separateTables = new ArrayList<>();

            // Find the first record element to analyze its structure
            NodeList records = document.getElementsByTagName(detectedRecord);
            if (records.getLength() > 0) {
                Element firstRecord = (Element) records.item(0);
                findSeparateTableCandidates(firstRecord, "", separateTables);
            }

            return separateTables;
        } catch (Exception e) {
            throw new InvalidXMLException("Error analyzing separate tables: " + e.getMessage());
        }
    }

    /**
     * Recursively finds candidates for separate tables
     */
    private void findSeparateTableCandidates(Element element, String parentPath, List<SeparateTableInfo> separateTables) {
        // Count occurrences of each child element name within this parent
        Map<String, Integer> childElementCounts = new HashMap<>();
        Map<String, Element> childElementSamples = new HashMap<>();

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String elementName = child.getNodeName();
                childElementCounts.put(elementName, childElementCounts.getOrDefault(elementName, 0) + 1);
                if (!childElementSamples.containsKey(elementName)) {
                    childElementSamples.put(elementName, (Element) child);
                }
            }
        }

        // Check which child elements appear multiple times within this parent
        for (Map.Entry<String, Integer> entry : childElementCounts.entrySet()) {
            String elementName = entry.getKey();
            int countInThisParent = entry.getValue();
            Element sampleElement = childElementSamples.get(elementName);

            if (countInThisParent > 1) {
                // This element appears multiple times within the same parent - it's a candidate for separate table
                String fullPath = parentPath.isEmpty() ? elementName : parentPath + "." + elementName;

                // Count total occurrences across the entire document
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(xmlFile);

                    NodeList allOccurrences = document.getElementsByTagName(elementName);

                    // Add to separate tables list
                    separateTables.add(new SeparateTableInfo(
                        elementName,
                        fullPath,
                        allOccurrences.getLength(),
                        analyzeElementStructure(sampleElement)
                    ));
                    System.out.println("DEBUG: Found separate table candidate: " + elementName +
                                     " (total occurrences: " + allOccurrences.getLength() +
                                     ", repeats in parent: " + countInThisParent + ")");
                } catch (Exception e) {
                    // Continue processing other elements
                }
            }
        }

        // Recursively check nested elements that have complex structure
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                String elementName = child.getNodeName();
                String fullPath = parentPath.isEmpty() ? elementName : parentPath + "." + elementName;

                // Only recurse into elements that have nested structure and don't repeat in this parent
                if (!hasOnlyTextContent(childElement) && childElementCounts.get(elementName) == 1) {
                    findSeparateTableCandidates(childElement, fullPath, separateTables);
                }
            }
        }
    }

    /**
     * Analyzes the structure of an element to determine its columns
     */
    private List<String> analyzeElementStructure(Element element) {
        List<String> columns = new ArrayList<>();

        // Add attributes as columns (without @ prefix for separate tables)
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            columns.add(attr.getNodeName());
        }

        // If element has only text content, add a "value" column
        if (hasOnlyTextContent(element)) {
            columns.add("value");
        } else {
            // If element has child elements, add them as columns
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    columns.add(child.getNodeName());
                }
            }
        }

        return columns;
    }

    /**
     * Information about a potential separate table
     */
    public static class SeparateTableInfo {
        private final String elementName;
        private final String fullPath;
        private final int occurrenceCount;
        private final List<String> columns;

        public SeparateTableInfo(String elementName, String fullPath, int occurrenceCount, List<String> columns) {
            this.elementName = elementName;
            this.fullPath = fullPath;
            this.occurrenceCount = occurrenceCount;
            this.columns = columns;
        }

        public String getElementName() { return elementName; }
        public String getFullPath() { return fullPath; }
        public int getOccurrenceCount() { return occurrenceCount; }
        public List<String> getColumns() { return columns; }

        @Override
        public String toString() {
            return String.format("%s (%d occurrences, %d columns)", elementName, occurrenceCount, columns.size());
        }
    }
}
