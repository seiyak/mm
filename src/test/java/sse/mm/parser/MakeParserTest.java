package sse.mm.parser;

import org.junit.Before;
import org.junit.Test;

public class MakeParserTest {

	private MakeParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new MakeParser("/home/seiyak/Documents/sse/test/infr/Makefile");
	}

	@Test
	public void testModify() {
		parser.modify();
	}

}
