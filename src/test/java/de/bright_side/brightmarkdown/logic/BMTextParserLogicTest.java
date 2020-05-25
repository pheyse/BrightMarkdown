package de.bright_side.brightmarkdown.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.bright_side.brightmarkdown.logic.BMTextParserLogic.MatchConfirmer;
import de.bright_side.brightmarkdown.model.BMPosAndTag;

public class BMTextParserLogicTest {
	private String toString(BMPosAndTag posAndTag) {
		if (posAndTag == null) {
			return "null";
		}
		return posAndTag.toString();
	}
	
	
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
	public void findNext_endOfString() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		assertEquals(null, new BMTextParserLogic().findNext("text", 4, tags));
		assertEquals(null, new BMTextParserLogic().findNext("text", 5, tags));
		assertEquals(null, new BMTextParserLogic().findNext("text", 6, tags));
		assertEquals(null, new BMTextParserLogic().findNext("fetch(", 6, tags));
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
	public void findNext_ignoreCase() throws Exception{
		BMTextParserLogic locic = new BMTextParserLogic();
		
		Collection<String> tags = Arrays.asList("ell", "cat");
		Collection<String> tagsUpperCase = Arrays.asList("ELL", "CAT");
		assertEquals(null, locic.findNext("HELLO", 0, false, tags));
		assertEquals(null, locic.findNext("Hello", 0, false, tagsUpperCase));
		assertEquals(new BMPosAndTag(1, "ell").toString(), locic.findNext("Hello", 0, false, tags).toString());
		assertEquals(new BMPosAndTag(1, "ell").toString(), locic.findNext("HELLO", 0, true, tags).toString());
		assertEquals(new BMPosAndTag(1, "ELL").toString(), locic.findNext("hello", 0, true, tagsUpperCase).toString());
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
	
	@Test
	public void skipCharOccurences_normal() {
		BMTextParserLogic locic = new BMTextParserLogic();
		assertEquals(3, locic.posAfterCharOccurences("xxxYZ", 0, 'x'));
		assertEquals(3, locic.posAfterCharOccurences("xxxYZ", 1, 'x'));
		assertEquals(3, locic.posAfterCharOccurences("xxxYZ", 2, 'x'));
		assertEquals(3, locic.posAfterCharOccurences("xxxYZ", 3, 'x'));
		assertEquals(4, locic.posAfterCharOccurences("xxxYZ", 4, 'x'));
		assertEquals(1, locic.posAfterCharOccurences("abc", 1, 'x'));
	}

	
	@Test
	public void getCharBeforeOrDefault_normal() {
		BMTextParserLogic locic = new BMTextParserLogic();
		assertEquals('?', locic.getCharBeforeOrDefault("abcde", -1, '?'));
		assertEquals('?', locic.getCharBeforeOrDefault("abcde", 0, '?'));
		assertEquals('a', locic.getCharBeforeOrDefault("abcde", 1, '?'));
		assertEquals('b', locic.getCharBeforeOrDefault("abcde", 2, '?'));
		assertEquals('?', locic.getCharBeforeOrDefault("abcde", 99, '?'));
	}

	@Test
	public void getCharAfterOrDefault_normal() {
		BMTextParserLogic locic = new BMTextParserLogic();
		assertEquals('?', locic.getCharAfterOrDefault("abcde", -2, '?'));
		assertEquals('a', locic.getCharAfterOrDefault("abcde", -1, '?'));
		assertEquals('b', locic.getCharAfterOrDefault("abcde", 0, '?'));
		assertEquals('c', locic.getCharAfterOrDefault("abcde", 1, '?'));
		assertEquals('e', locic.getCharAfterOrDefault("abcde", 3, '?'));
		assertEquals('?', locic.getCharAfterOrDefault("abcde", 4, '?'));
		assertEquals('?', locic.getCharAfterOrDefault("abcde", 5, '?'));
	}
	
	@Test
	public void findNext_matchConfirmer() throws Exception{
		final BMTextParserLogic logic = new BMTextParserLogic();
		
		MatchConfirmer matchConfirmer = new MatchConfirmer() {
			@Override
			public boolean confirmMatch(String text, int pos, String tag) {
				char charBefore = logic.getCharBeforeOrDefault(text, pos, '*');
				if (Character.isLetterOrDigit(charBefore)){
					return false;
				}
				char charAfter = logic.getCharAfterOrDefault(text, pos + tag.length() - 1, '*');
				if (Character.isLetterOrDigit(charAfter)){
					return false;
				}
				
				return true;
			}
		};
		
		Collection<String> tags = Arrays.asList("int", "int32");
		assertEquals(new BMPosAndTag(0, "int").toString(), toString(logic.findNext("int", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(1, "int").toString(), toString(logic.findNext(" int", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(1, "int").toString(), toString(logic.findNext(" int ", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(1, "int").toString(), toString(logic.findNext(" int", 1, false, tags, matchConfirmer)));
		assertEquals(null, logic.findNext(" int", 2, false, tags));
		assertEquals(new BMPosAndTag(0, "int32").toString(), toString(logic.findNext("int32", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(1, "int32").toString(), toString(logic.findNext(" int32", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(1, "int").toString(), toString(logic.findNext(" int int32", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(0, "int").toString(), toString(logic.findNext("int int32", 0, false, tags, matchConfirmer)));
		
		assertEquals(new BMPosAndTag(5, "int").toString(), toString(logic.findNext("hint int", 0, false, tags, matchConfirmer)));
		assertEquals(new BMPosAndTag(5, "int").toString(), toString(logic.findNext("intx int", 0, false, tags, matchConfirmer)));
	}

	
}
