package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.datafx.controller.FXMLController;
import org.datafx.crud.CrudException;

import de.xsrc.palaver.model.Account;

@FXMLController("/fxml/AddAccountView.fxml")
public class AddAccountController {

	@FXML
	private Button back;

	@FXML
	private TextField jidField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private void close() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void saveAccount() {
		Account account = new Account(jidField.getText(), passwordField.getText());
		try {
			Utils.getStorage().save(account);
			close();
		} catch (CrudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
}
