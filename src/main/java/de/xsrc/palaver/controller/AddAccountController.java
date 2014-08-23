package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.datafx.controller.FXMLController;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.context.FXMLViewFlowContext;
import org.datafx.controller.flow.context.ViewFlowContext;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.utils.Storage;

@FXMLController("/fxml/AddAccountView.fxml")
public class AddAccountController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	private Account account;

	@FXML
	private Button back;

	@FXML
	private TextField jidField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private void initialize() {
	}

	@FXML
	private void close() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void saveAccount() {
		if (account == null) {
			account = new Account(jidField.getText(), passwordField.getText());
			Storage.getList(Account.class).add(account);
		} else {
			account.setJid(jidField.getText());
			account.setPassword(passwordField.getText());
		}
		close();

	}

	public void setContext(ViewContext<AddAccountController> context) {
		account = (Account) context.getRegisteredObject("account");
		jidField.setText(account.getJid());
		passwordField.setText(account.getPassword());

	}
}
