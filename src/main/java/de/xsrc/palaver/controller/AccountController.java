package de.xsrc.palaver.controller;

import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.datafx.controller.FXMLController;
import org.datafx.controller.FxmlLoadException;
import org.datafx.controller.ViewConfiguration;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.BackAction;
import org.datafx.crud.CrudException;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.utils.Utils;

@FXMLController("/fxml/AccountView.fxml")
public class AccountController {
	@FXML
	@BackAction
	private Button back;
	
	@FXML
	private Button addAccountButton;

	@FXML
	private ListView<Account> palaverList;

	@FXML
	public void initialize() throws CrudException {
		Storage<Account,String> s = Utils.getStorage(Account.class);
		ObservableList<Account> all = (ObservableList<Account>) s.getAll();
		palaverList.setItems(all);
		AwesomeDude.setIcon(back, AwesomeIcon.CHEVRON_LEFT, "24");
		AwesomeDude.setIcon(addAccountButton, AwesomeIcon.PLUS, "24");
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

	@FXML
	private void editAction() {
		Account acc = palaverList.getSelectionModel().getSelectedItem();
		try {
			ResourceBundle b = ResourceBundle.getBundle("i18n.Palaver_en");
			ViewConfiguration config = new ViewConfiguration();
			config.setResources(b);
			ViewContext<AddAccountController> context = ViewFactory.getInstance().createByController(AddAccountController.class, null, config);
			context.register("account", acc);
			context.getController().setContext(context);
			Scene scene = new Scene((Parent) context.getRootNode());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.show();
		} catch (FxmlLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void removeAction(){
		Account acc = palaverList.getSelectionModel().getSelectedItem();
		palaverList.getItems().remove(acc);
	}
}
