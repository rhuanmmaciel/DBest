package enums;

public enum FileType {

	CSV("csv", ".csv"),
	EXCEL("excel", ".xlsx"),
	SQL("sql", ".sql"),
	FYI("fyi", ".dat"),
	HEADER("head", ".head");

	FileType(String id, String extension){

		this.ID = id;
		this.EXTENSION = extension;

	}

	public final String EXTENSION;
	public final String ID;

}
