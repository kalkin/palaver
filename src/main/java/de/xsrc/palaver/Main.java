package de.xsrc.palaver;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;

import de.xsrc.palaver.controller.MainController;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws FlowException {
		Flow flow = new Flow(MainController.class);
		Scene scene = handleI18n(flow);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Scene handleI18n(Flow f) throws FlowException {
		ResourceBundle b = ResourceBundle.getBundle("i18n.Palaver_en");
		DefaultFlowContainer container = new DefaultFlowContainer();
		FlowHandler flowHandler = f.createHandler();
		flowHandler.getViewConfiguration().setResources(b);
		flowHandler.start(container);
		return new Scene(container.getView());
	}

	public static void main(String[] args) {

		launch(args);
	}
}
