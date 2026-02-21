package database.jdbc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import engine.exceptions.DataBaseException;
import enums.DatabaseType;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UniversalConnectionConfig {
    public static final String CONFIG_FILES_FOLDER = "conf/jdbc/connections/";

    @SerializedName("id")
    private final String id;

    @SerializedName("name")
    private String name;

    @SerializedName("databaseType")
    private DatabaseType databaseType;

    @SerializedName("host")
    private String host;

    @SerializedName("port")
    private Integer port;

    @SerializedName("database")
    private String database;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("filePath")
    private String filePath;

    public UniversalConnectionConfig(String name, DatabaseType databaseType, String filePath) {
        this.id = String.valueOf(new Date().getTime());
        this.name = name;
        this.databaseType = databaseType;
        this.filePath = filePath;
    }

    public UniversalConnectionConfig(String name, DatabaseType databaseType, String host,
            Integer port, String database, String username, String password) {
        this.id = String.valueOf(new Date().getTime());
        this.name = name;
        this.databaseType = databaseType;
        this.host = host;
        this.port = port != null ? port : databaseType.getDefaultPort();
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String constructConnectionURL() {
        if (databaseType == null)
            return null;

        String url = databaseType.getUrlTemplate();

        if (databaseType.isFileBased()) {
            return url.replace("{file}", filePath != null ? filePath : "");
        } else {
            url = url.replace("{host}", host != null ? host : "localhost");
            url = url.replace("{port}", String.valueOf(port != null ? port : databaseType.getDefaultPort()));
            url = url.replace("{database}", database != null ? database : "");
            return url;
        }
    }

    public boolean test() {
        if (!DynamicDriverManager.isDriverAvailable(databaseType)) {
            return false;
        }

        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (!DynamicDriverManager.isDriverAvailable(databaseType)) {
            throw new SQLException("Driver not available for " + databaseType.getDisplayName());
        }

        if (databaseType.isFileBased() || username == null || password == null) {
            return DriverManager.getConnection(constructConnectionURL());
        } else {
            return DriverManager.getConnection(constructConnectionURL(), username, password);
        }
    }

    public List<String> getTableNames() {
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            List<String> tableNames = new ArrayList<>();

            String catalog = connection.getCatalog();
            String schema = connection.getSchema();

            switch (databaseType) {
                case MYSQL:
                case MARIADB:
                    try (ResultSet tables = metaData.getTables(catalog, null, "%", new String[] { "TABLE" })) {
                        while (tables.next()) {
                            tableNames.add(tables.getString("TABLE_NAME"));
                        }
                    }
                    break;

                case POSTGRESQL:
                    try (ResultSet tables = metaData.getTables(null, schema, "%", new String[] { "TABLE" })) {
                        while (tables.next()) {
                            String tableName = tables.getString("TABLE_NAME");
                            String tableSchema = tables.getString("TABLE_SCHEM");

                            if (tableSchema != null && !tableSchema.startsWith("information_schema") &&
                                    !tableSchema.startsWith("pg_")) {
                                tableNames.add(tableName);
                            }
                        }
                    }
                    break;

                case ORACLE:
                    String currentUser = connection.getMetaData().getUserName();
                    try (ResultSet tables = metaData.getTables(null, currentUser, "%", new String[] { "TABLE" })) {
                        while (tables.next()) {
                            tableNames.add(tables.getString("TABLE_NAME"));
                        }
                    }
                    break;

                case SQLITE:
                    try (ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" })) {
                        while (tables.next()) {
                            String tableName = tables.getString("TABLE_NAME");
                            String tableSchema = tables.getString("TABLE_SCHEM");
                            if (tableSchema == null || (!tableSchema.startsWith("INFORMATION_SCHEMA") &&
                                    !tableSchema.startsWith("SYS"))) {
                                tableNames.add(tableName);
                            }
                        }
                    }
                    break;

                default:
                    try (ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" })) {
                        while (tables.next()) {
                            tableNames.add(tables.getString("TABLE_NAME"));
                        }
                    }
                    break;
            }

            return tableNames;
        } catch (SQLException e) {
            throw new DataBaseException("UniversalConnectionConfig", e.getMessage());
        }
    }

    public List<String> getDatabaseNames() {
        if (!databaseType.supportsMultipleDatabases()) {
            return new ArrayList<>();
        }

        try {
            String originalDatabase = this.database;
            String defaultDb = getDefaultDatabaseForType(databaseType);
            this.database = defaultDb;

            String tempUrl = constructConnectionURL();

            Connection connection;
            if (username == null || password == null) {
                connection = DriverManager.getConnection(tempUrl);
            } else {
                connection = DriverManager.getConnection(tempUrl, username, password);
            }

            List<String> databaseNames = new ArrayList<>();

            try {
                switch (databaseType) {
                    case MYSQL:
                    case MARIADB:
                        try (Statement stmt = connection.createStatement();
                                ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
                            while (rs.next()) {
                                String dbName = rs.getString(1);
                                if (!"information_schema".equals(dbName) &&
                                        !"performance_schema".equals(dbName) &&
                                        !"mysql".equals(dbName) &&
                                        !"sys".equals(dbName)) {
                                    databaseNames.add(dbName);
                                }
                            }
                        }
                        break;

                    case POSTGRESQL:
                        try (Statement stmt = connection.createStatement();
                                ResultSet rs = stmt.executeQuery(
                                        "SELECT datname FROM pg_database WHERE datistemplate = false AND datname != 'postgres'")) {
                            while (rs.next()) {
                                databaseNames.add(rs.getString(1));
                            }
                        }
                        break;

                    case ORACLE:
                        try (Statement stmt = connection.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT name FROM v$database")) {
                            while (rs.next()) {
                                databaseNames.add(rs.getString(1));
                            }
                        }
                        break;

                    case SQLITE:
                        break;
                }
            } finally {
                connection.close();
                this.database = originalDatabase;
            }

            return databaseNames;
        } catch (SQLException e) {
            throw new DataBaseException("UniversalConnectionConfig", e.getMessage());
        }
    }

    private String getDefaultDatabaseForType(DatabaseType databaseType) {
        switch (databaseType) {
            case POSTGRESQL:
                return "postgres";
            case MYSQL:
            case MARIADB:
                return "mysql";
            case ORACLE:
                return "XE";
            default:
                return "";
        }
    }

    private void createConfigFolder() {
        File folder = new File(CONFIG_FILES_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void save() {
        createConfigFolder();

        String filePath = CONFIG_FILES_FOLDER + id + ".json";

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        String json = gson.toJson(this);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String filePath = CONFIG_FILES_FOLDER + id + ".json";
        File configFile = new File(filePath);

        if (configFile.exists() && configFile.isFile()) {
            configFile.delete();
        }
    }

    public static UniversalConnectionConfig load(String id) {
        String filePath = CONFIG_FILES_FOLDER + id + ".json";

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, UniversalConnectionConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<UniversalConnectionConfig> getAllConfiguredConnections() {
        List<UniversalConnectionConfig> configurations = new ArrayList<>();

        for (String configFile : getAllConfigurationFiles()) {
            UniversalConnectionConfig config = load(configFile.replaceAll("\\.json$", ""));

            if (config != null) {
                configurations.add(config);
            }
        }

        return configurations;
    }

    public static List<String> getAllConfigurationFiles() {
        List<String> configFiles = new ArrayList<>();

        File directory = new File(CONFIG_FILES_FOLDER);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        configFiles.add(file.getName());
                    }
                }
            }
        }

        return configFiles;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return name + " (" + databaseType.getDisplayName() + ")";
    }
}
