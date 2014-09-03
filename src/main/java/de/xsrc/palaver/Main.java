package de.xsrc.palaver;


import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.controller.ContactController;
import de.xsrc.palaver.controller.MainController;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Notifications;
import de.xsrc.palaver.utils.UiUtils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.control.cell.ServiceListCellFactory;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.FlowActionChain;
import org.datafx.controller.flow.action.FlowLink;
import org.datafx.controller.flow.action.FlowMethodAction;

import java.util.logging.Logger;

public class Main extends Application {

	private static final Logger logger = Logger.getLogger(Main.class
					.getName());
	private ApplicationContext applicationContext = ApplicationContext.getInstance();

	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws FlowException {
		AccountProvider accounts = new AccountProvider();
		PalaverProvider palavers = new PalaverProvider();

		applicationContext.register(accounts);
		applicationContext.register(new ObservableExecutor());
		applicationContext.register(palavers);

		Flow flow = new Flow(MainController.class);
		flow.withAction(ContactController.class, "startPalaverButton", new FlowActionChain(new FlowMethodAction("startPalaverAction"), new FlowLink<>(MainController.class)));
		flow.withAction(ContactController.class, "contactListView", new FlowActionChain(new FlowMethodAction("startPalaverAction"), new FlowLink<>(MainController.class)));
		flow.withAction(ContactController.class, "addContactButton", new FlowActionChain(new FlowMethodAction("addContactAction"), new FlowLink<>(MainController.class)));
		Scene scene = UiUtils.prepareFlow(flow, null);

		primaryStage.setScene(scene);

		primaryStage.show();
		showDebug();
		primaryStage.focusedProperty().addListener((ond, old, n) -> Notifications.setEnabled(!n));
		Platform.runLater(() -> {
			accounts.retrieve();
			palavers.retrieve();
			ConnectionManager.start(accounts.getData().get());
			palavers.getData().addListener((ListChangeListener<Palaver>) c -> {
				while (c.next()) if (c.wasAdded()) {
					c.getAddedSubList().stream().filter(palaver -> palaver.getConference() && !palaver.getClosed()).forEach((t) -> {
//						try {
//							Utils.joinMuc(t);
//						} catch (XMPPException.XMPPErrorException | SmackException e) {
//							e.printStackTrace();
//						}
					});
				}
			});

		});
	}

	private void showDebug() {
		ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);

		ListView<Service<?>> serviceListView = new ListView<>();
		serviceListView.setCellFactory(new ServiceListCellFactory());

		serviceListView.itemsProperty().bind(executor.currentServicesProperty());


		Scene scene = new Scene(serviceListView);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setMinHeight(400);
		stage.setMaxHeight(400);
		stage.show();

	}
}
