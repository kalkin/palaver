package de.xsrc.palaver;

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.controller.MainController;
import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.provider.ContactProvider;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.xmpp.ChatUtils;
import de.xsrc.palaver.xmpp.UiUtils;

public class Main extends Application {

	private static final Logger logger = Logger.getLogger(Storage.class
			.getName());

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
			handleXmpp(accounts.getData().get());
			palavers.getData()
					.get()
					.addListener(
							(Change<? extends Palaver> c) -> {
								while (c.next()) {
									if (c.getAddedSize() > 0) {
										for (Palaver p : c.getAddedSubList()) {
											String server = StringUtils
													.parseServer(p
															.getRecipient());
											if (server.startsWith("muc")) {
												ChatUtils.getMuc(p);
											}
										}
									}
								}
							});
		});
	}

	private void handleXmpp(ObservableList<Account> accountList) {
		ContactProvider provider = ApplicationContext.getInstance().getRegisteredObject(ContactProvider.class);
		
		accountList.addListener(new ListChangeListener<Account>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Account> c) {
				while (c.next()) {
					if (c.getAddedSize() > 0) {
						List<? extends Account> list = c.getAddedSubList();
						for (Account account : list) {
							logger.fine("Connecting to account " + account);
							XMPPConnection con = ChatUtils.getConnection(account);
							if(con.isAuthenticated()){
								logger.fine("Initializing roster for " + account);
								provider.initRoster(account, con.getRoster());
							}
						}
					}
				}

			}
		});

	}

	public static void main(String[] args) {

		launch(args);
	}
}
