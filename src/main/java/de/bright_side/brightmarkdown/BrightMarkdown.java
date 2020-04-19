package de.bright_side.brightmarkdown;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.logic.BMDocumentationCreator;
import de.bright_side.brightmarkdown.logic.BMHtmlCreator;
import de.bright_side.brightmarkdown.logic.BMSectionParserLogic;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

/**
 * 
 * @author Philip Heyse
 * @version 1.6.0
 * 
 */
public class BrightMarkdown {
	public static enum FormattingItem {H1, H2, H3, H4, H5, H6}
	public static enum OutputType {FULL_HTML_DOCUMENT, EMBEDDABLE_HTML_CODE}
	private Map<FormattingItem, Integer> fontSizesInMM = new EnumMap<>(FormattingItem.class);
	private Map<String, String> imageNameToPathMap = new TreeMap<String, String>();

	private BMSectionParserLogic sectionParser = new BMSectionParserLogic();
	
	public BrightMarkdown() {
	}

	public String createHTML(String markdownText) throws Exception{
		return createHTML(markdownText, OutputType.FULL_HTML_DOCUMENT);
	}
	
	/**
	 * 
	 * @param markdownText markdown text for which to create HTML
	 * @param outputType OutputType.FULL_HTML_DOCUMENT creates a document including the HTML and -if needed - header tags. 
	 * OutputType.EMBEDDABLE_HTML_CODE only returns the tags that are needed to be *included* as part of an existing document. E.g. inside a div tag 
	 * @return generated HTML code
	 * @throws Exception thrown if an error occurs
	 */
	public String createHTML(String markdownText, OutputType outputType) throws Exception{
		BMSection section = sectionParser.parseAll(getUseMarkdownText(markdownText));
		sectionParser.applyImageNameToPathMapping(section, imageNameToPathMap);
		return new BMHtmlCreator(fontSizesInMM).toHTML(section, outputType);
	}

	/**
	 * 
	 * @param markdownText markdown text for which to create the style code
	 * @return the text that can be placed inside the style tags in the HTML header
	 * @throws Exception thrown if an error occurs
	 */
	public String createStyleCode(String markdownText) throws Exception{
		BMSection section = sectionParser.parseAll(getUseMarkdownText(markdownText));
		return new BMHtmlCreator(fontSizesInMM).createStyleCode(section);
	}
	
	/**
	 * via mappings the location text is mapped from given parameter 'name' to given parameter 'path'.
	 * E.g. a mapping "img1" to "/image/image_1234_large.jpg" could be added and then the image tag
	 * "...!(My Image One)[img1]..." in the markdown text would be mapped to "...!(My Image One)[/image/image_1234_large.jpg]..."
	 * @param name name of the image
	 * @param path path with which the image name should be replaced
	 */
	public void addImageNameToPathMapping(String name, String path) {
		imageNameToPathMap.put(name, path);
	}
	
	/**
	 * 
	 * @param markdownText markdown text to be processed
	 * @return the deepest heading which is 0 if there are no headings, and e.g. 3 if there is h1, h2 and h3
	 * @throws Exception thrown if an error occurs
	 */
	public int getDeepestHeading(String markdownText) throws Exception{
		BMSection section = sectionParser.parseAll(getUseMarkdownText(markdownText));
		return getDeepestHeading(section);
	}
	
	public String getDocumentationAsHTML() throws Exception{
		return new BrightMarkdown().createHTML(getDocumentationAsMarkdown()).replace("\r", "").replace("\n", "");
	}
	
	public String getDocumentationAsMarkdown(){
		return new BMDocumentationCreator().getDocumentationAsMarkdown();
	}
	
	public void setFontSizeInMM(FormattingItem formattingItem, int sizeInMM){
		fontSizesInMM.put(formattingItem, sizeInMM);		
	}
	
	private int getDeepestHeading(BMSection section) {
		int max = 0;
		if (section.getType() == MDType.HEADING){
			max = Math.max(section.getLevel(), max);
		}
		if (section.getChildren() != null){
			for (BMSection i: section.getChildren()){
				max = Math.max(getDeepestHeading(i), max);
			}
		}
		return max;
	}
	
	private String getUseMarkdownText(String markdownText) {
		String useMarkdownText = "";
		if (markdownText != null) {
			if (markdownText.trim().startsWith(BMConstants.NO_MARKDOWN_MARK)) {
				useMarkdownText = BMUtil.escapeSpecialCharacters(markdownText.trim().substring(BMConstants.NO_MARKDOWN_MARK.length()).trim());
			} else {
				useMarkdownText = markdownText;
			}
		}
		
		return useMarkdownText;
	}
	
}
