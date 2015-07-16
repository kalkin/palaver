package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.utils.AppDataSource;
import javafx.collections.ObservableList;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

import java.util.logging.Logger;

public class PalaverProvider extends ListDataProvider<Conversation> {
    private static final Logger logger = Logger.getLogger(PalaverProvider.class
            .getName());

    public PalaverProvider() {
        super(null, null, null);
        AppDataSource<Conversation> dr = new AppDataSource<>(Conversation.class);
        this.setDataReader(dr);
        this.setAddEntryHandler(new WriteBackHandler<Conversation>() {
            @Override
            public WritableDataReader<Conversation> createDataSource(Conversation observable) {
                ObservableList<Conversation> list = getData();
                return new AppDataWriter<>(list, Conversation.class);
            }
        });

        this.setWriteBackHandler(new WriteBackHandler<Conversation>() {
            @Override
            public WritableDataReader<Conversation> createDataSource(Conversation observable) {
                ObservableList<Conversation> list = getData();
                return new AppDataWriter<>(list, Conversation.class);
            }
        });
    }

}
