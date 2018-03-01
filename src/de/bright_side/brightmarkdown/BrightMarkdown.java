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

/**
 * 
 * @author Philip Heyse
 * @version 1.3.0
 * 
 * version 1.1.2 (2017-12-08): empty lines, nested text format fix
 * version 1.1.3 (2018-01-05): Bug fix for bullet point level up
 * version 1.2.0 (2018-01-20): Simplified formatting, list levels by indent, TOC
 * version 1.3.0 (2018-03-03): Added table feature, underline formatting and tag to disable parsing

 */
public class BrightMarkdown {
	private static final String[] HEADINGS_INDICATOR = {"#", "##", "###", "####", "#####", "######"};
	private static final String[] BULLET_POINT_INDICATORS_A = {"*", "**", "***", "****", "*****"};
	private static final String[] BULLET_POINT_INDICATORS_B = {".", "..", "...", "....", "....."};
	private static final String[] BULLET_POINT_INDICATORS_C = {"+", "++", "+++", "++++", "+++++"};
	private static final String[] BULLET_POINT_INDICATORS_D = {"-", "--", "---", "----", "-----"};
	private static final String[] BULLET_POINT_INDICATORS_CHARS = {BULLET_POINT_INDICATORS_A[0], BULLET_POINT_INDICATORS_B[0], BULLET_POINT_INDICATORS_C[0], BULLET_POINT_INDICATORS_D[0]};
	private static final List<String> BULLET_POINT_INDICATORS_CHARS_LIST = Arrays.asList(BULLET_POINT_INDICATORS_CHARS);
	private static final String[] UNCHECKED_ITEM_INDICATORS = {"[]", "[ ]", "- []", "- [ ]"};
	private static final String[] CHECKED_ITEM_INDICATORS = {"[x]", "[X]", "- [x]", "- [X]"};
	private static final String[] NUMBERED_ITEM_INDICATORS = createList(100);
	private static final String[] HORIZONTAL_RULE_INDICATORS = {"-", "_", "#", "*", "="};
	private static final String LINK_LABEL_START = "[";
	private static final String LINK_LABEL_END = "]";
	private static final String LINK_LOCATION_START = "(";
	private static final String LINK_LOCATION_END = ")";
	private static final String TABLE_OF_CONTENT_MARKER = "{TOC}";
	private static final String TABLE_CELL_SEPARATOR = "|";
	protected static final String[] ESCAPE_CHARACTERS = {"\\", "b", "*", "a", "_", "u", "{", "1", "}", "2", "[", "3", "]", "4", "(", "5", ")", "6"
			                                           , "#", "h", "+", "p", "-", "m", ".", "d", "~", "t", "`", "c", "{", "o", "|", "s", "=", "e"};
	private static final String ESCAPE_MARK = "%%";
	private static final String CODE_BLOCK_MARK = "```";
	private static final String NO_MARKDOWN_MARK = "{NOMARKDOWN}";
	protected static final String ESCAPE_NEW_LINE_IN_CODE_BLOCK = "%%N%%";
	private static final int LIST_INDENT_LEVEL_THRESHOLD = 3;
	
	public static enum FormattingItem {H1, H2, H3, H4, H5, H6}
	private Map<FormattingItem, Integer> fontSizesInMM = new EnumMap<>(FormattingItem.class);

	public String createHTML(String markdownText) throws Exception{
		BrightMarkdownSection section = parseAll(getUseMarkdownText(markdownText));
		return new BrightMarkdownHTMLCreator(fontSizesInMM).toHTML(section);
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
		add(sb, "* Instead of \\* you can also use \\-, \\- or \\.");
		add(sb, "* Instead of multiple markers like \\*\\* you can also indent by three or more spaces than the previous item");
		add(sb, "");
		add(sb, "## Numbered list");
		add(sb, "* start line with \"1.\", \"2.\" etc. Which number is used does not matter");
		add(sb, "");
		add(sb, "## Formatting");
		add(sb, "* use \\_text\\_ to write text in _italic_");
		add(sb, "* use \\*text\\* to write text in *bold*");
		add(sb, "* use \\+text\\+ to write text in +underlined+");
		add(sb, "* use \\~text\\~ to write text in ~strike through~");
		add(sb, "");
		add(sb, "## Links");
		add(sb, "* \\[my link label\\]\\(www.wikipedia.de\\)");
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
		add(sb, "");
		add(sb, "## Escaping special characters");
		add(sb, "* Place a \\\\ before a special character like \\* to escape it (ignore for processing)");
		add(sb, "");
		add(sb, "## Code blocks");
		add(sb, "* Place a line of \\`\\`\\` before and after the text to indicate a code block");
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
	
	private static String[] createList(int end) {
		String[] result = new String[end];
		for (int i = 1; i <= end; i ++){
			result[i - 1] = i + ".";
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
		parseTableRows(section);

		
		
		log("before parseTextParagraphs: " + toString(section));
		parseTextParagraphs(section);
		log("after parseTextParagraphs: " + toString(section));
		
		parseLinks(section);

		parseFormatting(section, MDType.BOLD, "*");
		parseFormatting(section, MDType.UNDERLINE, "+");
		parseFormatting(section, MDType.ITALIC, "_");
		parseFormatting(section, MDType.STRIKETHROUGH, "~");
		
		
		log("parseAll result:\n" + toString(section) + "\n --------");
		
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
					newChildren.add(createSection(topSection, MDType.CODE_BLOCK, codeBlock.toString().replace("\n", ESCAPE_NEW_LINE_IN_CODE_BLOCK)));
				}
			} else {
				newChildren.add(section);
			}
			pos ++;
		}
		topSection.setChildren(newChildren);
	}

	private boolean isCodeBlockIndicator(BrightMarkdownSection section) {
		if (section.getRawText() == null){
			return false;
		}
		return section.getRawText().trim().startsWith(CODE_BLOCK_MARK);
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
			for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
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
		for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
			if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null)){
				String rawText = section.getRawText().trim();
				if ((rawText.equals(TABLE_OF_CONTENT_MARKER))){
					section.setType(MDType.TABLE_OF_CONTENTS);
					section.setRawText("");
				}
			}
		}
	}
	
	private List<BrightMarkdownSection> getAllSectionsAndSubSections(BrightMarkdownSection section){
		return getAllSectionsAndSubSections(section, false);
	}

	private List<BrightMarkdownSection> getAllSectionsAndSubSections(BrightMarkdownSection section, boolean excludeCodeBlocks){
		List<BrightMarkdownSection> result = new ArrayList<>();
		if ((excludeCodeBlocks) && (section.getType() == MDType.CODE_BLOCK)){
			return result;
		}
		result.add(section);
		if (section.getChildren() != null){
			for (BrightMarkdownSection i: section.getChildren()){
				result.addAll(getAllSectionsAndSubSections(i, excludeCodeBlocks));
			}
		}
		return result;
	}
	
	protected void parseListEntriesByLevel(BrightMarkdownSection topSection) {
		Integer previousListItemIndent = null;
		Integer previousListItemLevel = null;
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
			String rawText = section.getRawText();
			if ((section.getType() == MDType.RAW_LINE) && (rawText != null)){
				Integer listItemLevelIndent = readListItemIndet(rawText);
				
				Integer listItemLevel = null;
				
				if (listItemLevelIndent == null){
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
					section.setRawText(rawText.substring(readBulletPointAndIndentLength(rawText)));
//					trimAndRemoveRawTextStart(section, readBulletPointAndIndentLength(section.getRawText()));
					section.setType(MDType.BULLET_POINT);
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

	protected int readBulletPointAndIndentLength(String rawText) {
		int result = 0;
		Integer start = readListItemIndet(rawText);
		if (start == null){
			return 0;
		}
		start ++;
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

	protected Integer readListItemIndet(String rawText) {
		int indent = 0;
		boolean indicatorFound = false;
		for (char i: rawText.toCharArray()){
			if (!indicatorFound){
				if (i == ' '){
					indent ++;
				} else if (BULLET_POINT_INDICATORS_CHARS_LIST.contains(new String("" + i))){
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
				
//				if (BULLET_POINT_INDICATORS_CHARS_LIST.contains(new String("" + i))){
//					//: indicator found multiple times which is then another marker like '**' for a list item level 2 - no matter how many spaces are used for indentation
//					return null;
//				} else {
//					//: found non-bullet point indicator char after the bullet-point indicator, so it is ok
//					return indent;
//				}
			}
		}
		if (!indicatorFound){
			return null;
		}
		return indent;
	}

	private void parseRawLineEntries(BrightMarkdownSection topSection, MDType type, String[] indicators, boolean setLevel) {
		for (int level = 1; level <= indicators.length; level ++){
			String indicator = indicators[level - 1] + " ";
			for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
				if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null) && (section.getRawText().trim().startsWith(indicator))){
					trimAndRemoveRawTextStart(section, indicator.length());
					section.setOriginalPlainText(removeFormatting(section.getRawText()));
					section.setType(type);
					if (setLevel){
						section.setLevel(level);
					}
				}
			}
		}
	}

	private void parseTableRows(BrightMarkdownSection topSection) {
		for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
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
		for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection, true)){
			parseLinksForSingleSection(section);
		}
	}
	
	private void parseLinksForSingleSection(BrightMarkdownSection section) {
		String rest = "";
		int start = 0;
		int end = 0;
		try{
			if (section.getRawText() == null){
				return;
			}		
			if (section.getChildren() != null){
				throw new RuntimeException("Did not expect raw text and children in one section item! (Raw text = >>" + section.getRawText() + "<<");
			}
			rest = section.getRawText();
			start = rest.indexOf(LINK_LABEL_START);
			if (start < 0){
				return;
			}
			List<BrightMarkdownSection> children = new ArrayList<>();
			end = 0;
			while (start >= 0){
				end = rest.indexOf(LINK_LOCATION_END, start);
				if (end >= 0){
					if (start > 0){
						children.add(createSection(section, MDType.PLAIN_TEXT, rest.substring(0, start)));
					}
					String text = rest.substring(start, end + 1);
					boolean success = tryToAddLink(section, children, text);
					if (!success){
						children.add(createSection(section, MDType.PLAIN_TEXT, text));
					}
					rest = rest.substring(end + LINK_LOCATION_END.length());
					start = rest.indexOf(LINK_LABEL_START);
				} else {
					start = -1; //: end loop
				}
			}
			if (rest.length() > 0){
				children.add(createSection(section, MDType.PLAIN_TEXT, rest));
			}
			if (!children.isEmpty()){
				section.setChildren(children);
				section.setRawText(null);
			}
		} catch (RuntimeException e){
			throw e;
		}
	}

	private boolean tryToAddLink(BrightMarkdownSection parent, List<BrightMarkdownSection> children, String text) {
		String rest = text;
		if (rest.startsWith(LINK_LABEL_START)){
			rest = rest.substring(1);
		} else{
			return false;
		}
		int pos = rest.indexOf(LINK_LABEL_END);
		if (pos < 0){
			return false;
		}
		String label = rest.substring(0, pos).trim();
		if (label.isEmpty()){
			return false;
		}
		rest = rest.substring(pos + 1).trim();
		if (!rest.startsWith(LINK_LOCATION_START)){
			return false;
		}
		rest = rest.substring(1);
		if (rest.endsWith(LINK_LOCATION_END)){
			rest = rest.substring(0, rest.length() - 1);
		} else{
			return false;
		}
		String location = rest.trim();
		if (location.isEmpty()){
			return false;
		}
		BrightMarkdownSection section = createSection(parent, MDType.LINK, label);
		section.setLocation(location);
		children.add(section);
		return true;
	}

	private void parseFormatting(BrightMarkdownSection topSection, MDType type, String indicator) {
		for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
			parseFormattingForSingleSection(section, type, indicator);
		}
	}
	
	private void parseFormattingForSingleSection(BrightMarkdownSection section, MDType type, String indicator) {
		String rest = "";
		int start = 0;
		int end = 0;
		try{
			if (section.getRawText() == null){
				return;
			}
			if (section.getChildren() != null){
				throw new RuntimeException("Did not expect raw text and children in one section item! (Raw text = >>" + section.getRawText() + "<<");
			}
			rest = section.getRawText();
			start = rest.indexOf(indicator);
			if (start < 0){
				return;
			}
			
			List<BrightMarkdownSection> children = new ArrayList<>();
			end = 0;
			int skippedAtBeginning = 0;
			while (start >= 0){
				if (hasWhitespaceBefore(rest, start)){ //: if there is non whitespace before the indicator, it is within a word, so don't treat it as an indicator
					end = rest.indexOf(indicator, start + indicator.length());
					if (end >= 0){
						skippedAtBeginning = start + indicator.length();
						while (hasWhitespaceAfter(rest, end + indicator.length() - 1)){ //: if the end indicator before more text it is within a word and should be ignored
							skippedAtBeginning += indicator.length();
							end = rest.indexOf(indicator, skippedAtBeginning);
							if (end < 0){
								start = -1; //: end loop
							}
						}
						if (end >= 0){
							if (start > 0){
								children.add(createSection(section, MDType.PLAIN_TEXT, rest.substring(0, start)));
							}
							String text = rest.substring(start + indicator.length(), end);
							if (!text.isEmpty()){
								children.add(createSection(section, type, text));
							} else { //: special case there is an indicator start and an indicator end, but no text in between -> keep expression as plain text
								children.add(createSection(section, MDType.PLAIN_TEXT, indicator + indicator));
							}
							rest = rest.substring(end + indicator.length());
							skippedAtBeginning = 0;
							start = rest.indexOf(indicator);
						}
					} else {
						start = -1; //: end loop
					}
				} else {
					skippedAtBeginning += indicator.length();
					start = rest.indexOf(indicator, skippedAtBeginning);
				}
			}
			if (rest.length() > 0){
				children.add(createSection(section, MDType.PLAIN_TEXT, rest));
			}
			if (!children.isEmpty()){
				section.setChildren(children);
				section.setRawText(null);
			}
		} catch (RuntimeException t){
			RuntimeException exception = new RuntimeException("Coult not execute parseFormattingForSingleSection for type " 
					+ type + " and indicator >>" + indicator + "<<. Rest: >>" + rest + "<<, start = " + start + ", end = " + end, t);
			exception.printStackTrace();
			throw exception;
		}
	}
	
	private boolean hasWhitespaceBefore(String text, int index) {
		if (index <= 0){
			return true; //: treat beginning of line or text as "white space"
		}
		if (index - 1 > text.length()){
			return false; //: treat pos outside of text as "non white space"
		}
		return Character.isWhitespace(text.charAt(index - 1));
	}

	private boolean hasWhitespaceAfter(String text, int index) {
		if (index + 1 > text.length()){
			return true; //: treat end of line or text as "white space"
		}
		if (index < 0){
			return false; //: treat pos outside of text as "non white space"
		}
		return Character.isWhitespace(text.charAt(index - 1));
	}
	
	private BrightMarkdownSection createSection(BrightMarkdownSection parent, MDType type, String rawText){
		BrightMarkdownSection result = new BrightMarkdownSection();
		result.setParent(parent);
		result.setType(type);
		result.setRawText(rawText);
		return result;
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
		result.append("Sec(type=" + section.getType() + ", rawText=" + rawText + ")\n");
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
	
	private void log(String message) {
		System.out.println("BrightMarkdown> " + message);
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
	
}
