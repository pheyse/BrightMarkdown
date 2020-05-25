package de.bright_side.brightmarkdown.logic;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.bright_side.brightmarkdown.BrightMarkdown.FormattingItem;
import de.bright_side.brightmarkdown.BrightMarkdown.OutputType;
import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.base.BrightXmlNode;
import de.bright_side.brightmarkdown.model.BMLevelAndTitle;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

public class BMHtmlCreator {
	public static final String CODE_BOX_STYLE = "background:lightgrey";
	public static final Set<String> PARENT_NODES_THAT_DONT_NEED_SPAN = new HashSet<String>(Arrays.asList("span", "p", "td", "th", "div", "b", "i", "u", "h1", "h2", "h3", "h4", "h5"));
	private static final String SPAN_TAG = "span";
	private Map<FormattingItem, Integer> fontSizesInMM;
	private static final String CSS_CLASS_NAME = "brightmarkdown";
	private static final String DEFAULT_IMAGE_WIDTH = "75%";
	private static final String DEFAULT_IMAGE_BORDER = "1mm";
	private static final String XML_INFO_TAG_START = "<?xml";

	public BMHtmlCreator(Map<FormattingItem, Integer> fontSizesInMM) {
		this.fontSizesInMM = fontSizesInMM;
	}

	public String toHTML(BMSection section, OutputType outputType) throws Exception {
		log("toHTML. Processing section: \n" + BMUtil.toString(section));
		
		BrightXmlNode topElement;
		BrightXmlNode contentElement;
		
		if (outputType == OutputType.FULL_HTML_DOCUMENT) {
			BrightXmlNode rootElement = new BrightXmlNode("html");
			boolean containsTables = checkContainsTables(section);
			BrightXmlNode headElement = null;
			if ((isCSSStyleSet()) || (containsTables)){
				headElement = rootElement.appendNode("head");
				String styleCode = createStyleCode(containsTables);
				headElement.appendNode("style", styleCode.toString());
			}
			contentElement = rootElement.appendNode("body");
			topElement = rootElement;
		} else {
			contentElement = new BrightXmlNode("span");
			topElement = contentElement;
		}
		
		createHTMLNodes(contentElement, section);
		logHTMLString("after HTML nodes creation", topElement);
		
		removeUnneededNodes(contentElement);
		logHTMLString("after removing unneded nodes", topElement);
		return createHTMLString(topElement, true, outputType);
	}
	
	public String createStyleCode(BMSection section) {
		boolean containsTables = checkContainsTables(section);
		return createStyleCode(containsTables);
	}

	public String createStyleCode(boolean containsTables) {
		StringBuilder result = new StringBuilder();
		if (isCSSStyleSet()) {
			result.append(createFontSizesStyle() + "\n");
		}
		if (containsTables) {
			result.append(createTableStyles() + "\n");
		}
		return result.toString();
	}

	private void logHTMLString(String message, BrightXmlNode node) throws Exception {
		if (!BMConstants.LOGGING_ACTIVE) {
			return;
		}
		log("===================================\n" + message + ":\n" + createHTMLString(node, false, OutputType.FULL_HTML_DOCUMENT) + "===================================");
	}
	
	private String createHTMLString(BrightXmlNode node, boolean replaceEmptySpanAndParagraphNodes, OutputType outputType) throws Exception {
		Document document = node.toW3CDocument();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String result = writer.getBuffer().toString();
		if (replaceEmptySpanAndParagraphNodes) {
			result = result.replace("<p></p>", "");
			result = result.replace("<span></span>", "");
		}
		result = BMUtil.unescape(result);
        result = result.replace(BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK, "<br/>");
        if (outputType == OutputType.EMBEDDABLE_HTML_CODE) {
        	result = removeXmlInfoTag(result);
        }
		return result;
	}

	private String removeXmlInfoTag(String result) {
		if (result.startsWith(XML_INFO_TAG_START)) {
			int pos = result.indexOf(">");
			if (pos > 0) {
				result = result.substring(pos + 1);
			}
		}
		return result;
	}
	
	private void removeUnneededNodes(BrightXmlNode node) {
		List<BrightXmlNode> childNodes = node.getChildNodes();
		int index = 0;
		while (index < childNodes.size()) {
			removeUnneededNodes(childNodes.get(index));
			index ++;
		}
//		for (BrightXMLNode i: childNodes) {
//			removeUnneededNodes(i);
//		}
		
		if (!node.getNodeName().equals(SPAN_TAG)) {
			return;
		}
		
		if (node.hasChildNodes()) {
			return;
		}
		
		BrightXmlNode parent = node.getParentNode();
		if (parent == null) {
			return;
		}
		String parentName = parent.getNodeName();
		if (!PARENT_NODES_THAT_DONT_NEED_SPAN.contains(parentName)) {
			return;
		}

		boolean removed = false;
		if ((!node.hasNonEmptyTextContent()) && (!node.hasChildNodes())){
			log("removing node because it has no text content and no children: ", node);
			parent.removeChild(node);
			removed = true;
		} else if ((!node.hasAttributes()) && (parent.getChildNodes().size() == 1)){
			log("removing node because it has no attributes and parent only has this child", node);
			String nodeText = node.getTextContent();
			parent.removeChild(node);
			if (!BMUtil.isEmptyOrNull(nodeText)) {
				parent.setTextContent(parent.getTextContent() + nodeText);
			}
			removed = true;
		}
		
		if (removed) {
			//: process parent node again:
			removeUnneededNodes(parent);
		}
		
	}
	
	private boolean checkContainsTables(BMSection section) {
		if (section.getChildren() == null) {
			return false;
		}
		for (BMSection i: section.getChildren()) {
			if (i.getType() == MDType.TABLE_ROW) {
				return true;
			}
		}
		return false;
	}

	private StringBuilder createTableStyles() {
		StringBuilder result = new StringBuilder();
		result.append("table." + CSS_CLASS_NAME + "{border-collapse: collapse;}\n");
		result.append("table." + CSS_CLASS_NAME + " td {border: 1px solid black; padding: 3px;}\n");
		result.append("table." + CSS_CLASS_NAME + " th {border: 1px solid black; padding: 3px;}\n");
		result.append("table." + CSS_CLASS_NAME + " th {background-color: #a0a0a0;}\n");
		result.append("table." + CSS_CLASS_NAME + " tr:nth-child(odd) {background-color: #d8d8d8;}\n");
		result.append("table." + CSS_CLASS_NAME + " tr:nth-child(even) {background-color: #ffffff;}\n");
		return result;
	}

	private StringBuilder createFontSizesStyle() {
		StringBuilder cssText = new StringBuilder();
		for (Entry<FormattingItem, Integer> i: fontSizesInMM.entrySet()){
			if (cssText.length() > 0){
				cssText.append("\n");
			}
			cssText.append(i.getKey() + "{font-size:" + i.getValue() + "mm;}");
		}
		return cssText;
	}
	
	private boolean isCSSStyleSet() {
		return !fontSizesInMM.isEmpty();
	}

	private void createHTMLNodes(BrightXmlNode rootElement, BMSection topSection) throws Exception {
		List<BMSection> items = topSection.getChildren();
		int pos = 0;
		while (pos < items.size()){
			BMSection item = items.get(pos);
			if (item.getType() == MDType.RAW_LINE){
				createHTMLNodesForRawLine(rootElement, item);
			} else if (item.getType() == MDType.PARAGRAPH){
				createHTMLNodesForParagraph(rootElement, item);
			} else if (item.getType() == MDType.HEADING){
				addFormattedText(rootElement.appendNode("h" + item.getLevel(), null), item);
			} else if (item.getType() == MDType.HORIZONTAL_RULE){
				rootElement.appendNode("hr");
			} else if (item.getType() == MDType.CODE_BLOCK){
				createHTMLNodesForCodeBlock(rootElement, item);
			} else if (item.getType() == MDType.CHECKED_ITEM){
				createHTMLNodesForCheckedItem(rootElement, item);
			} else if (item.getType() == MDType.UNCHECKED_ITEM){
				createHTMLNodesForUncheckedItem(rootElement, item);
			} else if (item.getType() == MDType.BULLET_POINT){
				pos = createHTMLNodesForListItems(rootElement, items, pos, item);
			} else if (item.getType() == MDType.NUMBERED_ITEM){
				pos = createHTMLNodesForListItems(rootElement, items, pos, item);
			} else if (item.getType() == MDType.TABLE_ROW){
				pos = createHTMLNodesForTable(rootElement, items, pos, item);
			} else if (item.getType() == MDType.TABLE_OF_CONTENTS){
				createHTMLNodesForTableOfContents(rootElement, topSection);
			} else {
				throw new Exception("Unexpected item type: " + item.getType() + ". Raw text = >>" + item.getRawText() + "<<");
			}
			pos ++;
		}
	}
	
	private void createHTMLNodesForCodeBlock(BrightXmlNode parent, BMSection codeBlockSection) {
		log("createHTMLNodesForCodeBlock. code block section: \n" + BMUtil.toString(codeBlockSection));
		
		//: if not "stand alone", it is 1. nested in e.g. a bullet point item and 2. contains no line breaks
		boolean standaloneBlock = codeBlockSection.isMultiLine() || (!codeBlockSection.isNested()); 
		log("createHTMLNodesForCodeBlock. standaloneBlock = " + standaloneBlock);
		BrightXmlNode resultNode = null;
		BrightXmlNode codeNode = null;
		
		if (standaloneBlock) {
			BrightXmlNode node = parent.appendNode("pre", null);
			resultNode = node;
			codeNode = node.appendNode("code");
			BMSection firstCodeSectionChild = codeBlockSection.getChildren().get(0);
			if (!firstCodeSectionChild.getRawText().startsWith(BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK)) {
				//: start with a line break because otherwise the HTML indent in the first line is treated as an indent in the code
				codeNode.appendNode("span", BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK); 
			}
		} else {
			codeNode = parent.appendNode("code");
			resultNode = codeNode;
		}
		resultNode.setAttribute("style", CODE_BOX_STYLE);
		
		List<BMSection> relevantSections = new ArrayList<>(codeBlockSection.getChildren());
		removeLastLineBreakIfFound(relevantSections);
		
		for (BMSection section: relevantSections) {
			log("createHTMLNodesForCodeBlock. Section = " + section + ", raw text = >>" + section.getRawText() + "<<");
			BrightXmlNode itemNode = codeNode.appendNode("span", section.getRawText());
			String style = getCodeBlockStyle(section);
			if (style != null) {
				itemNode.setAttribute("style", style);
			}
		}
		
		log("createHTMLNodesForCodeBlock. code block section: \n" + BMUtil.toString(codeBlockSection) + "\n Result: \n" + resultNode.toString(true));
	}

	private void removeLastLineBreakIfFound(List<BMSection> sections) {
		if (sections.isEmpty()) {
			return;
		}
		BMSection listItem = sections.get(sections.size() - 1);
		if (((listItem.getType() == MDType.CODE_BLOCK_COMMAND) || (listItem.getType() == MDType.CODE_BLOCK_COMMENT)) && (listItem.getRawText().endsWith(BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK))){
			listItem.setRawText(listItem.getRawText().substring(0, listItem.getRawText().length() - BMConstants.ESCAPE_NEW_LINE_IN_CODE_BLOCK.length()));
			if (listItem.getRawText().isEmpty()) {
				sections.remove(sections.size() - 1);
			}
		}
		
	}

	private String getCodeBlockStyle(BMSection section) {
		switch (section.getType()) {
		case CODE_BLOCK_COMMAND:
			break;
		case CODE_BLOCK_COMMENT:
			return "color:darkgreen";
		case CODE_BLOCK_KEYWORD:
			return "color:purple;font-weight:bold";
		case CODE_BLOCK_TAG:
			return "color:purple;font-weight:bold";
		case CODE_BLOCK_STRING:
			return "color:blue";
		case FORMATTED_TEXT:
			return getStyleForFormattedText(section);
		default:
			break;
		}
		return null;
	}

	private String getStyleForFormattedText(BMSection section) {
		StringBuilder result = new StringBuilder();
		
		if (section.isBold()) {
			result.append("font-weight: bold;");
		}
		if (section.isItalic()) {
			result.append("font-style: italic;");
		}
		if (section.isUnderline()) {
			result.append("text-decoration: underline;");
		}
		if (section.getBackgroundColor() != null) {
			result.append("background-color: " + section.getBackgroundColor() + ";");
		}
		if (section.getColor() != null) {
			result.append("color: " + section.getColor() + ";");
		}
		
		return result.toString();
	}

	private void createHTMLNodesForTableOfContents(BrightXmlNode rootElement, BMSection topSection) {
		List<BMLevelAndTitle> headingItems = getHeadingItems(topSection);
		if (headingItems == null){
			return;
		}
		
		BrightXmlNode root = rootElement.appendNode("span");
		Map<Integer, BrightXmlNode> levelToListNode = new TreeMap<Integer, BrightXmlNode>();
		for (BMLevelAndTitle i: headingItems) {
			BrightXmlNode element = getOrCreateListElement(root, levelToListNode, i.getLevel());
			BMUtil.removeDeeperLevels(levelToListNode, i.getLevel());
			element.appendNode("li", i.getTitle());
		}			
	}

	private BrightXmlNode getOrCreateListElement(BrightXmlNode root, Map<Integer, BrightXmlNode> levelToListNode, int level) {
		BrightXmlNode element = levelToListNode.get(level);
		if (element == null){
			if (level == 1){
				element = root.appendNode("ul");
				levelToListNode.put(level, element);
			} else {
				BrightXmlNode parentElement = levelToListNode.get(level - 1);
				if (parentElement == null){
					parentElement = getOrCreateListElement(root, levelToListNode, level - 1);
				}
				element = parentElement.appendNode("ul");
				levelToListNode.put(level, element);
			}
		}
		return element;
	}

	private void createHTMLNodesForRawLine(BrightXmlNode rootElement, BMSection item) throws Exception {
		if ((item.getChildren() != null) || (notEmpty(item.getRawText()))){
			BrightXmlNode node = rootElement.appendNode("p");
			addFormattedText(node, item);
		}
	}

	private void createHTMLNodesForParagraph(BrightXmlNode rootElement, BMSection item) throws Exception {
		BrightXmlNode paragraphNode = rootElement.appendNode("p");
		int numberOfChildren = item.getChildren().size();
		if (numberOfChildren == 1){
			//: if there is only one item: no need for span and br-tags in-between
			addFormattedText(paragraphNode, item.getChildren().get(0));
			return;
		}
		
		int index = 0;
		for (BMSection i: item.getChildren()){
			BrightXmlNode paragraphElementNode = paragraphNode.appendNode("span");
			
			addFormattedText(paragraphElementNode, i);
			if (index < numberOfChildren - 1){ //: not the last item
				paragraphNode.appendNode("br");
			}
			
			index ++;
		}
	}
	
	private void createHTMLNodesForUncheckedItem(BrightXmlNode rootElement, BMSection item) throws Exception {
		BrightXmlNode node = rootElement.appendNode("input", null, "type", "checkbox", "disabled", "true");
		addFormattedText(node, item);
		rootElement.appendNode("br");
	}

	private void createHTMLNodesForCheckedItem(BrightXmlNode rootElement, BMSection item) throws Exception {
		BrightXmlNode node = rootElement.appendNode("input", null, "type", "checkbox", "disabled", "true", "checked", "true");
		addFormattedText(node, item);
		rootElement.appendNode("br");
	}

	private int createHTMLNodesForTable(BrightXmlNode rootElement, List<BMSection> items, int pos, BMSection item) throws Exception {
		BrightXmlNode tableNode = rootElement.appendNode("table", null, "class", CSS_CLASS_NAME);
		boolean firstRowIsHeader = false;
		
		List<BMSection> tableItems = new ArrayList<BMSection>();
		tableItems.add(item);
		while (nextChildHasType(items, pos, MDType.TABLE_ROW)){
			pos ++;
			tableItems.add(items.get(pos));
		}
		if (nextChildHasType(items, pos, MDType.HORIZONTAL_RULE)){
			pos ++;
			firstRowIsHeader = true;
		}
		while (nextChildHasType(items, pos, MDType.TABLE_ROW)){
			pos ++;
			tableItems.add(items.get(pos));
		}
		
		int numberOfColumns = countMaxNumberOfColumns(tableItems);
		boolean firstRow = true;
		for (BMSection rowSecion: tableItems) {
			BrightXmlNode rowNode = tableNode.appendNode("tr");
			
			if (rowSecion.getBackgroundColor() != null) {
				setBackgroundColorStyle(rowNode, rowSecion.getBackgroundColor());
			}
			
			String cellTagName = "td";
			if (firstRow && firstRowIsHeader) {
				cellTagName = "th";
			}
			for (BMSection cellSection: rowSecion.getChildren()) {
				if (cellSection.getType() != MDType.TABLE_CELL) {
					throw new RuntimeException("Expected type table cell, but found: " + cellSection.getType());
				}
				BrightXmlNode cellNode = rowNode.appendNode(cellTagName, null);
				if (cellSection.getBackgroundColor() != null) {
					setBackgroundColorStyle(cellNode, cellSection.getBackgroundColor());
				}
				
				addFormattedText(cellNode, cellSection);
			}
			int missingCells = numberOfColumns - rowSecion.getChildren().size();
			for (int i = 0; i < missingCells; i++) {
				rowNode.appendNode("td");
			}
			firstRow = false;
		}
		
		return pos;
	}
	
	private int countMaxNumberOfColumns(List<BMSection> tableItems) {
		int result = 0;
		for (BMSection i: tableItems) {
			result = Math.max(result, i.getChildren().size());
		}
		return result;
	}
	
	private String getHTMLListTag(MDType type) {
		if (type == MDType.BULLET_POINT) {
			return "ul";
		} else {
			return "ol";
		}
	}

	private int createHTMLNodesForListItems(BrightXmlNode rootElement, List<BMSection> items, int pos, BMSection item) throws Exception {
		int currentLevel = 1;
		Map<Integer, BrightXmlNode> levelToListNodeMap = new TreeMap<>();
		String listTag = getHTMLListTag(item.getType());
		BrightXmlNode listNode = rootElement.appendNode(listTag);
		levelToListNodeMap.put(currentLevel, listNode);
		
		while (item.getLevel() > currentLevel){
			currentLevel ++;
			listNode = listNode.appendNode(listTag);
			levelToListNodeMap.put(currentLevel, listNode);
		}
		
		BrightXmlNode itemNode = listNode.appendNode("li");
		addFormattedText(itemNode, item);
		while (nextChildHasType(items, pos, MDType.BULLET_POINT, MDType.NUMBERED_ITEM)){
			pos ++;
			item = items.get(pos);
			listTag = getHTMLListTag(item.getType());
			while (item.getLevel() > currentLevel){
				currentLevel ++;
				listNode = listNode.appendNode(listTag);
				levelToListNodeMap.put(currentLevel, listNode);
			}
			if (item.getLevel() < currentLevel){
				BMUtil.removeDeeperLevels(levelToListNodeMap, item.getLevel());
				listNode = levelToListNodeMap.get(item.getLevel());
				if (listNode == null){
					throw new RuntimeException("Could not get list node at level " + item.getLevel() + ". levels: " + levelToListNodeMap.keySet());
				}
				currentLevel = item.getLevel();
			}
			
			itemNode = listNode.appendNode("li");
			log("createHTMLNodesForListItems processing item >>" + BMUtil.toString(item) + "<<. Adding formatted text");
			addFormattedText(itemNode, item);
			log("createHTMLNodesForListItems Adding formatted text done.");
		}
		return pos;
	}

	private void addFormattedText(BrightXmlNode node, BMSection item) throws Exception {
		if (item.getRawText() != null){
			node.setTextContent(item.getRawText());
		} 
		
		if (BMUtil.hasChildren(item)) {
			for (BMSection child: item.getChildren()){
				log("addFormattedText. Processing child. " + child);
				if (child.getType() == MDType.LINK){
					if (child.getRawText() != null){
						node.appendNode("a", child.getRawText(), "href", child.getLocation());
					} else {
						BrightXmlNode linkNode = node.appendNode("a", null, "href", child.getLocation());
						if (child.getChildren() != null){
							addFormattedText(linkNode, child);
						}
					}
				} else if (child.getType() == MDType.CODE_BLOCK){
					createHTMLNodesForCodeBlock(node, child);
				} else if (child.getType() == MDType.IMAGE){
					createImageNode(node, child);
				} else if ((child.getType() == MDType.RAW_LINE) && (BMUtil.hasChildren(child))){
					//: recursively add the nested items
					addFormattedText(node, child); 
				} else if ((child.getType() == MDType.PARAGRAPH_ELEMENT) && (BMUtil.hasChildren(child))){
					//: recursively add the nested items
					addFormattedText(node, child); 
				} else if ((child.getType() == MDType.PLAIN_TEXT) && (BMUtil.hasChildren(child))){
					//: recursively add the nested items
					addFormattedText(node, child); 
				} else {
					BrightXmlNode currentNode = node;
					if (child.isBold()) {
						currentNode = currentNode.appendNode("b");
					}
					if (child.isItalic()) {
						currentNode = currentNode.appendNode("i");
					}
					if (child.isUnderline()) {
						currentNode = currentNode.appendNode("u");
					}
					if (child.isStrikeThrough()) {
						currentNode = currentNode.appendNode("strike");
					}
					if (child.getColor() != null) {
						currentNode = currentNode.appendNode("span", null, "style", "color:" + child.getColor());
					}
					if (child.getBackgroundColor() != null) {
						currentNode = currentNode.appendNode("span");
						setBackgroundColorStyle(currentNode, child.getBackgroundColor());
					}
					if (currentNode == node) { //: there is no formatting and no sub-node has been created, then create a sub node for the text
						currentNode = currentNode.appendNode("span");
					}

					currentNode.setTextContent(child.getRawText());
				}
			}
		}		
	}

	private void createImageNode(BrightXmlNode node, BMSection child) {
		BrightXmlNode imageNode = node.appendNode("img", null, "src", child.getLocation());
		
		//: height
		if (child.getImageHeight() != null) {
			imageNode.setAttribute("height", child.getImageHeight());
		}
		
		//: width
		String imageWidth = null;
		if (child.getImageWidth() != null) {
			imageWidth = child.getImageWidth();
		} else if (child.getImageHeight() == null) {
			imageWidth = DEFAULT_IMAGE_WIDTH; //: if neither width or heights are defined, use the default width (which also affects the height since the aspect ratio is kept)
		}
		if (imageWidth != null) {
			imageNode.setAttribute("width", imageWidth);
		}
		
		//: border
		String imageBorder = DEFAULT_IMAGE_BORDER;
		if (child.getImageBorder() != null) {
			imageBorder = child.getImageBorder();
		}
		imageNode.setAttribute("border", imageBorder);
		
		//: alt-text
		if (child.getImageAltText() != null) {
			imageNode.setAttribute("alt", child.getImageAltText());
		}
		
		//: align
		imageNode.setAttribute("align", "top");
	}

	private void setBackgroundColorStyle(BrightXmlNode currentNode, String backgroundColor) {
		currentNode.setAttribute("style", "background-color:" + backgroundColor);
	}
	
	private boolean nextChildHasType(List<BMSection> items, int pos, MDType type) {
		int checkPos = pos + 1;
		if (checkPos >= items.size()){
			return false;
		}
		return items.get(checkPos).getType() == type;
	}

	private boolean nextChildHasType(List<BMSection> items, int pos, MDType typeA, MDType typeB) {
		int checkPos = pos + 1;
		if (checkPos >= items.size()){
			return false;
		}
		MDType type = items.get(checkPos).getType();
		return (type == typeA) || (type == typeB);
	}

	private void log(String message) {
		if (BMConstants.LOGGING_ACTIVE) {
			System.out.println("BMHtmlCreator> " + message);
		}
	}

	private void log(String message, BrightXmlNode node) {
		if (BMConstants.LOGGING_ACTIVE) {
			System.out.println("BMHtmlCreator> " + message + node);
		}
	}


	public List<BMLevelAndTitle> getHeadingItems(BMSection section){
		List<BMLevelAndTitle> result = new ArrayList<BMLevelAndTitle>();
		
		if (section.getType() == MDType.HEADING){
			BMLevelAndTitle levelAndTitle = new BMLevelAndTitle();
			levelAndTitle.setLevel(section.getLevel());
			levelAndTitle.setTitle(section.getOriginalPlainText());
			result.add(levelAndTitle);
		}
		if (section.getChildren() != null){
			for (BMSection i: section.getChildren()){
				result.addAll(getHeadingItems(i));
			}
		}
		return result;
	}

	private boolean notEmpty(String text) {
		return text != null && !text.isEmpty();
	}


}
