package de.bright_side.brightmarkdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;
import de.bright_side.brightmarkdown.BrightMarkdownUtil.PosAndTag;

public class BrightMarkdownFormattingParser {
	
	public static final String INDICATOR_BOLD = "*";
	public static final String INDICATOR_UNDERLINE = "+";
	public static final String INDICATOR_ITALIC = "_";
	public static final String INDICATOR_STRIKETHROUGH = "~";
	public static final String COLOR_TAG_START = "{color";
	public static final String COLOR_SHORT_TAG_START = "{c";
	public static final String BACKGROUND_COLOR_TAG_START = "{bg-color";
	public static final String BACKGROUND_COLOR_SHORT_TAG_START = "{bc";
	public static final String COLOR_TAG_ENDING = "}";
	public static final String COLOR_END_TAG = COLOR_TAG_START + COLOR_TAG_ENDING;
	public static final String COLOR_SHORT_END_TAG = COLOR_SHORT_TAG_START + COLOR_TAG_ENDING;
	public static final String BACKGROUND_COLOR_END_TAG = BACKGROUND_COLOR_TAG_START + COLOR_TAG_ENDING;
	public static final String BACKGROUND_COLOR_SHORT_END_TAG = BACKGROUND_COLOR_SHORT_TAG_START + COLOR_TAG_ENDING;
	
	private static final List<String> TAG_STARTS = Arrays.asList(INDICATOR_BOLD, INDICATOR_UNDERLINE, INDICATOR_ITALIC, INDICATOR_STRIKETHROUGH, COLOR_TAG_START, BACKGROUND_COLOR_TAG_START, COLOR_SHORT_TAG_START, BACKGROUND_COLOR_SHORT_TAG_START);
	private static final Set<String> COLOR_TAG_STARTS = new HashSet<String>(Arrays.asList(COLOR_TAG_START, COLOR_SHORT_TAG_START, BACKGROUND_COLOR_TAG_START, BACKGROUND_COLOR_SHORT_TAG_START));

	private static final Set<String> FORMATTING_INDICATORS = new HashSet<String>(Arrays.asList(INDICATOR_BOLD, INDICATOR_UNDERLINE, INDICATOR_ITALIC, INDICATOR_STRIKETHROUGH));

	
	public static final Set<String> VALID_COLOR_NAMES = new TreeSet<String>(Arrays.asList("black", "blue", "brown", "cyan", "gold", "gray", "grey", "green", "lightgray", "lightgrey", "darkgray", "darkgrey", "magenta", "red", "teal", "white", "yellow", "pink"));
	

	private static final Pattern HEX_COLOR_CODE_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
	
	protected List<BrightMarkdownSection> createFormattedSections(String text){
		Map<Integer, String> tags = parseFormattingTags(text);
		return createSections(text, tags);
	}
	
	
	protected Map<Integer, String> parseFormattingTags(String text){
		Map<Integer, String> result = new TreeMap<Integer, String>();
		if (text == null) {
			return result;
		}
		int length = text.length();
		if ((text == null) || (length == 0)){
			return result;
		}
		int pos = 0;
		BrightMarkdownUtil util = new BrightMarkdownUtil();
		while (pos < length) {
			PosAndTag next = util.findNext(text, pos, TAG_STARTS);
			if (next == null) {
				return result;
			}
			
//			if ((next.tag.equals(COLOR_TAG_START)) || (next.tag.equals(BACKGROUND_COLOR_TAG_START))) { //: special case color tag: read until end
			if (COLOR_TAG_STARTS.contains(next.tag)) { //: special case color tag: read until end
				int end = text.indexOf(COLOR_TAG_ENDING, next.pos + 1);
				if (end > 0) {
					result.put(next.pos, text.substring(next.pos, end + 1));
					pos = end + COLOR_TAG_ENDING.length();
				} else {
					pos++;
				}
			} else {
				if (!isCharPartOfWord(text, next.pos)) {
					result.put(next.pos, next.tag);
				}
				pos = next.pos + next.tag.length();
			}
		}
		
		return result;
	}

	
	protected List<BrightMarkdownSection> createSections(String text, Map<Integer, String> tags){
		List<BrightMarkdownSection> result = new ArrayList<BrightMarkdownSection>();
		if (text == null) {
			return result;
		}
		
		boolean bold = false;
		boolean italic = false;
		boolean underline = false;
		boolean strikeThrough = false;
		String color = null;
		String backgroundColor = null;
		
		int lastPos = 0;
		for (Map.Entry<Integer, String> i: tags.entrySet()) {
			String sectionText = text.substring(lastPos, i.getKey());
			if (!sectionText.isEmpty()) {
				result.add(createSection(sectionText, bold, italic, underline, strikeThrough, color, backgroundColor));
			}
			
			if (i.getValue().equals(INDICATOR_BOLD)) {
				bold = !bold;
			} else if (i.getValue().equals(INDICATOR_ITALIC)) {
				italic = !italic;
			} else if (i.getValue().equals(INDICATOR_UNDERLINE)) {
				underline = !underline;
			} else if (i.getValue().equals(INDICATOR_STRIKETHROUGH)) {
				strikeThrough = !strikeThrough;
			} else if (i.getValue().equals(COLOR_END_TAG)) {
				color = null;
			} else if (i.getValue().equals(BACKGROUND_COLOR_END_TAG)) {
				backgroundColor = null;
			} else if (i.getValue().startsWith(COLOR_TAG_START)) {
				color = parseColor(i.getValue().substring(COLOR_TAG_START.length()));
			} else if (i.getValue().startsWith(COLOR_SHORT_TAG_START)) {
				color = parseColor(i.getValue().substring(COLOR_SHORT_TAG_START.length()));
			} else if (i.getValue().startsWith(BACKGROUND_COLOR_TAG_START)) {
				backgroundColor = parseColor(i.getValue().substring(BACKGROUND_COLOR_TAG_START.length()));
			} else if (i.getValue().startsWith(BACKGROUND_COLOR_SHORT_TAG_START)) {
				backgroundColor = parseColor(i.getValue().substring(BACKGROUND_COLOR_SHORT_TAG_START.length()));
			}
			
			lastPos = i.getKey() + i.getValue().length();
			
		}
		String restText = text.substring(lastPos);
		if (!restText.isEmpty()){
			result.add(createSection(restText, bold, italic, underline, strikeThrough, color, backgroundColor));
		}
		
		return result;
	}
	
	private BrightMarkdownSection createSection(String text, boolean bold, boolean italic, boolean underline, boolean strikeThrough, String color, String backgroundColor) {
		BrightMarkdownSection result = new BrightMarkdownSection();
		result.setType(MDType.FORMATTED_TEXT);
		result.setRawText(text);
		result.setBold(bold);
		result.setItalic(italic);
		result.setUnderline(underline);
		result.setStrikeThrough(strikeThrough);
		result.setColor(color);
		result.setBackgroundColor(backgroundColor);
		return result;
	}


	private String parseColor(String text) {
		String useText = text.trim();
		if ((useText.isEmpty()) || (!useText.endsWith(COLOR_TAG_ENDING))){
			return null;
		}
		
		useText = useText.substring(0, useText.length() - COLOR_TAG_ENDING.length()).trim().toLowerCase();
		if (!useText.startsWith(":")) {
			return null;
		}

		useText = useText.substring(1).trim();
		
		if (VALID_COLOR_NAMES.contains(useText)) {
			return useText;
		}
		
		if (!useText.startsWith("#")) {
			return null;
		}

		if (HEX_COLOR_CODE_PATTERN.matcher(useText).matches()) {
			return useText;
		}
		
		return null;
	}


	protected boolean isCharPartOfWord(String text, int index) {
		Character before = getCharBefore(text, index);
		Character after = getCharAfter(text, index);
		
		//: if there are multiple markers at the beginning or the end: keep reading text before and after while it is a marker
		int pos = index;
		while (isFormattingIndicator(before)) {
			pos --;
			before = getCharBefore(text, pos);
		}
		
		pos = index;
		while (isFormattingIndicator(after)) {
			pos ++;
			after = getCharAfter(text, pos);
		}
		
		
		return (isText(before)) && (isText(after));
	}

	
	private boolean isFormattingIndicator(Character character) {
		return ((character != null) && (FORMATTING_INDICATORS.contains("" + character)));
	}


	protected Character getCharAfter(String text, int pos) {
		if ((text == null) || (pos < 0) | (pos + 1 > text.length() - 1)){
			return null;
		}
		return text.charAt(pos + 1);
	}


	protected Character getCharBefore(String text, int pos) {
		if ((text == null) || (pos <= 0) | (pos - 1 > text.length() - 1)){
			return null;
		}
		return text.charAt(pos - 1);
	}


	private boolean isText(Character c) {
		if (c == null) {
			return false;
		}

		if ((Character.isAlphabetic(c)) || (Character.isDigit(c)) || (c == '_')){
			return true;
		}
		
		
		return false;
	}


}
