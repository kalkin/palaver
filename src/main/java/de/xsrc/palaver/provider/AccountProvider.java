package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.utils.AppDataSource;
import javafx.collections.ObservableList;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

public class AccountProvider extends ListDataProvider<Account> {

	public AccountProvider() {
		super(null, null, null);
		AppDataSource<Account> dr = new AppDataSource<>(Account.class);
		this.setDataReader(dr);
		this.setAddEntryHandler(new WriteBackHandler<Account>() {
			@Override
			public WritableDataReader<Account> createDataSource(Account observable) {
				ObservableList<Account> list = ApplicationContext.getInstance()
								.getRegisteredObject(AccountProvider.class).getData();
				return new AppDataWriter<>(list, Account.class);
			}
		});
		this.setWriteBackHandler(new WriteBackHandler<Account>() {
			@Override
			public WritableDataReader<Account> createDataSource(Account observable) {
				ObservableList<Account> list = ApplicationContext.getInstance()
								.getRegisteredObject(AccountProvider.class).getData();
				return new AppDataWriter<>(list, Account.class);
			}
		});
	}

	public static Account getByJid(String jid){
		ObservableList<Account> accounts = ApplicationContext.getInstance()
						.getRegisteredObject(AccountProvider.class).getData().get();
		for (Account account: accounts) {
			if(account.getJid().equals(jid))
			return account;
		}
		return null;
	}

}
