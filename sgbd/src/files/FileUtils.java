package files;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static void deleteFile(File file) {
        if (file != null) {
            file.delete();
        }
    }

    public static void clearMemory() {
        File directory = new File(".");
        File[] files = directory.listFiles();

        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();

            if (file.isFile() && (fileName.endsWith(".dat") || fileName.endsWith(".head"))) {
                deleteFile(file);
            }
        }
    }

    public static List<String> getDatFileNames() {
        File directory = new File(".");
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (files == null) return fileNames;

        for (File file : files) {
            String fileName = file.getName();

            if (file.isFile() && fileName.endsWith(".dat")) {
                fileNames.add(fileName.substring(0, fileName.length() - 4));
            }
        }

        return fileNames;
    }

    public static boolean copyDatFilesWithHead(String path, String tableName, Path destinationDirectory) {
        try {
            boolean shouldReplaceFileName = !path.endsWith(String.format("%s.head", tableName));

            Path headFilePath = Path.of(path);

            if (shouldReplaceFileName) {
                String newHeadFileName = String.format("%s.head", tableName);
                Path newHeadFilePath = destinationDirectory.resolve(newHeadFileName);
                Files.copy(headFilePath, newHeadFilePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path destinationHeadFilePath = destinationDirectory.resolve(headFilePath.getFileName());
                Files.copy(headFilePath, destinationHeadFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String newDatFileName = String.format("%s.dat", tableName);
            Path datFilePath = Path.of(path.replace(".head", ".dat"));

            if (shouldReplaceFileName) {
                Path newDatFilePath = destinationDirectory.resolve(newDatFileName);
                Files.copy(datFilePath, newDatFilePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path destinationDatFilePath = destinationDirectory.resolve(datFilePath.getFileName());
                Files.copy(datFilePath, destinationDatFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
