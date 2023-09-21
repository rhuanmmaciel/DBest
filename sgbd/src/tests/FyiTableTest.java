package tests;

import sgbd.prototype.Prototype;
import sgbd.prototype.column.Column;
import sgbd.source.components.Header;
import sgbd.source.table.Table;

import java.util.HashMap;

public class FyiTableTest {

    public static void main(String[] args){

        HashMap<String,String> mapa = new HashMap<>();
        Prototype p1 = new Prototype();
        p1.addColumn("id",4, Column.PRIMARY_KEY);
        mapa.put("id","int");
        p1.addColumn("nome",255,Column.DINAMIC_COLUMN_SIZE);
        mapa.put("nome","string");
        p1.addColumn("idade",4,Column.CAN_NULL_COLUMN);
        mapa.put("idade","int");
        p1.addColumn("salario",4,Column.NONE);
        mapa.put("salario","float");

        Table users = Table.openTable(new Header(p1,"users"));

    }

}
