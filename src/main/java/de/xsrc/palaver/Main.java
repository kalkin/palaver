package de.xsrc.palaver;


import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.controller.ContactController;
import de.xsrc.palaver.controller.MainController;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Notifications;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.utils.UiUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.FlowActionChain;
import org.datafx.controller.flow.action.FlowLink;
import org.datafx.controller.flow.action.FlowMethodAction;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.logging.Logger;

public class Main extends Application {

	private static final Logger logger = Logger.getLogger(Main.class
					.getName());

	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws FlowException {
		AccountProvider accounts = new AccountProvider();
		ApplicationContext.getInstance().register(accounts);
		PalaverProvider palavers = new PalaverProvider();
		ApplicationContext.getInstance().register(palavers);
		Flow flow = new Flow(MainController.class);

		flow.withAction(ContactController.class, "startPalaverButton", new FlowActionChain(new FlowMethodAction("startPalaverAction"), new FlowLink<>(MainController.class)));
		flow.withAction(ContactController.class, "contactListView", new FlowActionChain(new FlowMethodAction("startPalaverAction"), new FlowLink<>(MainController.class)));
		flow.withAction(ContactController.class, "addContactButton", new FlowActionChain(new FlowMethodAction("addContactAction"), new FlowLink<>(MainController.class)));
		Scene scene = UiUtils.prepareFlow(flow, null);

		primaryStage.setScene(scene);

		primaryStage.show();
		primaryStage.focusedProperty().addListener((ond, old, n) -> Notifications.setEnabled(!n));
		Platform.runLater(() -> {
			accounts.retrieve();
			palavers.retrieve();
			ConnectionManager.start(accounts.getData().get());
			palavers.getData().addListener((ListChangeListener<Palaver>) c -> {
				while (c.next()) if (c.wasAdded()) {
					c.getAddedSubList().stream().filter(palaver -> palaver.getConference() && !palaver.getClosed()).forEach((t) -> {
						try {
							Utils.joinMuc(t);
						} catch (XMPPException.XMPPErrorException | SmackException e) {
							e.printStackTrace();
						}
					});
				}
			});

		});
	}
}
