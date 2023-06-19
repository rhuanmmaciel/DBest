package files;

import entities.cells.Cell;
import entities.cells.TableCell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FileUtils {

	public static List<String> getDatFilesNames() {

		List<String> tableFileNames = new ArrayList<>();
		File directory = new File(".");
		File[] filesList = directory.listFiles();

		assert filesList != null;
		for (File file : filesList) {

			if (file.isFile() && file.getName().endsWith(".dat"))
				tableFileNames.add(file.getName().substring(0, file.getName().length() - 4));

		}

		return tableFileNames;

	}

	public static boolean copyDatFilesWithHead(String path, String tableName, Path destinationDirectory) {
		
		boolean replaceFileName = !path.endsWith(tableName + ".head");

		try {

			Path headFile = Path.of(path);

			if (replaceFileName) {
				
				String newHeadFileName = tableName + ".head";
				Path newHeadFile = destinationDirectory.resolve(newHeadFileName);
				Files.copy(headFile, newHeadFile, StandardCopyOption.REPLACE_EXISTING);
				
			} else {
				
				Path destinationHeadFile = destinationDirectory.resolve(headFile.getFileName());
				Files.copy(headFile, destinationHeadFile, StandardCopyOption.REPLACE_EXISTING);
				
			}

			String newDatFileName = tableName + ".dat";
			Path datFile = Path.of(path.replace(".head", ".dat"));

			if (replaceFileName) {
				
				Path newDatFile = destinationDirectory.resolve(newDatFileName);
				Files.copy(datFile, newDatFile, StandardCopyOption.REPLACE_EXISTING);
				
			} else {
				
				Path destinationDatFile = destinationDirectory.resolve(datFile.getFileName());
				Files.copy(datFile, destinationDatFile, StandardCopyOption.REPLACE_EXISTING);
				
			}

			return true;

		} catch (Exception e) {

			return false;

		}

	}

}
