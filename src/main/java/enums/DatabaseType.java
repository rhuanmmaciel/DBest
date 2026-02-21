package enums;

public enum DatabaseType {
    MYSQL(
            "🐬",
            "MySQL",
            "com.mysql.cj.jdbc.Driver",
            "jdbc:mysql://{host}:{port}/{database}",
            "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar",
            3306,
            false,
            true),
    POSTGRESQL(
            "🐬",
            "PostgreSQL",
            "org.postgresql.Driver",
            "jdbc:postgresql://{host}:{port}/{database}",
            "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar",
            5432,
            false,
            true),
    ORACLE(
            "🔶",
            "Oracle",
            "oracle.jdbc.driver.OracleDriver",
            "jdbc:oracle:thin:@{host}:{port}/{database}",
            "https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc11/23.3.0.23.09/ojdbc11-23.3.0.23.09.jar",
            1521,
            false,
            true),
    SQLITE(
            "💾",
            "SQLite",
            "org.sqlite.JDBC",
            "jdbc:sqlite:{file}",
            "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.34.0/sqlite-jdbc-3.34.0.jar",
            0,
            true,
            false),
    MARIADB(
            "🌊",
            "MariaDB",
            "org.mariadb.jdbc.Driver",
            "jdbc:mariadb://{host}:{port}/{database}",
            "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.3.2/mariadb-java-client-3.3.2.jar",
            3306,
            false,
            true);

    private final String displayIcon;
    private final String displayName;
    private final String driverClassName;
    private final String urlTemplate;
    private final String downloadUrl;
    private final int defaultPort;
    private final boolean isFileBased;
    private final boolean supportsMultipleDatabases;

    DatabaseType(
        String displayIcon,
        String displayName,
        String driverClassName,
        String urlTemplate,
        String downloadUrl,
        int defaultPort,
        boolean isFileBased,
        boolean supportsMultipleDatabases) {
        this.displayIcon = displayIcon;
        this.displayName = displayName;
        this.driverClassName = driverClassName;
        this.urlTemplate = urlTemplate;
        this.downloadUrl = downloadUrl;
        this.defaultPort = defaultPort;
        this.isFileBased = isFileBased;
        this.supportsMultipleDatabases = supportsMultipleDatabases;
    }

    public String getDisplayIcon() {
        return displayIcon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public boolean isFileBased() {
        return isFileBased;
    }

    public boolean supportsMultipleDatabases() {
        return supportsMultipleDatabases;
    }

    public String getJarFileName() {
        String url = getDownloadUrl();
        return url.substring(url.lastIndexOf('/') + 1);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
