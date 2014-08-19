package de.xsrc.palaver.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.datafx.controller.FXMLController;
import org.datafx.crud.CrudException;

import de.xsrc.palaver.model.Palaver;

@FXMLController("/fxml/AddPalaverView.fxml")
public class AddPalaverController {

	private static final Logger logger = Logger
			.getLogger(AddPalaverController.class.getName());

	@FXML
	private Button back;

	@FXML
	private TextField jid;

	@FXML
	private void addPalaverAction() throws CrudException {
		logger.warning("Called addPalaverAction");
		Palaver p = new Palaver();
		p.setRecipient(jid.getText());
		p.setAccount("adolf@xsrc.de");
		Utils.getStorage(Palaver.class).getAll().add(p);
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void close() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}
}
