package enums;

public enum FileType {

    CSV    ("csv", ".csv"),
    EXCEL  ("excel", ".xlsx"),
    SQL    ("sql", ".sql"),
    FYI    ("fyi", ".dat"),
    HEADER ("head", ".head"),
    TXT("txt", ".txt");

    public final String id;

    public final String extension;

    FileType(String id, String extension) {
        this.id = id;
        this.extension = extension;
    }
}
