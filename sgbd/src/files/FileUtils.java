package files;

import controller.ConstantController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {

	private static final File temp = new File("temp");

	private static void createTempIfNotExists(){

		if(Stream.of(ConstantController.ROOT_DIRECTORY.toFile().listFiles()).noneMatch(f -> f.isDirectory() && f.getName().equals("temp")))
			temp.mkdir();

	}

	public static Optional<File> getFileFromTempDirectory(String fileName){

		createTempIfNotExists();

		Optional<File> file = Optional.empty();

		for(File f : temp.listFiles())
			if(Objects.equals(fileName, f.getName())) {
				file = Optional.of(new File(f.getAbsolutePath()));
				break;
			}

		return file;

	}

	public static Path getTempDirectory(){

		createTempIfNotExists();

		return temp.toPath();

	}

	public static boolean copyToTempDirectory(File file){

		createTempIfNotExists();

		try {

			Files.copy(file.toPath(), new File(temp.getAbsolutePath(), file.getName()).toPath());
			return true;

		}catch (IOException ignored){

			return false;

		}

	}

	public static void moveToTempDirectory(File file){

		createTempIfNotExists();

		try {

			Files.move(file.toPath(), new File(temp.getAbsolutePath(), file.getName()).toPath());

		}catch (IOException ignored){

		}

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

}
