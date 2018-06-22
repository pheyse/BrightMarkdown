package de.bright_side.brightmarkdown;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestBrightMarkdownFormattingParser {

	private static String formattedTextSectionToString(BrightMarkdownSection section) {
		String result = "";
		result += "[b:" + (section.isBold() ? "y" : "n");
		result += ",i:" + (section.isItalic() ? "y" : "n");
		result += ",u:" + (section.isUnderline() ? "y" : "n");
		result += ",s:" + (section.isStrikeThrough() ? "y" : "n");
		result += ",c:" + (section.getColor() == null ? "-" : section.getColor());
		result += ",bc:" + (section.getBackgroundColor() == null ? "-" : section.getBackgroundColor());
		result += "][" + section.getRawText() + "]";
		return result;
	}
	@Test
	public void test_parseFormattingTags_normal() {
		String text = "Test *text with _formatting*_!"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals(4, result.size());
		assertEquals("*", result.get(5));
		assertEquals("_", result.get(16));
		assertEquals("*", result.get(27));
		assertEquals("_", result.get(28));
	}


	@Test
	public void test_parseFormattingTags_colors() {
		String text = "Test *text with {color:red} color{color}*!"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals(4, result.size());
		assertEquals("*", result.get(5));
		assertEquals("{color:red}", result.get(16));
		assertEquals("{color}", result.get(33));
		assertEquals("*", result.get(40));
	}

	@Test
	public void test_parseFormattingTags_noFormatting() {
		String text = "Test text without formatting!"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals(0, result.size());
	}

	@Test
	public void test_parseFormattingTags_formattingAtBeginning() {
		String text = "~Test *text with _formatting*_!"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals(5, result.size());
		assertEquals("~", result.get(0));
		assertEquals("*", result.get(6));
		assertEquals("_", result.get(17));
		assertEquals("*", result.get(28));
		assertEquals("_", result.get(29));
	}
	
	@Test
	public void test_parseFormattingTags_formattingAtEnd() {
		String text = "Test *text with _formatting*_!~"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals(5, result.size());
		assertEquals("*", result.get(5));
		assertEquals("_", result.get(16));
		assertEquals("*", result.get(27));
		assertEquals("_", result.get(28));
		assertEquals("~", result.get(30));
	}
	
	@Test
	public void test_parseFormattingTags_unclosedColorTag() {
		String text = "Test *text with {color:red color*!"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals(2, result.size());
		assertEquals("*", result.get(5));
		assertEquals("*", result.get(32));
	}

	@Test
	public void test_parseFormattingTags_ignoreWithinWord() {
		String text = "Time *is* : 2018_04_19"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
  		assertEquals("*", result.get(5));
		assertEquals("*", result.get(8));
	}

	@Test
	public void test_parseFormattingTags_ignoreWithinWordBeforeColum() {
		String text = "Time *is*: 2018_04_19"; 
		Map<Integer, String> result = new BrightMarkdownFormattingParser().parseFormattingTags(text);
		assertEquals("*", result.get(5));
		assertEquals("*", result.get(8));
	}

	@Test
	public void test_getCharBefore_normal() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharBefore(text, 1);
		assertEquals(Character.valueOf('0'), result);
	}

	@Test
	public void test_getCharBefore_end() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharBefore(text, 3);
		assertEquals(Character.valueOf('2'), result);
	}
	
	@Test
	public void test_getCharBefore_oneAfterEnd() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharBefore(text, 4);
		assertEquals(Character.valueOf('3'), result);
	}
	
	@Test
	public void test_getCharBefore_twoAfterEnd() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharBefore(text, 5);
		assertEquals(null, result);
	}
	
	@Test
	public void test_getCharBefore_beginning() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharBefore(text, 0);
		assertEquals(null, result);
	}

	@Test
	public void test_getCharAfter_normal() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharAfter(text, 1);
		assertEquals(Character.valueOf('2'), result);
	}
	
	@Test
	public void test_getCharAfter_end() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharAfter(text, 3);
		assertEquals(null, result);
	}
	
	@Test
	public void test_getCharAfter_oneBeforeStart() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharAfter(text, -1);
		assertEquals(null, result);
	}
	
	@Test
	public void test_getCharAfter_beginning() {
		String text = "0123"; 
		Character result = new BrightMarkdownFormattingParser().getCharAfter(text, 0);
		assertEquals(Character.valueOf('1'), result);
	}

	@Test
	public void test_getCharBefore_beginning2() {
		String text = "It _is_ a test!"; 
		Character result = new BrightMarkdownFormattingParser().getCharBefore(text, 3);
		assertEquals(Character.valueOf(' '), result);
	}

	
	@Test
	public void test_isCharPartOfWord_StartOfWord() {
		String text = "It _is_ a test!"; 
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 3);
		assertEquals(false, result);
	}

	
	@Test
	public void test_isCharPartOfWord_EndOfWord() {
		String text = "It _is_ a test!"; 
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 6);
		assertEquals(false, result);
	}
	
	@Test
	public void test_isCharPartOfWord_MiddleOfWord1() {
		String text = "It _is_a a test!"; 
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 6);
		assertEquals(true, result);
	}
	
	@Test
	public void test_isCharPartOfWord_MiddleOfWord2() {
		String text = "It _is_a_ a test!"; 
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 6);
		assertEquals(true, result);
	}
	
	@Test
	public void test_isCharPartOfWord_BeginingOfExpession() {
		String text = "_It is a test!_"; 
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 0);
		assertEquals(false, result);
	}
	@Test
	public void test_isCharPartOfWord_EndOfExpession() {
		String text = "_It is a test!_"; 
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 14);
		assertEquals(false, result);
	}

	@Test
	public void test_isCharPartOfWord_MultipleMarkersEnd() {
		String text = "Test *text with _formatting*_!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 28);
		assertEquals(false, result);
	}


	@Test
	public void test_isCharPartOfWord_MultipleMarkersBeforeEnd() {
		String text = "Test *text with _formatting*_!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 27);
		assertEquals(false, result);
	}

	@Test
	public void test_isCharPartOfWord_MultipleMarkersBeforeEndLong() {
		String text = "Test *text with _formatting*_~!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 27);
		assertEquals(false, result);
	}

	@Test
	public void test_isCharPartOfWord_MultipleMarkersStartLong1() {
		String text = "Test *text with _*~formatting*_~!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 16);
		assertEquals(false, result);
	}
	
	@Test
	public void test_isCharPartOfWord_MultipleMarkersStartLong2() {
		String text = "Test *text with _*~formatting*_~!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 17);
		assertEquals(false, result);
	}
	
	@Test
	public void test_isCharPartOfWord_MultipleMarkersStartLong3() {
		String text = "Test *text with _*~formatting*_~!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 18);
		assertEquals(false, result);
	}
	
	@Test
	public void test_isCharPartOfWord_MultipleMarkersMiddle() {
		String text = "Test *text withx_*~formatting*_~!";
		
		boolean result = new BrightMarkdownFormattingParser().isCharPartOfWord(text, 17);
		assertEquals(true, result);
	}
	
	@Test
	public void test_createFormattedSections_boldInMiddle() {
		String text = "It's a *formatting* test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:y,i:n,u:n,s:n,c:-,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}

	@Test
	public void test_createFormattedSections_boldBeginning() {
		String text = "*It's* a formatting test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(2, result.size());
		assertEquals("[b:y,i:n,u:n,s:n,c:-,bc:-][It's]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ a formatting test!]", formattedTextSectionToString(result.get(1)));
	}
	
	@Test
	public void test_createFormattedSections_boldEnding() {
		String text = "It's a formatting *test!*";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		for (BrightMarkdownSection i: result) {
			System.out.println("item 1: " + formattedTextSectionToString(i));
		}
		assertEquals(2, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a formatting ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:y,i:n,u:n,s:n,c:-,bc:-][test!]", formattedTextSectionToString(result.get(1)));
	}

	@Test
	public void test_createFormattedSections_boldInItalic() {
		String text = "It's _a *formatting* test_!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(5, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:y,u:n,s:n,c:-,bc:-][a ]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:y,i:y,u:n,s:n,c:-,bc:-][formatting]", formattedTextSectionToString(result.get(2)));
		assertEquals("[b:n,i:y,u:n,s:n,c:-,bc:-][ test]", formattedTextSectionToString(result.get(3)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][!]", formattedTextSectionToString(result.get(4)));
	}
	
	@Test
	public void test_createFormattedSections_italicInBold() {
		String text = "It's *a _formatting_ test*!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(5, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:y,i:n,u:n,s:n,c:-,bc:-][a ]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:y,i:y,u:n,s:n,c:-,bc:-][formatting]", formattedTextSectionToString(result.get(2)));
		assertEquals("[b:y,i:n,u:n,s:n,c:-,bc:-][ test]", formattedTextSectionToString(result.get(3)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][!]", formattedTextSectionToString(result.get(4)));
	}
	
	@Test
	public void test_createFormattedSections_namedColorInMiddle() {
		String text = "It's a {c:red}formatting{c} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:red,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}

	@Test
	public void test_createFormattedSections_hexColorInMiddle() {
		String text = "It's a {c:#ff0000}formatting{c} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:#ff0000,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}

	@Test
	public void test_createFormattedSections_brokenNamedColorInMiddle() {
		String text = "It's a {c:xyz}formatting{c} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}

	@Test
	public void test_createFormattedSections_brokenHexColorInMiddle() {
		String text = "It's a {c:#ff00zz}formatting{c} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}

	@Test
	public void test_createFormattedSections_namedColorLongTagInMiddle() {
		String text = "It's a {color:red}formatting{color} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:red,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}

	@Test
	public void test_createFormattedSections_namedBackgroundColorLongTagInMiddle() {
		String text = "It's a {bg-color:red}formatting{bg-color} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:red][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}
	

	@Test
	public void test_createFormattedSections_namedBackgroundColorInMiddle() {
		String text = "It's a {bc:red}formatting{bc} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:red][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}
	
	@Test
	public void test_createFormattedSections_namedColorBeginning() {
		String text = "{c:red}It's a formatting{c} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(2, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:red,bc:-][It's a formatting]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(1)));
	}
	
	@Test
	public void test_createFormattedSections_namedColorAndBoldInMiddle() {
		String text = "It's a *{c:red}formatting*{c} test!";
		List<BrightMarkdownSection> result = new BrightMarkdownFormattingParser().createFormattedSections(text);
		assertEquals(3, result.size());
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][It's a ]", formattedTextSectionToString(result.get(0)));
		assertEquals("[b:y,i:n,u:n,s:n,c:red,bc:-][formatting]", formattedTextSectionToString(result.get(1)));
		assertEquals("[b:n,i:n,u:n,s:n,c:-,bc:-][ test!]", formattedTextSectionToString(result.get(2)));
	}
	
}
