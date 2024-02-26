package examples;

import java.util.Map;

import sgbd.prototype.Prototype;
import sgbd.prototype.metadata.Metadata;
import sgbd.source.components.Header;
import sgbd.source.table.Table;

public class FYITableExample {

    public static void main(String[] args) {
        Map<String, String> table = Map.of(
            "id", "int",
            "nome", "string",
            "idade", "int",
            "salario", "float"
        );

        Prototype prototype = new Prototype();
        prototype.addColumn("id", 4, Metadata.PRIMARY_KEY);
        prototype.addColumn("nome", 255, Metadata.DINAMIC_COLUMN_SIZE);
        prototype.addColumn("idade", 4, Metadata.CAN_NULL_COLUMN);
        prototype.addColumn("salario", 4, Metadata.NONE);

        Table users = Table.openTable(new Header(prototype, "users"));
    }
}
