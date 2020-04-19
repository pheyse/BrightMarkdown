package de.bright_side.brightmarkdown.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.model.BMCodeFormat;
import de.bright_side.brightmarkdown.model.BMPosAndTag;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

public class BMSectionParserLogic {
	private BMTextParserLogic textParser = new BMTextParserLogic();
	private List<Exception> warnings = new ArrayList<Exception>();
	private static final Map<String, BMCodeFormat> CODE_FORMATS = new BMDefaultCodeFormatCreator().createCodeFormats();
	private static String SIZE_UNIT_MM = "mm";
	private static String SIZE_UNIT_PIXELS = "px";
	private static String SIZE_UNIT_INCH = "in";
	private static String SIZE_UNIT_PERCENT = "%";
	
	public BMSection parseAll(String markdownText){
		String escapedMarkedown = BMUtil.escape(markdownText);
		BMSection section = toMDSection(escapedMarkedown);
//		log("parseAll: escapedMarkedown = >>\n" + escapedMarkedown + "<<");
		BMUtil.logSection("parseAll: sections before parseCodeSections", section);
		parseCodeSections(section);
//		BMUtil.logSection("parseAll: sections after parseCodeSections", section);
		parseHorizontalRuleEntries(section);
		parseTableOfContentEntries(section);

		parseRawLineEntries(section, MDType.HEADING, BMConstants.HEADINGS_INDICATOR, true);
		parseRawLineEntries(section, MDType.UNCHECKED_ITEM, BMConstants.UNCHECKED_ITEM_INDICATORS, false);
		parseRawLineEntries(section, MDType.CHECKED_ITEM, BMConstants.CHECKED_ITEM_INDICATORS, false);
		parseListEntriesByLevel(section);
		
//		log("parseAll: sections before bullet point processing = >>\n" + BMUtil.toString(section) + "<<");
		parseRawLineEntries(section, MDType.BULLET_POINT, BMConstants.BULLET_POINT_INDICATORS_A, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BMConstants.BULLET_POINT_INDICATORS_B, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BMConstants.BULLET_POINT_INDICATORS_C, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BMConstants.BULLET_POINT_INDICATORS_D, true);
//		log("parseAll: sections after bullet point processing = >>\n" + BMUtil.toString(section) + "<<");
		parseRawLineEntries(section, MDType.NUMBERED_ITEM, BMConstants.NUMBERED_ITEM_INDICATORS, false);
		BMUtil.logSection("before parseTableRows", section);
		parseTableRows(section);
		BMUtil.logSection("after parseTableRows, before parseTextParagraphs", section);
		parseTextParagraphs(section);
		BMUtil.logSection("after parseTextParagraphs, before parseLinks", section);
		parseLinks(section);
		BMUtil.logSection("after parseLinks", section);

		BMUtil.logSection("before formatting", section);
		parseFormatting(section);
		BMUtil.logSection("after formatting", section);
		BMUtil.logSection("parseAll result", section);
		return section;
	}
	
	public void applyImageNameToPathMapping(BMSection topSection, Map<String, String> nameToPathMap) {
		for (BMSection i: BMUtil.getAllSectionsAndSubSections(topSection)){
			if ((i.getType() == MDType.IMAGE) && (i.getLocation() != null)){
				String mappedPath = nameToPathMap.get(i.getLocation());
				if (mappedPath != null) {
					i.setLocation(mappedPath);
				}
			}
		}
	}
	
	public void parseCodeSections(BMSection topSection) {
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection)){
			if (section.getType() == MDType.CODE_BLOCK){
				log("parseCodeSections. raw text = >>" + section.getRawText().replace("\n", "\\n"));
				String formatName = readFormatName(section.getRawText());
				log("parseCodeSections. formatName = '" + formatName + "'");
				BMCodeFormat format = readFormat(formatName);
				log("parseCodeSections. format = " + format);
				String rawTextWithoutFormatInfo = null;
				if (format != null) {
					rawTextWithoutFormatInfo = section.getRawText().substring(formatName.length());
					rawTextWithoutFormatInfo = textParser.removeLeadingChars(rawTextWithoutFormatInfo, 0, Arrays.asList(' ', '\n'));
				} else {
					rawTextWithoutFormatInfo = section.getRawText();
				}
				List<BMSection> codeSections = new BMCodeParser().createSections(topSection, rawTextWithoutFormatInfo, format, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK);
				section.setChildren(codeSections);
				section.setRawText(null);
			}
		}
	}
	
	protected void parseCodeSections_old(BMSection topSection) {
		int pos = 0;
		List<BMSection> sections = new ArrayList<>(topSection.getChildren());
		List<BMSection> newChildren = new ArrayList<>();
		int amount = sections.size();
		
		while (pos < amount){
			BMSection section = sections.get(pos);
			if (isCodeBlockIndicator(section)){
				BMCodeFormat format = readFormat_old(section.getRawText());
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
					BMSection codeBlockSection = BMUtil.createSection(topSection, MDType.CODE_BLOCK, "");
					codeBlockSection.setChildren(new BMCodeParser().createSections(topSection, codeBlock.toString(), format, BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK));
					newChildren.add(codeBlockSection);
					
//					newChildren.add(createSection(topSection, MDType.CODE_BLOCK, codeBlock.toString().replace("\n", ESCAPE_NEW_LINE_IN_CODE_BLOCK)));
				}
			} else {
//				int codeBlockIndicators = parserLogic.countTokens(section, CODE_BLOCK_MARKS);
//				/** true e.g. if a code block indicator was found in a bullet point list item. */
//				boolean expectingClosingIndicator = codeBlockIndicators % 2 == 1; 
				
				
				
				newChildren.add(section);
			}
			pos ++;
		}
		topSection.setChildren(newChildren);
	}
	
	private boolean isCodeBlockIndicator(BMSection section) {
		if (section.getRawText() == null){
			return false;
		}
		String text = section.getRawText().trim();
		for (String i: BMConstants.CODE_BLOCK_MARKS) {
			if (text.startsWith(i)){
				return true;
			}
		}
		return false;
	}

	private void parseHorizontalRuleEntries(BMSection topSection) {
		for (String indicator: BMConstants.HORIZONTAL_RULE_INDICATORS){
			String indicatorWithLength3 = indicator + indicator + indicator;
			for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection)){
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
	
	private void parseTableOfContentEntries(BMSection topSection) {
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection)){
			if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null)){
				String rawText = section.getRawText().trim();
				if ((rawText.equals(BMConstants.TABLE_OF_CONTENT_MARKER))){
					section.setType(MDType.TABLE_OF_CONTENTS);
					section.setRawText("");
				}
			}
		}
	}

	private void parseRawLineEntries(BMSection topSection, MDType type, String[] indicators, boolean setLevelDepth) {
		for (int level = 1; level <= indicators.length; level ++){
			String indicator = indicators[level - 1] + " ";
			for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection)){
				if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null) && (section.getRawText().trim().startsWith(indicator))){
					trimAndRemoveRawTextStart(section, indicator.length());
					section.setOriginalPlainText(removeFormatting(section.getRawText()));
					section.setType(type);
					if (setLevelDepth){
						section.setLevel(level);
					} else {
						section.setLevel(1);
					}
					
					if (BMUtil.hasChildren(section)) {
						section.getChildren().add(0, BMUtil.createSection(section, MDType.RAW_LINE, section.getRawText()));
						section.setRawText(null);
					}
				}
			}
		}
	}

	private void parseTableRows(BMSection topSection) {
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection)){
			if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null) && (section.getRawText().contains(BMConstants.TABLE_CELL_SEPARATOR))){
				List<String> cellTexts = readCellTexts(section.getRawText().trim());
				section.setOriginalPlainText(removeFormatting(section.getRawText()));
				section.setRawText(null);
				section.setType(MDType.TABLE_ROW);
				List<BMSection> children = new ArrayList<BMSection>();
				for (String i: cellTexts) {
					children.add(BMUtil.createSection(section, MDType.TABLE_CELL, i));
				}
				section.setChildren(children);
			}
		}
	}
	
	private List<String> readCellTexts(String fullText) {
		List<String> result = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(fullText, BMConstants.TABLE_CELL_SEPARATOR, true);
		boolean lastWasSeparator = false;
		boolean firstItem = true;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (BMConstants.TABLE_CELL_SEPARATOR.equals(token)) {
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
	
	private void parseLinks(BMSection topSection) {
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection, true)){
			parseLinksAndImagesForSingleSection(section);
		}
	}
	
	private void parseLinksAndImagesForSingleSection(BMSection section) {
		String rest = "";
		boolean linksDetected = false;
		try{
			if (section.getRawText() == null){
				return;
			}		
//			if (section.getChildren() != null){
//				throw new RuntimeException("Did not expect raw text and children in one section item! (Raw text = >>" + section.getRawText() + "<<");
//			}
			rest = section.getRawText();
			
			BMPosAndTag labelStart = textParser.findNext(rest, 0, BMConstants.LINK_AND_IMAGE_LABEL_START_TAGS);
			
			if (labelStart == null){
				return;
			}
			List<BMSection> children = new ArrayList<>();
			BMPosAndTag labelEnd = null;
			while (labelStart != null){
				labelEnd = textParser.findNext(rest, labelStart.getPos() + labelStart.getTag().length(), BMConstants.LINK_LABEL_END_TAGS);
				String labelText = rest.substring(labelStart.getPos() + labelStart.getTag().length(), labelEnd.getPos());
				if (labelEnd != null) {
//					String nextChar = textParser.readUntilLengthOrEnd(rest, labelEnd.getPos(), 1);
//					boolean nextParenthesisStartRightAfterwards = (BMConstants.LINK_LOCATION_START_A.equals(nextChar)) || (BMConstants.LINK_LOCATION_START_B.equals(nextChar));
					boolean isImageTag = labelStart.getTag().startsWith(BMConstants.IMAGE_LINK_PREFIX);
					
					
					BMPosAndTag locationStart = textParser.findNext(rest, labelEnd.getPos(), BMConstants.LINK_LOCATION_START_TAGS);
					if ((locationStart != null) && (locationStart.getPos() == labelEnd.getPos() + 1)){
						BMPosAndTag locationEnd = textParser.findNext(rest, locationStart.getPos(), BMConstants.LINK_LOCATION_END_TAGS);
						if (locationEnd != null) {
							String leadingPlainText = rest.substring(0, labelStart.getPos());
							if (!leadingPlainText.isEmpty()) {
								children.add(BMUtil.createSection(section, MDType.PLAIN_TEXT, leadingPlainText));
							}
							
//							String labelText = rest.substring(labelStart.getPos() + labelStart.getTag().length(), labelEnd.getPos());
							String locationText = rest.substring(locationStart.getPos() + 1, locationEnd.getPos());

							if ((!labelText.isEmpty()) && (!locationText.isEmpty())){
								BMSection linkSection = null;
								if (isImageTag) {
									linkSection = BMUtil.createSection(section, MDType.IMAGE, null);
									readImageLocationAndSize(linkSection, locationText);
									linkSection.setImageAltText(labelText);
								} else {
									linkSection = BMUtil.createSection(section, MDType.LINK, labelText);
									linkSection.setLocation(locationText);
								}
								children.add(linkSection);
								linksDetected = true;
							}

							rest = rest.substring(locationEnd.getPos() + locationEnd.getTag().length());
							labelStart = textParser.findNext(rest, 0, BMConstants.LINK_AND_IMAGE_LABEL_START_TAGS);
						} else {
							labelStart = null; //: end loop
						}
					} else {
						if (isImageTag) {
							//: image tag with only one set of parenthesis: then the first set is not the alternative text but the location and the image tag can be used

							String leadingPlainText = rest.substring(0, labelStart.getPos());
							if (!leadingPlainText.isEmpty()) {
								children.add(BMUtil.createSection(section, MDType.PLAIN_TEXT, leadingPlainText));
							}

							String locationText = labelText;
							labelText = null;
							BMSection linkSection = BMUtil.createSection(section, MDType.IMAGE, null);
							readImageLocationAndSize(linkSection, locationText);
							linkSection.setImageAltText(labelText);
							children.add(linkSection);
							linksDetected = true;
							
							rest = rest.substring(labelEnd.getPos() + labelEnd.getTag().length());
							labelStart = textParser.findNext(rest, 0, BMConstants.LINK_AND_IMAGE_LABEL_START_TAGS);
						} else {
							labelStart = null; //: end loop
						}
					}
				}
			}
			if (rest.length() > 0){
				children.add(BMUtil.createSection(section, MDType.PLAIN_TEXT, rest));
			}

			//: the raw text of the section was converted into the text and links. If it had sub sections, then those should appear after the 
			//: new child nodes of the section
			if (section.getChildren() != null) {
				children.addAll(section.getChildren());
			}
			
			if ((linksDetected) && (!children.isEmpty())){
				section.setChildren(children);
				section.setRawText(null);
			}
		} catch (RuntimeException e){
			throw e;
		}
	}

	private void log(String message) {
		if (BMConstants.LOGGING_ACTIVE) {
			System.out.println("BMSectionParserLogic> " + message);
		}
	}

	protected void readImageLocationAndSize(BMSection section, String text) {
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
			if (item.startsWith(BMConstants.IMAGE_WIDTH_LABEL)) {
				String sizeInfo = item.substring(BMConstants.IMAGE_WIDTH_LABEL.length()).trim();
				if (isValidSize(sizeInfo)) {
					section.setImageWidth(sizeInfo);
				}
			} else if (item.startsWith(BMConstants.IMAGE_HEIGHT_LABEL)) {
				String sizeInfo = item.substring(BMConstants.IMAGE_HEIGHT_LABEL.length()).trim();
				if (isValidSize(sizeInfo)) {
					section.setImageHeight(sizeInfo);
				}
			} else if (item.startsWith(BMConstants.IMAGE_BORDER_LABEL)) {
				String sizeInfo = item.substring(BMConstants.IMAGE_BORDER_LABEL.length()).trim();
				if (isValidSize(sizeInfo)) {
					section.setImageBorder(sizeInfo);
				}
			}
		}
	}

	protected boolean isValidSize(String sizeInfo) {
		if (BMUtil.isNumber(sizeInfo)) {
			return true;
		}
		if (sizeInfo.length() < 2) {
			return false;
		}
		
		int unitLength = 2;
		if (sizeInfo.endsWith(SIZE_UNIT_PERCENT)) {
			unitLength = 1;
		}
		
		String unit = sizeInfo.substring(sizeInfo.length() - unitLength);
		String value = sizeInfo.substring(0, sizeInfo.length() - unitLength);
		if (!BMUtil.isNumber(value)) {
			return false;
		}
		if (!Arrays.asList(SIZE_UNIT_MM, SIZE_UNIT_PIXELS, SIZE_UNIT_INCH, SIZE_UNIT_PERCENT).contains(unit)) {
			return false;
		}
		return true;
	}

	private void trimAndRemoveRawTextStart(BMSection section, int length) {
		section.setRawText(section.getRawText().trim().substring(length));
	}

	private void parseFormatting(BMSection topSection) {
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection, true)){
			List<BMSection> formattedSections = new BMFormattingParser().createFormattedSections(section.getRawText());
			if (!formattedSections.isEmpty()) {
				//: if there is still only one section without formatting: just keep it and there is no need for children with formatting
				if ((formattedSections.size() != 1) || (hasFormatting(formattedSections.get(0)))){
					if (BMUtil.hasChildren(section)) {
						section.getChildren().addAll(0, formattedSections);
					} else {
						section.setChildren(formattedSections);
					}
					section.setRawText(null);
				}
			}
		}
		
		BMUtil.logSection("after formatting parser", topSection);

		new BMFormatCascader().cascadeFormatting(topSection);
		BMUtil.logSection("after cascade formatting", topSection);
	}
	

	protected void parseListEntriesByLevel(BMSection topSection) {
		Integer previousListItemIndent = null;
		Integer previousListItemLevel = null;
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection)){
			String rawText = section.getRawText();
			if ((section.getType() == MDType.RAW_LINE) && (rawText != null)){
				MDType type = MDType.BULLET_POINT;
				Integer listItemLevelIndent = readListItemIndet(rawText, BMConstants.BULLET_POINT_INDICATORS_CHARS_LIST);
				if (listItemLevelIndent == null) { //: could not find bullet point, try numbered item indicator
					listItemLevelIndent = readListItemIndet(rawText, BMConstants.NUMBERED_ITEM__INDICATORS_CHARS_LIST);
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
					
					if (Math.abs(indentDifference) < BMConstants.LIST_INDENT_LEVEL_THRESHOLD){
						//: only few spaces, assume same level
						listItemLevel = previousListItemLevel;
					} else if (indentDifference > 0){
						//: one more level
						listItemLevel = previousListItemLevel + 1;
						levelToIndentMap.put(listItemLevel, listItemLevelIndent);
					} else {
						//: one or more(!) levels up
						listItemLevel = findMatchingLevel(levelToIndentMap, listItemLevelIndent);
						BMUtil.removeDeeperLevels(levelToIndentMap, listItemLevel);
					}
					previousListItemIndent = listItemLevelIndent;
					previousListItemLevel = listItemLevel;

				}
				
				if (listItemLevel != null){
					section.setType(type);
					section.setLevel(listItemLevel);
					String newRawText = rawText.substring(textParser.findPosAfterLeadindSpaces(rawText, listItemLevelIndent));
					if (BMUtil.hasChildren(section)) {
						section.getChildren().add(0, BMUtil.createSection(section, MDType.RAW_LINE, newRawText));
						section.setRawText(null);
					} else {
						section.setRawText(newRawText);
					}
				}
			}
		}
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

	private void parseTextParagraphs(BMSection topSection) {
		BMSection paragraphSection = null;
		List<Integer> sectionsToRemove = new ArrayList<Integer>();
		if (topSection.getChildren() == null){
			return;
		}

		int sectionIndex = 0;
		for (BMSection section: topSection.getChildren()){
			BMUtil.logSection("parseTextParagraphs. processing secion (index = " + sectionIndex + ")", section);
			if ((section.getType() == MDType.RAW_LINE)){
				if (paragraphSection == null){
					paragraphSection = section;
					section.setType(MDType.PARAGRAPH);
					if (paragraphSection.getChildren() == null){
						log("parseTextParagraphs. No children. Create new list");
						paragraphSection.setChildren(new ArrayList<BMSection>());
					}
				} else {
					log("parseTextParagraphs. The section with index " + sectionIndex + " needs to be removed");
					sectionsToRemove.add(sectionIndex);
				}
				boolean hasChildren = BMUtil.hasChildren(section);
				
				//: usually trim the text, but in the special case that there are children, e.g. because there is a code sub section the space before that section should be kept.
				boolean trim = !hasChildren;
				BMSection paragraphElementSection = BMUtil.createSection(paragraphSection, MDType.PARAGRAPH_ELEMENT, toNonNullString(section.getRawText(), trim));
				List<BMSection> sectionOldChildren = null;
				
				if (hasChildren) {
					sectionOldChildren = new ArrayList<BMSection>(section.getChildren());
					section.getChildren().clear();
				}
				
				paragraphSection.getChildren().add(paragraphElementSection);
				section.setRawText(null);
				
				
				if (hasChildren) {
					BMUtil.logSection("parseTextParagraphs. processing children of section", section);
					for (BMSection sectionChild: sectionOldChildren) {
						BMUtil.logSection("parseTextParagraphs. processing section-child", sectionChild);
						if (sectionChild.getType() == MDType.RAW_LINE) {
							BMSection paragraphElementSubSection = BMUtil.createSection(paragraphElementSection, MDType.PARAGRAPH_ELEMENT, toNonNullString(sectionChild.getRawText(), false));
							BMUtil.addChild(paragraphElementSection, paragraphElementSubSection);
						} else if (sectionChild.getType() == MDType.CODE_BLOCK) {
							BMUtil.addChild(paragraphElementSection, sectionChild);
						} else {
							String problem = "Unexpected section child type: " + sectionChild.getType(); 
							log("!!! parseTextParagraphs. problem: " + problem);
							warnings.add(new Exception(problem));
						}
					}
				}

				
				
				BMUtil.logSection("parseTextParagraphs. paragraphSection after processing (index = " + sectionIndex + ")", paragraphSection);
				
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

	private BMCodeFormat readFormat(String formatName) {
		if (formatName == null) {
			return null;
		}
		return CODE_FORMATS.get(formatName.toLowerCase());
	}

	private BMCodeFormat readFormat_old(String rawText) {
		String formatName = readFormatName_old(rawText);
		if (formatName == null) {
			return null;
		}
		return CODE_FORMATS.get(formatName.toLowerCase());
	}
	
	private String removeFormatting(String rawText) {
		return rawText.replace("\\", "").replace("*", "").replace("_", "").replace("~", "");
	}


	private String toTrimmedNonNullString(String rawText) {
		if (rawText == null){
			return "";
		}
		return rawText.trim();
	}

	private String toNonNullString(String rawText, boolean trim) {
		if (rawText == null){
			return "";
		}
		if (trim) {
			return rawText.trim();
		}
		return rawText;
	}
	
	private String readFormatName(String rawText) {
		if (rawText == null){
			return null;
		}
		
		return textParser.readUntilCharOrEnd(rawText, 0, Arrays.asList(' ', '\n'));
	}

	private String readFormatName_old(String rawText) {
		if (rawText == null){
			return null;
		}
		for (String i: BMConstants.CODE_BLOCK_MARKS) {
			if (rawText.startsWith(i)){
				return rawText.substring(i.length()).trim();
			}
		}
		return null;
	}
	
	protected boolean hasFormatting(BMSection section) {
		return section.isBold() 
				|| section.isItalic() 
				|| section.isStrikeThrough() 
				|| section.isUnderline() 
				|| section.getColor() != null 
				|| section.getBackgroundColor() != null 
				|| section.isBackgroundColorEndTag();
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

	public BMSection toMDSection_old(String markdownText){
		BMSection result = new BMSection();
		result.setType(MDType.ROOT);
		result.setChildren(new ArrayList<BMSection>());

		
		for (String line : markdownText.replace("\r", "").split("\n")){
			BMSection subSection = new BMSection();
			subSection.setType(MDType.RAW_LINE);
			subSection.setRawText(line);
			result.getChildren().add(subSection);
		}
		return result;
	}
	
	public BMSection toMDSection(String markdownText){
		BMSection result = new BMSection();
		result.setType(MDType.ROOT);
		result.setChildren(new ArrayList<BMSection>());
		
		String textWithoutCR = markdownText.replace("\r", "");
		List<String> tags = new ArrayList<String>();
		tags.add(BMConstants.LINE_BREAK);
		tags.addAll(BMConstants.CODE_BLOCK_MARKS);
		
		int startPos = 0;
		/** null means that the current row has ended and there is no open sub section*/
		BMSection subSectionOfCurrentRow = null;
		BMPosAndTag item = textParser.findNext(textWithoutCR, startPos, tags);
		while (item != null) {
//			log("toMDSection: item = " + item + ", startPos = " + startPos + ", subSectionOfCurrentRow = " + subSectionOfCurrentRow);
			String data = textWithoutCR.substring(startPos, item.getPos());
			BMSection subSection = null;
			subSection = new BMSection();
			subSection.setType(MDType.RAW_LINE);
			subSection.setRawText(data);
			startPos = item.getPos() + item.getTag().length();
			if (subSectionOfCurrentRow != null) {
				BMUtil.addChild(subSectionOfCurrentRow, subSection);
			} else {
				BMUtil.addChild(result, subSection);
			}
			
			if (BMConstants.CODE_BLOCK_MARKS.contains(item.getTag())) {
				BMPosAndTag endItem = textParser.findNext(textWithoutCR, startPos, BMConstants.CODE_BLOCK_MARKS);
				String codeBlockText = null; 
				if (endItem == null) {
					//: no end was found, so the end is the end of the raw text
					codeBlockText = textWithoutCR.substring(startPos);
					startPos = textWithoutCR.length();
				} else {
					codeBlockText = textWithoutCR.substring(startPos, endItem.getPos());
					startPos = endItem.getPos() + endItem.getTag().length();
				}
				BMSection codeSection = null;
				if (subSection.getRawText().isEmpty()) {
					//: the code block doesn't start within the line of another section, but at the beginning of the line
					//: in this case the code block is not nested, but the sub section IS the code block
					codeSection = subSection;
					//: in this case of course the code block begins with a line break. This can be removed
					codeBlockText = textParser.removeLeadingTextIfFound(codeBlockText, BMConstants.LINE_BREAK);
				} else {
					codeSection = new BMSection();
					if (subSectionOfCurrentRow != null) {
						BMUtil.addChild(subSectionOfCurrentRow, codeSection);
					} else {
						BMUtil.addChild(subSection, codeSection);
					}
					codeSection.setNested(true);
				}
				codeSection.setMultiLine(codeBlockText.contains(BMConstants.LINE_BREAK));
				codeSection.setType(MDType.CODE_BLOCK);
				codeSection.setRawText(codeBlockText);
				
				//: special case: the section ends with code block end and then new line. In this case the new line doesn't mean that
				//: another section with an empty line is given, so the new line character is skipped by changing the start pos to
				//: after the new line
				BMPosAndTag nextChar = textParser.findNextSkipSpaces(textWithoutCR, startPos);
				if ((nextChar != null) && (BMConstants.LINE_BREAK.equals(nextChar.getTag()))) {
					startPos = nextChar.getPos() + nextChar.getTag().length();
					subSectionOfCurrentRow = null;
				} else {
					//: after the code block, the line doesn't end, so there is still the same sub section of the current row
					if (subSectionOfCurrentRow == null) {
						subSectionOfCurrentRow = subSection;
					}
				}
			} else {
				subSectionOfCurrentRow = null;
			}
//			BMUtil.addChild(result, subSection);
			
			item = textParser.findNext(textWithoutCR, startPos, tags);
		}
		if (startPos < textWithoutCR.length()) {
			BMSection subSection = new BMSection();
			subSection.setType(MDType.RAW_LINE);
			subSection.setRawText(textWithoutCR.substring(startPos));
			result.getChildren().add(subSection);
		}
		return result;
	}

}
