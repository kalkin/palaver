package de.xsrc.palaver.utils;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.xsrc.palaver.model.Account;

public class ColdStorageTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() throws IOException {
		LinkedHashSet<Account> result = ColdStorage.get(Account.class);
		assertTrue(result.add(new Account()));
	}

	@Test
	public void testSet() throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, ClassCastException,
			ParserConfigurationException, JAXBException {
		LinkedHashSet<Account> result = ColdStorage.get(Account.class);
		result.add(new Account("alice@example.com", "password"));
		ColdStorage.save(Account.class, new LinkedList<Account>(result));
		result = ColdStorage.get(Account.class);
		boolean found = false;
		for (Account account : result) {
			if (account.getJid().equals("alice@example.com")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

}
