package tests;

import database.TableCreator;
import entities.Column;
import enums.ColumnDataType;
import sgbd.prototype.Prototype;
import sgbd.query.Operator;
import sgbd.query.TestOperators;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.source.components.Header;
import sgbd.source.table.CSVTable;

import java.util.*;

public class CsvTableTest {

    public static void main(String[] args){

        List<Column> columns = new ArrayList<>(List.of(
                new Column("Name", "biostats", ColumnDataType.STRING),
                new Column("Sex", "biostats", ColumnDataType.CHARACTER),
                new Column("Weightlbs", "biostats", ColumnDataType.INTEGER),
                new Column("Id", "biostats", ColumnDataType.INTEGER),
                new Column("Heightin", "biostats", ColumnDataType.INTEGER),
                new Column("Age", "biostats", ColumnDataType.INTEGER)
                ));

        Prototype prototype = TableCreator.createPrototype(columns);

        Header header = new Header(prototype, "biostats");
        header.set(Header.FILE_PATH, "/home/rhuan/Documents/biostats.csv");
        CSVTable table = new CSVTable(header, ',', '"', 1);
        table.open();
        table.saveHeader("/home/rhuan/Documents/biostats.head");

        Operator op = new FilterColumnsOperator(new TableScan(table), List.of("biostats.__IDX__"));

        TestOperators.testOperator(op);

        table.close();

    }


}
