package enums;

public enum DatabaseConnectionType {

    MYSQL ("mysql");

    private final String name;

    DatabaseConnectionType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
