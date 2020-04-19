package de.bright_side.brightmarkdown.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.bright_side.brightmarkdown.logic.BMSectionParserLogic;

public class BMUtilTest {
	private void log(String message) {
		if (TestingConstants.TEST_BM_UTIL_LOGGING_ACTIVE) {
			System.out.println("TestBMUtil> " + message);
		}
	}
	
	@Test
	public void escapeSpecialCharacters_normal() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is _italic_.\n---\nNice?";
		String result = BMUtil.escapeSpecialCharacters(input).replace("\r", "").replace("\n", "");
		String expected = "\\# Title\\* item 1\\* item 2\\* item 3This text is \\_italic\\_\\.\\-\\-\\-Nice?";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	

	
}
