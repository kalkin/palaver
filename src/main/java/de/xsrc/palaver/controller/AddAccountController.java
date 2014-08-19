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
import org.datafx.crud.CrudException;

import de.xsrc.palaver.model.Account;

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
		System.out.println(account);
	}

	@FXML
	private void close() {
		System.out.println(account);
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void saveAccount() {
		if (account == null) {
			Account account = new Account(jidField.getText(),
					passwordField.getText());
		} else {
			account.setJid(jidField.getText());
			account.setPassword(passwordField.getText());
		}
		try {
			Utils.getStorage(Account.class).save(account);
			close();
		} catch (CrudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setContext(ViewContext<AddAccountController> context) {
		account = (Account) context.getRegisteredObject("account");
		jidField.setText(account.getJid());
		passwordField.setText(account.getPassword());

	}
}
