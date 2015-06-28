package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.utils.AppDataSource;
import javafx.collections.ObservableList;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import java.util.logging.Logger;

public class PalaverProvider extends ListDataProvider<Palaver> {
    private static final Logger logger = Logger.getLogger(PalaverProvider.class
            .getName());

    public PalaverProvider() {
        super(null, null, null);
        AppDataSource<Palaver> dr = new AppDataSource<>(Palaver.class);
        this.setDataReader(dr);
        this.setAddEntryHandler(new WriteBackHandler<Palaver>() {
            @Override
            public WritableDataReader<Palaver> createDataSource(Palaver observable) {
                ObservableList<Palaver> list = ApplicationContext.getInstance()
                        .getRegisteredObject(PalaverProvider.class).getData();
                return new AppDataWriter<>(list, Palaver.class);
            }
        });

        this.setWriteBackHandler(new WriteBackHandler<Palaver>() {
            @Override
            public WritableDataReader<Palaver> createDataSource(Palaver observable) {
                ObservableList<Palaver> list = ApplicationContext.getInstance()
                        .getRegisteredObject(PalaverProvider.class).getData();
                return new AppDataWriter<>(list, Palaver.class);
            }
        });
    }

}
