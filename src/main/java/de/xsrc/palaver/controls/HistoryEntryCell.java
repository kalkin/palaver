package de.xsrc.palaver.controls;

import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.utils.UiUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

public class HistoryEntryCell extends HBox {
	private static final Logger logger = Logger.getLogger(HistoryEntryCell.class.getName());
	private final HistoryEntry historyEntry;
	private boolean showNick;

	@FXML
	private Text body;

	@FXML
	private Text nick;

	@FXML
	private Text date;

	@FXML
	private BorderPane bp;

	@FXML
	private StackPane avatar;


	public HistoryEntryCell(HistoryEntry e, boolean showNick) {
		historyEntry = e;
		this.showNick = showNick;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/HistoryEntryCell.fxml"));
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
		avatar.getChildren().add(UiUtils.getAvatar(historyEntry.getFrom()));
		body.setText(historyEntry.getBody());

		if (showNick) {
			nick.setText(historyEntry.getFrom());
		}
		date.setText((new PrettyTime()).format(new Date(historyEntry.getReceivedAt())));
		bp.prefWidthProperty().bind(this.widthProperty().subtract(64));

	}

}
