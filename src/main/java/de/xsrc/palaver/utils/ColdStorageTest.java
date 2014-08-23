package de.xsrc.palaver.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.util.EntityWithId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.xsrc.palaver.utils.AllTests.TestModel;

public class ColdStorageTest {

	private LinkedList<TestModel> list;

	@Before
	public void setUp() throws Exception {
		list = new LinkedList<TestModel>();
		list.add(new TestModel("alice@example.com", "password"));
		list.add(new TestModel("bob@example.com", "password"));
		list.add(new TestModel("eve@example.com", "password"));
		ColdStorage.save(TestModel.class, list);
	}

	@After
	public void tearDown() throws Exception {
		// ColdStorage.save(TestModel.class, new LinkedList<TestModel>());
	}

	@Test
	public void testGet() throws IOException {
		LinkedList<TestModel> resultList = ColdStorage.get(TestModel.class);
		assertTrue(checkIfListsMatch(this.list, resultList));
	}

	private boolean checkIfListsMatch(List<? extends EntityWithId<String>> expectedList,
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

	@SuppressWarnings("unchecked")
	@Test
	public void testSave() throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, ClassCastException,
			ParserConfigurationException, JAXBException {
		LinkedList<TestModel> expected = (LinkedList<TestModel>) list.clone();
		expected.add(new TestModel("foo@example.com", "password"));
		ColdStorage.save(TestModel.class, expected);
		LinkedList<EntityWithId<?>> result = ColdStorage.get(TestModel.class);
		assertTrue(checkIfListsMatch(expected, (List<? extends EntityWithId<String>>) result));
	}

}
