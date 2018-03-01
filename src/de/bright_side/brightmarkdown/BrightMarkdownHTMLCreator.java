package de.bright_side.brightmarkdown;

import java.io.StringWriter;
import java.util.ArrayList;
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

import de.bright_side.brightmarkdown.BrightMarkdown.FormattingItem;
import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

public class BrightMarkdownHTMLCreator {
	private Map<FormattingItem, Integer> fontSizesInMM;

	public BrightMarkdownHTMLCreator(Map<FormattingItem, Integer> fontSizesInMM) {
		this.fontSizesInMM = fontSizesInMM;
	}

	protected String toHTML(BrightMarkdownSection section) throws Exception {
		int indent = 4;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		Element rootElement = document.createElement("html");
		document.appendChild(rootElement);
		
		boolean containsTables = checkContainsTables(section);
		
		Element headElement = null;
		
		if ((isCSSStyleSet()) || (containsTables)){
			headElement = appendNode(rootElement, "head", null);
			StringBuilder sb = new StringBuilder();
			if (isCSSStyleSet()) {
				sb.append(createFontSizesStyle() + "\n");
			}
			if (containsTables) {
				sb.append(createTableStyles() + "\n");
			}
			
			appendNode(headElement, "style", sb.toString());
//			createCSSStyleNode(headElement);
		}
		
		Element bodyElement = appendNode(rootElement, "body", null);
		createHTMLNodes(bodyElement, section);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "" + indent);
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String result = writer.getBuffer().toString();
		log("result before replacing empty p-tag: >>\n" + result + "\n<<");
		result = result.replace("<p></p>", "");
		result = result.replace("<span></span>", "");
		result = BrightMarkdown.unescape(result);
        result = result.replace(BrightMarkdown.ESCAPE_NEW_LINE_IN_CODE_BLOCK, "<br/>");
		return result;
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
		result.append("table {border-collapse: collapse;}\n");
		result.append("td, th {border: 1px solid black; padding: 3px;}\n");
		result.append("th {background-color: #a0a0a0;}\n");
		result.append("tr:nth-child(odd) {background-color: #d8d8d8;}\n");
		result.append("tr:nth-child(even) {background-color: #ffffff;}\n");
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

	private void createHTMLNodes(Element rootElement, BrightMarkdownSection topSection) throws Exception {
		List<BrightMarkdownSection> items = topSection.getChildren();
		int pos = 0;
		while (pos < items.size()){
			BrightMarkdownSection item = items.get(pos);
			if (item.getType() == MDType.RAW_LINE){
				createHTMLNodesForRawLine(rootElement, item);
			} else if (item.getType() == MDType.PARAGRAPH){
				createHTMLNodesForParagraph(rootElement, item);
			} else if (item.getType() == MDType.HEADING){
				addFormattedText(appendNode(rootElement, "h" + item.getLevel(), null), item);
			} else if (item.getType() == MDType.HORIZONTAL_RULE){
				appendNode(rootElement, "hr", null);
			} else if (item.getType() == MDType.CODE_BLOCK){
				Element node = appendNode(rootElement, "pre", null);
				appendNode(node, "code", item.getRawText());
			} else if (item.getType() == MDType.CHECKED_ITEM){
				createHTMLNodesForCheckedItem(rootElement, item);
			} else if (item.getType() == MDType.UNCHECKED_ITEM){
				createHTMLNodesForUncheckedItem(rootElement, item);
			} else if (item.getType() == MDType.BULLET_POINT){
				pos = createHTMLNodesForBulletPoints(rootElement, items, pos, item);
			} else if (item.getType() == MDType.NUMBERED_ITEM){
				pos = createHTMLNodesForNumberedItems(rootElement, items, pos, item);
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

	private Element appendNode(Element parentElement, String tag, String content) {
		if (parentElement == null){
			throw new RuntimeException("Could not add node with tag '" + tag + "' to parent element which is null");
		}
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


	private void createHTMLNodesForTableOfContents(Element rootElement, BrightMarkdownSection topSection) {
		List<LevelAndTitle> headingItems = getHeadingItems(topSection);
		if (headingItems == null){
			return;
		}
		
		Element root = appendNode(rootElement, "span", null);
		Map<Integer, Element> levelToListNode = new TreeMap<Integer, Element>();
		for (LevelAndTitle i: headingItems) {
			Element element = getOrCreateListElement(root, levelToListNode, i.getLevel());
			BrightMarkdown.removeDeeperLevels(levelToListNode, i.getLevel());
			appendNode(element, "li", i.getTitle());
		}			
	}

	private Element getOrCreateListElement(Element root, Map<Integer, Element> levelToListNode, int level) {
		Element element = levelToListNode.get(level);
		if (element == null){
			if (level == 1){
				element = appendNode(root, "ul", null);
				levelToListNode.put(level, element);
			} else {
				Element parentElement = levelToListNode.get(level - 1);
				if (parentElement == null){
					parentElement = getOrCreateListElement(root, levelToListNode, level - 1);
				}
				element = appendNode(parentElement, "ul", null);
				levelToListNode.put(level, element);
			}
		}
		return element;
	}

	private void createHTMLNodesForRawLine(Element rootElement, BrightMarkdownSection item) throws Exception {
		if ((item.getChildren() != null) || (notEmpty(item.getRawText()))){
			Element node = appendNode(rootElement, "p", null);
			addFormattedText(node, item);
		}
	}

	private void createHTMLNodesForParagraph(Element rootElement, BrightMarkdownSection item) throws Exception {
		Element paragraphNode = appendNode(rootElement, "p", null);
		int numberOfChildren = item.getChildren().size();
		if (numberOfChildren == 1){
			//: if there is only one item: no need for span and br-tags in-between
			addFormattedText(paragraphNode, item.getChildren().get(0));
			return;
		}
		
		
		int index = 0;
		for (BrightMarkdownSection i: item.getChildren()){
			Element paragraphElementNode = appendNode(paragraphNode, "span", null);
			addFormattedText(paragraphElementNode, i);
			
			if (index < numberOfChildren - 1){ //: not the last item
				appendNode(paragraphNode, "br", null);
			}
			
			index ++;
		}
	}
	
	private void createHTMLNodesForUncheckedItem(Element rootElement, BrightMarkdownSection item) throws Exception {
		Element node = appendNode(rootElement, "input", null);
		setAttrib(node, "type", "checkbox");
		setAttrib(node, "disabled", "true");
		addFormattedText(node, item);
		appendNode(rootElement, "br", null);
	}

	private void createHTMLNodesForCheckedItem(Element rootElement, BrightMarkdownSection item) throws Exception {
		Element node = appendNode(rootElement, "input", null);
		setAttrib(node, "type", "checkbox");
		setAttrib(node, "disabled", "true");
		setAttrib(node, "checked", "true");
		addFormattedText(node, item);
		appendNode(rootElement, "br", null);
	}

	private int createHTMLNodesForNumberedItems(Element rootElement, List<BrightMarkdownSection> items, int pos, BrightMarkdownSection item) throws Exception {
		Element listNode = appendNode(rootElement, "ol", null);
		Element itemNode = appendNode(listNode, "li", null);
		addFormattedText(itemNode, item);
		while (nextChildHasType(items, pos, MDType.NUMBERED_ITEM)){
			pos ++;
			item = items.get(pos);
			itemNode = appendNode(listNode, "li", null);
			addFormattedText(itemNode, item);
		}
		return pos;
	}

	private int createHTMLNodesForTable(Element rootElement, List<BrightMarkdownSection> items, int pos, BrightMarkdownSection item) throws Exception {
		Element tableNode = appendNode(rootElement, "table", null);
//		setAttrib(tableNode, "style", "border: 1px solid");
//		setAttrib(tableNode, "border", "1");
//		setAttrib(tableNode, "cellspacing", "0");

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
			Element rowNode = appendNode(tableNode, "tr", null);
			String cellTagName = "td";
			if (firstRow && firstRowIsHeader) {
				cellTagName = "th";
			}
			for (BrightMarkdownSection cellSection: rowSecion.getChildren()) {
				if (cellSection.getType() != MDType.TABLE_CELL) {
					throw new RuntimeException("Expected type table cell, but found: " + cellSection.getType());
				}
				Element cellNode = appendNode(rowNode, cellTagName, null);
				addFormattedText(cellNode, cellSection);
			}
			int missingCells = numberOfColumns - rowSecion.getChildren().size();
			for (int i = 0; i < missingCells; i++) {
				appendNode(rowNode, "td", null);
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

	private int createHTMLNodesForBulletPoints(Element rootElement, List<BrightMarkdownSection> items, int pos,
			BrightMarkdownSection item) throws Exception {
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
				BrightMarkdown.removeDeeperLevels(levelToListNodeMap, item.getLevel());
				listNode = levelToListNodeMap.get(item.getLevel());
				if (listNode == null){
					throw new RuntimeException("Could not get list node at level " + item.getLevel() + ". levels: " + levelToListNodeMap.keySet());
				}
				currentLevel = item.getLevel();
			}
			
			itemNode = appendNode(listNode, "li", null);
			addFormattedText(itemNode, item);
		}
		return pos;
	}

	private void addFormattedText(Element node, BrightMarkdownSection item) throws Exception {
		if (item.getRawText() != null){
			node.setTextContent(item.getRawText());
		} else {
			for (BrightMarkdownSection child: item.getChildren()){
				if (child.getType() == MDType.BOLD){
					log("creating bold tag with content >>" + child.getRawText() + "<< and children: " + child.getChildren());
					if (hasChildren(child)){
						addFormattedText(appendNode(node, "b", null), child);
					} else {
						appendNodeIfConcentNotEmpty(node, "b", child.getRawText());
					}
				} else if (child.getType() == MDType.ITALIC){
					if (hasChildren(child)){
						addFormattedText(appendNode(node, "i", null), child);
					} else {
						appendNodeIfConcentNotEmpty(node, "i", child.getRawText());
					}
				} else if (child.getType() == MDType.UNDERLINE){
					if (hasChildren(child)){
						addFormattedText(appendNode(node, "u", null), child);
					} else {
						appendNodeIfConcentNotEmpty(node, "u", child.getRawText());
					}
				} else if (child.getType() == MDType.STRIKETHROUGH){
					if (hasChildren(child)){
						addFormattedText(appendNode(node, "strike", null), child);
					} else {
						appendNodeIfConcentNotEmpty(node, "strike", child.getRawText());
					}
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
	
	private boolean nextChildHasType(List<BrightMarkdownSection> items, int pos, MDType type) {
		int checkPos = pos + 1;
		if (checkPos >= items.size()){
			return false;
		}
		return items.get(checkPos).getType() == type;
	}


	private boolean hasChildren(BrightMarkdownSection section) {
		return (section.getChildren() != null) && (!section.getChildren().isEmpty());
	}

	private Element setAttrib(Element element, String attributeName, String attributeValue) {
		element.setAttribute(attributeName, attributeValue);
		return element;
	}
	
	private void log(String message) {
		System.out.println("BrightMarkdownHTMLCreator> " + message);
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
