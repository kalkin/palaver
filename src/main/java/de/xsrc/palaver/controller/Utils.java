package de.xsrc.palaver.controller;

import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;

public class Utils {
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
