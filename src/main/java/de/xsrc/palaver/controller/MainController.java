package de.xsrc.palaver.controller;

import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.datafx.controller.FXMLController;
import org.datafx.controller.FxmlLoadException;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.LinkAction;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Utils;

@FXMLController("/fxml/MainView.fxml")
public class MainController {

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

	@FXML
	@LinkAction(ContactController.class)
	private Button showBuddyListButton;

	@FXML
	private Button addPalaverButton;

	@FXML
	private ListView<Palaver> palaverListView;

	private HashMap<Palaver, ViewContext<HistoryController>> historyMap = new HashMap<Palaver, ViewContext<HistoryController>>();

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button hidePalaverButton;

	private Node palaverListTmp;

	@FXML
	private void initialize() {
		PalaverProvider provider = ApplicationContext.getInstance().getRegisteredObject(PalaverProvider.class);

		palaverListView.setItems(provider.getData());
		palaverListView
				.setCellFactory(new Callback<ListView<Palaver>, ListCell<Palaver>>() {
					@Override
					public ListCell<Palaver> call(ListView<Palaver> listView) {
						return new PalaverCell();
					}
				});
		MultipleSelectionModel<Palaver> selModel = palaverListView
				.getSelectionModel();

		selModel.setSelectionMode(SelectionMode.SINGLE);

		selModel.selectedItemProperty()
				.addListener(
						(ObservableValue<? extends Palaver> observable,
								Palaver oldValue, Palaver newValue) -> {
							if (newValue != null) {
								if (!historyMap.containsKey(newValue)) {
									try {
										ViewContext<HistoryController> context = ViewFactory
												.getInstance()
												.createByController(
														HistoryController.class);
										context.getController().setPalaver(
												newValue);
										historyMap.put(newValue, context);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								borderPane.setCenter(historyMap.get(newValue)
										.getRootNode());
								historyMap.get(newValue).getController()
										.requestFocus();
							}
						});
		AwesomeDude.setIcon(showAccountsButton, AwesomeIcon.GEAR, "24");
		AwesomeDude.setIcon(addPalaverButton, AwesomeIcon.PLUS, "24");
		AwesomeDude.setIcon(hidePalaverButton, AwesomeIcon.CHEVRON_LEFT, "24");
		AwesomeDude.setIcon(showBuddyListButton, AwesomeIcon.USERS, "24");
	}

	@FXML
	private void addPalaver() throws FxmlLoadException {
		Flow f = new Flow(AddPalaverController.class);
		try {
			Utils.getDialog(f, null).show();
		} catch (FlowException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void hidePalaverList() {
		if (palaverListTmp == null) {
			palaverListTmp = borderPane.getLeft();
			borderPane.setLeft(null);
			hidePalaverButton.setGraphic(AwesomeDude
					.createIconLabel(AwesomeIcon.CHEVRON_RIGHT));
		} else {
			borderPane.setLeft(palaverListTmp);
			palaverListTmp = null;
			hidePalaverButton.setGraphic(AwesomeDude
					.createIconLabel(AwesomeIcon.CHEVRON_LEFT));
		}
	}

	@FXML
	private void removeAction() {
		Palaver p = palaverListView.getSelectionModel().getSelectedItem();
		palaverListView.getItems().remove(p);
		historyMap.remove(p);
		p.setClosed(true);
	}
}
