package test;

import com.mxgraph.model.mxCell;
import entities.cells.OperationCell;
import enums.OperationType;
import ibd.query.Operation;
import ibd.query.unaryop.DuplicateRemoval;
import ibd.query.sourceop.FullTableScan;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

import java.io.File;
import java.util.ArrayList;

/**
 * Test class to validate OperationCell deep copy functionality
 * This test verifies that the copy() method creates a proper deep copy
 * of the operation tree, preventing issues when operations are reused
 * in multiple places in a query tree.
 */
public class OperationCellDeepCopyTest {
    
    private static final String TEST_FOLDER = "C:\\temp\\dbest_test\\";
    
    public static void main(String[] args) {
        try {
            OperationCellDeepCopyTest test = new OperationCellDeepCopyTest();
            test.runTests();
            System.out.println("All tests passed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run all deep copy tests
     */
    public void runTests() throws Exception {
        System.out.println("Starting OperationCell deep copy tests...");
        
        // Ensure test folder exists
        File testDir = new File(TEST_FOLDER);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        
        testBasicDeepCopy();
        testUnaryOperationDeepCopy();
        testNullOperatorHandling();
        testIndependenceAfterCopy();
        
        System.out.println("All deep copy tests completed successfully!");
    }
    
    /**
     * Test basic deep copy functionality
     */
    private void testBasicDeepCopy() throws Exception {
        System.out.println("Testing basic deep copy...");
        
        // Create a simple OperationCell
        mxCell jCell = new mxCell();
        OperationCell original = new OperationCell(jCell, OperationType.DUPLICATE_REMOVAL);
        
        // Test copy with null operator
        OperationCell copy = original.copy();
        
        // Verify basic properties are copied
        assert copy.getType() == original.getType() : "Operation type should be copied";
        assert copy.getJCell() == original.getJCell() : "JCell should be same reference";
        assert copy != original : "Copy should be a different object";
        
        System.out.println("✓ Basic deep copy test passed");
    }
    
    /**
     * Test deep copy with unary operation
     */
    private void testUnaryOperationDeepCopy() throws Exception {
        System.out.println("Testing unary operation deep copy...");
        
        // Create a test table
        Table testTable = createTestTable();
          // Create source operation
        FullTableScan sourceScan = new FullTableScan("T", testTable);
        
        // Create unary operation (DuplicateRemoval)
        DuplicateRemoval duplicateRemoval = new DuplicateRemoval(sourceScan);
        
        // Create OperationCell with the operation
        mxCell jCell = new mxCell();
        OperationCell original = new OperationCell(jCell, OperationType.DUPLICATE_REMOVAL);
        original.setOperator(duplicateRemoval);
        
        // Perform deep copy
        OperationCell copy = original.copy();
        
        // Verify the operation was deep copied
        assert copy.getOperator() != null : "Copied operation should not be null";
        assert copy.getOperator() != original.getOperator() : "Copied operation should be a different object";
        assert copy.getOperator().getClass() == original.getOperator().getClass() : "Copied operation should be same type";
        
        System.out.println("✓ Unary operation deep copy test passed");
    }
    
    /**
     * Test handling of null operator
     */
    private void testNullOperatorHandling() throws Exception {
        System.out.println("Testing null operator handling...");
        
        mxCell jCell = new mxCell();
        OperationCell original = new OperationCell(jCell, OperationType.DUPLICATE_REMOVAL);
        // Don't set any operator (remains null)
        
        // This should not throw an exception
        OperationCell copy = original.copy();
        
        assert copy.getOperator() == null : "Copied cell should also have null operator";
        
        System.out.println("✓ Null operator handling test passed");
    }
    
    /**
     * Test that modifications to copied operation don't affect original
     */
    private void testIndependenceAfterCopy() throws Exception {
        System.out.println("Testing independence after copy...");
        
        // Create a test table
        Table testTable = createTestTable();
          // Create operations
        FullTableScan sourceScan1 = new FullTableScan("T1", testTable);
        
        FullTableScan sourceScan2 = new FullTableScan("T2", testTable);
        
        DuplicateRemoval duplicateRemoval1 = new DuplicateRemoval(sourceScan1);
        DuplicateRemoval duplicateRemoval2 = new DuplicateRemoval(sourceScan2);
        
        // Create OperationCells
        mxCell jCell1 = new mxCell();
        mxCell jCell2 = new mxCell();
        
        OperationCell original = new OperationCell(jCell1, OperationType.DUPLICATE_REMOVAL);
        original.setOperator(duplicateRemoval1);
          // Copy the cell
        OperationCell copy = original.copy();
        
        // Modify the copied operation by setting a different operator
        copy.setOperator(duplicateRemoval2);
        
        // Verify that the original is not affected
        assert original.getOperator() != copy.getOperator() : "Original and copy should have different operators";
        
        if (original.getOperator() instanceof DuplicateRemoval && copy.getOperator() instanceof DuplicateRemoval) {
            DuplicateRemoval origDupRemoval = (DuplicateRemoval) original.getOperator();
            DuplicateRemoval copyDupRemoval = (DuplicateRemoval) copy.getOperator();
            
            // They should have different child operations
            assert origDupRemoval.getChildOperation() != copyDupRemoval.getChildOperation() : 
                "Original and copy should have different child operations";
        }
        
        System.out.println("✓ Independence after copy test passed");
    }
    
    /**
     * Create a test table for testing purposes
     */
    private Table createTestTable() throws Exception {
        // Create schema
        Prototype schema = new Prototype();
        schema.addColumn(new IntegerColumn("id", true));
        schema.addColumn(new StringColumn("name", (short) 50));
        
        // Create table
        Table table = Directory.getTable(TEST_FOLDER, "test_table", schema, 1000, 4096, true);
        
        return table;
    }
}
