package de.bright_side.brightmarkdown.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import de.bright_side.brightmarkdown.model.BMPosAndTag;

public class BMTextParserLogicTest {
	@Test
	public void findNext_firstItem() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		String text = "My tag1 text";
		BMPosAndTag result = new BMTextParserLogic().findNext(text, 0, tags);
		assertEquals(3, result.getPos());
		assertEquals("tag1", result.getTag());
	}

	@Test
	public void findNext_noMatches() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		String text = "My tag99 string";
		BMPosAndTag result = new BMTextParserLogic().findNext(text, 0, tags);
		assertEquals(null, result);
	}

	@Test
	public void findNext_secondItem() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		String text = "My nice t2 tag1 text";
		BMPosAndTag result = new BMTextParserLogic().findNext(text, 0, tags);
		assertEquals(8, result.getPos());
		assertEquals("t2", result.getTag());
	}
	
	@Test
	public void findNext_longerTagThatOccursFirst() {
		List<String>tags = Arrays.asList("taglong", "tag", "t2", "x");
		String text = "My nice taglong text";
		BMPosAndTag result = new BMTextParserLogic().findNext(text, 0, tags);
		assertEquals(8, result.getPos());
		assertEquals("taglong", result.getTag());
	}
	
	@Test
	public void findNext_longerTagThatOccursSecond() {
		List<String>tags = Arrays.asList("tag", "taglong", "t2", "x");
		String text = "My nice taglong text";
		BMPosAndTag result = new BMTextParserLogic().findNext(text, 0, tags);
		assertEquals(8, result.getPos());
		assertEquals("taglong", result.getTag());
	}
	
	@Test
	public void findPosAfterLeadindSpaces_multipleCases() throws Exception{
		BMTextParserLogic locic = new BMTextParserLogic();
		assertEquals(1, locic.findPosAfterLeadindSpaces("*", 0));
		assertEquals(2, locic.findPosAfterLeadindSpaces("* ", 0));
		assertEquals(2, locic.findPosAfterLeadindSpaces("* text", 0));
		assertEquals(3, locic.findPosAfterLeadindSpaces(" * text", 1));
		assertEquals(5, locic.findPosAfterLeadindSpaces(" *   text", 1));
		assertEquals(6, locic.findPosAfterLeadindSpaces("  *   text", 2));
		assertEquals(4, locic.findPosAfterLeadindSpaces("  - item 3", 2));
		assertEquals(5, locic.findPosAfterLeadindSpaces(" *   ", 1));
	}

	
	@Test
	public void findNext_simple() throws Exception{
		BMTextParserLogic locic = new BMTextParserLogic();
		assertEquals(new BMPosAndTag(0, "*").toString(), locic.findNextSkipSpaces("*", 0).toString());
		assertEquals(new BMPosAndTag(0, "*").toString(), locic.findNextSkipSpaces("* ", 0).toString());
		assertEquals(new BMPosAndTag(1, "*").toString(), locic.findNextSkipSpaces(" *", 0).toString());
		assertEquals(new BMPosAndTag(2, "*").toString(), locic.findNextSkipSpaces("  *", 0).toString());
		assertEquals(null, locic.findNextSkipSpaces("  ", 0));
		assertEquals(new BMPosAndTag(1, "*").toString(), locic.findNextSkipSpaces("x*", 1).toString());
		assertEquals(new BMPosAndTag(1, "*").toString(), locic.findNextSkipSpaces("x* ", 1).toString());
		assertEquals(new BMPosAndTag(2, "*").toString(), locic.findNextSkipSpaces("x *", 1).toString());
		assertEquals(new BMPosAndTag(3, "*").toString(), locic.findNextSkipSpaces("x  *", 1).toString());
		assertEquals(null, locic.findNextSkipSpaces("x  ", 1));
	}

	@Test
	public void removeLeadingChars_normal() {
		BMTextParserLogic locic = new BMTextParserLogic();
		List<Character> charsToRemove = Arrays.asList(' ', '\n');
		assertEquals("hello", locic.removeLeadingChars("hello", 0, charsToRemove));
		assertEquals("hello", locic.removeLeadingChars(" hello", 0, charsToRemove));
		assertEquals("hello", locic.removeLeadingChars("   hello", 0, charsToRemove));
		assertEquals("hello", locic.removeLeadingChars("\nhello", 0, charsToRemove));
		assertEquals("hello", locic.removeLeadingChars("\n hello", 0, charsToRemove));
		assertEquals("hello", locic.removeLeadingChars("x hello", 1, charsToRemove));
	}

	@Test
	public void readUntilLengthOrEnd_normal() {
		BMTextParserLogic locic = new BMTextParserLogic();
		assertEquals("", locic.readUntilLengthOrEnd("hello", 0, 0));
		assertEquals("h", locic.readUntilLengthOrEnd("hello", 0, 1));
		assertEquals("he", locic.readUntilLengthOrEnd("hello", 0, 2));
		assertEquals("hello", locic.readUntilLengthOrEnd("hello", 0, 5));
		assertEquals("hello", locic.readUntilLengthOrEnd("hello", 0, 6));
		assertEquals("hello", locic.readUntilLengthOrEnd("hello", 0, 100));
		assertEquals("", locic.readUntilLengthOrEnd("hello", 1, 0));
		assertEquals("e", locic.readUntilLengthOrEnd("hello", 1, 1));
		assertEquals("el", locic.readUntilLengthOrEnd("hello", 1, 2));
		assertEquals("ello", locic.readUntilLengthOrEnd("hello", 1, 4));
		assertEquals("ello", locic.readUntilLengthOrEnd("hello", 1, 5));
		assertEquals("ello", locic.readUntilLengthOrEnd("hello", 1, 100));
	}
	

}
