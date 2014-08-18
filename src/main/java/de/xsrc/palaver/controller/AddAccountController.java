package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.datafx.controller.FXMLController;

@FXMLController("/fxml/AddAccountView.fxml")
public class AddAccountController {

	@FXML
	private Button back;

	@FXML
	private void close() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}
}
