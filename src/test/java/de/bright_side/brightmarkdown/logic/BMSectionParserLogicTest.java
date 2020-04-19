package de.bright_side.brightmarkdown.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.base.TestingConstants;
import de.bright_side.brightmarkdown.model.BMLevelAndTitle;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

public class BMSectionParserLogicTest {
	private void log(String message) {
		if (TestingConstants.TEST_BM_SECTION_PARSER_LOGIC_LOGGING_ACTIVE) {
			System.out.println("TestBMSectionParserLogic> " + message);
		}
	}
	
	public static String toString(BMSection section){
		return BMUtil.toString(section);
	}
	
	@Test
	public void findMatchingLevel_normal() throws Exception{
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		levelToIndentMap.put(1, 0);
		levelToIndentMap.put(2, 3);
		levelToIndentMap.put(3, 7);
		levelToIndentMap.put(4, 10);
		Integer result = new BMSectionParserLogic().findMatchingLevel(levelToIndentMap, 8);
		assertEquals(3, result.intValue());
	}

	@Test
	public void findMatchingLevel_lessIndentThanLevel1() throws Exception{
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		levelToIndentMap.put(1, 2);
		levelToIndentMap.put(2, 5);
		levelToIndentMap.put(3, 8);
		levelToIndentMap.put(4, 12);
		Integer result = new BMSectionParserLogic().findMatchingLevel(levelToIndentMap, 1);
		assertEquals(1, result.intValue());
	}
	
	@Test
	public void findMatchingLevel_directMatch() throws Exception{
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		levelToIndentMap.put(1, 2);
		levelToIndentMap.put(2, 5);
		levelToIndentMap.put(3, 8);
		levelToIndentMap.put(4, 12);
		Integer result = new BMSectionParserLogic().findMatchingLevel(levelToIndentMap, 8);
		assertEquals(3, result.intValue());
	}

	@Test
	public void readListItemIndet_multipleCases() throws Exception{
		List<String> icl = BMConstants.BULLET_POINT_INDICATORS_CHARS_LIST;
		BMSectionParserLogic logic = new BMSectionParserLogic();
		assertEquals(0, logic.readListItemIndet("*", icl).intValue());
		assertEquals(0, logic.readListItemIndet("* ", icl).intValue());
		assertEquals(0, logic.readListItemIndet("* text", icl).intValue());
		assertEquals(1, logic.readListItemIndet(" * text", icl).intValue());
		assertEquals(1, logic.readListItemIndet(" *   text", icl).intValue());
		assertEquals(2, logic.readListItemIndet("  *   text", icl).intValue());
		assertEquals(1, logic.readListItemIndet(" *   ", icl).intValue());
		assertEquals(null, logic.readListItemIndet("**", icl));
		assertEquals(null, logic.readListItemIndet("** text", icl));
		assertEquals(null, logic.readListItemIndet(" ** text", icl));
		assertEquals(null, logic.readListItemIndet("x", icl));
		assertEquals(null, logic.readListItemIndet(" x", icl));
		assertEquals(4, logic.readListItemIndet("    - item 2.1", icl).intValue());
		assertEquals(null, logic.readListItemIndet("*Only Bold*", icl));
	}
	
	@Test
	public void getHeadingItems_normal() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# heading 1\n");
		sb.append("# heading 2\n");
		sb.append("## heading 2.1\n");
		sb.append("### heading 2.1.1\n");
		sb.append("# heading 3\n");
		String input = sb.toString();
		List<BMLevelAndTitle> result = new BMHtmlCreator(null).getHeadingItems(new BMSectionParserLogic().parseAll(input));
		assertEquals(5, result.size());
		assertEquals(1, result.get(0).getLevel());
		assertEquals("heading 1", result.get(0).getTitle());
		assertEquals(1, result.get(1).getLevel());
		assertEquals("heading 2", result.get(1).getTitle());
		assertEquals(2, result.get(2).getLevel());
		assertEquals("heading 2.1", result.get(2).getTitle());
		assertEquals(3, result.get(3).getLevel());
		assertEquals("heading 2.1.1", result.get(3).getTitle());
		assertEquals(1, result.get(4).getLevel());
		assertEquals("heading 3", result.get(4).getTitle());
	}

	@Test
	public void getHeadingItems_withFormatting() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# heading 1\n");
		sb.append("# heading 2 is *very* nice\n");
		sb.append("## heading 2.1\n");
		sb.append("### heading 2.1.1\n");
		sb.append("# heading 3\n");
		String input = sb.toString();
		List<BMLevelAndTitle> result = new BMHtmlCreator(null).getHeadingItems(new BMSectionParserLogic().parseAll(input));
		assertEquals(5, result.size());
		assertEquals(1, result.get(0).getLevel());
		assertEquals("heading 1", result.get(0).getTitle());
		assertEquals(1, result.get(1).getLevel());
		assertEquals("heading 2 is very nice", result.get(1).getTitle());
		assertEquals(2, result.get(2).getLevel());
		assertEquals("heading 2.1", result.get(2).getTitle());
		assertEquals(3, result.get(3).getLevel());
		assertEquals("heading 2.1.1", result.get(3).getTitle());
		assertEquals(1, result.get(4).getLevel());
		assertEquals("heading 3", result.get(4).getTitle());
	}
	
	@Test
	public void getHeadingItems_noHeadings() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("This is just some text\n");
		String input = sb.toString();
		List<BMLevelAndTitle> result = new BMHtmlCreator(null).getHeadingItems(new BMSectionParserLogic().parseAll(input));
		assertEquals(0, result.size());
	}

	@Test
	public void isValidSize_multiple() throws Exception{
		assertEquals(true, new BMSectionParserLogic().isValidSize("10"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("10px"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("10in"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("10mm"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("10.3mm"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("1mm"));
		assertEquals(false, new BMSectionParserLogic().isValidSize("1mx"));
		assertEquals(false, new BMSectionParserLogic().isValidSize("mm"));
		assertEquals(false, new BMSectionParserLogic().isValidSize("mmx"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("10%"));
		assertEquals(true, new BMSectionParserLogic().isValidSize("1%"));
		assertEquals(false, new BMSectionParserLogic().isValidSize("10%%"));
		assertEquals(false, new BMSectionParserLogic().isValidSize("x%"));
	}

	@Test
	public void readImageLocationAndSize_widthAndHeight() throws Exception{
		String input = "myimg.png width=10mm height=7mm";
		BMSection section = new BMSection();
		new BMSectionParserLogic().readImageLocationAndSize(section, input);
		
		assertEquals("myimg.png", section.getLocation());
		assertEquals("10mm", section.getImageWidth());
		assertEquals("7mm", section.getImageHeight());
	}

	@Test
	public void readImageLocationAndSize_widthAndHeightMultipleSpace() throws Exception{
		String input = "myimg.png   width=10mm   height=7mm";
		BMSection section = new BMSection();
		new BMSectionParserLogic().readImageLocationAndSize(section, input);
		
		assertEquals("myimg.png", section.getLocation());
		assertEquals("10mm", section.getImageWidth());
		assertEquals("7mm", section.getImageHeight());
	}
	
	@Test
	public void readImageLocationAndSize_width() throws Exception{
		String input = "myimg.png width=10mm ";
		BMSection section = new BMSection();
		new BMSectionParserLogic().readImageLocationAndSize(section, input);
		
		assertEquals("myimg.png", section.getLocation());
		assertEquals("10mm", section.getImageWidth());
		assertEquals(null, section.getImageHeight());
	}

	@Test
	public void parseAll_tableBackgroundAtStartAndEndTableRow() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("{bc:red}c2-1|c2-2|c2-3{bc}\n");
		String input = sb.toString();
		log("input:\n" + input);

		BMSectionParserLogic logic = new BMSectionParserLogic();
		
		BMSection result = logic.parseAll(input);
		log("==========================");
		log("sections:\n" + BMUtil.toString(result));

		BMSection section = result;
		assertEquals(1, section.getChildren().size());
		section = result.getChildren().get(0);
		
		log("==========================");
		log("table row sections:\n" + BMUtil.toString(section));

		
		assertEquals(MDType.TABLE_ROW, section.getType());
		assertEquals("red", section.getBackgroundColor());
		assertEquals(3, section.getChildren().size());
		
		assertEquals(1, section.getChildren().get(0).getChildren().size());
		assertEquals(0, BMUtil.countChildren(section.getChildren().get(0).getChildren().get(0)));
		assertEquals(0, BMUtil.countChildren(section.getChildren().get(1)));
		assertEquals(2, section.getChildren().get(2).getChildren().size());
		assertEquals(0, BMUtil.countChildren(section.getChildren().get(2).getChildren().get(0)));
		assertEquals(0, BMUtil.countChildren(section.getChildren().get(2).getChildren().get(1)));
		
		assertEquals(true, BMUtil.isEmptyOrNull(section.getChildren().get(0).getRawText()));
		assertEquals("c2-1", section.getChildren().get(0).getChildren().get(0).getRawText());
		
		assertEquals("c2-2", section.getChildren().get(1).getRawText());
		
		assertEquals(true, BMUtil.isEmptyOrNull(section.getChildren().get(2).getRawText()));
		assertEquals("c2-3", section.getChildren().get(2).getChildren().get(0).getRawText());
	}

	@Test
	public void toMDSection_noCodeBlocks(){
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*. Nice?";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		BMSection resultOld = new BMSectionParserLogic().toMDSection_old(input);
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "# Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, "* item 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, "* item 2");
		BMUtil.addSection(expected, MDType.RAW_LINE, "* item 3");
		BMUtil.addSection(expected, MDType.RAW_LINE, "");
		BMUtil.addSection(expected, MDType.RAW_LINE, "This text is *bold*. Nice?");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(resultOld));
		assertEquals(toString(expected), toString(result));
	}

	
	@Test
	public void toMDSection_withCodeBlockAsFullLines(){
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);

		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMUtil.addSection(expected, MDType.RAW_LINE, "more text");
		BMSection codeBlockSection = BMUtil.addSection(expected, MDType.CODE_BLOCK, "some source code\nnext line\nint a = 7;\nint b = 5;\n");
		codeBlockSection.setMultiLine(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");

		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockAsFullLinesAndOnlySpacesAfterCodeBlockEndSymbol(){
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```   \nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMUtil.addSection(expected, MDType.RAW_LINE, "more text");
		BMSection codeBlockSection = BMUtil.addSection(expected, MDType.CODE_BLOCK, "some source code\nnext line\nint a = 7;\nint b = 5;\n");
		codeBlockSection.setMultiLine(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockAsFullLinesAndEmptyLineAfterSection(){
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```\n\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMUtil.addSection(expected, MDType.RAW_LINE, "more text");
		BMSection codeBlockSection = BMUtil.addSection(expected, MDType.CODE_BLOCK, "some source code\nnext line\nint a = 7;\nint b = 5;\n");
		codeBlockSection.setMultiLine(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, "");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}

	@Test
	public void toMDSection_withCodeBlockAsFullLinesNeverEnding(){
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMUtil.addSection(expected, MDType.RAW_LINE, "more text");
		BMSection codeBlockSection = BMUtil.addSection(expected, MDType.CODE_BLOCK, "some source code\nnext line\nint a = 7;\nint b = 5;\n");
		codeBlockSection.setMultiLine(true);
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}

	@Test
	public void toMDSection_withCodeBlockStartingAndEndingInSameLineEndOfLine(){
		String input = "Title\n - bullet 1\n - bullet two```some source code```\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "some source code");
		codeBlockSection.setNested(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}

	@Test
	public void toMDSection_withCodeBlockStartingAndEndingInSameLineMiddleOfLine(){
		String input = "Title\n - bullet 1\n - bullet two```some source code``` bullet two end\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "some source code");
		codeBlockSection.setNested(true);
		BMUtil.addSection(section1, MDType.RAW_LINE, " bullet two end");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockStartingAndEndingInSameLineMultipleTimes(){
		String input = "Title\n - bullet 1\n - bullet two```some source code``` bullet two middle ```more source code``` bullet two end\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "some source code");
		codeBlockSection.setNested(true);
		BMUtil.addSection(section1, MDType.RAW_LINE, " bullet two middle ");
		codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "more source code");
		codeBlockSection.setNested(true);
		BMUtil.addSection(section1, MDType.RAW_LINE, " bullet two end");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockStartingInExistingLineButNeverEnding(){
		String input = "Title\n - bullet 1\n - bullet two```some source code";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "some source code");
		codeBlockSection.setNested(true);
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}

	
	@Test
	public void toMDSection_withCodeBlockStartingInLineAndEndingInLaterLine(){
		String input = "Title\n - bullet 1\n - bullet two```some source code\nint x = 5;int y = 6;```\n - bullet three\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "some source code\\nint x = 5;int y = 6;");
		codeBlockSection.setMultiLine(true);
		codeBlockSection.setNested(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet three");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockStartingInLineAndEndingInLaterLineNewLineAtEndOfCodeBlock(){
		String input = "Title\n - bullet 1\n - bullet two```some source code\nint x = 5;int y = 6;\n```\n - bullet three\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "some source code\\nint x = 5;int y = 6;\n");
		codeBlockSection.setMultiLine(true);
		codeBlockSection.setNested(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet three");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockStartingInLineAndEndingInLaterLineWhereCodeBlockStartsWithNewLine(){
		String input = "Title\n - bullet 1\n - bullet two```\nsome source code\nint x = 5;int y = 6;```\n - bullet three\nrest of the text";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMSection section1 = BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMSection codeBlockSection = BMUtil.addSection(section1, MDType.CODE_BLOCK, "\nsome source code\\nint x = 5;int y = 6;");
		codeBlockSection.setMultiLine(true);
		codeBlockSection.setNested(true);
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet three");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void toMDSection_withCodeBlockStartingInSeparateLineButNeverEnding(){
		String input = "Title\n - bullet 1\n - bullet two\n```some source code";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMUtil.addSection(expected, MDType.CODE_BLOCK, "some source code");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}
	
	@Test
	public void parseCodeSections_simple() {
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```java\nif (a == b) return null;\nnext line\nint a = 7;\n/*comment*/int b = 5;\n```\nrest of the text";
		
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		log("sections after toMDSection:\n" + BMUtil.toString(result));
		log("==========================");
		new BMSectionParserLogic().parseCodeSections(result);
		
		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMUtil.addSection(expected, MDType.RAW_LINE, "Title");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet 1");
		BMUtil.addSection(expected, MDType.RAW_LINE, " - bullet two");
		BMUtil.addSection(expected, MDType.RAW_LINE, "more text");
		BMSection section1 = BMUtil.addSection(expected, MDType.CODE_BLOCK, null);
		section1.setMultiLine(true);
		BMUtil.addSection(section1, MDType.CODE_BLOCK_KEYWORD, "if");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_COMMAND, " (a == b) ");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_KEYWORD, "return");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_COMMAND, " null;%%N%%next line%%N%%");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_KEYWORD, "int");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_COMMAND, " a = 7;%%N%%");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_COMMENT, "/*comment*/");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_KEYWORD, "int");
		BMUtil.addSection(section1, MDType.CODE_BLOCK_COMMAND, " b = 5;%%N%%");
		BMUtil.addSection(expected, MDType.RAW_LINE, "rest of the text");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + BMUtil.toString(result));
		
		assertEquals(toString(expected), toString(result));
	}

	@Test
	public void parseCodeSections_codeBlockOnly() throws Exception{
		String input = "```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		BMSection result = new BMSectionParserLogic().toMDSection(input);
		log("initial section:\n" + BMUtil.toString(result));
		log("==========================");
		new BMSectionParserLogic().parseCodeSections(result);
		log("Result:\n" + BMUtil.toString(result));
		log("==========================");

		BMSection expected = BMUtil.createSection(null, MDType.ROOT, null);
		BMSection section = BMUtil.addSection(expected, MDType.CODE_BLOCK, null);
		BMUtil.addSection(section, MDType.CODE_BLOCK_COMMAND, "some source code%%N%%next line%%N%%int a = 7;%%N%%int b = 5;%%N%%");
		section.setMultiLine(true);
		
		assertEquals(toString(expected), toString(result));
	}

}
