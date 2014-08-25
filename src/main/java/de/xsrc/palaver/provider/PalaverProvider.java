package de.xsrc.palaver.provider;

import javafx.collections.ObservableList;

import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.AppDataSource;

public class PalaverProvider extends ListDataProvider<Palaver> {

	public PalaverProvider() {
		super(null, null, null);
		AppDataSource<Palaver> dr = new AppDataSource<Palaver>(Palaver.class);
		this.setDataReader(dr);
		this.setAddEntryHandler(new WriteBackHandler<Palaver>() {
			@Override
			public WritableDataReader<Palaver> createDataSource(
					Palaver observable) {
				ObservableList<Palaver> list = ApplicationContext.getInstance()
						.getRegisteredObject(PalaverProvider.class).getData();
				return new AppDataWriter<Palaver>(list, Palaver.class);
			}
		});
		this.setWriteBackHandler(new WriteBackHandler<Palaver>() {
			@Override
			public WritableDataReader<Palaver> createDataSource(
					Palaver observable) {
				ObservableList<Palaver> list = ApplicationContext.getInstance()
						.getRegisteredObject(PalaverProvider.class).getData();
				return new AppDataWriter<Palaver>(list, Palaver.class);
			}
		});
	}

	public Palaver getById(String account, String recipient) {
		String id = account + ":" + recipient;
		return getById(id);
	}

	public Palaver getById(String id) {
		for (Palaver p : getData()) {
			if (p.getId().equals(id)) {
				return p;
			}
		}
		return null;
	}

}
