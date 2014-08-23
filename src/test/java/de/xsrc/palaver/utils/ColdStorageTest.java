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

import de.xsrc.palaver.utils.ColdStorage;

public class ColdStorageTest {

	private LinkedList<AllTests.TestModel> list;

	@Before
	public void setUp() throws Exception {
		list = new LinkedList<AllTests.TestModel>();
		list.add(new AllTests.TestModel("alice@example.com", "password"));
		list.add(new AllTests.TestModel("bob@example.com", "password"));
		list.add(new AllTests.TestModel("eve@example.com", "password"));
		ColdStorage.save(AllTests.TestModel.class, list);
	}

	@After
	public void tearDown() throws Exception {
		// ColdStorage.save(AllTests.TestModel.class, new LinkedList<AllTests.TestModel>());
	}

	@Test
	public void testGet() throws IOException {
		LinkedList<AllTests.TestModel> resultList = ColdStorage.get(AllTests.TestModel.class);
		assertTrue(checkIfListsMatch(this.list, resultList));
	}

	private boolean checkIfListsMatch(List<? extends EntityWithId<?>> expectedList,
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
	public void testSave() throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, ClassCastException,
			ParserConfigurationException, JAXBException {
		LinkedList<AllTests.TestModel> expected = (LinkedList<AllTests.TestModel>) list.clone();
		expected.add(new AllTests.TestModel("foo@example.com", "password"));
		ColdStorage.save(AllTests.TestModel.class, expected);
		LinkedList<EntityWithId<?>> result = ColdStorage.get(AllTests.TestModel.class);
		assertTrue(checkIfListsMatch(expected, (List<? extends EntityWithId<?>>) result));
	}

}
