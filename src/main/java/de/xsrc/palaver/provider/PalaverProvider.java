package de.xsrc.palaver.provider;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import de.xsrc.palaver.model.Palaver;

public class PalaverProvider {

	protected ObservableList<Palaver> openPalavers;

	public PalaverProvider(ObservableList<Palaver> list) {
		openPalavers = FXCollections.observableArrayList(list.filtered(p -> !p
				.getClosed()));
		// Listen for changes on the main palaver list
		list.addListener((Change<? extends Palaver> change) -> {
			while (change.next()) {
				if (change.getRemovedSize() > 0) {
					throw new IllegalStateException(
							"Palavers should ne be removed from storage, only closed");
				} else if (change.getAddedSize() > 0) {
					change.getAddedSubList().forEach((Palaver p) -> {
						if(!p.getClosed() && !openPalavers.contains(p)){
							openPalavers.add(p);
						}
					});
				}
			}
		});
	}

	public ObservableList<Palaver> getOpenPalaver() {
		return openPalavers;
	}

}
