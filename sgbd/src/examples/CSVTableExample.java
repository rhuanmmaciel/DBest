package examples;

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

import java.util.List;

public class CSVTableExample {

    public static void main(String[] args) {
        List<Column> columns = List.of(
            new Column("Name", "biostats", ColumnDataType.STRING),
            new Column("Sex", "biostats", ColumnDataType.CHARACTER),
            new Column("Weightlbs", "biostats", ColumnDataType.INTEGER),
            new Column("Id", "biostats", ColumnDataType.INTEGER),
            new Column("Heightin", "biostats", ColumnDataType.INTEGER),
            new Column("Age", "biostats", ColumnDataType.INTEGER)
        );

        Prototype prototype = new TableCreator(false).createPrototype(columns);

        Header header = new Header(prototype, "biostats");
        header.set(Header.FILE_PATH, "/home/rhuan/Documents/biostats.csv");

        CSVTable table = new CSVTable(header, ',', '"', 1);
        table.open();
        table.saveHeader("/home/rhuan/Documents/biostats.head");

        Operator operator = new FilterColumnsOperator(new TableScan(table), List.of("biostats.__IDX__"));

        TestOperators.testOperator(operator);

        table.close();
    }
}
