package de.bright_side.brightmarkdown;

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
import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

public class BrightMarkdownHTMLCreator {
	public static final String CODE_BOX_STYLE = "background:lightgrey";
	public static final Set<String> PARENT_NODES_THAT_DONT_NEED_SPAN = new HashSet<String>(Arrays.asList("span", "p", "td", "th", "div", "b", "i", "u", "h1", "h2", "h3", "h4", "h5"));
	private static final String SPAN_TAG = "span";
	private Map<FormattingItem, Integer> fontSizesInMM;
	private boolean loggingActive;
	private static final String CSS_CLASS_NAME = "brightmarkdown";

	public BrightMarkdownHTMLCreator(boolean loggingActive, Map<FormattingItem, Integer> fontSizesInMM) {
		this.loggingActive = loggingActive;
		this.fontSizesInMM = fontSizesInMM;
	}

	protected String toHTML(BrightMarkdownSection section) throws Exception {
		BrightXMLNode rootElement = new BrightXMLNode("html");
		
		boolean containsTables = checkContainsTables(section);
		
		BrightXMLNode headElement = null;
		
		if ((isCSSStyleSet()) || (containsTables)){
			headElement = rootElement.appendNode("head");
			StringBuilder sb = new StringBuilder();
			if (isCSSStyleSet()) {
				sb.append(createFontSizesStyle() + "\n");
			}
			if (containsTables) {
				sb.append(createTableStyles() + "\n");
			}
			
			headElement.appendNode("style", sb.toString());
		}
		
		BrightXMLNode bodyElement = rootElement.appendNode("body");
		createHTMLNodes(bodyElement, section);
		logHTMLString("after HTML nodes creation", rootElement);
		
		removeUnneededNodes(bodyElement);
		logHTMLString("after removing unneded nodes", rootElement);
		return createHTMLString(rootElement, true);
	}

	private void logHTMLString(String message, BrightXMLNode node) throws Exception {
		if (!loggingActive) {
			return;
		}
		log("===================================\n" + message + ":\n" + createHTMLString(node, false) + "===================================");
	}
	
	private String createHTMLString(BrightXMLNode node, boolean replaceEmptySpanAndParagraphNodes) throws Exception {
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
		result = BrightMarkdown.unescape(result);
        result = result.replace(BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK, "<br/>");
		return result;
	}
	
	private void removeUnneededNodes(BrightXMLNode node) {
		List<BrightXMLNode> childNodes = node.getChildNodes();
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
		
		BrightXMLNode parent = node.getParentNode();
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
			if (!BrightMarkdownUtil.isEmptyOrNull(nodeText)) {
				parent.setTextContent(parent.getTextContent() + nodeText);
			}
			removed = true;
		}
		
		if (removed) {
			//: process parent node again:
			removeUnneededNodes(parent);
		}
		
	}
	
	private boolean checkContainsTables(BrightMarkdownSection section) {
		if (section.getChildren() == null) {
			return false;
		}
		for (BrightMarkdownSection i: section.getChildren()) {
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

	private void createHTMLNodes(BrightXMLNode rootElement, BrightMarkdownSection topSection) throws Exception {
		List<BrightMarkdownSection> items = topSection.getChildren();
		int pos = 0;
		while (pos < items.size()){
			BrightMarkdownSection item = items.get(pos);
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
//				Element node = appendNode(rootElement, "pre", null);
//				appendNode(node, "code", item.getRawText());
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
	
	private void createHTMLNodesForCodeBlock(BrightXMLNode parent, BrightMarkdownSection codeBlockSection) {
		BrightXMLNode node = parent.appendNode("pre", null, "style", CODE_BOX_STYLE);
		BrightXMLNode codeNode = node.appendNode("code");
		
		codeNode.appendNode("span", BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK); //: start with a line break because otherwise the HTML indent in the first line is treated as an indent in the code
		
		List<BrightMarkdownSection> relevantSections = new ArrayList<>(codeBlockSection.getChildren());
		removeLastLineBreakIfFound(relevantSections);
		
		for (BrightMarkdownSection section: relevantSections) {
			log("createHTMLNodesForCodeBlock. Section = " + section + ", raw text = >>" + section.getRawText() + "<<");
			node = codeNode.appendNode("span", section.getRawText());
			String style = getCodeBlockStyle(section.getType());
			if (style != null) {
				node.setAttribute("style", style);
			}
		}
	}

	private void removeLastLineBreakIfFound(List<BrightMarkdownSection> sections) {
		if (sections.isEmpty()) {
			return;
		}
		BrightMarkdownSection listItem = sections.get(sections.size() - 1);
		if (((listItem.getType() == MDType.CODE_BLOCK_COMMAND) || (listItem.getType() == MDType.CODE_BLOCK_COMMENT)) && (listItem.getRawText().endsWith(BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK))){
			listItem.setRawText(listItem.getRawText().substring(0, listItem.getRawText().length() - BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK.length()));
			if (listItem.getRawText().isEmpty()) {
				sections.remove(sections.size() - 1);
			}
		}
		
	}

	private String getCodeBlockStyle(MDType type) {
		switch (type) {
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
		default:
			break;
		}
		return null;
	}

	private void createHTMLNodesForTableOfContents(BrightXMLNode rootElement, BrightMarkdownSection topSection) {
		List<LevelAndTitle> headingItems = getHeadingItems(topSection);
		if (headingItems == null){
			return;
		}
		
		BrightXMLNode root = rootElement.appendNode("span");
		Map<Integer, BrightXMLNode> levelToListNode = new TreeMap<Integer, BrightXMLNode>();
		for (LevelAndTitle i: headingItems) {
			BrightXMLNode element = getOrCreateListElement(root, levelToListNode, i.getLevel());
			BrightMarkdown.removeDeeperLevels(levelToListNode, i.getLevel());
			element.appendNode("li", i.getTitle());
		}			
	}

	private BrightXMLNode getOrCreateListElement(BrightXMLNode root, Map<Integer, BrightXMLNode> levelToListNode, int level) {
		BrightXMLNode element = levelToListNode.get(level);
		if (element == null){
			if (level == 1){
				element = root.appendNode("ul");
				levelToListNode.put(level, element);
			} else {
				BrightXMLNode parentElement = levelToListNode.get(level - 1);
				if (parentElement == null){
					parentElement = getOrCreateListElement(root, levelToListNode, level - 1);
				}
				element = parentElement.appendNode("ul");
				levelToListNode.put(level, element);
			}
		}
		return element;
	}

	private void createHTMLNodesForRawLine(BrightXMLNode rootElement, BrightMarkdownSection item) throws Exception {
		if ((item.getChildren() != null) || (notEmpty(item.getRawText()))){
			BrightXMLNode node = rootElement.appendNode("p");
			addFormattedText(node, item);
		}
	}

	private void createHTMLNodesForParagraph(BrightXMLNode rootElement, BrightMarkdownSection item) throws Exception {
		BrightXMLNode paragraphNode = rootElement.appendNode("p");
		int numberOfChildren = item.getChildren().size();
		if (numberOfChildren == 1){
			//: if there is only one item: no need for span and br-tags in-between
			addFormattedText(paragraphNode, item.getChildren().get(0));
			return;
		}
		
		
		int index = 0;
		for (BrightMarkdownSection i: item.getChildren()){
			BrightXMLNode paragraphElementNode = paragraphNode.appendNode("span");
			addFormattedText(paragraphElementNode, i);
			
			if (index < numberOfChildren - 1){ //: not the last item
				paragraphNode.appendNode("br");
			}
			
			index ++;
		}
	}
	
	private void createHTMLNodesForUncheckedItem(BrightXMLNode rootElement, BrightMarkdownSection item) throws Exception {
		BrightXMLNode node = rootElement.appendNode("input", null, "type", "checkbox", "disabled", "true");
		addFormattedText(node, item);
		rootElement.appendNode("br");
	}

	private void createHTMLNodesForCheckedItem(BrightXMLNode rootElement, BrightMarkdownSection item) throws Exception {
		BrightXMLNode node = rootElement.appendNode("input", null, "type", "checkbox", "disabled", "true", "checked", "true");
		addFormattedText(node, item);
		rootElement.appendNode("br");
	}

	private int createHTMLNodesForTable(BrightXMLNode rootElement, List<BrightMarkdownSection> items, int pos, BrightMarkdownSection item) throws Exception {
		BrightXMLNode tableNode = rootElement.appendNode("table", null, "class", CSS_CLASS_NAME);
		boolean firstRowIsHeader = false;
		
		List<BrightMarkdownSection> tableItems = new ArrayList<BrightMarkdownSection>();
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
		for (BrightMarkdownSection rowSecion: tableItems) {
			BrightXMLNode rowNode = tableNode.appendNode("tr");
			
			if (rowSecion.getBackgroundColor() != null) {
				setBackgroundColorStyle(rowNode, rowSecion.getBackgroundColor());
			}
			
			String cellTagName = "td";
			if (firstRow && firstRowIsHeader) {
				cellTagName = "th";
			}
			for (BrightMarkdownSection cellSection: rowSecion.getChildren()) {
				if (cellSection.getType() != MDType.TABLE_CELL) {
					throw new RuntimeException("Expected type table cell, but found: " + cellSection.getType());
				}
				BrightXMLNode cellNode = rowNode.appendNode(cellTagName, null);
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
	
	private int countMaxNumberOfColumns(List<BrightMarkdownSection> tableItems) {
		int result = 0;
		for (BrightMarkdownSection i: tableItems) {
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

	private int createHTMLNodesForListItems(BrightXMLNode rootElement, List<BrightMarkdownSection> items, int pos, BrightMarkdownSection item) throws Exception {
		int currentLevel = 1;
		Map<Integer, BrightXMLNode> levelToListNodeMap = new TreeMap<>();
		String listTag = getHTMLListTag(item.getType());
		BrightXMLNode listNode = rootElement.appendNode(listTag);
		levelToListNodeMap.put(currentLevel, listNode);
		
		while (item.getLevel() > currentLevel){
			currentLevel ++;
			listNode = listNode.appendNode(listTag);
			levelToListNodeMap.put(currentLevel, listNode);
		}
		
		BrightXMLNode itemNode = listNode.appendNode("li");
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
				BrightMarkdown.removeDeeperLevels(levelToListNodeMap, item.getLevel());
				listNode = levelToListNodeMap.get(item.getLevel());
				if (listNode == null){
					throw new RuntimeException("Could not get list node at level " + item.getLevel() + ". levels: " + levelToListNodeMap.keySet());
				}
				currentLevel = item.getLevel();
			}
			
			itemNode = listNode.appendNode("li");
			addFormattedText(itemNode, item);
		}
		return pos;
	}

	private void addFormattedText(BrightXMLNode node, BrightMarkdownSection item) throws Exception {
		if (item.getRawText() != null){
			node.setTextContent(item.getRawText());
		} else {
			for (BrightMarkdownSection child: item.getChildren()){
				if (child.getType() == MDType.LINK){
					if (child.getRawText() != null){
						node.appendNode("a", child.getRawText(), "href", child.getLocation());
					} else {
						BrightXMLNode linkNode = node.appendNode("a", null, "href", child.getLocation());
						if (child.getChildren() != null){
							addFormattedText(linkNode, child);
						}
					}
				} else if (child.getType() == MDType.IMAGE){
					BrightXMLNode imageNode = node.appendNode("img", null, "src", child.getLocation());
					if (child.getImageHeight() != null) {
						imageNode.setAttribute("height", child.getImageHeight());
					}
					if (child.getImageWidth() != null) {
						imageNode.setAttribute("width", child.getImageWidth());
					}
					if (child.getImageAltText() != null) {
						imageNode.setAttribute("alt", child.getImageAltText());
					}
				} else {
					BrightXMLNode currentNode = node;
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

	private void setBackgroundColorStyle(BrightXMLNode currentNode, String backgroundColor) {
		currentNode.setAttribute("style", "background-color:" + backgroundColor);
	}
	
	private boolean nextChildHasType(List<BrightMarkdownSection> items, int pos, MDType type) {
		int checkPos = pos + 1;
		if (checkPos >= items.size()){
			return false;
		}
		return items.get(checkPos).getType() == type;
	}

	private boolean nextChildHasType(List<BrightMarkdownSection> items, int pos, MDType typeA, MDType typeB) {
		int checkPos = pos + 1;
		if (checkPos >= items.size()){
			return false;
		}
		MDType type = items.get(checkPos).getType();
		return (type == typeA) || (type == typeB);
	}

	private void log(String message) {
		if (loggingActive) {
			System.out.println("BrightMarkdownHTMLCreator> " + message);
		}
	}

	private void log(String message, BrightXMLNode node) {
		if (loggingActive) {
			System.out.println("BrightMarkdownHTMLCreator> " + message + node);
		}
	}


	protected List<LevelAndTitle> getHeadingItems(BrightMarkdownSection section){
		List<LevelAndTitle> result = new ArrayList<LevelAndTitle>();
		
		if (section.getType() == MDType.HEADING){
			LevelAndTitle levelAndTitle = new LevelAndTitle();
			levelAndTitle.setLevel(section.getLevel());
			levelAndTitle.setTitle(section.getOriginalPlainText());
			result.add(levelAndTitle);
		}
		if (section.getChildren() != null){
			for (BrightMarkdownSection i: section.getChildren()){
				result.addAll(getHeadingItems(i));
			}
		}
		return result;
	}

	protected class LevelAndTitle{
		private String title;
		private int level;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
	}
	

	private boolean notEmpty(String text) {
		return text != null && !text.isEmpty();
	}


}
