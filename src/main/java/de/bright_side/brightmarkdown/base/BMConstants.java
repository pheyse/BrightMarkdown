package de.bright_side.brightmarkdown.base;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.bright_side.brightmarkdown.logic.BMDefaultCodeFormatCreator;
import de.bright_side.brightmarkdown.model.BMCodeFormat;

public class BMConstants {
	public static final boolean LOGGING_ACTIVE = false;
	
	
	public static final String[] ESCAPE_CHARACTERS = {"\\", "b", "*", "a", "_", "u", "{", "1", "}", "2", "[", "3", "]", "4", "(", "5", ")", "6"
            , "#", "h", "+", "p", "-", "m", ".", "d", "~", "t", "`", "c", "´", "7", "{", "o", "|", "s", "=", "e", "o", "r"};

	public static final String ESCAPE_MARK = "%%";

	public static final String[] HEADINGS_INDICATOR = {"#", "##", "###", "####", "#####", "######"};
	public static final String[] BULLET_POINT_INDICATORS_A = {"*", "**", "***", "****", "*****"};
	public static final String[] BULLET_POINT_INDICATORS_B = {"+", "++", "+++", "++++", "+++++"};
	public static final String[] BULLET_POINT_INDICATORS_C = {"-", "--", "---", "----", "-----"};
	public static final String[] BULLET_POINT_INDICATORS_D = {"o", "oo", "ooo", "oooo", "ooooo"};
	public static final char NUMBERED_ITEM_INDICATOR_CHAR = '.';
	public static final String[] BULLET_POINT_INDICATORS_CHARS = {BULLET_POINT_INDICATORS_A[0], BULLET_POINT_INDICATORS_B[0], BULLET_POINT_INDICATORS_C[0], BULLET_POINT_INDICATORS_D[0]};
	public static final List<String> BULLET_POINT_INDICATORS_CHARS_LIST = Arrays.asList(BULLET_POINT_INDICATORS_CHARS);
	public static final List<String> NUMBERED_ITEM__INDICATORS_CHARS_LIST = Arrays.asList(new String[]{"" + NUMBERED_ITEM_INDICATOR_CHAR});
	public static final String[] UNCHECKED_ITEM_INDICATORS = {"[]", "[ ]", "- []", "- [ ]"};
	public static final String[] CHECKED_ITEM_INDICATORS = {"[x]", "[X]", "- [x]", "- [X]"};
	public static final String[] NUMBERED_ITEM_INDICATORS = createNumberedItemIndicatorList(100);
	public static final String[] HORIZONTAL_RULE_INDICATORS = {"-", "_", "#", "*", "="};

	public static final String IMAGE_LINK_PREFIX = "!";
	public static final String LINK_LABEL_START_A = "[";
	public static final String LINK_LABEL_END_A = "]";
	public static final String LINK_LABEL_START_B = "(";
	public static final String LINK_LABEL_END_B = ")";
	public static final String LINK_LOCATION_START_A = "[";
	public static final String LINK_LOCATION_END_A = "]";
	public static final String LINK_LOCATION_START_B = "(";
	public static final String LINK_LOCATION_END_B = ")";
	
	public static final List<String> IMAGE_LINK_LABEL_START_TAGS = Arrays.asList(IMAGE_LINK_PREFIX + LINK_LABEL_START_A, IMAGE_LINK_PREFIX + LINK_LABEL_START_B);
	public static final List<String> LINK_LABEL_START_TAGS = Arrays.asList(LINK_LABEL_START_A, LINK_LABEL_START_B);
	public static final List<String> LINK_LABEL_END_TAGS = Arrays.asList(LINK_LABEL_END_A, LINK_LABEL_END_B);
	public static final List<String> LINK_LOCATION_START_TAGS = Arrays.asList(LINK_LOCATION_START_A, LINK_LOCATION_START_B);
	public static final List<String> LINK_LOCATION_END_TAGS = Arrays.asList(LINK_LOCATION_END_A, LINK_LOCATION_END_B);
	
	public static final List<String> LINK_AND_IMAGE_LABEL_START_TAGS = BMUtil.joinLists(IMAGE_LINK_LABEL_START_TAGS, LINK_LABEL_START_TAGS);
	
	public static final String LINE_BREAK = "\n";
	public static final String TABLE_OF_CONTENT_MARKER = "{TOC}";
	public static final String TABLE_CELL_SEPARATOR = "|";
	public static final String CODE_BLOCK_MARK_A_LONG = "```";
	public static final String CODE_BLOCK_MARK_B_LONG = "´´´";
	public static final String CODE_BLOCK_MARK_A_SHORT = "``";
	public static final String CODE_BLOCK_MARK_B_SHORT = "´´";
	public static final List<String> CODE_BLOCK_MARKS = Arrays.asList(CODE_BLOCK_MARK_A_LONG, CODE_BLOCK_MARK_B_LONG, CODE_BLOCK_MARK_A_SHORT, CODE_BLOCK_MARK_B_SHORT);
	public static final String CODE_BLOCK_SPECIAL_FORMAT_SECTION_MARK = "!!!";
	public static final String CODE_BLOCK_SPECIAL_FORMAT_ELIPSE = "...";
	public static final String CODE_BLOCK_SPECIAL_FORMAT_HIGHLIGHT = "hl";
	public static final String CODE_BLOCK_SPECIAL_FORMAT_PLACEHOLDER = "ph";
	public static final String CODE_BLOCK_SPECIAL_FORMAT_INFO = "info";

	public static final String NO_MARKDOWN_MARK = "{NOMARKDOWN}";
	public static final String ESCAPE_NEW_LINE_IN_CODE_BLOCK = "%%N%%";
	public static final int LIST_INDENT_LEVEL_THRESHOLD = 3;

	public static final String IMAGE_WIDTH_LABEL = "width=";
	public static final String IMAGE_HEIGHT_LABEL = "height=";
	public static final String IMAGE_BORDER_LABEL = "border=";


	public static final String CODE_BLOCK_HIGHLIGHT_BACKGROUND_COLOR = "#f7ea04";
	public static final String CODE_BLOCK_HIGHLIGHT_FOREGROUND_COLOR = "black";
	public static final String CODE_BLOCK_PLACEHOLDER_FOREGROUND_COLOR = "#0000ff";
	public static final String CODE_BLOCK_SPECIAL_FORMAT_ELIPSE_FORGROUND_COLOR = "grey";
	public static final String CODE_BLOCK_SPECIAL_FORMAT_ELIPSE_BACKGROUND_COLOR = "lightgreen";
	public static final String CODE_BLOCK_INFO_FOREGROUND_COLOR = "#4b2eff";
	public static final String CODE_BLOCK_INFO_BACKGROUND_COLOR = "#efefef";
	
	public static final Map<String, BMCodeFormat> CODE_FORMATS = new BMDefaultCodeFormatCreator().createCodeFormats();

	
	private static String[] createNumberedItemIndicatorList(int end) {
		String[] result = new String[end + 1];
		result[0] = "" + NUMBERED_ITEM_INDICATOR_CHAR;
		for (int i = 1; i <= end; i ++){
			result[i] = i + ".";
		}
		return result;
	}


}
