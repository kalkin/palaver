package de.xsrc.palaver.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
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
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.LinkAction;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.utils.Utils;

@FXMLController("/fxml/MainView.fxml")
public class MainController {

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

	@FXML
	@LinkAction(BuddyListView.class)
	private Button showBuddyListButton;

	@FXML
	private Button addPalaverButton;

	@FXML
	private ListView<Palaver> palaverListView;

	private HashMap<Palaver, ViewContext<HistoryController>> historyMap = new HashMap<Palaver, ViewContext<HistoryController>>();

	private ObservableList<Palaver> all = FXCollections
			.observableList(new LinkedList<Palaver>());

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button hidePalaverButton;

	private Node palaverListTmp;

	@FXML
	private void initialize() {
		ObservableList<Palaver> palavers = Storage.getList(Palaver.class);
		for (Palaver palaver : palavers) {
			if(!palaver.getClosed()){
				all.add(palaver);
			}
		}
		palavers.addListener((Change<? extends Palaver> c) -> {
			while (c.next()) {
				System.out.println("CHANGES");
				if (c.getAddedSize() > 0) {
					List<? extends Palaver> subList = c.getAddedSubList();
					for (Palaver p : subList) {
						if (p.getClosed() && all.contains(p)) {
							all.remove(p);
						} else if (!p.getClosed() && !all.contains(p)) {
							all.add(p);
						}
					}
				} else if (c.wasUpdated()) {
					ObservableList<? extends Palaver> subList = c.getList();
					for (Palaver p : subList) {
						if (p.getClosed() && all.contains(p)) {
							all.remove(p);
						} else if (!p.getClosed() && !all.contains(p)) {
							all.add(p);
						}
					}
				}
			}
		});
		// palavers.removeIf(p -> p.getClosed());
		palaverListView.setItems(all);
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
							historyMap.get(newValue).getController()
									.requestFocus();
						});
		AwesomeDude.setIcon(showAccountsButton, AwesomeIcon.GEAR, "24");
		AwesomeDude.setIcon(addPalaverButton, AwesomeIcon.PLUS, "24");
		AwesomeDude.setIcon(hidePalaverButton, AwesomeIcon.CHEVRON_LEFT, "24");
		AwesomeDude.setIcon(showBuddyListButton, AwesomeIcon.USERS, "24");
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
		System.out.println("drin");
	}

	@FXML
	private void removeAction() {
		Palaver p = palaverListView.getSelectionModel().getSelectedItem();
		p.setClosed(true);
	}
}
