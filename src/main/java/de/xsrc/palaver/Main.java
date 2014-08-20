package de.xsrc.palaver;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import de.xsrc.palaver.controller.MainController;
import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ChatUtils;

public class Main extends Application {

	private static final Logger logger = Logger.getLogger(Storage.class
			.getName());

	@Override
	public void start(Stage primaryStage) throws FlowException {
		Platform.runLater(() -> {
			handleXmpp();
		});
		Flow flow = new Flow(MainController.class);
		Scene scene = handleI18n(flow);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	private ObservableMap<Account, XMPPConnection> handleXmpp() {
		ObservableList<Account> accountList = Utils.getStorage(Account.class)
				.getAll();
		ObservableMap<Account, XMPPConnection> conMap = FXCollections
				.observableHashMap();
		for (Account account : accountList) {
			logger.fine("Connection account " + account);
			ChatUtils.getConnection(account);
			logger.info("Connected account: " + account);
		}
		return conMap;
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
