package enums;

public enum FileType {

    CSV    ("csv", ".csv"),
    EXCEL  ("excel", ".xlsx"),
    SQL    ("sql", ".sql"),
    FYI    ("fyi", ".dat"),
    HEADER ("head", ".head");

    public final String ID;

    public final String EXTENSION;

    FileType(String id, String extension) {
        this.ID = id;
        this.EXTENSION = extension;
    }
}
