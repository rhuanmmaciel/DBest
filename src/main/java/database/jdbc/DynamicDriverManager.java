package database.jdbc;

import enums.DatabaseType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class DynamicDriverManager {
    private static final String DRIVERS_FOLDER = "conf/jdbc/drivers";

    private static final Map<DatabaseType, Boolean> driverStatus = new HashMap<>();
    private static final Map<DatabaseType, DriverShim> registeredDrivers = new HashMap<>();
    private static final List<DriverDownloadListener> listeners = new CopyOnWriteArrayList<>();

    static {
        createDriversFolder();
        checkExistingDrivers();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownAllDrivers();
        }));
    }

    public interface DriverDownloadListener {
        void onDownloadStarted(DatabaseType databaseType);

        void onDownloadProgress(DatabaseType databaseType, int progress);

        void onDownloadCompleted(DatabaseType databaseType, boolean success);

        void onDriverLoaded(DatabaseType databaseType, boolean success);
    }

    public static void addListener(DriverDownloadListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(DriverDownloadListener listener) {
        listeners.remove(listener);
    }

    private static void notifyDownloadStarted(DatabaseType databaseType) {
        for (DriverDownloadListener listener : listeners) {
            try {
                listener.onDownloadStarted(databaseType);
            } catch (Exception e) {
            }
        }
    }

    private static void notifyDownloadProgress(DatabaseType databaseType, int progress) {
        for (DriverDownloadListener listener : listeners) {
            try {
                listener.onDownloadProgress(databaseType, progress);
            } catch (Exception e) {
            }
        }
    }

    private static void notifyDownloadCompleted(DatabaseType databaseType, boolean success) {
        for (DriverDownloadListener listener : listeners) {
            try {
                listener.onDownloadCompleted(databaseType, success);
            } catch (Exception e) {
            }
        }
    }

    private static void notifyDriverLoaded(DatabaseType databaseType, boolean success) {
        for (DriverDownloadListener listener : listeners) {
            try {
                listener.onDriverLoaded(databaseType, success);
            } catch (Exception e) {
            }
        }
    }

    private static void createDriversFolder() {
        File driversDir = new File(DRIVERS_FOLDER);
        if (!driversDir.exists()) {
            driversDir.mkdirs();
        }
    }

    private static void checkExistingDrivers() {
        File driversDir = new File(DRIVERS_FOLDER);

        if (!driversDir.exists()) {
            return;
        }

        for (DatabaseType dbType : DatabaseType.values()) {
            File driverFile = new File(driversDir, dbType.getJarFileName());

            if (driverFile.exists()) {
                boolean loaded = loadDriverFromFile(dbType, driverFile);
                driverStatus.put(dbType, loaded);
            } else {
                driverStatus.put(dbType, false);
            }
        }
    }

    public static boolean isDriverAvailable(DatabaseType databaseType) {
        return driverStatus.getOrDefault(databaseType, false);
    }

    public static boolean isDriverLoaded(DatabaseType databaseType) {
        return registeredDrivers.containsKey(databaseType);
    }

    public static CompletableFuture<Boolean> downloadAndLoadDriver(DatabaseType databaseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                notifyDownloadStarted(databaseType);

                File driverFile = downloadDriver(databaseType);

                if (driverFile == null) {
                    notifyDownloadCompleted(databaseType, false);
                    return false;
                }

                notifyDownloadCompleted(databaseType, true);

                boolean loaded = loadDriverFromFile(databaseType, driverFile);
                driverStatus.put(databaseType, loaded);

                notifyDriverLoaded(databaseType, loaded);
                return loaded;

            } catch (Exception e) {
                e.printStackTrace();
                notifyDownloadCompleted(databaseType, false);
                notifyDriverLoaded(databaseType, false);

                return false;
            }
        });
    }

    private static File downloadDriver(DatabaseType databaseType) {
        try {
            String downloadUrl = databaseType.getDownloadUrl();
            String fileName = databaseType.getJarFileName();
            File outputFile = new File(DRIVERS_FOLDER, fileName);

            @SuppressWarnings("deprecation")
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + responseCode + " for URL: " + downloadUrl);
            }

            long contentLength = connection.getContentLengthLong();

            try (InputStream inputStream = connection.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[8192];
                long totalBytesRead = 0;
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    if (contentLength > 0) {
                        int progress = (int) ((totalBytesRead * 100) / contentLength);
                        notifyDownloadProgress(databaseType, progress);
                    }
                }
            }

            return outputFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean loadDriverFromFile(DatabaseType databaseType, File driverFile) {
        try {
            if (registeredDrivers.containsKey(databaseType)) {
                return true;
            }

            URL[] driverUrls = { driverFile.toURI().toURL() };
            URLClassLoader specificDriverClassLoader = new URLClassLoader(
                    driverUrls,
                    ClassLoader.getSystemClassLoader());

            Class<?> driverClass = specificDriverClassLoader.loadClass(databaseType.getDriverClassName());
            Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();

            DriverShim driverShim = new DriverShim(driver, specificDriverClassLoader);
            DriverManager.registerDriver(driverShim);

            registeredDrivers.put(databaseType, driverShim);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class DriverShim implements Driver {
        private final Driver driver;
        private final URLClassLoader classLoader;

        public DriverShim(Driver driver, URLClassLoader classLoader) {
            this.driver = driver;
            this.classLoader = classLoader;
        }

        @Override
        public java.sql.Connection connect(String url, Properties info) throws java.sql.SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws java.sql.SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public java.sql.DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws java.sql.SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }

        // Package-private method to clean up resources when the driver is being
        // unloaded
        void cleanup() {
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException e) {
                    // Ignore cleanup errors
                }
            }
        }
    }

    public static DatabaseType[] getAllDatabaseTypes() {
        return DatabaseType.values();
    }

    public static List<DatabaseType> getAvailableDatabaseTypes() {
        List<DatabaseType> available = new ArrayList<>();

        for (DatabaseType dbType : DatabaseType.values()) {
            if (isDriverAvailable(dbType)) {
                available.add(dbType);
            }
        }

        return available;
    }

    public static boolean unloadDriver(DatabaseType databaseType) {
        DriverShim driverShim = registeredDrivers.get(databaseType);
        if (driverShim != null) {
            try {
                DriverManager.deregisterDriver(driverShim);
                driverShim.cleanup();
                registeredDrivers.remove(databaseType);
                driverStatus.put(databaseType, false);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static void shutdownAllDrivers() {
        for (Map.Entry<DatabaseType, DriverShim> entry : new HashMap<>(registeredDrivers).entrySet()) {
            unloadDriver(entry.getKey());
        }
    }

    /**
     * Get information about currently loaded drivers
     */
    public static Set<DatabaseType> getLoadedDrivers() {
        return new HashSet<>(registeredDrivers.keySet());
    }
}
