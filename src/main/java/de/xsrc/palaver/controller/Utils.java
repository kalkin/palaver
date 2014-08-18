package de.xsrc.palaver.controller;

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

	//private static final CrudService<EntityWithId.T>, CrudService.T> storage;
	
	private static Storage<EntityWithId<String>, String> storage;

	public static synchronized Storage getStorage(Class clazz){
		if (Utils.storage == null){
			Utils.storage = new Storage(clazz);
		}
		return Utils.storage;
	}

	public static Stage getDialog(Flow f) throws FlowException {
		ResourceBundle b = ResourceBundle
				.getBundle("de.xsrc.palaver.i18n.Palaver_en");
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
