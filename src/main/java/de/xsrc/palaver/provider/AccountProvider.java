package de.xsrc.palaver.provider;

import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.utils.AppDataSource;

public class AccountProvider extends ListDataProvider<Account> {

	public AccountProvider() {
		super(new AppDataSource<Account>(Account.class), null, null);
		this.setAddEntryHandler(new WriteBackHandler<Account>() {

			@Override
			public WritableDataReader<Account> createDataSource(
					Account observable) {
				// TODO Implement this
				System.out.println("would create new Account");
				return null;
			}
		});

	}

}
