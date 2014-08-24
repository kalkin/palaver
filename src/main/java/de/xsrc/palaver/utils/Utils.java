package de.xsrc.palaver.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.datafx.controller.flow.context.ViewFlowContext;

public class Utils {

	// private static final CrudService<EntityWithId.T>, CrudService.T> storage;

	private static HashMap<Class, Storage> storage;
	private static final Logger logger = Logger.getLogger(Storage.class
			.getName());

	public static Stage getDialog(Flow f, ViewFlowContext flowContext)
			throws FlowException {
		ResourceBundle b = ResourceBundle.getBundle("i18n.Palaver_en");
		DefaultFlowContainer container = new DefaultFlowContainer();
		FlowHandler flowHandler;
		if (flowContext != null) {
			flowHandler = f.createHandler(flowContext);
		} else {
			flowHandler = f.createHandler();
		}
		flowHandler.getViewConfiguration().setResources(b);
		flowHandler.start(container);
		Scene scene = new Scene(container.getView());
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.WINDOW_MODAL);
		return stage;
	}

	/**
	 * Loads the file containing models of the class c
	 * 
	 * @param c
	 *            - Class name
	 * @return
	 * @throws IOException
	 */
	public static File getFile( Class<?> c) throws IOException {
		return getFile(workingDirectory(), c);
	}
	
	public static File getFile(String workingDir, Class<?> c) throws IOException{
		File file = new File(workingDir + "/" + c.getSimpleName() + "s"); // Make

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			System.out.println(file.getPath());
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
