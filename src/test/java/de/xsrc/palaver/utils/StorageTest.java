package de.xsrc.palaver.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.util.EntityWithId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.xsrc.palaver.utils.ColdStorage;
import de.xsrc.palaver.utils.Storage;

public class StorageTest {

	private static LinkedList<AllTests.TestModel> list;
	private static Storage db;

	@Before
	public void setUpClass() throws Exception {
		// Clear the xml file
		list = new LinkedList<AllTests.TestModel>();
		list.add(new AllTests.TestModel("alice@example.com", "password"));
		list.add(new AllTests.TestModel("bob@example.com", "password"));
		list.add(new AllTests.TestModel("eve@example.com", "password"));
		ColdStorage.save(AllTests.TestModel.class, list);
		db = new Storage();
		db.initialize(AllTests.TestModel.class);
	}

	@After
	public void tearDown() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ClassCastException,
			ParserConfigurationException, JAXBException, IOException {

		LinkedList<AllTests.TestModel> list = new LinkedList<AllTests.TestModel>();
		ColdStorage.save(AllTests.TestModel.class, list);

	}

	@Test
	public void testGetList() {
		@SuppressWarnings("unused")
		ObservableList<AllTests.TestModel> result = Storage
				.getList(AllTests.TestModel.class);
	}

	@Test
	public void testAddWriteback() {
		Storage.getList(AllTests.TestModel.class).add(
				new AllTests.TestModel("hans@example.com", "password"));
		checkStorage();
	}

	private void checkStorage() {
		ObservableList<AllTests.TestModel> expected = Storage
				.getList(AllTests.TestModel.class);
		LinkedList<EntityWithId<?>> result = ColdStorage
				.get(AllTests.TestModel.class);
		assertTrue(checkIfListsMatch(expected, extracted(result)));
	}

	private List<? extends EntityWithId<?>> extracted(
			LinkedList<EntityWithId<?>> result) {
		return result;
	}

	@Test
	public void testAddUpdateWriteback() {
		Storage.getList(AllTests.TestModel.class).add(
				new AllTests.TestModel("hans@example.com", "password"));
		AllTests.TestModel m = Storage.getById(AllTests.TestModel.class,
				"hans@example.com");
		m.setPassword("newpassword");
		checkStorage();
	}

	@Test
	public void testUpdateWriteback() {
		Storage.getList(AllTests.TestModel.class).get(0).setPassword("test");
		checkStorage();
	}

	@Test
	public void testDeleteWriteback() {
		ObservableList<AllTests.TestModel> expected = Storage
				.getList(AllTests.TestModel.class);
		expected.remove(0);
		LinkedList<EntityWithId<?>> result = ColdStorage
				.get(AllTests.TestModel.class);
		assertTrue(checkIfListsMatch(expected, extracted(result)));
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

	@Test
	public void testGetById() throws IOException {
		String expected = "alice@example.com";
		AllTests.TestModel t = Storage.getById(AllTests.TestModel.class,
				"alice@example.com");
		assertTrue(t.getId().equals(expected));
	}

}
