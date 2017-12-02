package de.bright_side.brightmarkdown;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

public class BrightMarkdown {
	private static final String[] HEADINGS_INDICATOR = {"#", "##", "###", "####", "#####", "######"};
	private static final String[] BULLET_POINT_INDICATORS_A = {"*", "**", "***", "****", "*****"};
	private static final String[] BULLET_POINT_INDICATORS_B = {".", "..", "...", "....", "....."};
	private static final String[] BULLET_POINT_INDICATORS_C = {"+", "++", "+++", "++++", "+++++"};
	private static final String[] BULLET_POINT_INDICATORS_D = {"-", "--", "---", "----", "-----"};
	private static final String[] UNCHECKED_ITEM_INDICATORS = {"[]", "[ ]", "- []", "- [ ]"};
	private static final String[] CHECKED_ITEM_INDICATORS = {"[x]", "[X]", "- [x]", "- [X]"};
	private static final String[] NUMBERED_ITEM_INDICATORS = createList(100);
	private static final String[] HORIZONTAL_RULE_INDICATORS = {"-", "_", "#", "*"};
	private static final String LINK_LABEL_START = "[";
	private static final String LINK_LABEL_END = "]";
	private static final String LINK_LOCATION_START = "(";
	private static final String LINK_LOCATION_END = ")";
	private static final String[] ESCAPE_CHARACTERS = {"\\", "b", "*", "a", "_", "u", "{", "1", "}", "2", "[", "3", "]", "4", "(", "5", ")", "6"
			                                           , "#", "h", "+", "p", "-", "m", ".", "d", "~", "t", "`", "c"};
	private static final String ESCAPE_MARK = "%%";
	private static final String CODE_BLOCK_MARK = "```";
	private static final String ESCAPE_NEW_LINE_IN_CODE_BLOCK = "%%N%%";
	
	public static enum FormattingItem {H1, H2, H3, H4, H5, H6}
	private Map<FormattingItem, Integer> fontSizesInMM = new EnumMap<>(FormattingItem.class);

	public String createHTML(String markdownText) throws Exception{
		BrightMarkdownSection section = parseAll(markdownText);
		return toHTML(section);
	}

	/**
	 * 
	 * @param markdownText
	 * @return the deepest heading which is 0 if there are no headings, and e.g. 3 if there is h1, h2 and h3
	 * @throws Exception
	 */
	public int getDeepestHeading(String markdownText) throws Exception{
		BrightMarkdownSection section = parseAll(markdownText);
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
		add(sb, "");
		add(sb, "## Numbered list");
		add(sb, "* start line with \"1.\", \"2.\" etc. Which number is used does not matter");
		add(sb, "");
		add(sb, "## Formatting");
		add(sb, "* use \\*text\\* to write text in *italic*");
		add(sb, "* use \\*\\*text\\*\\* to write text in **bold**");
		add(sb, "* use \\~\\~text\\~\\~ to write text in ~~strike through~~");
		add(sb, "");
		add(sb, "## Links");
		add(sb, "* \\[my link label\\]\\(www.wikipedia.de\\)");
		add(sb, "");
		add(sb, "## Checkbox lists");
		add(sb, "* Start the line with \\[x\\] for a checked box and \\[\\] for an unchecked box)");
		add(sb, "");
		add(sb, "## Horizontal rule");
		add(sb, "* have a line that contains of 3 or more \\*\\*\\*");
		add(sb, "* instead of \\* you can also use \\_, \\- or \\#");
		add(sb, "");
		add(sb, "## Escaping special characters");
		add(sb, "* Place a \\\\ before a special character like \\* to escape it (ignore for processing)");
		add(sb, "");
		add(sb, "## Code blocks");
		add(sb, "* Place a line of \\`\\`\\` before and after the text to indicate a code block");
		add(sb, "");
		
		return sb.toString();
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


		parseRawLineEntries(section, MDType.HEADING, HEADINGS_INDICATOR, true);
		parseRawLineEntries(section, MDType.UNCHECKED_ITEM, UNCHECKED_ITEM_INDICATORS, false);
		parseRawLineEntries(section, MDType.CHECKED_ITEM, CHECKED_ITEM_INDICATORS, false);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_A, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_B, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_C, true);
		parseRawLineEntries(section, MDType.BULLET_POINT, BULLET_POINT_INDICATORS_D, true);
		parseRawLineEntries(section, MDType.NUMBERED_ITEM, NUMBERED_ITEM_INDICATORS, false);
		
		parseLinks(section);

		parseFormatting(section, MDType.BOLD, "__");
		parseFormatting(section, MDType.BOLD, "**");
		parseFormatting(section, MDType.ITALIC, "_");
		parseFormatting(section, MDType.ITALIC, "*");
		parseFormatting(section, MDType.STRIKETHROUGH, "~~");
		
		
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

	private String unescape(String text) {
		log("text before unescape: >>" + text.replace("\n", "\\n") + "<<");
		String result = text;
		for (int i = 0; i < ESCAPE_CHARACTERS.length; i+= 2){
			String input = ESCAPE_MARK + ESCAPE_CHARACTERS[i + 1] + ESCAPE_MARK;
			String output = ESCAPE_CHARACTERS[i];
			result = result.replace(input, output);
		}
		log("text after unescape: >>" + result.replace("\n", "\\n") + "<<");
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
	
	private void parseRawLineEntries(BrightMarkdownSection topSection, MDType type, String[] indicators, boolean setLevel) {
		for (int level = 1; level <= indicators.length; level ++){
			String indicator = indicators[level - 1] + " ";
			for (BrightMarkdownSection section: getAllSectionsAndSubSections(topSection)){
				if ((section.getType() == MDType.RAW_LINE) && (section.getRawText() != null) && (section.getRawText().trim().startsWith(indicator))){
					trimAndRemoveRawTextStart(section, indicator.length());
					section.setType(type);
					if (setLevel){
						section.setLevel(level);
					}
				}
			}
		}
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

	protected String toHTML(BrightMarkdownSection section) throws Exception {
		int indent = 4;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		Element rootElement = document.createElement("html");
		document.appendChild(rootElement);
		if (isCSSStyleSet()){
			Element headElement = appendNode(rootElement, "head", null);
			createCSSStyleNode(headElement);
		}
		
		Element bodyElement = appendNode(rootElement, "body", null);
		createHTMLNodes(bodyElement, section.getChildren());
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "" + indent);
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String result = writer.getBuffer().toString().replace("<p></p>", "").replace("<span></span>", "");
		result = unescape(result);
        result = result.replace(ESCAPE_NEW_LINE_IN_CODE_BLOCK, "<br/>");
		return result;
	}

	private void createCSSStyleNode(Element parentElement) {
		StringBuilder cssText = new StringBuilder();
		for (Entry<FormattingItem, Integer> i: fontSizesInMM.entrySet()){
			if (cssText.length() > 0){
				cssText.append("\n");
			}
			cssText.append(i.getKey() + "{font-size:" + i.getValue() + "mm;}");
		}
		appendNode(parentElement, "style", cssText.toString());
	}

	private boolean isCSSStyleSet() {
		return !fontSizesInMM.isEmpty();
	}

	private void createHTMLNodes(Element rootElement, List<BrightMarkdownSection> items) throws Exception {
		int pos = 0;
		while (pos < items.size()){
			BrightMarkdownSection item = items.get(pos);
			if (item.getType() == MDType.RAW_LINE){
				if ((item.getChildren() != null) || (notEmpty(item.getRawText()))){
					Element node = appendNode(rootElement, "p", null);
					addFormattedText(node, item);
				}
			} else if (item.getType() == MDType.HEADING){
				Element node = appendNode(rootElement, "h" + item.getLevel(), null);
				addFormattedText(node, item);
			} else if (item.getType() == MDType.HORIZONTAL_RULE){
				appendNode(rootElement, "hr", null);
			} else if (item.getType() == MDType.CODE_BLOCK){
				Element node = appendNode(rootElement, "pre", null);
//                setAttrib(node, "style", "word-wrap:break-word;white-space: pre-wrap;");
				appendNode(node, "code", item.getRawText());
			} else if (item.getType() == MDType.CHECKED_ITEM){
				Element node = appendNode(rootElement, "input", null);
				setAttrib(node, "type", "checkbox");
				setAttrib(node, "disabled", "true");
				setAttrib(node, "checked", "true");
				addFormattedText(node, item);
				appendNode(rootElement, "br", null);
			} else if (item.getType() == MDType.UNCHECKED_ITEM){
				Element node = appendNode(rootElement, "input", null);
				setAttrib(node, "type", "checkbox");
				setAttrib(node, "disabled", "true");
				addFormattedText(node, item);
				appendNode(rootElement, "br", null);
			} else if (item.getType() == MDType.BULLET_POINT){
				int currentLevel = 1;
				Map<Integer, Element> levelToListNodeMap = new TreeMap<>();
				Element listNode = appendNode(rootElement, "ul", null);
				levelToListNodeMap.put(currentLevel, listNode);
				
				while (item.getLevel() > currentLevel){
					currentLevel ++;
					listNode = appendNode(listNode, "ul", null);
					levelToListNodeMap.put(currentLevel, listNode);
				}
				
				Element itemNode = appendNode(listNode, "li", null);
				addFormattedText(itemNode, item);
				while (nextChildHasType(items, pos, MDType.BULLET_POINT)){
					pos ++;
					item = items.get(pos);
					while (item.getLevel() > currentLevel){
						currentLevel ++;
						listNode = appendNode(listNode, "ul", null);
						levelToListNodeMap.put(currentLevel, listNode);
					}
					if (item.getLevel() < currentLevel){
						removeLowerLevels(levelToListNodeMap, item.getLevel());
						listNode = levelToListNodeMap.get(item.getLevel());
						currentLevel = item.getLevel();
					}
					
					itemNode = appendNode(listNode, "li", null);
					addFormattedText(itemNode, item);
				}
			} else if (item.getType() == MDType.NUMBERED_ITEM){
				Element listNode = appendNode(rootElement, "ol", null);
				Element itemNode = appendNode(listNode, "li", null);
				addFormattedText(itemNode, item);
				while (nextChildHasType(items, pos, MDType.NUMBERED_ITEM)){
					pos ++;
					item = items.get(pos);
					itemNode = appendNode(listNode, "li", null);
					addFormattedText(itemNode, item);
				}
			} else {
				throw new Exception("Unexpected item type: " + item.getType() + ". Raw text = >>" + item.getRawText() + "<<");
			}
			pos ++;
		}
	}

	private boolean notEmpty(String text) {
		return text != null && !text.isEmpty();
	}

	private void removeLowerLevels(Map<Integer, Element> levelToListNodeMap, int level) {
		List<Integer> lowerLevels = new ArrayList<>();
		for (int i: levelToListNodeMap.keySet()){
			if (i < level){
				lowerLevels.add(i);
			}
		}
		for (Integer i: lowerLevels){
			levelToListNodeMap.remove(i);
		}
	}

	private boolean nextChildHasType(List<BrightMarkdownSection> items, int pos, MDType type) {
		int checkPos = pos + 1;
		if (checkPos >= items.size()){
			return false;
		}
		return items.get(checkPos).getType() == type;
	}

	private void addFormattedText(Element node, BrightMarkdownSection item) throws Exception {
		if (item.getRawText() != null){
			node.setTextContent(item.getRawText());
		} else {
			for (BrightMarkdownSection child: item.getChildren()){
				if (child.getType() == MDType.BOLD){
					log("creating bold tag with content >>" + child.getRawText() + "<<");
					appendNodeIfConcentNotEmpty(node, "b", child.getRawText());
				} else if (child.getType() == MDType.ITALIC){
					appendNodeIfConcentNotEmpty(node, "i", child.getRawText());
				} else if (child.getType() == MDType.STRIKETHROUGH){
					appendNodeIfConcentNotEmpty(node, "strike", child.getRawText());
				} else if (child.getType() == MDType.LINK){
					if (child.getRawText() != null){
						Element linkNode = appendNode(node, "a", child.getRawText());
						setAttrib(linkNode, "href", child.getLocation());
					} else {
						Element linkNode = appendNode(node, "a", null);
						setAttrib(linkNode, "href", child.getLocation());
						if (child.getChildren() != null){
							addFormattedText(linkNode, child);
						}
					}
				} else if (child.getType() == MDType.PLAIN_TEXT){
					appendNodeIfConcentNotEmpty(node, "span", child.getRawText());
					if (child.getChildren() != null){
						addFormattedText(node, child);
					}
				} else {
					throw new Exception("Unexpected type within text: " + child.getType());
				}
			}
		}		
	}

	private Element appendNode(Element parentElement, String tag, String content) {
		Element child;
		try{
			child = parentElement.getOwnerDocument().createElement(tag);
		} catch (RuntimeException t){
			throw new RuntimeException("Could not add node with tag '" + tag + "'", t);
		}
		parentElement.appendChild(child);
 		log("setting text content >>" + content + "<<");
		child.setTextContent(content);
		return child;
	}

	private void appendNodeIfConcentNotEmpty(Element parentElement, String tag, String content) {
		if ((content != null) && (!content.isEmpty())){
			appendNode(parentElement, tag, content);
		}
	}

	private void log(String message) {
//		System.out.println("MDLogic> " + message);
	}

	private Element setAttrib(Element element, String attributeName, String attributeValue) {
		element.setAttribute(attributeName, attributeValue);
		return element;
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
