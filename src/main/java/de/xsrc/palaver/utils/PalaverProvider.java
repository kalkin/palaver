package de.xsrc.palaver.utils;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;

import org.datafx.provider.ListDataProvider;
import org.datafx.reader.AbstractDataReader;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import de.xsrc.palaver.model.Palaver;

public class PalaverProvider extends AbstractDataReader<Palaver> implements
		WritableDataReader<Palaver> {

	private int currentIndex = 0;
	private ListProperty<Palaver> data;

	public PalaverProvider(){
		ListDataProvider<Palaver> provider = new ListDataProvider<Palaver>(new  AppDataSource<Palaver>(Palaver.class));
		provider.setAddEntryHandler(new WriteBackHandler<Palaver>() {
			
			@Override
			public WritableDataReader createDataSource(Palaver observable) {
				System.out.println("would create new palaver");
				observable.closedProperty().addListener(new InvalidationListener() {
					
					@Override
					public void invalidated(Observable observable) {
						System.out.println("closed");
						
					}
				});
				return null;
			}
		});
		provider.retrieve();
		data = provider.getData();
	}

	@Override
	public boolean next() {
		return currentIndex < data.getSize();
	}

	@Override
	public Palaver get() throws IOException {
		int index = currentIndex;
		currentIndex++;
		return data.get(index);
	}

	@Override
	public void writeBack() {
		System.out.println("Would write back?");

	}

}
