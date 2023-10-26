package files;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import controllers.MainController;
import database.TableCreator;
import entities.cells.TableCell;
import entities.utils.cells.CellUtils;
import enums.FileType;
import gui.frames.main.MainFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class FileUtils {

    private static final File TEMP = new File("temp");

    private FileUtils() {

    }

    public static void verifyExistingFilesToInitialize(){

        try
        {
            File initDirectory = new File("init");

            for (File file : Objects.requireNonNull(initDirectory.listFiles())) {

                if (!file.isFile() || !file.getName().endsWith(FileType.HEADER.extension)) continue;

                TableCell tableCell = TableCreator.createTable(file);

                assert tableCell != null;
                mxCell tableJCell = (mxCell) MainFrame
                        .getTablesGraph()
                        .insertVertex(
                                MainFrame.getTablesGraph().getDefaultParent(), null, tableCell.getName(),
                                0, MainController.getCurrentTableYPosition(), tableCell.getWidth(),
                                tableCell.getHeight(), tableCell.getStyle()
                        );

                CellUtils.addCell(tableJCell, tableCell);

                MainController.getTables().put(tableCell.getName(), tableCell);

                MainFrame.getTablesPanel().revalidate();

                MainController.incrementCurrentTableYPosition(40);
            }

        }
        catch (Exception ignored){

        }

    }

    private static void createTempIfNotExists() {
        if (Stream.of(Objects.requireNonNull(ConstantController.ROOT_DIRECTORY.toFile().listFiles())).noneMatch(file -> file.isDirectory() && file.getName().equals("temp"))) {
            TEMP.mkdir();
        }
    }

    public static Optional<File> getFileFromTempDirectory(String fileName) {
        createTempIfNotExists();

        File file = null;

        for (File tempDirectoryFile : Objects.requireNonNull(TEMP.listFiles())) {
            if (Objects.equals(fileName, tempDirectoryFile.getName())) {
                file = new File(tempDirectoryFile.getAbsolutePath());
                break;
            }
        }

        return file == null ? Optional.empty() : Optional.of(file);
    }

    public static Path getTempDirectory() {
        createTempIfNotExists();

        return TEMP.toPath();
    }

    public static void moveToTempDirectory(File file) {
        createTempIfNotExists();

        try {
            Files.move(file.toPath(), new File(TEMP.getAbsolutePath(), file.getName()).toPath());
        } catch (IOException ignored) {

        }
    }

    public static File getFile(String fileName) {
        for (File file : Objects.requireNonNull(ConstantController.ROOT_DIRECTORY.toFile().listFiles())) {
            if (fileName.equals(file.getName())) {
                return file;
            }
        }

        throw new NoSuchElementException("The file doesn't exist");
    }

    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException ignored) {

        }
    }

    public static void clearMemory() {
        File directory = ConstantController.ROOT_DIRECTORY.toFile();
        File[] files = directory.listFiles();

        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory() && file.getName().equals("temp")) {
                for (File insideFiles : Objects.requireNonNull(file.listFiles())) {
                    deleteFile(insideFiles.toPath());
                }

                deleteFile(file.toPath());
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

            if (file.isFile() && fileName.endsWith(FileType.FYI.extension)) {
                fileNames.add(fileName.substring(0, fileName.length() - 4));
            }
        }

        return fileNames;
    }

    public static boolean copyDatFilesWithHead(String path, String tableName, Path destinationDirectory) {
        try {
            boolean shouldReplaceFileName = !path.endsWith(String.format("%s%s", tableName, FileType.HEADER.extension));

            Path headFilePath = Path.of(path);

            if (shouldReplaceFileName) {
                String newHeadFileName = String.format("%s%s", tableName, FileType.HEADER.extension);
                Path newHeadFilePath = destinationDirectory.resolve(newHeadFileName);
                Files.copy(headFilePath, newHeadFilePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path destinationHeadFilePath = destinationDirectory.resolve(headFilePath.getFileName());
                Files.copy(headFilePath, destinationHeadFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String newDatFileName = String.format("%s%s", tableName, FileType.FYI.extension);
            Path datFilePath = Path.of(path.replace(FileType.HEADER.extension, FileType.FYI.extension));

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
