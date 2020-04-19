package de.bright_side.brightmarkdown.logic;

import java.util.ArrayList;
import java.util.List;

import de.bright_side.brightmarkdown.base.BMUtil;
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
	
	public List<BMSection> createSections(BMSection parent, String text, BMCodeFormat format, String escapeNewLine){
		List<BMSection> result = new ArrayList<BMSection>();
		
		if (format == null) {
			result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text, escapeNewLine));
			return result;
		}
		
		int pos = 0;
		int length = text.length();
		
		List<String> tags = new ArrayList<>();
		tags.addAll(format.getStringIndicators());
		tags.add(format.getBlockCommentStart());
		if (format.getLineCommentStart() != null) {
			tags.add(format.getLineCommentStart());
		}
		if (format.getKeywords() != null){
			tags.addAll(format.getKeywords());
		}
		if (format.getTagStarts() != null){
			tags.addAll(format.getTagStarts());
		}
		if (format.getTagEnds() != null){
			tags.addAll(format.getTagEnds());
		}
		
		while (pos < length) {
			BMPosAndTag next = parserLogic.findNext(text, pos, tags);
			if (next == null) {
				result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(pos), escapeNewLine));
				return result;
			}

			if (next.getPos() > pos) { //: more text before the next tag?
				result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(pos, next.getPos()), escapeNewLine));
			}
			
			if (format.getStringIndicators().contains(next.getTag())) {
				pos = processString(parent, text, next.getTag(), next.getPos(), format, result, escapeNewLine);
			} else if (next.getTag().equals(format.getBlockCommentStart())) {
				pos = processComment(parent, text, next.getTag(), next.getPos(), format, result, format.getBlockCommentEnd(), true, MDType.CODE_BLOCK_COMMENT, escapeNewLine);
			} else if (next.getTag().equals(format.getLineCommentStart())) {
				pos = processComment(parent, text, next.getTag(), next.getPos(), format, result, LINE_END, false, MDType.CODE_BLOCK_COMMENT, escapeNewLine);
			} else if (format.getTagStarts().contains(next.getTag())) {
				pos = processTagStart(parent, text, next.getTag(), next.getPos(), format, result, escapeNewLine);
			} else if (format.getTagEnds().contains(next.getTag())) {
				pos = processTagEnd(parent, text, next.getTag(), next.getPos(), format, result, escapeNewLine);
			} else {
				pos = processKeyword(parent, text, next.getTag(), next.getPos(), format, result, escapeNewLine);
			}
		}
		
		return result;
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

	private int processComment(BMSection parent, String text, String tag, int startPos, BMCodeFormat format, List<BMSection> sections, String endTag, boolean includeEndTag, MDType type, String escapeNewLine) {
		int endPos = text.indexOf(endTag, startPos);
		if (endPos < 0) {
			sections.add(createSection(parent, type, text.substring(startPos), escapeNewLine));
			return text.length();
		}
		if (includeEndTag) {
			endPos += endTag.length();
		}
		sections.add(createSection(parent, type, text.substring(startPos, endPos), escapeNewLine));
		return endPos;
	}
	
	private int processString(BMSection parent, String text, String tag, int startPos, BMCodeFormat format, List<BMSection> sections, String escapeNewLine) {
		List<String> tags = new ArrayList<>();
		if (format.getEscapeCharacter() != null) {
			tags.add(format.getEscapeCharacter());
		}
		tags.addAll(format.getStringIndicators());
		int pos = startPos + 1;
		
		BMPosAndTag next = parserLogic.findNext(text, pos, tags);
		
		while (next != null) {
			if (next.getTag().equals(format.getEscapeCharacter())) {
				pos ++;
			} else {
				pos = next.getPos() + next.getTag().length();
				sections.add(createSection(parent, MDType.CODE_BLOCK_STRING, text.substring(startPos, pos), escapeNewLine));
				return pos;
			}
		}
		
		return pos;
	}

}
