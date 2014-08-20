package de.xsrc.palaver.controller;

import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;

import org.datafx.controller.FXMLController;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.LinkAction;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Utils;

@FXMLController("/fxml/MainView.fxml")
public class MainController {

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

	@FXML
	private Button addPalaverButton;

	
	@FXML
	private ListView<Palaver> palaverList;

	private HashMap<Palaver, ViewContext<HistoryController>> historyMap = new HashMap<Palaver, ViewContext<HistoryController>>();

	@FXML
	private BorderPane borderPane;

	@FXML
	private void initialize() {
		ObservableList<Palaver> palavers = Utils.getStorage(Palaver.class)
				.getAll();
		palaverList.setItems(palavers);
		MultipleSelectionModel<Palaver> selModel = palaverList
				.getSelectionModel();

		selModel.setSelectionMode(SelectionMode.SINGLE);

		selModel.selectedItemProperty()
				.addListener(
						(ObservableValue<? extends Palaver> observable,
								Palaver oldValue, Palaver newValue) -> {
							if (!historyMap.containsKey(newValue)) {
								try {
									ViewContext<HistoryController> context = ViewFactory
											.getInstance().createByController(
													HistoryController.class);
									context.getController()
											.setPalaver(newValue);
									historyMap.put(newValue, context);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							borderPane.setCenter(historyMap.get(newValue)
									.getRootNode());
						});
		showAccountsButton.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.USER));
		addPalaverButton.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.PLUS));
	}

	@FXML
	private void addPalaver() {
		Flow f = new Flow(AddPalaverController.class);
		try {
			Utils.getDialog(f).show();
		} catch (FlowException e) {
			e.printStackTrace();
		}
	}

}
