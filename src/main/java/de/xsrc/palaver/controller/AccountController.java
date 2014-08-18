package de.xsrc.palaver.controller;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.BackAction;
import org.datafx.crud.CrudException;

import de.xsrc.palaver.Storage;
import de.xsrc.palaver.model.Account;

@FXMLController("/fxml/AccountView.fxml")
public class AccountController {
	@FXML
	@BackAction
	private Button back;

	@FXML
	private ListView<Account> palaverList;

	@FXML
	public void initialize() throws CrudException {
		Storage s = new Storage(Account.class);
		ObservableList<Account> all = (ObservableList<Account>) s.getAll();
		palaverList.setItems(all);
	}

	@FXML
	// @ActionMethod(value = "addAccountAction")
	private void addAccount() {
		Flow f = new Flow(AddAccountController.class);
		try {
			Utils.getDialog(f).show();
		} catch (FlowException e) {
			e.printStackTrace();
		}

	}
}
