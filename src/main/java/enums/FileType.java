package enums;

public enum FileType {

    CSV    ("csv", ".csv"),
    EXCEL  ("excel", ".xlsx"),
    SQL    ("sql", ".sql"),
    DAT    ("dat", ".dat"),
    HEADER ("head", ".head"),
    TXT    ("txt", ".txt"),
    XML    ("xml", ".xml");

    public final String id;

    public final String extension;

    FileType(String id, String extension) {
        this.id = id;
        this.extension = extension;
    }
}
