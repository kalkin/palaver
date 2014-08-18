package de.xsrc.palaver;

import java.io.File;
import java.io.IOException;

import org.datafx.reader.FileSource;
import org.datafx.util.AppDataConverter;

public class AppDataSource<T> extends FileSource<T> {

	public AppDataSource(Class<T> c) throws IOException {
		super(getFile(c), new AppDataConverter<T>(c));
	}

	/**
	 * Loads the file containing models of the class c
	 * 
	 * @param c
	 *            - Class name
	 * @return
	 * @throws IOException
	 */
	private static File getFile(@SuppressWarnings("rawtypes") Class c) throws IOException {

		File file = new File(workingDirectory() + "/" + c.getSimpleName() + "s"); // Make

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	/**
	 * Tries to find User Home and create the config dir
	 * 
	 * @return
	 */
	private static String workingDirectory() {
		String os = System.getProperty("os.name").toUpperCase();
		String workingDirectory;
		if (os.contains("WIN")) {
			// it is simply the location of the "AppData" folder =>
			// StackOverflow
			workingDirectory = System.getenv("AppData");
		} else if (os.contains("DARWIN") || os.contains("MAC")) {
			workingDirectory = System.getProperty("user.home")
					+ "/Library/Application Support/Palaver";
		} else {
			// It's a UNIX system! I know this!
			workingDirectory = System.getProperty("user.home") + "/.palaver";
		}
		return workingDirectory;
	}
}
