/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import ibd.query.binaryop.set.Difference;
import ibd.query.binaryop.join.BlockNestedLoopJoin;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.sourceop.FullTableScan;
import ibd.query.unaryop.HashIndex;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.binaryop.join.outer.NestedLoopLeftJoin;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.CompositeLookupFilter;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.IndexScan;
import ibd.query.unaryop.aggregation.Aggregation;
import ibd.query.unaryop.Projection;
import ibd.query.unaryop.aggregation.AggregationType;
import ibd.query.unaryop.filter.Filter;
import ibd.query.unaryop.sort.Sort;
import static ibd.table.ComparisonTypes.*;
import ibd.table.Params;
import ibd.table.DataFaker;
import ibd.table.Directory;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class MainPerson {

    //returns all records from a table
    public Operation testSimpleQuery() throws Exception {
        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, true, 1, 50);
        //GenericTable1_1 table1 = Directory.getTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
        Operation scan = new IndexScan("t1", table1);

        //Operation scan = new FullTableScan("t1", table1);
        return scan;

    }

    //returns the record that satisfies a simple filter
    public Operation testSimpleFilterQuery(String col, Comparable value, int compType) throws Exception {
        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, true, 1, 50);
        //Table table1 = Directory.getTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
        Operation scan1 = new IndexScan("t1", table1);

        SingleColumnLookupFilter filter = new SingleColumnLookupFilter(new ColumnElement(col), compType, new LiteralElement(value));

        Filter scan = new Filter(scan1, filter);
        return scan;
    }

    //returns the record that satisfies a simple filter
    public Operation testCompositeFilterQuery(String col1, Comparable value1, int comparisonType1,
            String col2, Comparable value2, int comparisonType2) throws Exception {
        //Table table1 = createTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, true, 1, 50);
        Table table1 = Directory.getTable("c:\\teste\\ibd", "tab1", null, 99999, Table.DEFULT_PAGE_SIZE, false);
        Operation scan1 = new IndexScan("t1", table1);

        CompositeLookupFilter compositeFilter = new CompositeLookupFilter(CompositeLookupFilter.AND);

        SingleColumnLookupFilter filter1 = new SingleColumnLookupFilter(new ColumnElement(col1), comparisonType1, new LiteralElement(value1));
        SingleColumnLookupFilter filter2 = new SingleColumnLookupFilter(new ColumnElement(col2), comparisonType2, new LiteralElement(value2));
        compositeFilter.addFilter(filter1);
        compositeFilter.addFilter(filter2);

        Filter scan = new Filter(scan1, compositeFilter);
        return scan;
    }

    //returns all records from a table sorted by content
    public Operation testSortQuery(String[] col) throws Exception {
        //Table table1 = createTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, true, 1, 50);
        Table table1 = Directory.getTable("c:\\teste\\ibd", "tab1", null, 99999, Table.DEFULT_PAGE_SIZE, false);

        Operation scan = new Sort(new FullTableScan("t1", table1), col, true);
        //Operation scan = new ContentSort(new FullTableScan("t1", table1), "t1");
        return scan;

    }

    public Operation testGroupByQuery(String groupByColumn, String groupedColumn, int type) throws Exception {
        //Table table1 = createTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 10, true, 1, 50);
        //Table table1 = Directory.getTable(null, "c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 10, false, 1, 50);
        FullTableScan scan = new FullTableScan("t1", table1);
        Operation groupBy = new Aggregation(scan, "g", groupByColumn, groupedColumn, type, false);
        //Operation scan = new ContentSort(new FullTableScan("t1", table1), "t1");
        return groupBy;

    }
    
    public Operation testGroupByQuery2(String groupByColumn, List<AggregationType> operations) throws Exception {
        //Table table1 = createTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 10, true, 1, 50);
        //Table table1 = Directory.getTable(null, "c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 10, false, 1, 50);
        FullTableScan scan = new FullTableScan("t1", table1);
        Operation groupBy = new Aggregation(scan, "g", groupByColumn, operations, false);
        //Operation scan = new ContentSort(new FullTableScan("t1", table1), "t1");
        return groupBy;

    }

    public Operation testDifferenceQuery(String col, Comparable value1, Comparable value2) throws Exception {
        //Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 10, true, 1, 50);
        //Table table1 = Directory.getTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
        Table table1 = Directory.getTable("c:\\teste\\ibd", "tab1", null, 99999, Table.DEFULT_PAGE_SIZE, false);
        Operation scan1 = new IndexScan("t1", table1);
        SingleColumnLookupFilter lookupFilter1 = new SingleColumnLookupFilter(new ColumnElement(col), LOWER_EQUAL_THAN, new LiteralElement(value1));
        Filter filter1 = new Filter(scan1, lookupFilter1);

        Operation scan2 = new IndexScan("t2", table1);
        SingleColumnLookupFilter lookupFilter2 = new SingleColumnLookupFilter(new ColumnElement(col), LOWER_EQUAL_THAN, new LiteralElement(value2));
        Filter filter2 = new Filter(scan2, lookupFilter2);

        Projection proj1 = new Projection(filter1, new String[]{col});
        Projection proj2 = new Projection(filter2, new String[]{col});
        Difference dif = new Difference(proj1, proj2);

//        SingleColumnLookupFilterByValue lookupFilter3 = new SingleColumnLookupFilterByValue(col, EQUAL, 46);
//        Filter filter3 = new Filter(dif,  lookupFilter3);
        //Operation scan = new ContentSort(new FullTableScan("t1", table1), "t1");
        return dif;

    }

    //returns all records produced after a join between two tables using nested loop join.
    public Operation testNestedLoopJoinQuery() throws Exception {

        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, true, 20, 50);
        Table table2 = createPersonTable("c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE, 1000, true, 50, 50);

//        Table table1 = Directory.getTable(null, "c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
//        Table table2 = Directory.getTable(null, "c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE,  false);
//        
        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        Operation join1 = new NestedLoopLeftJoin(scan1, scan2, terms);
        //Operation join1 = new MergeJoin(scan1, scan2);
        return join1;

    }

    //returns all records produced after a join between two tables using indexed nested loop join.
    public Operation testIndexedNestedLoopJoinQuery() throws Exception {

        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Table table2 = createPersonTable("c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE, 1000, false, 25, 50);

        //Table table1 = Directory.getTable(null, "c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,false);
        //Table table2 = Directory.getTable(null, "c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE,false);
        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        //use this to rely on the existing physical btree index over the table
        Operation indexed = new IndexScan("t2", table2);
        //use this to built an in-memory index over the table specifically to answer this query
        Operation indexed2 = new HashIndex(scan2);

        //use indexed instead of indexed2 if you want the in-memory index instead of the physical index
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        Operation join1 = new BlockNestedLoopJoin(scan1, scan2, terms, 10);
        return join1;

    }

    //returns all records produced after a join between two tables and a filter.
    public Operation testNestedLoopJoinQueryWithFilter(String col, Comparable pk) throws Exception {

        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);
        Table table2 = createPersonTable("c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE, 1000, false, 50, 50);

        //Table table1 = Directory.getTable(null, "c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE,  false);
        //Table table2 = Directory.getTable(null, "c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE,  false);
        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new FullTableScan("t2", table2);

        SingleColumnLookupFilter filter_ = new SingleColumnLookupFilter(new ColumnElement(col), EQUAL, new LiteralElement(pk));

        Filter filter = new Filter(scan2, filter_);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        Operation join1 = new NestedLoopJoin(scan1, filter, terms);
        return join1;

    }

    //returns all records produced after a join among three tables.
    public Operation testNestedLoopJoinQuery1() throws Exception {

        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, false, 5, 50);
        Table table2 = createPersonTable("c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE, 1000, false, 2, 50);
        Table table3 = createPersonTable("c:\\teste\\ibd", "tab3", Table.DEFULT_PAGE_SIZE, 1000, false, 1, 50);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);
        Operation scan3 = new IndexScan("t3", table3);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        Operation join1 = new NestedLoopJoin(scan1, scan2, terms);
        Operation join2 = new NestedLoopJoin(join1, scan3, terms);

        return join2;

    }

    public Operation testMergeJoinQuery1() throws Exception {

        Table table1 = createPersonTable("c:\\teste\\ibd", "tab1", Table.DEFULT_PAGE_SIZE, 1000, false, 5, 50);
        Table table2 = createPersonTable("c:\\teste\\ibd", "tab2", Table.DEFULT_PAGE_SIZE, 1000, false, 2, 50);

        Operation scan1 = new FullTableScan("t1", table1);
        Operation scan2 = new IndexScan("t2", table2);

        Sort sorter1 = new Sort(scan1, "id", true);
        Sort sorter2 = new Sort(scan2, "id", true);

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        Operation join1 = new MergeJoin(sorter1, sorter2, terms);

        return join1;

    }

    static public Table createPersonTable(String folder, String name, int pageSize, int size, boolean shuffled, int range, int cardinality) throws Exception {

        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome", (short)20));
        pt.addColumn(new IntegerColumn("idade"));
        pt.addColumn(new StringColumn("cidade",(short)20));

        Table table = Directory.getTable(folder, name, pt, 999999, pageSize, true);
        //table.initLoad();

        Integer[] array1 = new Integer[(int) Math.ceil((double) size / range)];
        for (int i = 0; i < array1.length; i++) {
            array1[i] = i * range;
        }

        if (shuffled) {
            DataFaker.shuffleArray(array1);
        }

        String names_[] = DataFaker.generateNames(cardinality, array1.length);

        String cidades[] = DataFaker.generateStrings(new String[]{"Santa Maria", "Porto Alegre", "Sao Paulo"}, array1.length, true);

        //Integer idades[] = DataFaker.generateInts(20,array1.length, 20, 2, true );
        Integer idades[] = new Integer[array1.length];

        int groupSize = 1 + array1.length / 3;
        int startValue = 20;
        int gap = 20;
        int offset = 0;
        for (int i = 0; i < 3; i++) {
            Integer idades_[] = DataFaker.generateInts(20, groupSize, startValue, 2, true);
            System.arraycopy(idades_, 0, idades, offset, idades_.length);
            startValue += gap;
            offset += idades_.length;
            if (offset + groupSize > idades.length) {
                groupSize = idades.length - offset;
            }
        }

        for (int i = 0; i < array1.length; i++) {
            //String text = name + "(" + array1[i] + ")";
            String text = names_[i];
            //text = Utils.pad(text, 40);
            BasicDataRow row = new BasicDataRow();
            row.setInt("id", array1[i]);
            row.setString("nome", text);
            row.setInt("idade", idades[i]);
            row.setString("cidade", cidades[i]);
            table.addRecord(row, true);

            //table.addRecord(array1[i], String.valueOf(array1[i]));
            //table.addRecord(array1[i], "0");
        }
        table.flushDB();
        return table;
    }

    public void run(Operation op) throws Exception {

        Params.BLOCKS_LOADED = 0;
        Params.BLOCKS_SAVED = 0;

        TuplesPrinter printer = new TuplesPrinter();
        printer.execQueryAndPrint(op, -1);

        System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
        System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
    }

    public static void main(String[] args) {
        try {
            MainPerson m = new MainPerson();

            //Operation op = m.testUpdateQuery(990, "xxxxxxxxxx");
            //Operation op = m.testNestedLoopJoinQuery();
            //Operation op = m.testIndexedNestedLoopJoinQuery();
            //Operation op = m.testMergeJoinQuery1();
            //Operation op = m.testNestedLoopJoinQuery1();

            //Operation op = m.testNestedLoopJoinQueryWithFilter("t2.nome","Ana");
            //Operation op = m.testSimpleFilterQuery("nome","Ana", EQUAL);  
            //Operation op = m.testCompositeFilterQuery("cidade","Santa Maria", EQUAL, "idade",45, LOWER_THAN);  
            //Operation op = m.testSimpleQuery();
            //Operation op = m.testSortQuery(new String[]{"cidade","idade"});
            //Operation op = m.testDifferenceQuery("idade",50, 40);
            
            List<AggregationType> aggrOp = new ArrayList();
                aggrOp.add(new AggregationType("t1","idade", AggregationType.AVG ));
                aggrOp.add(new AggregationType("t1","idade", AggregationType.COUNT ));
                aggrOp.add(new AggregationType("t1","idade", AggregationType.MIN ));
                aggrOp.add(new AggregationType("t1","idade", AggregationType.MAX ));
            
            //Operation op = m.testGroupByQuery("t1.cidade", "t1.idade", Aggregation.MAX);
            Operation op = m.testGroupByQuery2("t1.cidade", aggrOp);
            //op = new ContentSort(op, "t1");
            //op = new PKFilter(op, "t1", GREATER_THAN, 20);
            TreePrinter printer = new TreePrinter();
            printer.printTree(op);

            //op = new ibd.query.unaryop.DuplicateRemoval(op, "t1", false);
            m.run(op);

        } catch (Exception ex) {
            Logger.getLogger(MainPerson.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
