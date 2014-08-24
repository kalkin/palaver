package de.xsrc.palaver.provider;

import java.io.IOException;

import javafx.beans.property.ListProperty;

import org.datafx.provider.ListDataProvider;
import org.datafx.reader.AbstractDataReader;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.utils.AppDataSource;

public class AccountProvider extends AbstractDataReader<Account> {

	private int currentIndex = 0;
	private ListProperty<Account> data;

	public AccountProvider() {
		ListDataProvider<Account> provider = new ListDataProvider<Account>(
				new AppDataSource<Account>(Account.class));
		provider.retrieve();
		provider.setAddEntryHandler(new WriteBackHandler<Account>() {

			@Override
			public WritableDataReader createDataSource(Account observable) {
				System.out.println("would create new Account");
				return null;
			}
		});
		data = provider.getData();

	}

	@Override
	public Account get() throws IOException {
		int index = currentIndex;
		currentIndex++;
		return data.get(index);
	}

	@Override
	public boolean next() {
		return currentIndex < data.getSize();
	}

}
