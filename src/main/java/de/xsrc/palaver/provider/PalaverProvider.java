package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.utils.AppDataSource;
import de.xsrc.palaver.utils.ColdStorage;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
        getData().addListener((ListChangeListener<Conversation>) change -> {
            save();
            while (change.next()) if (change.wasAdded()) {
                change.getAddedSubList().parallelStream().forEach(conversation -> {
                    // save conversation if conversation was opened or closed
                    conversation.closedProperty().addListener((observable, oldValue, newValue) -> {
                        if (oldValue != newValue)
                            save();
                    } );

                    final ObservableList<HistoryEntry> historyEntries = conversation.history.entryListProperty();
                    // save when new message is added to the history
                    historyEntries.addListener((ListChangeListener<HistoryEntry>) c -> save());
                    // save when a message sendState is changed to true
                    final FilteredList<HistoryEntry> unsendHistoryEntries = historyEntries.filtered(historyEntry -> !historyEntry.getSendState());
                    unsendHistoryEntries.forEach(historyEntry -> {
                        historyEntry.sendStateProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                save();
                            }
                        });
                    });
                });
            }
        });
    }

    private synchronized void save() {
        logger.finer("Saving conversations to storage");
        ColdStorage.save(Conversation.class, getData());
    }
}
