package de.bright_side.brightmarkdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;
import de.bright_side.brightmarkdown.BrightMarkdownUtil.PosAndTag;

/**
 * 
 * @author Philip Heyse
 * @version 1.5.1
 * 
 * version 1.1.2 (2017-12-08): empty lines, nested text format fix
 * version 1.1.3 (2018-01-05): Bug fix for bullet point level up
 * version 1.2.0 (2018-01-20): Simplified formatting, list levels by indent, TOC
 * version 1.3.0 (2018-03-03): Added table feature, underline formatting and tag to disable parsing
 * version 1.4.0 (2018-06-21): Images, combined numbered and bullet point lists with indents, text foreground and background color, source code formatting, nested formatting of bold, italic, underline and strikethrough in any order
 * version 1.5.0 (2019-04-02): Background color for table rows and table cells
 * version 1.5.1 (2019-04-10): Bugfix so that HTML creation also works on Android
 *
 */
public class BrightMarkdown {
	private static final String[] HEADINGS_INDICATOR = {"#", "##", "###", "####", "#####", "######"};
	private static final String[] BULLET_POINT_INDICATORS_A = {"*", "**", "***", "****", "*****"};
	private static final String[] BULLET_POINT_INDICATORS_B = {"+", "++", "+++", "++++", "+++++"};
	private static final String[] BULLET_POINT_INDICATORS_C = {"-", "--", "---", "----", "-----"};
	private static final String[] BULLET_POINT_INDICATORS_D = {"o", "oo", "ooo", "oooo", "ooooo"};
	private static final char NUMBERED_ITEM_INDICATOR_CHAR = '.';
	private static final String[] BULLET_POINT_INDICATORS_CHARS = {BULLET_POINT_INDICATORS_A[0], BULLET_POINT_INDICATORS_B[0], BULLET_POINT_INDICATORS_C[0], BULLET_POINT_INDICATORS_D[0]};
	protected static final List<String> BULLET_POINT_INDICATORS_CHARS_LIST = Arrays.asList(BULLET_POINT_INDICATORS_CHARS);
	private static final List<String> NUMBERED_ITEM__INDICATORS_CHARS_LIST = Arrays.asList(new String[]{"" + NUMBERED_ITEM_INDICATOR_CHAR});
	private static final String[] UNCHECKED_ITEM_INDICATORS = {"[]", "[ ]", "- []", "- [ ]"};
	private static final String[] CHECKED_ITEM_INDICATORS = {"[x]", "[X]", "- [x]", "- [X]"};
	private static final String[] NUMBERED_ITEM_INDICATORS = createNumberedItemIndicatorList(100);
	private static final String[] HORIZONTAL_RULE_INDICATORS = {"-", "_", "#", "*", "="};

	private static final String IMAGE_LINK_PREFIX = "!";
	private static final String LINK_LABEL_START_A = "[";
	private static final String LINK_LABEL_END_A = "]";
	private static final String LINK_LABEL_START_B = "(";
	private static final String LINK_LABEL_END_B = ")";
	private static final String LINK_LOCATION_START_A = "[";
	private static final String LINK_LOCATION_END_A = "]";
	private static final String LINK_LOCATION_START_B = "(";
	private static final String LINK_LOCATION_END_B = ")";
	
	private static final List<String> IMAGE_LINK_LABEL_START_TAGS = Arrays.asList(IMAGE_LINK_PREFIX + LINK_LABEL_START_A, IMAGE_LINK_PREFIX + LINK_LABEL_START_B);
	private static final List<String> LINK_LABEL_START_TAGS = Arrays.asList(LINK_LABEL_START_A, LINK_LABEL_START_B);
	private static final List<String> LINK_LABEL_END_TAGS = Arrays.asList(LINK_LABEL_END_A, LINK_LABEL_END_B);
	private static final List<String> LINK_LOCATION_START_TAGS = Arrays.asList(LINK_LOCATION_START_A, LINK_LOCATION_START_B);
	private static final List<String> LINK_LOCATION_END_TAGS = Arrays.asList(LINK_LOCATION_END_A, LINK_LOCATION_END_B);
	
	private static final List<String> LINK_AND_IMAGE_LABEL_START_TAGS = joinLists(IMAGE_LINK_LABEL_START_TAGS, LINK_LABEL_START_TAGS);
	
	
	private static final String TABLE_OF_CONTENT_MARKER = "{TOC}";
	private static final String TABLE_CELL_SEPARATOR = "|";
	protected static final String[] ESCAPE_CHARACTERS = {"\\", "b", "*", "a", "_", "u", "{", "1", "}", "2", "[", "3", "]", "4", "(", "5", ")", "6"
			                                           , "#", "h", "+", "p", "-", "m", ".", "d", "~", "t", "`", "c", "´", "7", "{", "o", "|", "s", "=", "e", "o", "r"};
	private static final String ESCAPE_MARK = "%%";
	private static final String CODE_BLOCK_MARK_A = "```";
	private static final String CODE_BLOCK_MARK_B = "´´´";
	private static final List<String> CODE_BLOCK_MARKS = Arrays.asList(CODE_BLOCK_MARK_A, CODE_BLOCK_MARK_B);

	private static final String NO_MARKDOWN_MARK = "{NOMARKDOWN}";
	protected static final String ESCAPE_NEW_LINE_IN_CODE_BLOCK = "%%N%%";
	private static final int LIST_INDENT_LEVEL_THRESHOLD = 3;
	
	public static enum FormattingItem {H1, H2, H3, H4, H5, H6}
	private Map<FormattingItem, Integer> fontSizesInMM = new EnumMap<>(FormattingItem.class);
	private boolean loggingActive = false;

	private static final Map<String, BrightMarkdownCodeFormat> CODE_FORMATS = new BrightMarkdownCodeFormatDefinition().createCodeFormats();
	private static final String IMAGE_WIDTH_LABEL = "width=";
	private static final String IMAGE_HEIGHT_LABEL = "height=";
	
	public BrightMarkdown() {
	}
	
	protected BrightMarkdown(boolean loggingActive) {
		this.loggingActive = loggingActive;
	}
	
	public String createHTML(String markdownText) throws Exception{
		BrightMarkdownSection section = parseAll(getUseMarkdownText(markdownText));
		return new BrightMarkdownHTMLCreator(loggingActive, fontSizesInMM).toHTML(section);
	}
	
	private String getUseMarkdownText(String markdownText) {
		String useMarkdownText = "";
		if (markdownText != null) {
			if (markdownText.trim().startsWith(NO_MARKDOWN_MARK)) {
				useMarkdownText = escapeSpecialCharacters(markdownText.trim().substring(NO_MARKDOWN_MARK.length()).trim());
			} else {
				useMarkdownText = markdownText;
			}
		}
		
		return useMarkdownText;
	}
	
	/**
	 * 
	 * @param markdownText
	 * @return the deepest heading which is 0 if there are no headings, and e.g. 3 if there is h1, h2 and h3
	 * @throws Exception
	 */
	public int getDeepestHeading(String markdownText) throws Exception{
		BrightMarkdownSection section = parseAll(getUseMarkdownText(markdownText));
		return getDeepestHeading(section);
	}
	
	public String getDocumentationAsHTML() throws Exception{
		return new BrightMarkdown().createHTML(getDocumentationAsMarkdown()).replace("\r", "").replace("\n", "");
	}
	
	public String getDocumentationAsMarkdown(){
		StringBuilder sb = new StringBuilder();
		add(sb, "# Syntax");
		add(sb, "## Headings");
		add(sb, "* \\# heading level 1");
		add(sb, "* \\#\\# heading level 2");
		add(sb, "* \\#\\#\\# heading level 3");
		add(sb, "* \\#\\#\\#\\# heading level 4");
		add(sb, "* \\#\\#\\#\\#\\# heading level 5");
		add(sb, "");
		add(sb, "## Bullet Point Lists");
		add(sb, "* \\* bullet point item level 1");
		add(sb, "* \\*\\* bullet point item level 2");
		add(sb, "* \\*\\*\\* bullet point item level 3");
		add(sb, "* \\*\\*\\*\\* bullet point item level 4");
		add(sb, "* \\*\\*\\*\\*\\* bullet point item level 5");
		add(sb, "* Instead of \\* you can also use \\-, \\o or \\-");
		add(sb, "* Instead of multiple markers like \\*\\* you can also indent by three or more spaces than the previous item");
		add(sb, "");
		add(sb, "## Numbered list");
		add(sb, "* start line with a \".\" followed by at least one space ");
		add(sb, "* like bullet point lists you can also indent by three or more spaces than the previous item for sub lists");
		add(sb, "* you can also use \"1.\", \"2.\" etc. Which number is used does not matter. This does not work for sub lists though.");
		add(sb, "");
		add(sb, "## Formatting");
		add(sb, "* use \\_text\\_ to write text in _italic_");
		add(sb, "* use \\*text\\* to write text in *bold*");
		add(sb, "* use \\+text\\+ to write text in +underlined+");
		add(sb, "* use \\~text\\~ to write text in ~strike through~");
		add(sb, "");
		add(sb, "## Colors");
		add(sb, "* put text between \\{color:_*value*_} and \\{color} to set a text {c:red}color{c}");
		add(sb, "* put text between \\{bg-color:_*value*_} and \\{bg-color} to set a text {bc:yellow}background color{bc}");
		add(sb, "* instead of \"color\" and \"bg-color\" you can also use \"c\" and \"bc\"");
		add(sb, "* possible color _*values*_: \"black\", \"blue\", \"brown\", \"cyan\", \"gold\", \"gray\", \"grey\", \"green\", \"lightgray\", \"lightgrey\", \"darkgray\", \"darkgrey\", \"magenta\", \"red\", \"teal\", \"white\", \"yellow\", \"pink\"");
		add(sb, "* you can also specify a color as a hex value like #ffaa00");
		add(sb, "");
		add(sb, "## Links");
		add(sb, "* \\[my link label\\]\\[www.wikipedia.de\\]");
		add(sb, "* instead of \"\\[\" and \"\\]\" you can also use \"\\(\" and \"\\)\"");
		add(sb, "");
		add(sb, "## Images");
		add(sb, "* !\\[alt text\\]\\[http://path/to/image.png\\]");
		add(sb, "* instead of \"\\[\" and \"\\]\" you can also use \"\\(\" and \"\\)\"");
		add(sb, "* you can also specify the width and/or height of the image like this: !\\[alt text\\]\\[http://path/to/image.png *width=20mm height=10mm* \\]");
		add(sb, "* when specifying width and height you can use the units \"px\" (pixels), \"mm\" (milimeters) and \"in\" (inches). You can also leave out the unit for \"px\".");
		add(sb, "");
		add(sb, "## Checkbox lists");
		add(sb, "* Start the line with \\[x\\] for a checked box and \\[\\] for an unchecked box)");
		add(sb, "");
		add(sb, "## Horizontal rule");
		add(sb, "* have a line that contains of 3 or more \\*\\*\\*");
		add(sb, "* instead of \\* you can also use \\_, \\-, \\= or \\#");
		add(sb, "");
		add(sb, "## Tables:");
		add(sb, "* use the \\| character to separate cells");
		add(sb, "* place a few \\- chars underneath the first row to make it a header row");
		add(sb, "* place \\{bg-color:_*value*_\\} or \\{bc:_*value*_\\} at the beginning of a row to set the row background or at the beginning of a cell to set the cell background");
		add(sb, "");
		add(sb, "## Escaping special characters");
		add(sb, "* Place a \\\\ before a special character like \\* to escape it (ignore for processing)");
		add(sb, "");
		add(sb, "## Code blocks");
		add(sb, "* Place a line of \\`\\`\\` or \\´\\´\\´ before and after the text to indicate a code block");
		add(sb, "* write the format (java or xml) behind the indicator for syntax highlighting. Example \"\\`\\`\\`java\"");
		add(sb, "");
		add(sb, "## Table of contents");
		add(sb, "* have a line with only the text \\{TOC}");
		add(sb, "");
		add(sb, "## Disable processing");
		add(sb, "* place \\{NOMARKDOWN} at the beginning of the text");
		add(sb, "");
		
		return sb.toString();
	}
	
	public String escapeSpecialCharacters(String text) {
		String result = text;
		log("text before escape: >>" + text.replace("\n", "\\n") + "<<");
		for (int i = 0; i < ESCAPE_CHARACTERS.length; i+= 2){
			String input = ESCAPE_CHARACTERS[i];
			String output = "\\" + ESCAPE_CHARACTERS[i];
			result = result.replace(input, output);
		}
		log("text after escape: >>" + result.replace("\n", "\\n") + "<<");
		return result;
	}

	private void add(StringBuilder sb, String text){
		sb.append(text + "\n");
	}
	
	private static String[] createNumberedItemIndicatorList(int end) {
		String[] result = new String[end + 1];
		result[0] = "" + NUMBERED_ITEM_INDICATOR_CHAR;
		for (int i = 1; i <= end; i ++){
			result[i] = i + ".";
		}
		return result;
	}

	protected BrightMarkdownSection parseAll(String markdownText){
		String escapedMarkedown = escape(markdownText);
		BrightMarkdownSection section = toMDSection(escapedMarkedown);
		parseCodeSections(section);
		parseHorizontalRuleEntries(section);
		parseTableOfContentEntries(section);

		parseRawLineEntries(section, MDType.HEADING, HEADINGS_INDICATOR, true);
		parseRawLineEntries(section, MDType.UNCHECKED_ITEM, UNCHECKED_ITEM_INDICATORS, false);
		parseRawLineEntries(section, MDType.CHECKED_ITEM, CHECKED_ITEM_INDICATORS, false);
		parseListEntriesByLevel(section);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_A, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_B, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_C, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_D, true);
		parseRawLineEntries(section, MDType.NUMBERED_ITEM, NUMBERED_ITEM_INDICATORS, false);
		logSection("before parseTableRows", section);
		parseTableRows(section);
		logSection("after parseTableRows", section);
		logSection("before parseTextParagraphs", section);
		parseTextParagraphs(section);
		logSection("after parseTextParagraphs", section);
		parseLinks(section);

		logSection("before formatting", section);
		parseFormatting(section);
		logSection("after formatting", section);
		logSection("parseAll result", section);
		return section;
	}
	
	private void parseCodeSections(BrightMarkdownSection topSection) {
		int pos = 0;
		List<BrightMarkdownSection> sections = new ArrayList<>(topSection.getChildren());
		List<BrightMarkdownSection> newChildren = new ArrayList<>();
		int amount = sections.size();
		
		while (pos < amount){
			BrightMarkdownSection section = sections.get(pos);
			if (isCodeBlockIndicator(section)){
				BrightMarkdownCodeFormat format = readFormat(section.getRawText());
				StringBuilder codeBlock = new StringBuilder();
				pos ++;
				if (pos < amount){
					section = sections.get(pos);
				}
				while ((!isCodeBlockIndicator(section)) && (pos < amount)){
					codeBlock.append(section.getRawText() + "\n");
					pos ++;
					if (pos < amount){
						section = sections.get(pos);
					}
				}
				if (codeBlock.length() > 0){
					BrightMarkdownSection codeBlockSection = createSection(topSection, MDType.CODE_BLOCK, "");
					codeBlockSection.setChildren(new BrightMarkdownCodeParser().createSections(topSection, codeBlock.toString(), format, ESCAPE_NEW_LINE_IN_CODE_BLOCK));
					newChildren.add(codeBlockSection);
					
//					newChildren.add(createSection(topSection, MDType.CODE_BLOCK, codeBlock.toString().replace("\n", ESCAPE_NEW_LINE_IN_CODE_BLOCK)));
				}
			} else {
				newChildren.add(section);
			}
			pos ++;
		}
		topSection.setChildren(newChildren);
	}

	private BrightMarkdownCodeFormat readFormat(String rawText) {
		String formatName = readFormatName(rawText);
		if (formatName == null) {
			return null;
		}
		return CODE_FORMATS.get(formatName.toLowerCase());
	}

	
	private String readFormatName(String rawText) {
		if (rawText == null){
			return null;
		}
		for (String i: CODE_BLOCK_MARKS) {
			if (rawText.startsWith(i)){
				return rawText.substring(i.length()).trim();
			}
		}
		return null;
	}

	private boolean isCodeBlockIndicator(BrightMarkdownSection section) {
		if (section.getRawText() == null){
			return false;
		}
		String text = section.getRawText().trim();
		for (String i: CODE_BLOCK_MARKS) {
			if (text.startsWith(i)){
				return true;
			}
		}
		return false;
	}

	private String escape(String text) {
		String result = text;
		log("text before escape: >>" + text.replace("\n", "\\n") + "<<");
		for (int i = 0; i < ESCAPE_CHARACTERS.length; i+= 2){
			String input = "\\" + ESCAPE_CHARACTERS[i];
			String output = ESCAPE_MARK + ESCAPE_CHARACTERS[i + 1] + ESCAPE_MARK;
			result = result.replace(input, output);
		}
		log("text after escape: >>" + result.replace("\n", "\\n") + "<<");
		return result;
	}
	
	protected static String unescape(String text) {
//		log("text before unescape: >>" + text.replace("\n", "\\n") + "<<");
		String result = text;
		for (int i = 0; i < ESCAPE_CHARACTERS.length; i+= 2){
			String input = ESCAPE_MARK + ESCAPE_CHARACTERS[i + 1] + ESCAPE_MARK;
			String output = ESCAPE_CHARACTERS[i];
			result = result.replace(input, output);
		}
//		log("text after unescape: >>" + result.replace("\n", "\\n") + "<<");
		return result;
	}
	


	private void parseHorizontalRuleEntries(BrightMarkdownSection topSection) {
		for (String indicator: HORIZONTAL_RULE_INDICATORS){
			String indicatorWithLength3 = indicator + indicator + indicator;
			for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection)){
				if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null)){
					String rawText = section.getRawText().trim();
					if ((rawText.startsWith(indicatorWithLength3)) && (rawText.replace(indicator, "").isEmpty())){
						//: indicator occurs 3 times or more and there is nothing else on the line (except leading/trailing white space)
						section.setType(MDType.HORIZONTAL_RULE);
						section.setRawText("");
					}
				}
			}
		}
	}
	
	private void parseTableOfContentEntries(BrightMarkdownSection topSection) {
		for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection)){
			if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null)){
				String rawText = section.getRawText().trim();
				if ((rawText.equals(TABLE_OF_CONTENT_MARKER))){
					section.setType(MDType.TABLE_OF_CONTENTS);
					section.setRawText("");
				}
			}
		}
	}
	
	protected void parseListEntriesByLevel(BrightMarkdownSection topSection) {
		Integer previousListItemIndent = null;
		Integer previousListItemLevel = null;
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection)){
			String rawText = section.getRawText();
			if ((section.getType() == MDType.RAW_LINE) && (rawText != null)){
				MDType type = MDType.BULLET_POINT;
				Integer listItemLevelIndent = readListItemIndet(rawText, BULLET_POINT_INDICATORS_CHARS_LIST);
				if (listItemLevelIndent == null) { //: could not find bullet point, try numbered item indicator
					listItemLevelIndent = readListItemIndet(rawText, NUMBERED_ITEM__INDICATORS_CHARS_LIST);
					type = MDType.NUMBERED_ITEM;
				}
				
				Integer listItemLevel = null;
				
				if (listItemLevelIndent == null){ //: neither bullet point nor numbered item
					previousListItemIndent = null;
					previousListItemLevel = null;
				} else {
					int indentDifference = 0;
					if (previousListItemIndent != null){
						indentDifference = listItemLevelIndent - previousListItemIndent;  
					} else {
						previousListItemLevel = 1;
						listItemLevel = 1;
						levelToIndentMap.put(listItemLevel, listItemLevelIndent);
					}
					
					if (Math.abs(indentDifference) < LIST_INDENT_LEVEL_THRESHOLD){
						//: only few spaces, assume same level
						listItemLevel = previousListItemLevel;
					} else if (indentDifference > 0){
						//: one more level
						listItemLevel = previousListItemLevel + 1;
						levelToIndentMap.put(listItemLevel, listItemLevelIndent);
					} else {
						//: one or more(!) levels up
						listItemLevel = findMatchingLevel(levelToIndentMap, listItemLevelIndent);
						removeDeeperLevels(levelToIndentMap, listItemLevel);
					}
					previousListItemIndent = listItemLevelIndent;
					previousListItemLevel = listItemLevel;

				}
				
				if (listItemLevel != null){
//					section.setRawText(rawText.substring(readBulletPointAndIndentLength(rawText)));
					section.setRawText(rawText.substring(findPosAfterLeadindSpaces(rawText, listItemLevelIndent)));
					section.setType(type);
					section.setLevel(listItemLevel);
				}
			}
		}
	}

	protected Integer findMatchingLevel(Map<Integer, Integer> levelToIndentMap, int indent) {
		int bestMatchDifference = Integer.MAX_VALUE;
		int bestMatchLevel = 1;
		
		for (Entry<Integer, Integer> i: levelToIndentMap.entrySet()){
			int difference = Math.abs(indent - i.getValue());
			if (difference < bestMatchDifference){
				bestMatchDifference = difference;
				bestMatchLevel = i.getKey();
			}
		}
		
		return bestMatchLevel;
	}

//	protected int readBulletPointAndIndentLength(String rawText) {
//		int result = 0;
//		Integer start = readBulletPointListItemIndet(rawText);
//		if (start == null){
//			return 0;
//		}
//		start ++;
//		if (rawText.length() <= start){
//			return start;
//		}
//		result = start;
//		for (char i: rawText.substring(start).toCharArray()){
//			if (i == ' '){
//				result ++;
//			} else {
//				return result;
//			}
//		}
//		return result;
//	}

	protected int findPosAfterLeadindSpaces(String rawText, int startPos) {
		int result = 0;
		int start = startPos + 1;
		if (rawText.length() <= start){
			return start;
		}
		result = start;
		for (char i: rawText.substring(start).toCharArray()){
			if (i == ' '){
				result ++;
			} else {
				return result;
			}
		}
		return result;
	}
	
	protected Integer readListItemIndet(String rawText, List<String> indicatorsList) {
		int indent = 0;
		boolean indicatorFound = false;
		for (char i: rawText.toCharArray()){
			if (!indicatorFound){
				if (i == ' '){
					indent ++;
				} else if (indicatorsList.contains(new String("" + i))){
					indicatorFound = true;
				} else {
					//: after optional spaces there is no bullet point indicator but something else
					return null;
				}
			} else {
				if (i == ' '){
					//: found space after indicator
					return indent;
				} else {
					//: found a different character after the indicator, so either it is another indicator like '**' or it is not the syntax for list items
					return null;
				}
			}
		}
		if (!indicatorFound){
			return null;
		}
		return indent;
	}
	
	private void parseRawLineEntries(BrightMarkdownSection topSection, MDType type, String[] indicators, boolean setLevelDepth) {
		for (int level = 1; level <= indicators.length; level ++){
			String indicator = indicators[level - 1] + " ";
			for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection)){
				if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null) && (section.getRawText().trim().startsWith(indicator))){
					trimAndRemoveRawTextStart(section, indicator.length());
					section.setOriginalPlainText(removeFormatting(section.getRawText()));
					section.setType(type);
					if (setLevelDepth){
						section.setLevel(level);
					} else {
						section.setLevel(1);
					}
				}
			}
		}
	}

	private void parseTableRows(BrightMarkdownSection topSection) {
		for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection)){
			if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null) && (section.getRawText().contains(TABLE_CELL_SEPARATOR))){
				List<String> cellTexts = readCellTexts(section.getRawText().trim());
				section.setOriginalPlainText(removeFormatting(section.getRawText()));
				section.setRawText(null);
				section.setType(MDType.TABLE_ROW);
				List<BrightMarkdownSection> children = new ArrayList<BrightMarkdownSection>();
				for (String i: cellTexts) {
					children.add(createSection(section, MDType.TABLE_CELL, i));
				}
				section.setChildren(children);
			}
		}
	}
	
	private List<String> readCellTexts(String fullText) {
		List<String> result = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(fullText, TABLE_CELL_SEPARATOR, true);
		boolean lastWasSeparator = false;
		boolean firstItem = true;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (TABLE_CELL_SEPARATOR.equals(token)) {
				if ((lastWasSeparator) || (firstItem)) {
					result.add("");
				}
				lastWasSeparator = true;
			} else {
				lastWasSeparator = false;
				result.add(token);
			}
			firstItem = false;
		}
		if (lastWasSeparator) {
			result.add("");
		}
		return result;
	}

	private String removeFormatting(String rawText) {
		return rawText.replace("\\", "").replace("*", "").replace("_", "").replace("~", "");
	}

	private void parseTextParagraphs(BrightMarkdownSection topSection) {
		BrightMarkdownSection paragraphSection = null;
		List<Integer> sectionsToRemove = new ArrayList<Integer>();
		if (topSection.getChildren() == null){
			return;
		}

		int sectionIndex = 0;
		for (BrightMarkdownSection section: topSection.getChildren()){
			if ((section.getType() == MDType.RAW_LINE)){
				if (paragraphSection == null){
					paragraphSection = section;
					section.setType(MDType.PARAGRAPH);
					if (paragraphSection.getChildren() == null){
						paragraphSection.setChildren(new ArrayList<BrightMarkdownSection>());
					}
				} else {
					sectionsToRemove.add(sectionIndex);
				}
				BrightMarkdownSection paragraphElementSection = createSection(paragraphSection, MDType.PARAGRAPH_ELEMENT, toTrimmedNonNullString(section.getRawText()));
				paragraphSection.getChildren().add(paragraphElementSection);
				section.setRawText(null);
			} else {
				paragraphSection = null;
			}
			sectionIndex ++;
		}
		Collections.sort(sectionsToRemove);
		Collections.reverse(sectionsToRemove);
		for (Integer i: sectionsToRemove){
			topSection.getChildren().remove(i.intValue());
		}
		
	}
	
	private String toTrimmedNonNullString(String rawText) {
		if (rawText == null){
			return "";
		}
		return rawText.trim();
	}

	private void parseLinks(BrightMarkdownSection topSection) {
		for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection, true)){
			parseLinksAndImagesForSingleSection(section);
		}
	}
	
	private void parseLinksAndImagesForSingleSection(BrightMarkdownSection section) {
		String rest = "";
//		int start = 0;
//		int end = 0;
		boolean linksDetected = false;
		BrightMarkdownUtil util = new BrightMarkdownUtil();
		try{
			if (section.getRawText() == null){
				return;
			}		
			if (section.getChildren() != null){
				throw new RuntimeException("Did not expect raw text and children in one section item! (Raw text = >>" + section.getRawText() + "<<");
			}
			rest = section.getRawText();
			
			PosAndTag labelStart = util.findNext(rest, 0, LINK_AND_IMAGE_LABEL_START_TAGS);
			
			
			if (labelStart == null){
				return;
			}
			List<BrightMarkdownSection> children = new ArrayList<>();
			PosAndTag labelEnd = null;
			while (labelStart != null){
				labelEnd = util.findNext(rest, labelStart.pos + labelStart.tag.length(), LINK_LABEL_END_TAGS);
				if (labelEnd != null) {
					PosAndTag locationStart = util.findNext(rest, labelEnd.pos, LINK_LOCATION_START_TAGS);
					if ((locationStart != null) && (locationStart.pos == labelEnd.pos + 1)){
						PosAndTag locationEnd = util.findNext(rest, locationStart.pos, LINK_LOCATION_END_TAGS);
						if (locationEnd != null) {
							String leadingPlainText = rest.substring(0, labelStart.pos);
							if (!leadingPlainText.isEmpty()) {
								children.add(createSection(section, MDType.PLAIN_TEXT, leadingPlainText));
							}
							
							String labelText = rest.substring(labelStart.pos + labelStart.tag.length(), labelEnd.pos);
							String locationText = rest.substring(locationStart.pos + 1, locationEnd.pos);

							if ((!labelText.isEmpty()) && (!locationText.isEmpty())){
								BrightMarkdownSection linkSection = null;
								if (labelStart.tag.startsWith(IMAGE_LINK_PREFIX)) {
									linkSection = createSection(section, MDType.IMAGE, null);
									readImageLocationAndSize(linkSection, locationText);
									linkSection.setImageAltText(labelText);
								} else {
									linkSection = createSection(section, MDType.LINK, labelText);
									linkSection.setLocation(locationText);
								}
								children.add(linkSection);
								linksDetected = true;
							}

							rest = rest.substring(locationEnd.pos + locationEnd.tag.length());
							labelStart = util.findNext(rest, 0, LINK_AND_IMAGE_LABEL_START_TAGS);
						} else {
							labelStart = null; //: end loop
						}
					} else {
						labelStart = null; //: end loop
					}
				} else {
					labelStart = null; //: end loop
				}
				
			}
			if (rest.length() > 0){
				children.add(createSection(section, MDType.PLAIN_TEXT, rest));
			}
			if ((linksDetected) && (!children.isEmpty())){
				section.setChildren(children);
				section.setRawText(null);
			}
		} catch (RuntimeException e){
			throw e;
		}
	}

//	private void parseLinksForSingleSection(BrightMarkdownSection section) {
//		String rest = "";
//		int start = 0;
//		int end = 0;
//		try{
//			if (section.getRawText() == null){
//				return;
//			}		
//			if (section.getChildren() != null){
//				throw new RuntimeException("Did not expect raw text and children in one section item! (Raw text = >>" + section.getRawText() + "<<");
//			}
//			rest = section.getRawText();
//			start = rest.indexOf(LINK_LABEL_START);
//			if (start < 0){
//				return;
//			}
//			List<BrightMarkdownSection> children = new ArrayList<>();
//			end = 0;
//			while (start >= 0){
//				end = rest.indexOf(LINK_LOCATION_END, start);
//				if (end >= 0){
//					if (start > 0){
//						children.add(createSection(section, MDType.PLAIN_TEXT, rest.substring(0, start)));
//					}
//					String text = rest.substring(start, end + 1);
//					boolean success = tryToAddLink(section, children, text);
//					if (!success){
//						children.add(createSection(section, MDType.PLAIN_TEXT, text));
//					}
//					rest = rest.substring(end + LINK_LOCATION_END.length());
//					start = rest.indexOf(LINK_LABEL_START);
//				} else {
//					start = -1; //: end loop
//				}
//			}
//			if (rest.length() > 0){
//				children.add(createSection(section, MDType.PLAIN_TEXT, rest));
//			}
//			if (!children.isEmpty()){
//				section.setChildren(children);
//				section.setRawText(null);
//			}
//		} catch (RuntimeException e){
//			throw e;
//		}
//	}
//	
//	private boolean tryToAddLink(BrightMarkdownSection parent, List<BrightMarkdownSection> children, String text) {
//		String rest = text;
//		if (rest.startsWith(LINK_LABEL_START)){
//			rest = rest.substring(1);
//		} else{
//			return false;
//		}
//		int pos = rest.indexOf(LINK_LABEL_END);
//		if (pos < 0){
//			return false;
//		}
//		String label = rest.substring(0, pos).trim();
//		if (label.isEmpty()){
//			return false;
//		}
//		rest = rest.substring(pos + 1).trim();
//		if (!rest.startsWith(LINK_LOCATION_START)){
//			return false;
//		}
//		rest = rest.substring(1);
//		if (rest.endsWith(LINK_LOCATION_END)){
//			rest = rest.substring(0, rest.length() - 1);
//		} else{
//			return false;
//		}
//		String location = rest.trim();
//		if (location.isEmpty()){
//			return false;
//		}
//		BrightMarkdownSection section = createSection(parent, MDType.LINK, label);
//		section.setLocation(location);
//		children.add(section);
//		return true;
//	}

	protected void readImageLocationAndSize(BrightMarkdownSection section, String text) {
		if (text.indexOf(" ") < 0) {
			section.setLocation(text);
			return;
		}
		String[] items = text.split(" ");
		if (items.length < 2) {
			section.setLocation(text);
			return;
		}
		section.setLocation(items[0]);
		for (int i = 1; i < items.length; i++) {
			String item = items[i];
			if (item.startsWith(IMAGE_WIDTH_LABEL)) {
				String sizeInfo = item.substring(IMAGE_WIDTH_LABEL.length()).trim();
				if (isValidSize(sizeInfo)) {
					section.setImageWidth(sizeInfo);
				}
			} else if (item.startsWith(IMAGE_HEIGHT_LABEL)) {
				String sizeInfo = item.substring(IMAGE_HEIGHT_LABEL.length()).trim();
				if (isValidSize(sizeInfo)) {
					section.setImageHeight(sizeInfo);
				}
			}
		}
		
	}

	protected boolean isValidSize(String sizeInfo) {
		if (BrightMarkdownUtil.isNumber(sizeInfo)) {
			return true;
		}
		if (sizeInfo.length() < 3) {
			return false;
		}
		String unit = sizeInfo.substring(sizeInfo.length() - 2);
		String value = sizeInfo.substring(0, sizeInfo.length() - 2);
		if (!BrightMarkdownUtil.isNumber(value)) {
			return false;
		}
		if (!Arrays.asList("mm", "px", "in").contains(unit)) {
			return false;
		}
		return true;
	}

	private void parseFormatting(BrightMarkdownSection topSection) {
		for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection, true)){
			List<BrightMarkdownSection> formattedSections = new BrightMarkdownFormattingParser(loggingActive).createFormattedSections(section.getRawText());
			if (!formattedSections.isEmpty()) {
				if ((formattedSections.size() != 1) || (hasFormatting(formattedSections.get(0)))){ //: if there is still only one section without formatting: just keep it and there is no need for children with formatting
					section.setChildren(formattedSections);
					section.setRawText(null);
				}
			}
		}
		
		logSection("after formatting parser", topSection);

		new BrightMarkdownFormatCascader(loggingActive).cascadeFormatting(topSection);
		logSection("after cascade formatting", topSection);
	}
	
	protected boolean hasFormatting(BrightMarkdownSection section) {
		return section.isBold() || section.isItalic() || section.isStrikeThrough() || section.isUnderline() || section.getColor() != null || section.getBackgroundColor() != null || section.isBackgroundColorEndTag();
	}
	
	private BrightMarkdownSection createSection(BrightMarkdownSection parent, MDType type, String rawText){
		return BrightMarkdownUtil.createSection(parent, type, rawText);
	}

	private void trimAndRemoveRawTextStart(BrightMarkdownSection section, int length) {
		section.setRawText(section.getRawText().trim().substring(length));
	}

	protected BrightMarkdownSection toMDSection(String markdownText){
		BrightMarkdownSection result = new BrightMarkdownSection();
		result.setType(MDType.ROOT);
		result.setChildren(new ArrayList<BrightMarkdownSection>());
		
		for (String line : markdownText.replace("\r", "").split("\n")){
			BrightMarkdownSection subSection = new BrightMarkdownSection();
			subSection.setType(MDType.RAW_LINE);
			subSection.setRawText(line); //x trim removed here
			result.getChildren().add(subSection);
		}
		return result;
	}
	
	protected String toString(BrightMarkdownSection section){
		return toString(section, 0);
	}
	
	
	
	private String toString(BrightMarkdownSection section, int indent){
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < indent; i ++){
			result.append(" ");
		}
		String rawText = "(none)";
		if (section.getRawText() != null){
			rawText = ">>" + section.getRawText() + "<<";
		}
		String properties = "";
		properties += BrightMarkdownUtil.addPropertiesString(section.getLevel() != null, "level", section.getLevel());
		properties += BrightMarkdownUtil.addPropertiesString(section.isBold(), "bold", section.isBold());
		properties += BrightMarkdownUtil.addPropertiesString(section.isItalic(), "italic", section.isItalic());
		properties += BrightMarkdownUtil.addPropertiesString(section.isUnderline(), "underline", section.isUnderline());
		properties += BrightMarkdownUtil.addPropertiesString(section.isStrikeThrough(), "strikeThrough", section.isStrikeThrough());
		properties += BrightMarkdownUtil.addPropertiesString(section.getColor() != null, "color", section.getColor());
		properties += BrightMarkdownUtil.addPropertiesString(section.getBackgroundColor() != null, "backgroundColor", section.getBackgroundColor());
		properties += BrightMarkdownUtil.addPropertiesString(section.isBackgroundColorEndTag(), "backgroundColorEndTag", section.isBackgroundColorEndTag());
		
		result.append("Sec(type=" + section.getType() + ", rawText=" + rawText);
		if (!properties.isEmpty()) {
			result.append(", properties: {" + properties.trim() + "}");
		}
		result.append(")\n");
		
		
		
		
		if (section.getChildren() != null){
			for (BrightMarkdownSection i : section.getChildren()){
				result.append(toString(i, indent + 4));
			}
		}
		return result.toString();
	}

	protected static void removeDeeperLevels(Map<Integer, ?> levelToListNodeMap, int level) {
		List<Integer> deeperLevels = new ArrayList<>();
		for (int i: levelToListNodeMap.keySet()){
			if (i > level){
				deeperLevels.add(i);
			}
		}
		for (Integer i: deeperLevels){
			levelToListNodeMap.remove(i);
		}
	}
	
	private void logSection(String message, BrightMarkdownSection topSection) {
		if (!loggingActive) {
			return;
		}
		log(message + ":\n" + toString(topSection));
	}
	
	private void log(String message) {
		if (loggingActive) {
			System.out.println("BrightMarkdown> " + message);
		}
	}

	public void setFontSizeInMM(FormattingItem formattingItem, int sizeInMM){
		fontSizesInMM.put(formattingItem, sizeInMM);		
	}
	
	private int getDeepestHeading(BrightMarkdownSection section) {
		int max = 0;
		if (section.getType() == MDType.HEADING){
			max = Math.max(section.getLevel(), max);
		}
		if (section.getChildren() != null){
			for (BrightMarkdownSection i: section.getChildren()){
				max = Math.max(getDeepestHeading(i), max);
			}
		}
		return max;
	}
	
	
	
	private static List<String> joinLists(List<String> listA, List<String> listB) {
		List<String> result = new ArrayList<String>(listA);
		result.addAll(listB);
		return result;
	}
	
	public void setLogginActive(boolean logginActive) {
		this.loggingActive = logginActive;
	}


	
}
