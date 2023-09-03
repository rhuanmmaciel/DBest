package files;

import com.google.gson.internal.Streams;
import controller.ConstantController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class FileUtils {

	private static final File temp = new File("temp");

	private static void createTempIfNotExists(){

		if(Stream.of(ConstantController.ROOT_DIRECTORY.toFile().listFiles()).noneMatch(f -> f.isDirectory() && f.getName().equals("temp")))
			temp.mkdir();

	}

	public static void useTempDirectory(File file){

		createTempIfNotExists();

		try {

			Files.move(file.toPath(), new File(temp.getAbsolutePath(), file.getName()).toPath());

		}catch (IOException ignored){}

	}

	public static File getFile(String fileName){

		for(File f : ConstantController.ROOT_DIRECTORY.toFile().listFiles())
			if(fileName.equals(f.getName()))
				return f;

		throw new NoSuchElementException("The file doesn't exist");

	}

	public static void deleteFile(Path path){

		path.toFile().delete();

	}

	public static void clearMemory(){

		File directory = ConstantController.ROOT_DIRECTORY.toFile();
		File[] filesList = directory.listFiles();
		assert filesList != null;
		for (File file : filesList)
			if (file.isDirectory() && file.getName().equals("temp")) {

				for(File insideFiles : file.listFiles())
					deleteFile(insideFiles.toPath());

				deleteFile(file.toPath());

			}
	}

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
