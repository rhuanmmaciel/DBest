package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static List<String> getDatFileNames() {

		List<String> tableFileNames = new ArrayList<>();
		File directory = new File(".");
		File[] filesList = directory.listFiles();

		for (File file : filesList) {

			if (file.isFile() && file.getName().endsWith(".dat"))
				tableFileNames.add(file.getName().substring(0, file.getName().length() - 4));

		}

		return tableFileNames;

	}
	
}
