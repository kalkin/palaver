package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.utils.AppDataSource;
import javafx.collections.ObservableList;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

public class AccountProvider extends ListDataProvider<Credentials> {

    public AccountProvider() {
        super(null, null, null);
        AppDataSource<Credentials> dr = new AppDataSource<>(Credentials.class);
        this.setDataReader(dr);
        this.setAddEntryHandler(new WriteBackHandler<Credentials>() {
            @Override
            public WritableDataReader<Credentials> createDataSource(Credentials observable) {
                ObservableList<Credentials> list = ApplicationContext.getInstance()
                        .getRegisteredObject(AccountProvider.class).getData();
                return new AppDataWriter<>(list, Credentials.class);
            }
        });
        this.setWriteBackHandler(new WriteBackHandler<Credentials>() {
            @Override
            public WritableDataReader<Credentials> createDataSource(Credentials observable) {
                ObservableList<Credentials> list = ApplicationContext.getInstance()
                        .getRegisteredObject(AccountProvider.class).getData();
                return new AppDataWriter<>(list, Credentials.class);
            }
        });
    }

    public static Credentials getByJid(String jid) {
        ObservableList<Credentials> credentialses = ApplicationContext.getInstance()
                .getRegisteredObject(AccountProvider.class).getData().get();
        for (Credentials credentials : credentialses) {
            if (credentials.getJid().equals(jid))
                return credentials;
        }
        return null;
    }

}
