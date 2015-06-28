package de.xsrc.palaver;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.utils.AppDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.util.EntityWithId;
import org.datafx.writer.WriteBackHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AppDataSourceTest {

    private ListDataProvider<Palaver> provider;
    private ObservableList<Palaver> expected;

    @Before
    public void setUp() throws FileNotFoundException, IOException {
        new JFXPanel(); // Initialize JFX :)
        provider = new ListDataProvider<Palaver>();
        expected = FXCollections.observableArrayList();
        provider.setDataReader(new AppDataSource<Palaver>("./", Palaver.class));
        provider.setResultObservableList(expected);
        provider.setAddEntryHandler(new WriteBackHandler<Palaver>() {

            @Override
            public WritableDataReader<Palaver> createDataSource(
                    Palaver observable) {
                return null;
            }
        });
        provider.retrieve();
    }

    @Test
    public void testAdd() {
        expected.add(new Palaver("hans@example.com", "alice@example.com"));
        expected.add(new Palaver("hans@example.com", "bob@example.com"));
        expected.add(new Palaver("hans@example.com", "eve@example.com"));
        ObservableList<Palaver> result = provider.getData().get();
        checkIfListsMatch(expected, result);
    }

    @Test
    public void testRemove() {
        ObservableList<Palaver> result = provider.getData().get();
        Palaver p = new Palaver("hans@example.com", "eve@example.com");
        result.add(new Palaver("hans@example.com", "alice@example.com"));
        result.add(new Palaver("hans@example.com", "bob@example.com"));
        result.add(p);
        expected.remove(p);
        checkIfListsMatch(expected, result);

    }

    @Test
    public void testWriteBackHandler() {
        ObservableList<Palaver> result = provider.getData().get();
        Palaver p = new Palaver("hans@example.com", "eve@example.com");
        result.add(new Palaver("hans@example.com", "alice@example.com"));
        result.add(new Palaver("hans@example.com", "bob@example.com"));
        result.add(p);

        result.add(new Palaver("hans@example.com", "bob@example.com"));
        p.setClosed(true);
    }

    private boolean checkIfListsMatch(
            List<? extends EntityWithId<?>> expectedList,
            List<? extends EntityWithId<?>> resultList) {
        assertEquals(resultList.size(), expectedList.size());
        for (EntityWithId<?> result : resultList) {
            boolean found = false;
            for (EntityWithId<?> expected : expectedList) {
                if (result.getId().equals(expected.getId())) {
                    found = true;
                    break;
                }
            }

            if (!found)
                return false;
        }
        return true;
    }

}
