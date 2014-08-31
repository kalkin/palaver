package de.xsrc.palaver;

import de.xsrc.palaver.controller.MainController;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.provider.ContactProvider;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.xmpp.UiUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;

public class Main extends Application {

	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws FlowException {
		AccountProvider accounts = new AccountProvider();
		ApplicationContext.getInstance().register(accounts);
		PalaverProvider palavers = new PalaverProvider();
		ApplicationContext.getInstance().register(palavers);
		ContactProvider contacts = new ContactProvider();
		ApplicationContext.getInstance().register(contacts);

		Flow flow = new Flow(MainController.class);
		Scene scene = UiUtils.prepareFlow(flow, null);

		primaryStage.setScene(scene);

		primaryStage.show();
		Platform.runLater(() -> {
							accounts.retrieve();
							palavers.retrieve();
							ConnectionManager.start(accounts.getData().get());
						}

		);
	}
}
