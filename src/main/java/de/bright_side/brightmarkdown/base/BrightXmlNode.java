package de.bright_side.brightmarkdown.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BrightXmlNode {
	private String name;
	private Map<String, String> attributes;
	private String textContent;
	private List<BrightXmlNode> children;
	private BrightXmlNode parent;
	
	public BrightXmlNode(String name) {
		this.name = name;
	}
	
	public Document toW3CDocument() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		Element rootElement = document.createElement(name);
		
		setTextContent(rootElement, textContent);
		setAttributes(rootElement, attributes);
		setChildren(document, rootElement, children);
		
		document.appendChild(rootElement);
		return document;
	}


	private void setChildren(Document document, Element rootElement, List<BrightXmlNode> children) {
		if (children == null) {
			return;
		}
		for (BrightXmlNode i: children) {
			Element childElement = document.createElement(i.name);
			rootElement.appendChild(childElement);
			setTextContent(childElement, i.textContent);
			setAttributes(childElement, i.attributes);
			setChildren(document, childElement, i.children);
		}
	}

	private void setTextContent(Element element, String textContent) {
		if (textContent != null) {
			element.setTextContent(textContent);
		}
	}

	private void setAttributes(Element element, Map<String, String> attributes) {
		if (attributes == null) {
			return;
		}
		for (Entry<String, String> i: attributes.entrySet()) {
			element.setAttribute(i.getKey(), i.getValue());
		}
	}
	
	public BrightXmlNode appendNode(String name) {
		BrightXmlNode result = new BrightXmlNode(name);
		result.parent = this;
		if (children == null) {
			children = new ArrayList<BrightXmlNode>();
		}
		children.add(result);
		return result;
	}

	public BrightXmlNode appendNode(String name, String textContent, String...attribNamesAndValues) {
		BrightXmlNode result = appendNode(name);
		result.textContent = textContent;
		
		if ((attribNamesAndValues != null) && (attribNamesAndValues.length > 0)){
			result.initAttributes();
			int length = attribNamesAndValues.length;
			if (length % 2 != 0) {
				length --;
			}
			for (int i = 0; i < length; i+= 2) {
				result.attributes.put(attribNamesAndValues[i], attribNamesAndValues[i + 1]);
			}
			if (length != attribNamesAndValues.length) {
				result.attributes.put(attribNamesAndValues[attribNamesAndValues.length - 1], "");
			}
		}
		
		return result;
	}
	
	public List<BrightXmlNode> getChildNodes(){
		if (children == null) {
			return new ArrayList<BrightXmlNode>();
		}
		return children;
	}
	
	public boolean hasChildNodes() {
		return (children != null) && (!children.isEmpty());
	}

	public String getNodeName() {
		return name;
	}
	
	public BrightXmlNode getParentNode() {
		return parent;
	}
	
	public boolean hasNonEmptyTextContent() {
		return (textContent != null) && (!textContent.isEmpty());
	}

	public void removeChild(BrightXmlNode node) {
		if (children == null) {
			return;
		}
		children.remove(node);
	}

	public boolean hasAttributes() {
		return (attributes != null) && (!attributes.isEmpty());
	}

	public String getTextContent() {
		if (textContent == null) {
			return "";
		}
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
		
	}

	public void setAttribute(String name, String value) {
		initAttributes();
		attributes.put(name, value);
	}

	private void initAttributes() {
		if (attributes == null) {
			attributes = new LinkedHashMap<String, String>();
		}
	}

	@Override
	public String toString() {
		return toString(false, 0);
//		return "BrightXmlNode [name=" + name + ", textContent=" + textContent + ", attributes=" + attributes + ", #children:" + getChildNodes().size() + "]";
	}
	
	public String toString(boolean includeChildren) {
		return toString(includeChildren, 0);
	}
	
	public String toString(boolean includeChildren, int indent) {
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < indent; i++) {
			result.append("   ");
		}
		result.append("BrightXmlNode [name=" + name + ", textContent=" + textContent + ", attributes=" + attributes + "]");
		if ((children != null) && (children.size() > 0)) {
			result.append(" ] children:\n");
			for (BrightXmlNode i: children) {
				result.append(i.toString(true, indent + 1) + "\n");
			}
		} else {
			result.append(", no children]");
		}
		
		return result.toString();
	}
	
	

}
