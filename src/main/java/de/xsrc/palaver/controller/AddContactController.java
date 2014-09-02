package de.xsrc.palaver.controller;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.provider.ContactProvider;
import de.xsrc.palaver.provider.PalaverProvider;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.datafx.controller.FXMLController;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.util.StringUtils;

import java.util.logging.Logger;

@FXMLController("/fxml/AddContactView.fxml")
public class AddContactController {

	private static final Logger logger = Logger.getLogger(AccountController.class
					.getName());

	@FXML
	private Button back;

	@FXML
	private TextField jid;

	@FXML
	private Button saveButton;

	@FXML
	private ChoiceBox<Account> accountChoice;

	@FXML
	private void initialize() {
		ListProperty<Account> accounts = ApplicationContext.getInstance()
						.getRegisteredObject(AccountProvider.class).getData();
		accountChoice.getItems().addAll(accounts);
		if (accounts.size() > 0) {
			accountChoice.getSelectionModel().select(0);
		}

		jid.textProperty().addListener(observable -> {
			boolean isJid = StringUtils.isFullJID(jid.textProperty().get() + "/Foo");
			saveButton.setDisable(!isJid);
		});
	}

	@FXML
	private void addContactAction() throws NotLoggedInException,
					NoResponseException, XMPPErrorException, NotConnectedException {
		ContactProvider provider = ApplicationContext.getInstance()
						.getRegisteredObject(ContactProvider.class);
		Account account = accountChoice.getSelectionModel().getSelectedItem();
		provider.addContact(account, jid.getText());
		PalaverProvider.openPalaver(account.getJid(), jid.getText());
		close();
	}

	@FXML
	private void close() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}
}
