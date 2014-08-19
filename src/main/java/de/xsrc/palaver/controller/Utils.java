package de.xsrc.palaver.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.MapChangeListener.Change;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.datafx.controller.flow.context.ViewFlowContext;
import org.datafx.util.EntityWithId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.xsrc.palaver.Storage;
import de.xsrc.palaver.utils.AppDataSource;

public class Utils {

	// private static final CrudService<EntityWithId.T>, CrudService.T> storage;

	private static HashMap<Class, Storage> storage;

	public static synchronized Storage getStorage(Class clazz) {
		if (Utils.storage == null) {
			Utils.storage = new HashMap<Class, Storage>();
		}

		Storage result = storage.get(clazz);
		if (result == null) {
			result = new Storage<EntityWithId<String>, String>(clazz);
			storage.put(clazz, result);
		}
		return result;

	}
	public static Stage getDialog(Flow f) throws FlowException{
		return getDialog(f, null);
	}

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
	
	

}
