package de.xsrc.palaver.controller;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.datafx.controller.FXMLController;
import org.datafx.crud.CrudException;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.utils.Utils;

@FXMLController("/fxml/AddPalaverView.fxml")
public class AddPalaverController {

	private static final Logger logger = Logger
			.getLogger(AddPalaverController.class.getName());

	@FXML
	private Button back;

	@FXML
	private TextField jid;

	@FXML
	private ChoiceBox<Account> accountChoice;

	@FXML
	private void initialize() {
		ObservableList<Account> accounts = Storage.getList(Account.class);
		accountChoice.getItems().addAll(accounts);
		if (accounts.size() > 0) {
			accountChoice.getSelectionModel().select(0);
		}
	}

	@FXML
	private void addPalaverAction() throws CrudException {
		Palaver p = new Palaver();
		p.setRecipient(jid.getText());
		Account account = accountChoice.getSelectionModel().getSelectedItem();
		logger.finer(account.getJid() + " is starting palaver with "
				+ jid.getText());
		p.setAccount(account.getId());
		Storage.getList(Palaver.class).add(p);
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void close() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}
}
