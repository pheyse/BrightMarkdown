package de.bright_side.brightmarkdown.model;

import java.util.Set;

public class BMCodeFormat {

	private Set<String> stringIndicators; 
	private String escapeCharacter; 
	private Set<String> keywords;
	private String blockCommentStart;
	private String blockCommentEnd;
	private String lineCommentStart;
	private Set<String> tagStarts;
	private Set<String> tagEnds;

	
	public Set<String> getStringIndicators() {
		return stringIndicators;
	}
	public void setStringIndicators(Set<String> stringIndicators) {
		this.stringIndicators = stringIndicators;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	public String getEscapeCharacter() {
		return escapeCharacter;
	}
	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}
	public String getBlockCommentStart() {
		return blockCommentStart;
	}
	public void setBlockCommentStart(String blockCommentStart) {
		this.blockCommentStart = blockCommentStart;
	}
	public String getLineCommentStart() {
		return lineCommentStart;
	}
	public void setLineCommentStart(String lineCommentStart) {
		this.lineCommentStart = lineCommentStart;
	}
	public String getBlockCommentEnd() {
		return blockCommentEnd;
	}
	public void setBlockCommentEnd(String blockCommentEnd) {
		this.blockCommentEnd = blockCommentEnd;
	}
	public Set<String> getTagStarts() {
		return tagStarts;
	}
	public void setTagStarts(Set<String> tagStarts) {
		this.tagStarts = tagStarts;
	}
	public Set<String> getTagEnds() {
		return tagEnds;
	}
	public void setTagEnds(Set<String> tagEnds) {
		this.tagEnds = tagEnds;
	} 

}
