package de.bright_side.brightmarkdown.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.base.TestingConstants;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

public class BMCodeParserTest {
	private void log(String message) {
		if (TestingConstants.TEST_BM_CODE_PARSER_LOGGING_ACTIVE) {
			System.out.println("TestBMCodeParser> " + message);
		}
	}
	
	private List<BMSection> createJavaSections(String text) {
		BMSection parentSection = BMUtil.createSection(null, MDType.RAW_LINE, "");
		return new BMCodeParser().createSections(parentSection, text, new BMDefaultCodeFormatCreator().createCodeFormats().get("java"), BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
	}
	
	private List<BMSection> createXMLSections(String text) {
		BMSection parentSection = BMUtil.createSection(null, MDType.RAW_LINE, "");
		return new BMCodeParser().createSections(parentSection, text, new BMDefaultCodeFormatCreator().createCodeFormats().get("xml"), BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
	}
	
	private List<BMSection> createUnknownFormatSections(String text) {
		BMSection parentSection = BMUtil.createSection(null, MDType.RAW_LINE, "");
		return new BMCodeParser().createSections(parentSection, text, new BMDefaultCodeFormatCreator().createCodeFormats().get("unknown"), BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
	}
	
	public String toString(List<BMSection> sections, int index) {
		return sections.get(index).getType() + ": " + sections.get(index).getRawText();
	}

	@Test
	public void createSections_javaNoFormat() {
		String text = "x = 7;";
		List<BMSection> result = createJavaSections(text);
		
		assertEquals(1, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND, result.get(0).getType());
		assertEquals(text, result.get(0).getRawText());
	}

	@Test
	public void createSections_javaNoText() {
		String text = "";
		List<BMSection> result = createJavaSections(text);
		
		assertEquals(0, result.size());
	}
	
	@Test
	public void createSections_javaKeyword() {
		String text = "int x = 7;";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(2, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7;", toString(result, index ++));
	}
	
	@Test
	public void createSections_javaKeywordInMiddle() {
		String text = "Integer x = new Integer(3);";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "Integer x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "new", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " Integer(3);", toString(result, index ++));
	}
	
	@Test
	public void createSections_javaString() {
		String text = "x = \"hi\";";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"hi\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";", toString(result, index ++));
		
	}

	@Test
	public void createSections_javaEmptyString() {
		String text = "x = \"\";";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";", toString(result, index ++));
		
	}

	@Test
	public void createSections_javaLineCommentEnd() {
		String text = "int x = 7; //comment";
		List<BMSection> result = createJavaSections(text);

		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//comment", toString(result, index ++));
	}

	@Test
	public void createSections_javaLineCommentMiddle() {
		String text = "int x = 7; //comment\na = b;";
		List<BMSection> result = createJavaSections(text);

		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//comment", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void createSections_javaLineCommentEmpty() {
		String text = "int x = 7; //\na = b;";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void createSections_javaBlockCommentMiddle() {
		String text = "int x = 7; /*comment line 1\nline 2*/\na = b;";
		List<BMSection> result = createJavaSections(text);

		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "/*comment line 1" + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "line 2*/", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void createSections_javaBlockCommentEmpty() {
		String text = "int x = 7; /**/\na = b;";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "/**/", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void createSections_xmlNoFormat() {
		String text = "test";
		List<BMSection> result = createXMLSections(text);
		
		assertEquals(1, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND, result.get(0).getType());
		assertEquals(text, result.get(0).getRawText());
	}

	@Test
	public void createSections_xmlString() {
		String text = "x = \"hi\"";
		List<BMSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(2, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"hi\"", toString(result, index ++));
		
	}

	@Test
	public void createSections_xmlEmptyString() {
		String text = "x = \"\";";
		List<BMSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";", toString(result, index ++));
		
	}

	@Test
	public void createSections_xmlTag() {
		String text = "<tag1 value=\"x\">";
		List<BMSection> result = createXMLSections(text);

		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
	}

	@Test
	public void createSections_xmlTagWithInnerEnd() {
		String text = "<tag1 value=\"x\"/>";
		List<BMSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "/>", toString(result, index ++));
	}

	@Test
	public void createSections_xmlTagWithOuterEnd() {
		String text = "<tag1 value=\"x\">hello</tag1>";
		List<BMSection> result = createXMLSections(text);

		log("result = " + result);
		
		int index = 0;
		assertEquals(6, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "hello", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "</tag1>", toString(result, index ++));
	}
	
	
	@Test
	public void createSections_xmlBlockCommentMiddle() {
		String text = "<tag1 value=\"x\"><tag2 value=\"y\"><!--comment --><tag3 x='5'/>";
		List<BMSection> result = createXMLSections(text);

		int index = 0;
		assertEquals(13, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag2", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"y\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "<!--comment -->", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag3", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "'5'", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "/>", toString(result, index ++));
	}

	@Test
	public void createSections_xmlBlockCommentEmpty() {
		String text = "<tag1 value=\"x\"><tag2 value=\"y\"><!----><tag3 x='5'/>";
		List<BMSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(13, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag2", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"y\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "<!---->", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag3", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "'5'", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "/>", toString(result, index ++));
	}

	@Test
	public void createSections_unknownFormat() {
		String text = "some text";
		List<BMSection> result = createUnknownFormatSections(text);
		
		int index = 0;
		assertEquals(1, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + text, toString(result, index ++));
		
	}

	
	@Test
	public void createSections_javaNormalWithIndent() {
		String text = "if (x == 4) {\n    //commented out\n    int y = \"hi!\";\n    /*block\ncomment*/\n}\n    a = b;";
		List<BMSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(10, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "if", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " (x == 4) {" + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//commented out", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " y = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"hi!\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";" + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "/*block" + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "comment*/", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "}" + BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    a = b;", toString(result, index ++));
	}

	@Test
	public void applySpecialFormat_highlight() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, BMConstants.CODE_BLOCK_SPECIAL_FORMAT_HIGHLIGHT);
		
		assertEquals(true, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(BMConstants.CODE_BLOCK_HIGHLIGHT_BACKGROUND_COLOR, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_placeholder() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, BMConstants.CODE_BLOCK_SPECIAL_FORMAT_PLACEHOLDER);
		
		assertEquals(true, section.isBold());
		assertEquals(true, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(BMConstants.CODE_BLOCK_PLACEHOLDER_FOREGROUND_COLOR, section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_forground() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "c:green");
		
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals("green", section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_background() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "bc:green");
		
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals("green", section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_bold() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "b");
		
		assertEquals(true, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_italic() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "i");
		
		assertEquals(false, section.isBold());
		assertEquals(true, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_underline() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "u");
		
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(true, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_combination() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "b u i c:blue bc:red");
		
		assertEquals(true, section.isBold());
		assertEquals(true, section.isItalic());
		assertEquals(true, section.isUnderline());
		assertEquals("blue", section.getColor());
		assertEquals("red", section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_combinationExtraSpaces() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, " b   u i  c:blue bc:red ");
		
		assertEquals(true, section.isBold());
		assertEquals(true, section.isItalic());
		assertEquals(true, section.isUnderline());
		assertEquals("blue", section.getColor());
		assertEquals("red", section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_forgroundEmpty() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "c:");
		
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}

	@Test
	public void applySpecialFormat_backgroundEmpty() {
		BMSection section = new BMSection();
		new BMCodeParser().applySpecialFormat(section, "bc:");
		
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
	}
	
	@Test
	public void processSpecialFormatSection_highlight() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!hl!hello!!!Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(20, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("hello", section.getRawText());
		assertEquals(true, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(BMConstants.CODE_BLOCK_HIGHLIGHT_BACKGROUND_COLOR, section.getBackgroundColor());
		assertEquals(MDType.FORMATTED_TEXT, section.getType());
	}
	
	@Test
	public void processSpecialFormatSection_multipleItems() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!c:green b i!hello!!!Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(29, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("hello", section.getRawText());
		assertEquals(true, section.isBold());
		assertEquals(true, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals("green", section.getColor());
		assertEquals(null, section.getBackgroundColor());
		assertEquals(MDType.FORMATTED_TEXT, section.getType());
	}
	
	@Test
	public void processSpecialFormatSection_elipse() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!...Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(12, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("[...]", section.getRawText());
		assertEquals(true, section.isBold());
		assertEquals(true, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_ELIPSE_FORGROUND_COLOR, section.getColor());
		assertEquals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_ELIPSE_BACKGROUND_COLOR, section.getBackgroundColor());
		section.setBold(true);
		section.setItalic(true);
		
		assertEquals(MDType.FORMATTED_TEXT, section.getType());
		
	}
	
	@Test
	public void processSpecialFormatSection_escape4() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!\\!!!!Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(14, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("!!!!", section.getRawText());
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
		assertEquals(MDType.CODE_BLOCK_COMMAND, section.getType());
		
	}
	
	@Test
	public void processSpecialFormatSection_escape3() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!\\!!!Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(13, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("!!!", section.getRawText());
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
		assertEquals(MDType.CODE_BLOCK_COMMAND, section.getType());
		
	}

	@Test
	public void processSpecialFormatSection_escape2() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!\\!!Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(12, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("!!", section.getRawText());
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
		assertEquals(MDType.CODE_BLOCK_COMMAND, section.getType());
		
	}
	
	@Test
	public void processSpecialFormatSection_escape0() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!\\Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(10, resultPos);
		assertEquals(0, sections.size());
	}
	
	@Test
	public void processSpecialFormatSection_broken() {
		//: prepare
		BMSection parent = new BMSection();
		List<BMSection> sections = new ArrayList<BMSection>();
		String text = "BlaBla!!!Bla";
		int startPos = 6;
		
		//: action
		int resultPos = new BMCodeParser().processSpecialFormatSection(parent, text, startPos, sections, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
		
		//: check
		assertEquals(9, resultPos);
		assertEquals(1, sections.size());
		BMSection section = sections.get(0);
		assertEquals("!!!", section.getRawText());
		assertEquals(false, section.isBold());
		assertEquals(false, section.isItalic());
		assertEquals(false, section.isUnderline());
		assertEquals(null, section.getColor());
		assertEquals(null, section.getBackgroundColor());
		assertEquals(MDType.CODE_BLOCK_COMMAND, section.getType());
		
	}
	
	
}
