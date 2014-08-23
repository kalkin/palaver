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

import de.xsrc.palaver.utils.AllTests.TestModel;

public class StorageTest {

	private static LinkedList<TestModel> list;
	private static Storage db;

	@Before
	public void setUpClass() throws Exception {
		// Clear the xml file
		list = new LinkedList<TestModel>();
		list.add(new TestModel("alice@example.com", "password"));
		list.add(new TestModel("bob@example.com", "password"));
		list.add(new TestModel("eve@example.com", "password"));
		ColdStorage.save(TestModel.class, list);
		db = new Storage();
		db.initialize(TestModel.class);
	}

	@After
	public void tearDown() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ClassCastException,
			ParserConfigurationException, JAXBException, IOException {

		LinkedList<TestModel> list = new LinkedList<TestModel>();
		ColdStorage.save(TestModel.class, list);

	}

	@Test
	public void testGetList() {
		@SuppressWarnings("unused")
		ObservableList<TestModel> result = Storage.getList(TestModel.class);
	}

	@Test
	public void testAddWriteback() {
		Storage.getList(TestModel.class).add(
				new TestModel("hans@example.com", "password"));
		checkStorage();
	}

	private void checkStorage() {
		ObservableList<TestModel> expected = Storage.getList(TestModel.class);
		LinkedList<EntityWithId<?>> result = ColdStorage.get(TestModel.class);
		assertTrue(checkIfListsMatch(expected,
				extracted(result)));
	}

	@SuppressWarnings("unchecked")
	private List<? extends EntityWithId<String>> extracted(
			LinkedList<EntityWithId<?>> result) {
		return (List<? extends EntityWithId<String>>) result;
	}

	@Test
	public void testAddUpdateWriteback() {
		Storage.getList(TestModel.class).add(
				new TestModel("hans@example.com", "password"));
		TestModel m = Storage.getById(TestModel.class, "hans@example.com");
		m.setPassword("newpassword");
		checkStorage();
	}

	@Test
	public void testUpdateWriteback() {
		Storage.getList(TestModel.class).get(0).setPassword("test");
		checkStorage();
	}

	@Test
	public void testDeleteWriteback() {
		ObservableList<TestModel> expected = Storage.getList(TestModel.class);
		expected.remove(0);
		LinkedList<EntityWithId<?>> result = ColdStorage.get(TestModel.class);
		assertTrue(checkIfListsMatch(expected,
				extracted(result)));
	}

	private boolean checkIfListsMatch(
			List<? extends EntityWithId<String>> expectedList,
			List<? extends EntityWithId<String>> resultList) {
		assertEquals(resultList.size(), expectedList.size());
		for (EntityWithId<String> result : resultList) {
			boolean found = false;
			for (EntityWithId<String> expected : expectedList) {
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
		TestModel t = Storage.getById(TestModel.class, "alice@example.com");
		assertTrue(t.getId().equals(expected));
	}

}
