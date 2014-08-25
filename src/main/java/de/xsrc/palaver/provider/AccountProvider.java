package de.xsrc.palaver.provider;

import javafx.collections.ObservableList;

import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.utils.AppDataSource;

public class AccountProvider extends ListDataProvider<Account> {
	
	

	public AccountProvider() {
		super(null, null, null);
		AppDataSource<Account> dr = new AppDataSource<Account>(Account.class);
		this.setDataReader(dr);
		this.setAddEntryHandler(new WriteBackHandler<Account>() {
			@Override
			public WritableDataReader<Account> createDataSource(Account observable) {
				ObservableList<Account> list = ApplicationContext.getInstance().getRegisteredObject(AccountProvider.class).getData();
				return new AppDataWriter<Account>(list, Account.class);
			}
		});
	}
}
