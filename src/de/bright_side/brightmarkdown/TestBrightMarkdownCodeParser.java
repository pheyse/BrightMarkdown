package de.bright_side.brightmarkdown;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

public class TestBrightMarkdownCodeParser {
	
	private List<BrightMarkdownSection> createJavaSections(String text) {
		BrightMarkdownSection parentSection = BrightMarkdownUtil.createSection(null, MDType.RAW_LINE, "");
		return new BrightMarkdownCodeParser().createSections(parentSection, text, new BrightMarkdownCodeFormatDefinition().createCodeFormats().get("java"), BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
	}
	
	private List<BrightMarkdownSection> createXMLSections(String text) {
		BrightMarkdownSection parentSection = BrightMarkdownUtil.createSection(null, MDType.RAW_LINE, "");
		return new BrightMarkdownCodeParser().createSections(parentSection, text, new BrightMarkdownCodeFormatDefinition().createCodeFormats().get("xml"), BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
	}
	
	private List<BrightMarkdownSection> createUnknownFormatSections(String text) {
		BrightMarkdownSection parentSection = BrightMarkdownUtil.createSection(null, MDType.RAW_LINE, "");
		return new BrightMarkdownCodeParser().createSections(parentSection, text, new BrightMarkdownCodeFormatDefinition().createCodeFormats().get("unknown"), BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
	}
	
	public String toString(List<BrightMarkdownSection> sections, int index) {
		return sections.get(index).getType() + ": " + sections.get(index).getRawText();
	}
	
	@Test
	public void test_createSections_javaNoFormat() {
		String text = "x = 7;";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		assertEquals(1, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND, result.get(0).getType());
		assertEquals(text, result.get(0).getRawText());
	}

	@Test
	public void test_createSections_javaNoText() {
		String text = "";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		assertEquals(0, result.size());
	}
	
	@Test
	public void test_createSections_javaKeyword() {
		String text = "int x = 7;";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(2, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7;", toString(result, index ++));
	}
	
	@Test
	public void test_createSections_javaKeywordInMiddle() {
		String text = "Integer x = new Integer(3);";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "Integer x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "new", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " Integer(3);", toString(result, index ++));
	}
	
	@Test
	public void test_createSections_javaString() {
		String text = "x = \"hi\";";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"hi\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";", toString(result, index ++));
		
	}

	@Test
	public void test_createSections_javaEmptyString() {
		String text = "x = \"\";";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";", toString(result, index ++));
		
	}

	@Test
	public void test_createSections_javaLineCommentEnd() {
		String text = "int x = 7; //comment";
		List<BrightMarkdownSection> result = createJavaSections(text);

		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//comment", toString(result, index ++));
	}

	@Test
	public void test_createSections_javaLineCommentMiddle() {
		String text = "int x = 7; //comment\na = b;";
		List<BrightMarkdownSection> result = createJavaSections(text);

		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//comment", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void test_createSections_javaLineCommentEmpty() {
		String text = "int x = 7; //\na = b;";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void test_createSections_javaBlockCommentMiddle() {
		String text = "int x = 7; /*comment line 1\nline 2*/\na = b;";
		List<BrightMarkdownSection> result = createJavaSections(text);

		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "/*comment line 1" + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "line 2*/", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void test_createSections_javaBlockCommentEmpty() {
		String text = "int x = 7; /**/\na = b;";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " x = 7; ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "/**/", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "a = b;", toString(result, index ++));
	}

	@Test
	public void test_createSections_xmlNoFormat() {
		String text = "test";
		List<BrightMarkdownSection> result = createXMLSections(text);
		
		assertEquals(1, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND, result.get(0).getType());
		assertEquals(text, result.get(0).getRawText());
	}

	@Test
	public void test_createSections_xmlString() {
		String text = "x = \"hi\"";
		List<BrightMarkdownSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(2, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"hi\"", toString(result, index ++));
		
	}

	@Test
	public void test_createSections_xmlEmptyString() {
		String text = "x = \"\";";
		List<BrightMarkdownSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(3, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + "x = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";", toString(result, index ++));
		
	}

	@Test
	public void test_createSections_xmlTag() {
		String text = "<tag1 value=\"x\">";
		List<BrightMarkdownSection> result = createXMLSections(text);

		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + ">", toString(result, index ++));
	}

	@Test
	public void test_createSections_xmlTagWithInnerEnd() {
		String text = "<tag1 value=\"x\"/>";
		List<BrightMarkdownSection> result = createXMLSections(text);
		
		int index = 0;
		assertEquals(4, result.size());
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "<tag1", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " value=", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"x\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_TAG + ": " + "/>", toString(result, index ++));
	}

	@Test
	public void test_createSections_xmlTagWithOuterEnd() {
		String text = "<tag1 value=\"x\">hello</tag1>";
		List<BrightMarkdownSection> result = createXMLSections(text);

		System.out.println("result = " + result);
		
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
	public void test_createSections_xmlBlockCommentMiddle() {
		String text = "<tag1 value=\"x\"><tag2 value=\"y\"><!--comment --><tag3 x='5'/>";
		List<BrightMarkdownSection> result = createXMLSections(text);

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
	public void test_createSections_xmlBlockCommentEmpty() {
		String text = "<tag1 value=\"x\"><tag2 value=\"y\"><!----><tag3 x='5'/>";
		List<BrightMarkdownSection> result = createXMLSections(text);
		
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
	public void test_createSections_unknownFormat() {
		String text = "some text";
		List<BrightMarkdownSection> result = createUnknownFormatSections(text);
		
		int index = 0;
		assertEquals(1, result.size());
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + text, toString(result, index ++));
		
	}

	
	@Test
	public void test_createSections_javaNormalWithIndent() {
		String text = "if (x == 4) {\n    //commented out\n    int y = \"hi!\";\n    /*block\ncomment*/\n}\n    a = b;";
		List<BrightMarkdownSection> result = createJavaSections(text);
		
		int index = 0;
		assertEquals(10, result.size());
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "if", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " (x == 4) {" + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "//commented out", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_KEYWORD + ": " + "int", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + " y = ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_STRING + ": " + "\"hi!\"", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + ";" + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    ", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMENT + ": " + "/*block" + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "comment*/", toString(result, index ++));
		assertEquals(MDType.CODE_BLOCK_COMMAND + ": " + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "}" + BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK + "    a = b;", toString(result, index ++));
	}
	

	
}
