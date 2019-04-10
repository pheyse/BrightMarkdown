package de.bright_side.brightmarkdown;

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

public class BrightXMLNode {
	private String name;
	private Map<String, String> attributes;
	private String textContent;
	private List<BrightXMLNode> children;
	private BrightXMLNode parent;
	
	public BrightXMLNode(String name) {
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


	private void setChildren(Document document, Element rootElement, List<BrightXMLNode> children) {
		if (children == null) {
			return;
		}
		for (BrightXMLNode i: children) {
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
	
	public BrightXMLNode appendNode(String name) {
		BrightXMLNode result = new BrightXMLNode(name);
		result.parent = this;
		if (children == null) {
			children = new ArrayList<BrightXMLNode>();
		}
		children.add(result);
		return result;
	}

	public BrightXMLNode appendNode(String name, String textContent, String...attribNamesAndValues) {
		BrightXMLNode result = appendNode(name);
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
	
	public List<BrightXMLNode> getChildNodes(){
		if (children == null) {
			return new ArrayList<BrightXMLNode>();
		}
		return children;
	}
	
	public boolean hasChildNodes() {
		return (children != null) && (!children.isEmpty());
	}

	public String getNodeName() {
		return name;
	}
	
	public BrightXMLNode getParentNode() {
		return parent;
	}
	
	public boolean hasNonEmptyTextContent() {
		return (textContent != null) && (!textContent.isEmpty());
	}

	public void removeChild(BrightXMLNode node) {
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
		return "BrightXMLNode [name=" + name + ", textContent=" + textContent + ", attributes=" + attributes + ", #children:" + getChildNodes().size() + "]";
	}
	
	

}
