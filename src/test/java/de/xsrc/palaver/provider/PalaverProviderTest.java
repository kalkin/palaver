package de.xsrc.palaver.provider;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Before;
import org.junit.Test;

import de.xsrc.palaver.model.Palaver;

public class PalaverProviderTest {

	ObservableList<Palaver> list;
	private ObservableList<Palaver> result;

	@Before
	public void setUp() {
		list = FXCollections.observableList(new LinkedList<Palaver>());
		list.add(new Palaver("hans@example.com", "alice@example.com"));
		list.add(new Palaver("hans@example.com", "bob@example.com"));
		list.add(new Palaver("hans@example.com", "eve@example.com"));
		Palaver p = new Palaver("hans@example.com", "dave@example.com");
		p.setClosed(true);
		list.add(p);
		//pp = new PalaverProvider(this.list);
	//	result = pp.getOpenPalaver();
	}

	@Test
	public void shoudlOnlyShowNotClosedPalavers() {
		assertEquals(3, result.size() );
	}

	@Test
	public void addNewPalvaerToOriginalList() {
		list.add(new Palaver("hans@example.com", "bar@example.com"));
		assertEquals(4, result.size());
	}
	
	@Test
	public void testAddPalaver(){
		result.add(new Palaver("hans@example.com", "foo@example.com"));
		assertEquals(4, result.size());
	}
	
	@Test
	public void testClosePalaver(){
		result.remove(0);
		assertEquals(2, result.size());
		System.out.println(list.filtered(p -> !p.getClosed()));
		assertEquals(2, list.filtered(p -> !p.getClosed()).size());
	}
}
