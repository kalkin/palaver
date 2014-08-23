package de.xsrc.palaver.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractModelTest {

	@Test
	public void test() {
		AbstractModel result = new AbstractModel();
		if(!(result.getCreatedAt() instanceof Long)){
			fail();
		}
	}

}
