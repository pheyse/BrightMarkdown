package de.bright_side.brightmarkdown;

import java.util.ArrayList;
import java.util.List;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;
import de.bright_side.brightmarkdown.BrightMarkdownUtil.PosAndTag;

public class BrightMarkdownCodeParser {
	private static final String LINE_END = "\n";
	
	protected List<BrightMarkdownSection> createSections(BrightMarkdownSection parent, String text, BrightMarkdownCodeFormat format, String escapeNewLine){
		List<BrightMarkdownSection> result = new ArrayList<BrightMarkdownSection>();
		
		if (format == null) {
			result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text, escapeNewLine));
			return result;
		}
		
		int pos = 0;
		int length = text.length();
		
		BrightMarkdownUtil util = new BrightMarkdownUtil();
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
			PosAndTag next = util.findNext(text, pos, tags);
			if (next == null) {
				result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(pos), escapeNewLine));
				return result;
			}

			if (next.pos > pos) { //: more text before the next tag?
				result.add(createSection(parent, MDType.CODE_BLOCK_COMMAND, text.substring(pos, next.pos), escapeNewLine));
			}
			
			if (format.getStringIndicators().contains(next.tag)) {
				pos = processString(parent, text, next.tag, next.pos, format, result, escapeNewLine);
			} else if (next.tag.equals(format.getBlockCommentStart())) {
				pos = processComment(parent, text, next.tag, next.pos, format, result, format.getBlockCommentEnd(), true, MDType.CODE_BLOCK_COMMENT, escapeNewLine);
			} else if (next.tag.equals(format.getLineCommentStart())) {
				pos = processComment(parent, text, next.tag, next.pos, format, result, LINE_END, false, MDType.CODE_BLOCK_COMMENT, escapeNewLine);
			} else if (format.getTagStarts().contains(next.tag)) {
				pos = processTagStart(parent, text, next.tag, next.pos, format, result, escapeNewLine);
			} else if (format.getTagEnds().contains(next.tag)) {
				pos = processTagEnd(parent, text, next.tag, next.pos, format, result, escapeNewLine);
			} else {
				pos = processKeyword(parent, text, next.tag, next.pos, format, result, escapeNewLine);
			}
		}
		
		return result;
	}

	
	private BrightMarkdownSection createSection(BrightMarkdownSection parent, MDType type, String rawText, String escapeNewLineInCodeBlock){
		String text = rawText;
		if (text != null) {
			text = text.replace("\n", escapeNewLineInCodeBlock);
		}
		return BrightMarkdownUtil.createSection(parent, type, text);
	}
	
	
	private int processTagEnd(BrightMarkdownSection parent, String text, String tag, int pos, BrightMarkdownCodeFormat format, List<BrightMarkdownSection> sections, String escapeNewLine) {
		sections.add(createSection(parent, MDType.CODE_BLOCK_TAG, text.substring(pos, pos + tag.length()), escapeNewLine));
		return pos + tag.length();
	}

	private int processKeyword(BrightMarkdownSection parent, String text, String tag, int pos, BrightMarkdownCodeFormat format, List<BrightMarkdownSection> sections, String escapeNewLine) {
		sections.add(createSection(parent, MDType.CODE_BLOCK_KEYWORD, text.substring(pos, pos + tag.length()), escapeNewLine));
		return pos + tag.length();
	}

	private int processTagStart(BrightMarkdownSection parent, String text, String tag, int startPos, BrightMarkdownCodeFormat format, List<BrightMarkdownSection> sections, String escapeNewLine) {
		List<String> tags = new ArrayList<>();
		if (format.getTagEnds() != null) {
			tags.addAll(format.getTagEnds());
		}
		tags.add(" ");
		PosAndTag next = new BrightMarkdownUtil().findNext(text, startPos, tags);

		int endPos = 0;
		if (next == null) {
			sections.add(createSection(parent, MDType.CODE_BLOCK_TAG, text.substring(startPos), escapeNewLine));
			return text.length();
 		} else {
 			if (next.tag.equals(" ")) {
 				endPos = next.pos;
 			} else {
 				endPos = next.pos + next.tag.length();
 			}
 			sections.add(createSection(parent, MDType.CODE_BLOCK_TAG, text.substring(startPos, endPos), escapeNewLine));		
 		}
		return endPos;
	}

	private int processComment(BrightMarkdownSection parent, String text, String tag, int startPos, BrightMarkdownCodeFormat format, List<BrightMarkdownSection> sections, String endTag, boolean includeEndTag, MDType type, String escapeNewLine) {
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
	
	private int processString(BrightMarkdownSection parent, String text, String tag, int startPos, BrightMarkdownCodeFormat format, List<BrightMarkdownSection> sections, String escapeNewLine) {
		List<String> tags = new ArrayList<>();
		if (format.getEscapeCharacter() != null) {
			tags.add(format.getEscapeCharacter());
		}
		tags.addAll(format.getStringIndicators());
		int pos = startPos + 1;
		
		BrightMarkdownUtil util = new BrightMarkdownUtil();
		PosAndTag next = util.findNext(text, pos, tags);
		
		while (next != null) {
			if (next.tag.equals(format.getEscapeCharacter())) {
				pos ++;
			} else {
				pos = next.pos + next.tag.length();
				sections.add(createSection(parent, MDType.CODE_BLOCK_STRING, text.substring(startPos, pos), escapeNewLine));
				return pos;
			}
		}
		
		return pos;
	}

}
