package de.bright_side.brightmarkdown.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

public class BMUtil {
	private static void log(String message) {
		if (BMConstants.LOGGING_ACTIVE) {
			System.out.println("BMUtil> " + message);
		}
	}
	
	public static BMSection addSection(BMSection parent, MDType type, String rawText){
		BMSection result = createSection(parent, type, rawText);
		if (parent == null) {
			return result;
		}
		if (parent.getChildren() == null) {
			parent.setChildren(new ArrayList<BMSection>());
		}
		parent.getChildren().add(result);
		
		return result;
	}

	public static BMSection createSection(BMSection parent, MDType type, String rawText){
		BMSection result = new BMSection();
		result.setParent(parent);
		result.setType(type);
		result.setRawText(rawText);
		return result;
	}

	public static boolean isNumber(String text) {
		try {
			Double.valueOf(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String addPropertiesString(boolean condition, String label, Object value) {
		if (!condition) {
			return "";
		}
		return label + "=" + value + "; ";
	}
	
	public static List<BMSection> getAllSectionsAndSubSections(BMSection section){
		return getAllSectionsAndSubSections(section, false);
	}
	
	public static List<BMSection> getAllSectionsAndSubSections(BMSection section, boolean excludeCodeBlocks){
		List<BMSection> result = new ArrayList<>();
		if ((excludeCodeBlocks) && (section.getType() == MDType.CODE_BLOCK)){
			return result;
		}
		result.add(section);
		if (section.getChildren() != null){
			for (BMSection i: section.getChildren()){
				result.addAll(getAllSectionsAndSubSections(i, excludeCodeBlocks));
			}
		}
		return result;
	}
	
	public static boolean isEmptyOrNull(String text) {
		return (text == null) || (text.isEmpty());
	}
	
	public static int countChildren(BMSection section) {
		if (section.getChildren() == null) {
			return 0;
		}
		return section.getChildren().size();
	}

	public static String escapeSpecialCharacters(String text) {
		String result = text;
		log("text before escape: >>" + text.replace("\n", "\\n") + "<<");
		for (int i = 0; i < BMConstants.ESCAPE_CHARACTERS.length; i+= 2){
			String input = BMConstants.ESCAPE_CHARACTERS[i];
			String output = "\\" + BMConstants.ESCAPE_CHARACTERS[i];
			result = result.replace(input, output);
		}
		log("text after escape: >>" + result.replace("\n", "\\n") + "<<");
		return result;
	}

	public static String escape(String text) {
		String result = text;
		log("text before escape: >>" + text.replace("\n", "\\n") + "<<");
		for (int i = 0; i < BMConstants.ESCAPE_CHARACTERS.length; i+= 2){
			String input = "\\" + BMConstants.ESCAPE_CHARACTERS[i];
			String output = BMConstants.ESCAPE_MARK + BMConstants.ESCAPE_CHARACTERS[i + 1] + BMConstants.ESCAPE_MARK;
			result = result.replace(input, output);
		}
		log("text after escape: >>" + result.replace("\n", "\\n") + "<<");
		return result;
	}
	
	public static String unescape(String text) {
//		log("text before unescape: >>" + text.replace("\n", "\\n") + "<<");
		String result = text;
		for (int i = 0; i < BMConstants.ESCAPE_CHARACTERS.length; i+= 2){
			String input = BMConstants.ESCAPE_MARK + BMConstants.ESCAPE_CHARACTERS[i + 1] + BMConstants.ESCAPE_MARK;
			String output = BMConstants.ESCAPE_CHARACTERS[i];
			result = result.replace(input, output);
		}
//		log("text after unescape: >>" + result.replace("\n", "\\n") + "<<");
		return result;
	}
	
	public static void removeDeeperLevels(Map<Integer, ?> levelToListNodeMap, int level) {
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
	
	public static List<String> joinLists(List<String> listA, List<String> listB) {
		List<String> result = new ArrayList<String>(listA);
		result.addAll(listB);
		return result;
	}
	
	public static void logSection(String message, BMSection topSection) {
		if (!BMConstants.LOGGING_ACTIVE) {
			return;
		}
		log(message + ":\n" + toString(topSection));
	}
	
	public static String toString(BMSection section){
		return toString(section, 0);
	}
	
	public static String toString(BMSection section, int indent){
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < indent; i ++){
			result.append(" ");
		}
		String rawText = "(none)";
		if (section.getRawText() != null){
			rawText = ">>" + section.getRawText().replace("\n", "\\n") + "<<";
		}
		String properties = "";
		properties += BMUtil.addPropertiesString(section.getLevel() != null, "level", section.getLevel());
		properties += BMUtil.addPropertiesString(section.isBold(), "bold", section.isBold());
		properties += BMUtil.addPropertiesString(section.isItalic(), "italic", section.isItalic());
		properties += BMUtil.addPropertiesString(section.isUnderline(), "underline", section.isUnderline());
		properties += BMUtil.addPropertiesString(section.isStrikeThrough(), "strikeThrough", section.isStrikeThrough());
		properties += BMUtil.addPropertiesString(section.getColor() != null, "color", section.getColor());
		properties += BMUtil.addPropertiesString(section.getBackgroundColor() != null, "backgroundColor", section.getBackgroundColor());
		properties += BMUtil.addPropertiesString(section.isBackgroundColorEndTag(), "backgroundColorEndTag", section.isBackgroundColorEndTag());
		properties += BMUtil.addPropertiesString(section.getImageWidth() != null, "imageWidth", section.getImageWidth());
		properties += BMUtil.addPropertiesString(section.getImageHeight() != null, "imageHeight", section.getImageHeight());
		properties += BMUtil.addPropertiesString(section.getImageBorder() != null, "imageBorder", section.getImageBorder());
		properties += BMUtil.addPropertiesString(section.isMultiLine(), "multiLine", true);
		properties += BMUtil.addPropertiesString(section.isNested(), "nested", true);
		
		result.append("Sec(type=" + section.getType() + ", rawText=" + rawText);
		if (!properties.isEmpty()) {
			result.append(", properties: {" + properties.trim() + "}");
		}
		result.append(")\n");
		
		if (section.getChildren() != null){
			for (BMSection i : section.getChildren()){
				result.append(toString(i, indent + 4));
			}
		}
		return result.toString();
	}

	public static void addChild(BMSection parent, BMSection child) {
		if (parent.getChildren() == null) {
			parent.setChildren(new ArrayList<BMSection>());
		}
		parent.getChildren().add(child);
	}

	public static boolean hasChildren(BMSection section) {
		if (section == null) {
			return false;
		}
		if (section.getChildren() == null) {
			return false;
		}
		if (section.getChildren().isEmpty()) {
			return false;
		}
		return true;
	}

	
}
