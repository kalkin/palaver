package de.xsrc.palaver.controls;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.controller.PalaverCell;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.utils.UiUtils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.logging.Logger;


public class OpenPalaverList extends BorderPane {

	private static final Logger logger = Logger.getLogger(OpenPalaverList.class.getName());

	private static final String OPEN_PALAVER_LIST_FXML = "/fxml/OpenPalaverList.fxml";

	private PalaverModel model = PalaverModel.getInstance();


	@FXML
	private ListView<Palaver> openPalaversList;

	@FXML
	private Button hideButton;

	public OpenPalaverList() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(OPEN_PALAVER_LIST_FXML));
		fxmlLoader.setResources(UiUtils.getRessourceBundle());
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	private void initialize() {
		openPalaversList.setItems(model.getOpenPalavers());
		MultipleSelectionModel<Palaver> selModel = openPalaversList
						.getSelectionModel();
		selModel.setSelectionMode(SelectionMode.SINGLE);
		openPalaversList.setSelectionModel(selModel);
		openPalaversList.setCellFactory(listView -> new PalaverCell());
		hideButton.cancelButtonProperty().bind(this.visibleProperty()); // There can only be one cancelButton
	}

	public ReadOnlyObjectProperty<Palaver> selectedPalaver() {
		logger.finer(String.format("Selected %s", openPalaversList.getSelectionModel().selectedItemProperty().get()));
		return openPalaversList.getSelectionModel().selectedItemProperty();
	}

	@FXML
	private void hideAction() {
		hide(true);
	}

	public void hide(boolean b){
		setVisible(!b);
		setManaged(!b);
	}

}
