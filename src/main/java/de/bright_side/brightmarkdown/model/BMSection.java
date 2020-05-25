package de.bright_side.brightmarkdown.model;

import java.util.List;

/**
 * 
 * @author Philip Heyse
 *
 */
public class BMSection {
	public enum MDType{ROOT, RAW_LINE, PARAGRAPH, PARAGRAPH_ELEMENT, PLAIN_TEXT, HEADING, HORIZONTAL_RULE, BULLET_POINT, NUMBERED_ITEM
		, LINK, CHECKED_ITEM, UNCHECKED_ITEM, CODE_BLOCK, CODE_BLOCK_KEYWORD, CODE_BLOCK_COMMENT, CODE_BLOCK_STRING, CODE_BLOCK_COMMAND
		, CODE_BLOCK_TAG, TABLE_OF_CONTENTS, TABLE_ROW, TABLE_CELL, FORMATTED_TEXT, IMAGE}
	private MDType type;
	private Integer level;
	private String rawText;
	private String originalPlainText;
	private List<BMSection> children;
	private BMSection parent;
	private String location;
	private boolean bold = false;
	private boolean italic = false;
	private boolean underline = false;
	private boolean strikeThrough = false;
	private String color = null;
	private String backgroundColor = null;
	private String imageWidth;
	private String imageHeight;
	private String imageAltText;
	private String imageBorder;
	private boolean backgroundColorEndTag = false;
	private boolean multiLine = false;
	private boolean nested = false;

	public MDType getType() {
		return type;
	}
	public void setType(MDType type) {
		this.type = type;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public List<BMSection> getChildren() {
		return children;
	}
	public void setChildren(List<BMSection> children) {
		this.children = children;
	}
	public BMSection getParent() {
		return parent;
	}
	public void setParent(BMSection parent) {
		this.parent = parent;
	}
	public String getRawText() {
		return rawText;
	}
	public void setRawText(String rawText) {
		this.rawText = rawText;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getOriginalPlainText() {
		return originalPlainText;
	}
	public void setOriginalPlainText(String originalPlainText) {
		this.originalPlainText = originalPlainText;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	public boolean isItalic() {
		return italic;
	}
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	public boolean isUnderline() {
		return underline;
	}
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	public boolean isStrikeThrough() {
		return strikeThrough;
	}
	public void setStrikeThrough(boolean strikeThrough) {
		this.strikeThrough = strikeThrough;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}
	public String getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}
	public String getImageAltText() {
		return imageAltText;
	}
	public void setImageAltText(String imageAltText) {
		this.imageAltText = imageAltText;
	}
	public boolean isBackgroundColorEndTag() {
		return backgroundColorEndTag;
	}
	public void setBackgroundColorEndTag(boolean backgroundColorEndTag) {
		this.backgroundColorEndTag = backgroundColorEndTag;
	}
	public boolean isMultiLine() {
		return multiLine;
	}
	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}
	public boolean isNested() {
		return nested;
	}
	public void setNested(boolean nested) {
		this.nested = nested;
	}
	public String getImageBorder() {
		return imageBorder;
	}
	public void setImageBorder(String imageBorder) {
		this.imageBorder = imageBorder;
	}
	@Override
	public String toString() {
		return "BMSection [type=" + type + ", rawText=" + rawText + ", nested=" + nested + ", level=" + level + ", multiLine=" + multiLine + "]";
	}
	
}
