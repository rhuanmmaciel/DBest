package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static List<String> getDatFilesNames() {

		List<String> tableFileNames = new ArrayList<>();
		File directory = new File(".");
		File[] filesList = directory.listFiles();

		for (File file : filesList) {

			if (file.isFile() && file.getName().endsWith(".dat"))
				tableFileNames.add(file.getName().substring(0, file.getName().length() - 4));

		}

		return tableFileNames;

	}

	public static void importDatFiles(String path) {
		
		try {
		
			Path sourceFile = Path.of(path);
			Path destinationDirectory = Path.of("");
			Files.move(sourceFile, destinationDirectory.resolve(sourceFile.getFileName()),
					StandardCopyOption.REPLACE_EXISTING);
	
		} catch (Exception e) {
			
			System.out.println("Ocorreu um erro ao mover o arquivo: " + e.getMessage());
		
		}
		
	}

}
