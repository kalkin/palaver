package de.xsrc.palaver.controller;

import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.datafx.util.EntityWithId;

import de.xsrc.palaver.Storage;

public class Utils {

	// private static final CrudService<EntityWithId.T>, CrudService.T> storage;

	private static HashMap<Class, Storage> storage;

	public static synchronized Storage getStorage(Class clazz) {
		if (Utils.storage == null) {
			Utils.storage = new HashMap<Class, Storage>();
		}
		
		Storage result = storage.get(clazz);
		if (result == null){
			result = new Storage<EntityWithId<String>, String>(clazz);
			storage.put(clazz, result);
		}
		return result;

	}

	public static Stage getDialog(Flow f) throws FlowException {
		ResourceBundle b = ResourceBundle
				.getBundle("i18n.Palaver_en");
		DefaultFlowContainer container = new DefaultFlowContainer();
		FlowHandler flowHandler = f.createHandler();
		flowHandler.getViewConfiguration().setResources(b);
		flowHandler.start(container);
		Scene scene = new Scene(container.getView());
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.WINDOW_MODAL);
		return stage;
	}
}
