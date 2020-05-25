package de.bright_side.brightmarkdown.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.logic.BMTextParserLogic.MatchConfirmer;
import de.bright_side.brightmarkdown.model.BMCodeFormat;
import de.bright_side.brightmarkdown.model.BMPosAndTag;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

/**
 * parser for code blocks (e.g. to apply Java formatting)
 * @author Philip Heyse
 *
 */
public class BMCodeParser {
	private static final String LINE_END = "\n";
	private BMTextParserLogic parserLogic = new BMTextParserLogic();
	private static final String SPECIAL_FORMAT_SECTION_SEPARATOR = "!";
	private static final String SPECIAL_FORMAT_SECTION_ESCAPE = "\\";
	
	public static final String SPECIAL_FORMAT_COLOR_LONG = "color";
	public static final String SPECIAL_FORMAT_COLOR_SHORT = "c";
	public static final String SPECIAL_FORMAT_BACKGROUND_COLOR_LONG = "bg-color";
	public static final String SPECIAL_FORMAT_BACKGROUND_COLOR_SHORT = "bc";
	public static final String SPECIAL_FORMAT_BOLD = "b";
	public static final String SPECIAL_FORMAT_ITALIC = "i";
	public static final String SPECIAL_FORMAT_UNDERLINE = "u";
	
	public List<BMSection> createSections(BMSection parent, String text, BMCodeFormat format, String escapeNewLine){
		List<BMSection> result = new ArrayList<BMSection>();
		
		if ((format == null) && (!text.contains(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK))){
			result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text, escapeNewLine));
			return result;
		}

		final BMCodeFormat useFormat = (format != null) ? format : BMConstants.CODE_FORMATS.get("");
		
		int pos = 0;
		int length = text.length();
		
		List<String> tags = new ArrayList<>();
		addIfNotNull(tags, BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK);
		addIfNotNull(tags, useFormat.getStringIndicators());
		addIfNotNull(tags, useFormat.getBlockCommentStart());
		addIfNotNull(tags, useFormat.getLineCommentStart());
		addIfNotNull(tags, useFormat.getKeywords());
		addIfNotNull(tags, useFormat.getTagStarts());
		addIfNotNull(tags, useFormat.getTagEnds());
		
		MatchConfirmer matchConfirmer = createKeywordMatchConfirmer(useFormat); 
		
		while (pos < length) {
			BMPosAndTag next = parserLogic.findNext(text, pos, useFormat.isKeywordsIgnoreCase(), tags, matchConfirmer);
			log("createSections. pos = " + pos + ", next = " + next);
			if (next == null) {
				result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(pos), escapeNewLine));
				return result;
			}

			if (next.getPos() > pos) { //: more text before the next tag?
				result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(pos, next.getPos()), escapeNewLine));
			}
			
			if (useFormat.getStringIndicators().contains(next.getTag())) {
				pos = processString(parent, text, next.getTag(), next.getTag(), next.getPos(), useFormat, result, escapeNewLine);
			} else if (next.getTag().equals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK)) {
				pos = processSpecialFormatSection(parent, text, next.getPos(), result, escapeNewLine);
			} else if (next.getTag().equals(useFormat.getBlockCommentStart())) {
				pos = processComment(parent, text, next.getTag(), next.getPos(), useFormat, result, useFormat.getBlockCommentEnd(), true, MDType.CODE_BLOCK_COMMENT, escapeNewLine);
			} else if (next.getTag().equals(useFormat.getLineCommentStart())) {
				pos = processComment(parent, text, next.getTag(), next.getPos(), useFormat, result, LINE_END, false, MDType.CODE_BLOCK_COMMENT, escapeNewLine);
			} else if (useFormat.getTagStarts().contains(next.getTag())) {
				pos = processTagStart(parent, text, next.getTag(), next.getPos(), useFormat, result, escapeNewLine);
			} else if (useFormat.getTagEnds().contains(next.getTag())) {
				pos = processTagEnd(parent, text, next.getTag(), next.getPos(), useFormat, result, escapeNewLine);
			} else {
				pos = processKeyword(parent, text, next.getTag(), next.getPos(), useFormat, result, escapeNewLine);
			}
		}
		
		return result;
	}


	private MatchConfirmer createKeywordMatchConfirmer(final BMCodeFormat useFormat) {
		return new MatchConfirmer() {
			@Override
			public boolean confirmMatch(String text, int pos, String tag) {
				//: match unless a keyword was found but there is more text before/after the keyword. Example: finding "int" in "print" is not a match
				if (!useFormat.getKeywords().contains(tag)) {
					//: not a keyword, then it is ok
					return true;
				}
				
				if (Character.isLetterOrDigit(parserLogic.getCharBeforeOrDefault(text, pos, '*'))){
					return false;
				}
				if (Character.isLetterOrDigit(parserLogic.getCharAfterOrDefault(text, pos + tag.length() - 1, '*'))){
					return false;
				}
				
				return true;
			}
		};
	}

	
	private void addIfNotNull(List<String> tags, String string) {
		if (string != null) {
			tags.add(string);
		}
	}

	private void addIfNotNull(List<String> tags, Collection<String> strings) {
		if (strings != null) {
			tags.addAll(strings);
		}
	}


	private BMSection createSection(BMSection parent, MDType type, String rawText, String escapeNewLineInCodeBlock){
		String text = rawText;
		if (text != null) {
			text = text.replace("\n", escapeNewLineInCodeBlock);
		}
		return BMUtil.createSection(parent, type, text);
	}
	
	
	private int processTagEnd(BMSection parent, String text, String tag, int pos, BMCodeFormat format, List<BMSection> sections, String escapeNewLine) {
		sections.add(createSection(parent, MDType.CODE_BLOCK_TAG, text.substring(pos, pos + tag.length()), escapeNewLine));
		return pos + tag.length();
	}

	private int processKeyword(BMSection parent, String text, String tag, int pos, BMCodeFormat format, List<BMSection> sections, String escapeNewLine) {
		sections.add(createSection(parent, MDType.CODE_BLOCK_KEYWORD, text.substring(pos, pos + tag.length()), escapeNewLine));
		return pos + tag.length();
	}

	private int processTagStart(BMSection parent, String text, String tag, int startPos, BMCodeFormat format, List<BMSection> sections, String escapeNewLine) {
		List<String> tags = new ArrayList<>();
		if (format.getTagEnds() != null) {
			tags.addAll(format.getTagEnds());
		}
		tags.add(" ");
		BMPosAndTag next = parserLogic.findNext(text, startPos, tags);

		int endPos = 0;
		if (next == null) {
			sections.add(createSection(parent, MDType.CODE_BLOCK_TAG, text.substring(startPos), escapeNewLine));
			return text.length();
 		} else {
 			if (next.getTag().equals(" ")) {
 				endPos = next.getPos();
 			} else {
 				endPos = next.getPos() + next.getTag().length();
 			}
 			sections.add(createSection(parent, MDType.CODE_BLOCK_TAG, text.substring(startPos, endPos), escapeNewLine));		
 		}
		return endPos;
	}

	private int processComment(BMSection parent, String text, String tag, int startPos, BMCodeFormat format, List<BMSection> sections, String endTag
			, boolean includeEndTag, MDType type, String escapeNewLine) {
		
		return processStringOrComment(parent, text, tag, endTag, startPos, format, sections, includeEndTag, type, escapeNewLine);
		
		
//		int endPos = text.indexOf(endTag, startPos);
//		if (endPos < 0) {
//			sections.add(createSection(parent, type, text.substring(startPos), escapeNewLine));
//			return text.length();
//		}
//		if (includeEndTag) {
//			endPos += endTag.length();
//		}
//		sections.add(createSection(parent, type, text.substring(startPos, endPos), escapeNewLine));
//		return endPos;
	}
	
	private int processString(BMSection parent, String text, String startTag, String endTag, int startPos
			, BMCodeFormat format, List<BMSection> sections, String escapeNewLine) {
		return processStringOrComment(parent, text, startTag, endTag, startPos
				, format, sections, true, MDType.CODE_BLOCK_STRING, escapeNewLine);
	}
	
	private int processStringOrComment(BMSection parent, String text, String startTag, String endTag, int startPos
			, BMCodeFormat format, List<BMSection> sections, boolean includeEndTag, MDType type, String escapeNewLine) {
		List<String> tags = new ArrayList<>();
		if (format.getEscapeCharacter() != null) {
			tags.add(format.getEscapeCharacter());
		}
		tags.add(endTag);
		tags.add(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK);
		int pos = startPos;
		
		BMPosAndTag next = parserLogic.findNext(text, pos + 1, tags);
		
		while (next != null) {
			if (next.getTag().equals(format.getEscapeCharacter())) {
				pos ++;
			} else{
				if (next.getTag().equals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK)) {
					int length = next.getPos() - startPos;
					if (length > 0) {
						sections.add(createSection(parent, type, text.substring(startPos, next.getPos()), escapeNewLine));
					}
					pos = processSpecialFormatSection(parent, text, next.getPos(), sections, escapeNewLine);
				} else {
					int endPos = next.getPos();
					if (includeEndTag) {
						endPos += next.getTag().length();
					}
					sections.add(createSection(parent, type, text.substring(pos, endPos), escapeNewLine));
					return endPos;
				}
			}
			next = parserLogic.findNext(text, pos + 1, tags);
		}
		
		sections.add(createSection(parent, type, text.substring(pos), escapeNewLine));
		return text.length();
	}

	protected int processSpecialFormatSection(BMSection parent, String text, int startPosIncludingTag, List<BMSection> sections, String escapeNewLine) {
		int tagLength = BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK.length();
		int startPos = startPosIncludingTag + tagLength;
		BMPosAndTag nextItem = parserLogic.findNext(text, startPos, SPECIAL_FORMAT_SECTION_SEPARATOR, SPECIAL_FORMAT_SECTION_ESCAPE, BMConstants.CODE_BLOCK_SPECIAL_FORMAT_ELIPSE);

		//: incorrect special format section that has a start but no end. In this case: just format as a command and continue right after the mark 
		if (nextItem == null) {
			sections.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(startPosIncludingTag, startPosIncludingTag + tagLength), escapeNewLine));
			return startPos;
		}

		//: it is a "..." block
		if (nextItem.getTag().equals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_ELIPSE)) {
			int endPos = nextItem.getPos() + nextItem.getTag().length();
			BMSection section = createSection(parent, MDType.FORMATTED_TEXT, "[...]", escapeNewLine);
			section.setColor(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_ELIPSE_FORGROUND_COLOR);
			section.setBackgroundColor(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_ELIPSE_BACKGROUND_COLOR);
			section.setBold(true);
			section.setItalic(true);
			sections.add(section);
			return endPos;
		}
		
		//: it is an escaped "!" so return all the "!" after the "\"
		if (nextItem.getTag().equals(SPECIAL_FORMAT_SECTION_ESCAPE)) {
			int posAfterEscape = nextItem.getPos() + nextItem.getTag().length();
			int endPos = parserLogic.posAfterCharOccurences(text, posAfterEscape, '!');
			if (posAfterEscape != endPos) {
				sections.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(posAfterEscape, endPos), escapeNewLine));
			}
			return endPos;
		}
		
		String formatString = text.substring(startPos, nextItem.getPos()).trim();
		
		//: find end pos and read text to format
		BMPosAndTag textToFormatEnd = parserLogic.findNext(text, nextItem.getPos() + 1, BMConstants.CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK);
		int textToFormatStart = nextItem.getPos() + nextItem.getTag().length(); 
		String textToFormat = null; 
		int endPos = 0;
		if (textToFormatEnd != null) {
			endPos = textToFormatEnd.getPos() + textToFormatEnd.getTag().length();
			textToFormat = text.substring(textToFormatStart, textToFormatEnd.getPos());
		} else {
			endPos = text.length();
			textToFormat = text.substring(textToFormatStart);
		}

		BMSection section = createSection(parent, MDType.FORMATTED_TEXT, textToFormat, escapeNewLine);
		applySpecialFormat(section, formatString);
		sections.add(section);
		
		return endPos;
	}


	protected void applySpecialFormat(BMSection section, String formatString) {
		if (formatString.length() == 0) {
			return;
		}

		//: process highlight formatting
		if (formatString.equals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_HIGHLIGHT)) {
			section.setBold(true);
			section.setBackgroundColor(BMConstants.CODE_BLOCK_HIGHLIGHT_BACKGROUND_COLOR);
			return;
		}
		//: process placeholder formatting
		if (formatString.equals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_PLACEHOLDER)) {
			section.setBold(true);
			section.setItalic(true);
			section.setColor(BMConstants.CODE_BLOCK_PLACEHOLDER_FOREGROUND_COLOR);
			return;
		}
		//: process info formatting
		if (formatString.equals(BMConstants.CODE_BLOCK_SPECIAL_FORMAT_INFO)) {
			section.setColor(BMConstants.CODE_BLOCK_INFO_FOREGROUND_COLOR);
			section.setBackgroundColor(BMConstants.CODE_BLOCK_INFO_BACKGROUND_COLOR);
			section.setItalic(true);
			return;
		}
		if (SPECIAL_FORMAT_BOLD.equals(formatString)) {
			section.setBold(true);
			return;
		}
		if (SPECIAL_FORMAT_ITALIC.equals(formatString)) {
			section.setItalic(true);
			return;
		}
		if (SPECIAL_FORMAT_UNDERLINE.equals(formatString)) {
			section.setUnderline(true);
			return;
		}
		
		
		//: several formatting items? Then apply each
		if (formatString.contains(" ")) {
			for (String formattingItem : formatString.split(" ")) {
				applySpecialFormat(section, formattingItem.trim());
			}
			return;
		}
		
			
		int pos = formatString.indexOf(":");
		if (pos < 0) {
			//: formatting cannot be read -> ignore it
			return;
		}
		
		String key = formatString.substring(0, pos);
		String value = formatString.substring(pos + 1);
		
		if (value.length() <= 0) {
			//: value is missing -> ignore it
			return;
		}
		
		if ((SPECIAL_FORMAT_COLOR_LONG.equals(key)) || (SPECIAL_FORMAT_COLOR_SHORT.equals(key))) {
			section.setColor(value);
		}
		if ((SPECIAL_FORMAT_BACKGROUND_COLOR_LONG.equals(key)) || (SPECIAL_FORMAT_BACKGROUND_COLOR_SHORT.equals(key))) {
			section.setBackgroundColor(value);
		}
	}

	private void log(String message) {
		if (BMConstants.LOGGING_ACTIVE) {
			System.out.println("BMCodeParser> " + message);
		}
	}

}
