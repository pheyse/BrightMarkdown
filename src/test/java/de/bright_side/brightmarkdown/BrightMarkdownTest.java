package de.bright_side.brightmarkdown;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.bright_side.brightmarkdown.BrightMarkdown.FormattingItem;
import de.bright_side.brightmarkdown.BrightMarkdown.OutputType;
import de.bright_side.brightmarkdown.base.BMConstants;
import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.base.TestingConstants;
import de.bright_side.brightmarkdown.base.TestingUtil;
import de.bright_side.brightmarkdown.logic.BMHtmlCreator;
import de.bright_side.brightmarkdown.logic.BMSectionParserLogic;
import de.bright_side.brightmarkdown.model.BMSection;

/**
 * 
 * @author Philip Heyse
 *
 */
public class BrightMarkdownTest {
	private static final String TABLE_STYLE = "<style>table.brightmarkdown{border-collapse: collapse;}table.brightmarkdown td {border: 1px solid black; padding: 3px;}table.brightmarkdown th {border: 1px solid black; padding: 3px;}table.brightmarkdown th {background-color: #a0a0a0;}table.brightmarkdown tr:nth-child(odd) {background-color: #d8d8d8;}table.brightmarkdown tr:nth-child(even) {background-color: #ffffff;}</style>";

	private void log(String message) {
		if (TestingConstants.TEST_BRIGHT_MARKDOWN_LOGGING_ACTIVE) {
			System.out.println("TestBrightMarkdown> " + message);
		}
	}

	private String removeFormatting(String htmlString) {
		return htmlString.replace("\r", "").replace("\n", "").replace("    ", "");
	}
	
	private String processAndProvideDebugInfo(StringBuilder input, String testName, OutputType outputType) throws Exception {
		return processAndProvideDebugInfo(new BrightMarkdown(), input.toString(), testName, outputType);
	}
	
	private String processAndProvideDebugInfo(String input, String testName, OutputType outputType) throws Exception {
		return processAndProvideDebugInfo(new BrightMarkdown(), input, testName, outputType);
	}
	
	private String processAndProvideDebugInfo(BrightMarkdown brightMarkdown, String input, String testName, OutputType outputType) throws Exception {
		String logPrefix = "["+ testName + "] ";
		
		log(logPrefix + "input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log(logPrefix + "==========================");
		log(logPrefix + "processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log(logPrefix + "==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log(logPrefix + "==========================");
		log(logPrefix + "parseAll sections:\n" + BMUtil.toString(sections));
		log(logPrefix + "==========================");
		
		String resultRaw = brightMarkdown.createHTML(input, outputType);
		String result = removeFormatting(resultRaw);
		
		log(logPrefix + "==========================");
		log(logPrefix + "resultRaw:\n" + resultRaw);
		log(logPrefix + "==========================");
		log(logPrefix + "Result:\n" + result);
		
		
		TestingUtil.writeDebugFileAndResources(testName, resultRaw);
		return result;
	}

	@Test
	public void getDocumentationAsHTML_normal() throws Exception{
		String documentationAsHTML = new BrightMarkdown().getDocumentationAsHTML();
		log("Documentation:\n" + documentationAsHTML);
		TestingUtil.writeDebugFileAndResources("getDocumentationAsHTML_normal", documentationAsHTML);
	}

	@Test
	public void createDemoHTML() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("*+_Table of Contents:_+*\n");
		sb.append("{TOC}\n");
		sb.append("# BrightMarkdown Demo\n");
		sb.append("## Lists & Formatting\n");
		sb.append("### List of Formatting Options\n");
		sb.append(" - Plain text\n");
		sb.append(" - *Bold* text\n");
		sb.append(" - _italic_ text\n");
		sb.append(" - +unterlined+ text\n");
		sb.append(" - ~strikethrough~ text\n");
		sb.append(" - {c:red}red{c} text\n");
		sb.append(" - text with {bc:yellow}background color{bc}\n");
		sb.append(" \n");
		sb.append("### Numbered Lists\n");
		sb.append(". One\n");
		sb.append(". Two\n");
		sb.append(". Three\n");
		sb.append("\n");
		sb.append("### Horizontal Lines\n");
		sb.append("Here's a horizontal line:\n");
		sb.append("-------\n");
		sb.append("\n");
		sb.append("## External references\n");
		sb.append("### Links\n");
		sb.append("This is a link to [Wikipedia][https://www.wikipedia.org/].\n");
		sb.append("\n");
		sb.append("### Images\n");
		sb.append("It is also possible to add images:\n");
		sb.append("![img1.jpg height=30mm] ![img1.jpg height=60mm] ![img1.jpg height=120mm]\n");
		sb.append("\n");
		sb.append("## Advanced Elements\n");
		sb.append("### Code Blocks\n");
		sb.append("It is also possible to have code blocks like ´´´java x=\"hello\";´´´ or longer code blocks such as\n");
		sb.append("´´´java\n");
		sb.append("public String myFunction(int a, int b){\n");
		sb.append("   // compare both values\n");
		sb.append("   if (a == b){\n");
		sb.append("      return \"equals\";\n");
		sb.append("   } else {\n");
		sb.append("      return \"different\";\n");
		sb.append("   }\n");
		sb.append("}\n");
		sb.append("´´´\n");
		sb.append("### Tables\n");
		sb.append("Fruit|Color\n");
		sb.append("====\n");
		sb.append("Banana|{bc:yellow}yellow{bc}\n");
		sb.append("Cherry|{bc:red}{c:white}red{c}{bc}\n");
		sb.append("Pear|{bc:green}{c:white}pear{c}{bc}\n");
		sb.append("Blueberry|{bc:blue}{c:white}blue{c}{bc}\n");
		log("Demo Input:\n>>\n" + sb.toString() + "\n<<");
		String result = new BrightMarkdown().createHTML(sb.toString());
		TestingUtil.writeDebugFileAndResources("createDemoHTML", result);
	}
	
	@Test
	public void createHTML_normal_bold() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><b>bold</b><span>. Nice?</span></span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_normal_italic() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is _italic_. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><i>italic</i><span>. Nice?</span></span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	
	@Test
	public void createHTML_specialCharacters() throws Exception{
		String input = "# Title\nHello! Special: <>&?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p>Hello! Special: &lt;&gt;&amp;?</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_noText() throws Exception{
		String input = "";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_nullInput() throws Exception{
		String input = null;
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_noTextOutputTypeEmbeddable() throws Exception{
		String input = "";
		String result = removeFormatting(new BrightMarkdown().createHTML(input, OutputType.EMBEDDABLE_HTML_CODE));
		String expected = "<span/>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_nullInputOutputTypeEmbeddable() throws Exception{
		String input = null;
		String result = removeFormatting(new BrightMarkdown().createHTML(input, OutputType.EMBEDDABLE_HTML_CODE));
		String expected = "<span/>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_singleCharOutputTypeEmbeddable() throws Exception{
		String input = "x";
		String result = removeFormatting(new BrightMarkdown().createHTML(input, OutputType.EMBEDDABLE_HTML_CODE));
		String expected = "<span><p>x</p></span>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_noMarkdown() throws Exception{
		String input = "Simple Text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Simple Text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_onlyBold() throws Exception{
		String input = "*Only Bold*";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><b>Only Bold</b></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_textAndStrikeThrough() throws Exception{
		String input = "Hello ~strike through~ there";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Hello </span><strike>strike through</strike><span> there</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_onlyHeading() throws Exception{
		String input = "## my title";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h2>my title</h2></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_headingsOnDifferentLevels() throws Exception{
		String input = "# my title 1\n## my title 2\n### my title 3\n# other";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>my title 1</h1><h2>my title 2</h2><h3>my title 3</h3><h1>other</h1></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_headingWithPartItalic() throws Exception{
		String input = "## Title with _italic_ part\nbla";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h2><span>Title with </span><i>italic</i><span> part</span></h2><p>bla</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_fullTextBulletPoints() throws Exception{
		String input = "* item 1\n* item 2\n. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointsWithPartBold() throws Exception{
		String input = "* item 1\n* item 2 with *bold* text\n. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li><span>item 2 with </span><b>bold</b><span> text</span></li><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_plainTextBulletPointsPlainText() throws Exception{
		String input = "Some plain text\n* item 1\n* item 2 with text\n. item 3\nmore plain text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><ul><li>item 1</li><li>item 2 with text</li><li>item 3</li></ul><p>more plain text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_checkBoxes() throws Exception{
		String input = "Some plain text\n[] box 1\n[x] box 2\n[ ] box 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><input disabled=\"true\" type=\"checkbox\">box 1</input><br><input checked=\"true\" disabled=\"true\" type=\"checkbox\">box 2</input><br><input disabled=\"true\" type=\"checkbox\">box 3</input><br></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_checkBoxesWithDifferentSyntax() throws Exception{
		String input = "Some plain text\n - [] box 1\n - [x] box 2\n - [ ] box 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><input disabled=\"true\" type=\"checkbox\">box 1</input><br><input checked=\"true\" disabled=\"true\" type=\"checkbox\">box 2</input><br><input disabled=\"true\" type=\"checkbox\">box 3</input><br></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointsOnDifferentLevelsNormal() throws Exception{
		String input = "Hello\n* item 1\n* item 2\n** item 2.1\n** item 2.2\n* item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_bulletPointsLevelDownAndTwoLevelsUp() throws Exception{
		String input = "Hello\n* item 1\n* item 2\n** item 2.1\n** item 2.2\n*** item 2.2.1\n*** item 2.2.2\n* item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_bulletPointsLevel3WithDashes() throws Exception{
		String input = "Hello\n - item 1\n - item 2\n -- item 2.1\n -- item 2.2\n --- item 2.2.1\n --- item 2.2.2\n - item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointsBeginningWithLevel3() throws Exception{
		String input = "Hello\n*** item 2.2.1\n*** item 2.2.2\n* item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><ul><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void createHTML_horizontalRuleInText() throws Exception{
		String input = "Some plain text\n --------\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><hr><p>other text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_almostHorizontalRulesInText() throws Exception{
		String input = "Some plain text\n __\n\n ___x\n\n ___\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Some plain text</span><br><span>__</span><br><br><span><i>x</i></span><br></p><hr><p>other text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_emptyBold() throws Exception{
		String input = "Some plain text\n**x\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Some plain text</span><br><span>**x</span><br><span>other text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_emptyItalic() throws Exception{
		String input = "Some plain text\n__x\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Some plain text</span><br><span>__x</span><br><span>other text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_emptyItalicOnly() throws Exception{
		String input = "__";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>__</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_linkInTextSquareRoundBrackets() throws Exception{
		String input = "this is a [link](www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_linkInTextSquareBrackets() throws Exception{
		String input = "this is a [link][www.wikipedia.de] and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_linkInTextRoundBrackets() throws Exception{
		String input = "this is a (link)(www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_linkInTextRoundAndSquareBrackets() throws Exception{
		String input = "this is a (link)[www.wikipedia.de] and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	

	
	@Test
	public void createHTML_linkOnly() throws Exception{
		String input = "[link](www.wikipedia.de)";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><a href=\"www.wikipedia.de\">link</a></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_linkInBulletPoints() throws Exception{
		String input = "Title\n - bullet 1 with [link](www.wikipedia.de)\n - bullet two\nmore text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li><span>bullet 1 with </span><a href=\"www.wikipedia.de\">link</a></li><li>bullet two</li></ul><p>more text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_linkWithFormattedText() throws Exception{
		String input = "this is a [link with *formatted* text](www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\"><span>link with </span><b>formatted</b><span> text</span></a><span> and more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_almostLink() throws Exception{
		String input = "this is a not a [link www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>this is a not a [link www.wikipedia.de) and more text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_escapeChars() throws Exception{
		String input = "# Title\nThis text is *bold*. And this _italic_ and this is \\*escaped\\*. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This text is </span><b>bold</b><span>. And this </span><i>italic</i><span> and this is *escaped*. Nice?</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void createHTML_aboutText() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Fonotes\n");
		sb.append("2016-2017 by Philip Heyse - www.bright-side.de\n\n");
		sb.append("## Used libraries and licences:\n");
		sb.append(" - Fliesen UI: Apache V2\n");
		sb.append(" - GSON (https://github.com/google/gson): Apache V2\n");
		sb.append(" - Jetty (http://www.eclipse.org/jetty/): Apache V2\n");
		sb.append("\n\n\n");
		sb.append("## Apache V2:\n");
		sb.append("```\n");
		String headingText = sb.toString();
		sb = new StringBuilder();
		sb.append("                                Apache License\n");
		sb.append("                           Version 2.0, January 2004\n");
		sb.append("                        http://www.apache.org/licenses/\n");
		sb.append("   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n");
		sb.append("   1. Definitions.\n");
		sb.append("      \"License\" shall mean the terms and conditions for use, reproduction,\n");
		sb.append("      and distribution as defined by Sections 1 through 9 of this document.\n");
		sb.append("      \"Licensor\" shall mean the copyright owner or entity authorized by\n");
		sb.append("      the copyright owner that is granting the License.\n");
		sb.append("      \"Legal Entity\" shall mean the union of the acting entity and all\n");
		sb.append("      other entities that control, are controlled by, or are under common\n");
		sb.append("      control with that entity. For the purposes of this definition,\n");
		sb.append("      \"control\" means (i) the power, direct or indirect, to cause the\n");
		sb.append("      direction or management of such entity, whether by contract or\n");
		sb.append("      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n");
		sb.append("      outstanding shares, or (iii) beneficial ownership of such entity.\n");
		sb.append("      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n");
		sb.append("      exercising permissions granted by this License.\n");
		sb.append("      \"Source\" form shall mean the preferred form for making modifications,\n");
		sb.append("      including but not limited to software source code, documentation\n");
		sb.append("      source, and configuration files.\n");
		sb.append("      \"Object\" form shall mean any form resulting from mechanical\n");
		sb.append("      transformation or translation of a Source form, including but\n");
		sb.append("      not limited to compiled object code, generated documentation,\n");
		sb.append("      and conversions to other media types.\n");
		sb.append("      \"Work\" shall mean the work of authorship, whether in Source or\n");
		sb.append("      Object form, made available under the License, as indicated by a\n");
		sb.append("      copyright notice that is included in or attached to the work\n");
		sb.append("      (an example is provided in the Appendix below).\n");
		sb.append("      \"Derivative Works\" shall mean any work, whether in Source or Object\n");
		sb.append("      form, that is based on (or derived from) the Work and for which the\n");
		sb.append("      editorial revisions, annotations, elaborations, or other modifications\n");
		sb.append("      represent, as a whole, an original work of authorship. For the purposes\n");
		sb.append("      of this License, Derivative Works shall not include works that remain\n");
		sb.append("      separable from, or merely link (or bind by name) to the interfaces of,\n");
		sb.append("      the Work and Derivative Works thereof.\n");
		sb.append("      \"Contribution\" shall mean any work of authorship, including\n");
		sb.append("      the original version of the Work and any modifications or additions\n");
		sb.append("      to that Work or Derivative Works thereof, that is intentionally\n");
		sb.append("      submitted to Licensor for inclusion in the Work by the copyright owner\n");
		sb.append("      or by an individual or Legal Entity authorized to submit on behalf of\n");
		sb.append("      the copyright owner. For the purposes of this definition, \"submitted\"\n");
		sb.append("      means any form of electronic, verbal, or written communication sent\n");
		sb.append("      to the Licensor or its representatives, including but not limited to\n");
		sb.append("      communication on electronic mailing lists, source code control systems,\n");
		sb.append("      and issue tracking systems that are managed by, or on behalf of, the\n");
		sb.append("      Licensor for the purpose of discussing and improving the Work, but\n");
		sb.append("      excluding communication that is conspicuously marked or otherwise\n");
		sb.append("      designated in writing by the copyright owner as \"Not a Contribution.\"\n");
		sb.append("      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n");
		sb.append("      on behalf of whom a Contribution has been received by Licensor and\n");
		sb.append("      subsequently incorporated within the Work.\n");
		sb.append("   2. Grant of Copyright License. Subject to the terms and conditions of\n");
		sb.append("      this License, each Contributor hereby grants to You a perpetual,\n");
		sb.append("      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n");
		sb.append("      copyright license to reproduce, prepare Derivative Works of,\n");
		sb.append("      publicly display, publicly perform, sublicense, and distribute the\n");
		sb.append("      Work and such Derivative Works in Source or Object form.\n");
		sb.append("   3. Grant of Patent License. Subject to the terms and conditions of\n");
		sb.append("      this License, each Contributor hereby grants to You a perpetual,\n");
		sb.append("      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n");
		sb.append("      (except as stated in this section) patent license to make, have made,\n");
		sb.append("      use, offer to sell, sell, import, and otherwise transfer the Work,\n");
		sb.append("      where such license applies only to those patent claims licensable\n");
		sb.append("      by such Contributor that are necessarily infringed by their\n");
		sb.append("      Contribution(s) alone or by combination of their Contribution(s)\n");
		sb.append("      with the Work to which such Contribution(s) was submitted. If You\n");
		sb.append("      institute patent litigation against any entity (including a\n");
		sb.append("      cross-claim or counterclaim in a lawsuit) alleging that the Work\n");
		sb.append("      or a Contribution incorporated within the Work constitutes direct\n");
		sb.append("      or contributory patent infringement, then any patent licenses\n");
		sb.append("      granted to You under this License for that Work shall terminate\n");
		sb.append("      as of the date such litigation is filed.\n");
		sb.append("   4. Redistribution. You may reproduce and distribute copies of the\n");
		sb.append("      Work or Derivative Works thereof in any medium, with or without\n");
		sb.append("      modifications, and in Source or Object form, provided that You\n");
		sb.append("      meet the following conditions:\n");
		sb.append("      (a) You must give any other recipients of the Work or\n");
		sb.append("          Derivative Works a copy of this License; and\n");
		sb.append("      (b) You must cause any modified files to carry prominent notices\n");
		sb.append("          stating that You changed the files; and\n");
		sb.append("      (c) You must retain, in the Source form of any Derivative Works\n");
		sb.append("          that You distribute, all copyright, patent, trademark, and\n");
		sb.append("          attribution notices from the Source form of the Work,\n");
		sb.append("          excluding those notices that do not pertain to any part of\n");
		sb.append("          the Derivative Works; and\n");
		sb.append("      (d) If the Work includes a \"NOTICE\" text file as part of its\n");
		sb.append("          distribution, then any Derivative Works that You distribute must\n");
		sb.append("          include a readable copy of the attribution notices contained\n");
		sb.append("          within such NOTICE file, excluding those notices that do not\n");
		sb.append("          pertain to any part of the Derivative Works, in at least one\n");
		sb.append("          of the following places: within a NOTICE text file distributed\n");
		sb.append("          as part of the Derivative Works; within the Source form or\n");
		sb.append("          documentation, if provided along with the Derivative Works; or,\n");
		sb.append("          within a display generated by the Derivative Works, if and\n");
		sb.append("          wherever such third-party notices normally appear. The contents\n");
		sb.append("          of the NOTICE file are for informational purposes only and\n");
		sb.append("          do not modify the License. You may add Your own attribution\n");
		sb.append("          notices within Derivative Works that You distribute, alongside\n");
		sb.append("          or as an addendum to the NOTICE text from the Work, provided\n");
		sb.append("          that such additional attribution notices cannot be construed\n");
		sb.append("          as modifying the License.\n");
		sb.append("      You may add Your own copyright statement to Your modifications and\n");
		sb.append("      may provide additional or different license terms and conditions\n");
		sb.append("      for use, reproduction, or distribution of Your modifications, or\n");
		sb.append("      for any such Derivative Works as a whole, provided Your use,\n");
		sb.append("      reproduction, and distribution of the Work otherwise complies with\n");
		sb.append("      the conditions stated in this License.\n");
		sb.append("   5. Submission of Contributions. Unless You explicitly state otherwise,\n");
		sb.append("      any Contribution intentionally submitted for inclusion in the Work\n");
		sb.append("      by You to the Licensor shall be under the terms and conditions of\n");
		sb.append("      this License, without any additional terms or conditions.\n");
		sb.append("      Notwithstanding the above, nothing herein shall supersede or modify\n");
		sb.append("      the terms of any separate license agreement you may have executed\n");
		sb.append("      with Licensor regarding such Contributions.\n");
		sb.append("   6. Trademarks. This License does not grant permission to use the trade\n");
		sb.append("      names, trademarks, service marks, or product names of the Licensor,\n");
		sb.append("      except as required for reasonable and customary use in describing the\n");
		sb.append("      origin of the Work and reproducing the content of the NOTICE file.\n");
		sb.append("   7. Disclaimer of Warranty. Unless required by applicable law or\n");
		sb.append("      agreed to in writing, Licensor provides the Work (and each\n");
		sb.append("      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n");
		sb.append("      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n");
		sb.append("      implied, including, without limitation, any warranties or conditions\n");
		sb.append("      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n");
		sb.append("      PARTICULAR PURPOSE. You are solely responsible for determining the\n");
		sb.append("      appropriateness of using or redistributing the Work and assume any\n");
		sb.append("      risks associated with Your exercise of permissions under this License.\n");
		sb.append("   8. Limitation of Liability. In no event and under no legal theory,\n");
		sb.append("      whether in tort (including negligence), contract, or otherwise,\n");
		sb.append("      unless required by applicable law (such as deliberate and grossly\n");
		sb.append("      negligent acts) or agreed to in writing, shall any Contributor be\n");
		sb.append("      liable to You for damages, including any direct, indirect, special,\n");
		sb.append("      incidental, or consequential damages of any character arising as a\n");
		sb.append("      result of this License or out of the use or inability to use the\n");
		sb.append("      Work (including but not limited to damages for loss of goodwill,\n");
		sb.append("      work stoppage, computer failure or malfunction, or any and all\n");
		sb.append("      other commercial damages or losses), even if such Contributor\n");
		sb.append("      has been advised of the possibility of such damages.\n");
		sb.append("   9. Accepting Warranty or Additional Liability. While redistributing\n");
		sb.append("      the Work or Derivative Works thereof, You may choose to offer,\n");
		sb.append("      and charge a fee for, acceptance of support, warranty, indemnity,\n");
		sb.append("      or other liability obligations and/or rights consistent with this\n");
		sb.append("      License. However, in accepting such obligations, You may act only\n");
		sb.append("      on Your own behalf and on Your sole responsibility, not on behalf\n");
		sb.append("      of any other Contributor, and only if You agree to indemnify,\n");
		sb.append("      defend, and hold each Contributor harmless for any liability\n");
		sb.append("      incurred by, or claims asserted against, such Contributor by reason\n");
		sb.append("      of your accepting any such warranty or additional liability.\n");
		sb.append("   END OF TERMS AND CONDITIONS\n");
		sb.append("   APPENDIX: How to apply the Apache License to your work.\n");
		sb.append("      To apply the Apache License to your work, attach the following\n");
		sb.append("      boilerplate notice, with the fields enclosed by brackets \"[]\"\n");
		sb.append("      replaced with your own identifying information. (Don't include\n");
		sb.append("      the brackets!)  The text should be enclosed in the appropriate\n");
		sb.append("      comment syntax for the file format. We also recommend that a\n");
		sb.append("      file or class name and description of purpose be included on the\n");
		sb.append("      same \"printed page\" as the copyright notice for easier\n");
		sb.append("      identification within third-party archives.\n");
		sb.append("   Copyright [yyyy] [name of copyright owner]\n");
		sb.append("   Licensed under the Apache License, Version 2.0 (the \"License\");\n");
		sb.append("   you may not use this file except in compliance with the License.\n");
		sb.append("   You may obtain a copy of the License at\n");
		sb.append("       http://www.apache.org/licenses/LICENSE-2.0\n");
		sb.append("   Unless required by applicable law or agreed to in writing, software\n");
		sb.append("   distributed under the License is distributed on an \"AS IS\" BASIS,\n");
		sb.append("   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
		sb.append("   See the License for the specific language governing permissions and\n");
		sb.append("   limitations under the License.\n");
		String codeBlockText = sb.toString();
		sb = new StringBuilder();
		sb.append("```\n");
		sb.append("\n\n\n");
		String footerText = sb.toString();
		
		String input = headingText + codeBlockText + footerText;
		String result = removeFormatting(new BrightMarkdown().createHTML(input));

		String codeBlockTextUse = codeBlockText.replace("\n", "<br/>");
		codeBlockTextUse = codeBlockTextUse.substring(0, codeBlockTextUse.length() - "<br/>".length());
		
		String expected = "<html><body><h1>Fonotes</h1><p><span>2016-2017 by Philip Heyse - www.bright-side.de</span><br></p><h2>Used libraries and licences:</h2><ul><li>Fliesen UI: Apache V2</li>"
				+ "<li>GSON (https://github.com/google/gson): Apache V2</li><li>Jetty (http://www.eclipse.org/jetty/): Apache V2</li></ul><p><br><br></p><h2>Apache V2:</h2>"
				+ "<pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>" + codeBlockTextUse + "</span></code></pre><p><br><br></p></body></html>";
		expected = removeFormatting(expected);
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		assertEquals(expected.replace(">", ">\n"), result.replace(">", ">\n"));
	}
	
	@Test
	public void createHTML_aboutTextShort() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("Text\n");
		sb.append("## Apache V2:\n");
		sb.append("```\n");
		String headingText = sb.toString();
		sb = new StringBuilder();
		sb.append("                                Apache License\n");
		sb.append("                           Version 2.0, January 2004\n");
		String codeBlockText = sb.toString();
		sb = new StringBuilder();
		sb.append("```\n");
		sb.append("\n\n\n");
		String footerText = sb.toString();
		log("codeBlockText:\n>>" + codeBlockText.replace("\n", "\\n") + "<<");
		log("==========================");
		
		String input = headingText + codeBlockText + footerText;
		BMSection section = new BMSectionParserLogic().toMDSection(input);
		log("toMDSection:\n" + BMUtil.toString(section));
		log("==========================");
		String resultWithFormatting = new BrightMarkdown().createHTML(input);
		log("==========================");
		log("resultWithFormatting:\n" + resultWithFormatting);
		log("==========================");
		String result = removeFormatting(new BrightMarkdown().createHTML(input));

		String codeBlockTextUse = codeBlockText.replace("\n", "<br/>");
		codeBlockTextUse = codeBlockTextUse.substring(0, codeBlockTextUse.length() - "<br/>".length());
		
		String expected = "<html><body><h1>Title</h1><p>Text</p><h2>Apache V2:</h2>"
				+ "<pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>" + codeBlockTextUse + "</span></code></pre><p><br><br></p></body></html>";
		expected = removeFormatting(expected);
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		assertEquals(expected.replace(">", ">\n"), result.replace(">", ">\n"));
	}
	
	@Test
	public void createHTML_ignoreFormattingWithinWord() throws Exception{
		String input = "# Title\nThis is time 2017_11_19__08_11 and only _this_ is formatted";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This is time 2017_11_19__08_11 and only </span><i>this</i><span> is formatted</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_ignoreSingleFormattingWithinWord() throws Exception{
		String input = "# Title\nThere is a file_name in the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p>There is a file_name in the text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_ignoreFormattingInWordWith2CharIndicator() throws Exception{
		String input = "# Title\nThis is non__formatted_text and this is _formatted_ text.";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This is non__formatted_text and this is </span><i>formatted</i><span> text.</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_fontSizes() throws Exception{
		String input = "# Title\n## Title 2\nText";
		BrightMarkdown markdown = new BrightMarkdown();
		markdown.setFontSizeInMM(FormattingItem.H1, 40);
		markdown.setFontSizeInMM(FormattingItem.H2, 8);
		String result = removeFormatting(markdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><style>H1{font-size:40mm;}H2{font-size:8mm;}</style></head><body><h1>Title</h1><h2>Title 2</h2><p>Text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void getDeepestHeading_normal() throws Exception{
		String input = "# Title\n## Title 2\nText";
		BrightMarkdown markdown = new BrightMarkdown();
		int result = markdown.getDeepestHeading(input);
		int expected = 2;
		assertEquals(expected, result);
	}
	
	@Test
	public void getDeepestHeading_noHeadings() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n*rest* of the text";
		BrightMarkdown markdown = new BrightMarkdown();
		int result = markdown.getDeepestHeading(input);
		int expected = 0;
		assertEquals(expected, result);
	}
	
	@Test
	public void getDeepestHeading_noText() throws Exception{
		String input = "";
		BrightMarkdown markdown = new BrightMarkdown();
		int result = markdown.getDeepestHeading(input);
		int expected = 0;
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_emptyLines() throws Exception{
		String input = "# Title\nLine 1\nLine 2\n\nLine 3 (after empty line)";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span>Line 2</span><br><br><span>Line 3 (after empty line)</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_emptyLinesAndMoreElements() throws Exception{
		String input = "# Title\nLine 1\nLine 2\n\nLine 3 (after empty line)\n * item 1\n * item 2\nmore text in paragraph\nlast line";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span>Line 2</span><br><br><span>Line 3 (after empty line)</span></p><ul><li>item 1</li><li>item 2</li></ul><p><span>more text in paragraph</span><br><span>last line</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_emptyLinesAndMoreElementsWithFormatting() throws Exception{
		String input = "# Title\nLine 1\n_Line_ 2\n\nLine 3 (after *empty* line)\n * item 1\n * item 2\nmore text in paragraph\nlast line";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span><i>Line</i><span> 2</span></span><br><br><span><span>Line 3 (after </span><b>empty</b><span> line)</span></span></p><ul><li>item 1</li><li>item 2</li></ul><p><span>more text in paragraph</span><br><span>last line</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_nestedFormatting() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append(" - item 1 \"special\" text\n");
		sb.append(" - item 2\n");
		sb.append("  - item 3\n");
		sb.append(" - item 4 with \"quotes\"\n");
		sb.append(" - *bold _italic!_ rest of bold* unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1 \"special\" text</li><li>item 2</li><li>item 3</li><li>item 4 with \"quotes\"</li><li><b>bold </b><b><i>italic!</i></b><b> rest of bold</b><span> unformatted</span></li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_nestedFormattingItalicInBold() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("*bold _italic!_ rest of bold* unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><b>bold </b><b><i>italic!</i></b><b> rest of bold</b><span> unformatted</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_nestedFormattingColorInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic {c:red}red!{c} rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><i><span style=\"color:red\">red!</span></i><i> rest of italic</i><span> unformatted</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_nestedFormattingBackgroundColorInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic {bc:red}red!{bc} rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><i><span style=\"background-color:red\">red!</span></i><i> rest of italic</i><span> unformatted</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_nestedFormattingBoldInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic *bold!* rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><b><i>bold!</i></b><i> rest of italic</i><span> unformatted</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_nestedFormattingBoldAndUnderlineInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic *bold! +and underline+* rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><b><i>bold! </i></b><b><i><u>and underline</u></i></b><i> rest of italic</i><span> unformatted</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_listEntryByLevelOnlyLevel1() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("  - item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_listEntryByLevel2Levels() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("    - item 2.1\n");
		sb.append("    - item 2.2\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li></ul></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}


	
	@Test
	public void createHTML_listEntryByLevelTextListTextListText() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("bla1\n");
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("  - item 3\n");
		sb.append("bla2\n");
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("  - item 3\n");
		sb.append("bla3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>bla1</p><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p>bla2</p><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p>bla3</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_listEntryByLevelUp2Levels() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("    - item 2.1\n");
		sb.append("    - item 2.2\n");
		sb.append("       - item 2.2.1\n");
		sb.append("  - item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li></ul></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_listEntryByLevelUp2LevelsButNotToTop() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("    - item 2.1\n");
		sb.append("    - item 2.2\n");
		sb.append("       - item 2.2.1\n");
		sb.append("          - item 2.2.1.1\n");
		sb.append("    - item 2.3\n");
		sb.append(" - item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><ul><li>item 2.2.1.1</li></ul></ul><li>item 2.3</li></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_listEntryByLevelUp2LevelsButNotToTopNoLeadindSpace() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("    - item 2.1\n");
		sb.append("    - item 2.2\n");
		sb.append("       - item 2.2.1\n");
		sb.append("          - item 2.2.1.1\n");
		sb.append("    - item 2.3\n");
		sb.append("- item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><ul><li>item 2.2.1.1</li></ul></ul><li>item 2.3</li></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_listEntryByLevelMultipleLevelsWith1SpaceOff() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append("  - item 2\n");
		sb.append("     - item 2.1\n");
		sb.append("    - item 2.2\n");
		sb.append("       - item 2.2.1\n");
		sb.append("- item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li></ul></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_listEntryByLevelMultipleLevelsWith2SpacesOff() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append("   - item 2\n");
		sb.append("       - item 2.1\n");
		sb.append("      - item 2.2\n");
		sb.append("          - item 2.2.1\n");
		sb.append("- item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li></ul></ul><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	
	@Test
	public void createHTML_numberedListEntryByLevelOnlyLevel1() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("  . item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_numberedListEntryByLevel2Levels() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    . item 2.2\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li></ol></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_numberedListEntryByLevelTextListTextListText() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("bla1\n");
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("  . item 3\n");
		sb.append("bla2\n");
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("  . item 3\n");
		sb.append("bla3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>bla1</p><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol><p>bla2</p><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol><p>bla3</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_numberedListEntryByLevelUp2Levels() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    . item 2.2\n");
		sb.append("       . item 2.2.1\n");
		sb.append("  . item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><ol><li>item 2.2.1</li></ol></ol><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_numberedListEntryByLevelUp2LevelsButNotToTop() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    . item 2.2\n");
		sb.append("       . item 2.2.1\n");
		sb.append("          . item 2.2.1.1\n");
		sb.append("    . item 2.3\n");
		sb.append(" . item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><ol><li>item 2.2.1</li><ol><li>item 2.2.1.1</li></ol></ol><li>item 2.3</li></ol><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_numberedListEntryByLevelUp2LevelsButNotToTopNoLeadindSpace() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    . item 2.2\n");
		sb.append("       . item 2.2.1\n");
		sb.append("          . item 2.2.1.1\n");
		sb.append("    . item 2.3\n");
		sb.append(". item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><ol><li>item 2.2.1</li><ol><li>item 2.2.1.1</li></ol></ol><li>item 2.3</li></ol><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_numberedListEntryByLevelMultipleLevelsWith1SpaceOff() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append("  . item 2\n");
		sb.append("     . item 2.1\n");
		sb.append("    . item 2.2\n");
		sb.append("       . item 2.2.1\n");
		sb.append(". item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><ol><li>item 2.2.1</li></ol></ol><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_numberedListEntryByLevelMultipleLevelsWith2SpacesOff() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append("   . item 2\n");
		sb.append("       . item 2.1\n");
		sb.append("      . item 2.2\n");
		sb.append("          . item 2.2.1\n");
		sb.append(". item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><ol><li>item 2.2.1</li></ol></ol><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}



	@Test
	public void createHTML_withTOC() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("bla 1\n");
		sb.append("*Content:*\n");
		sb.append("{TOC}\n");
		sb.append("# header 1\n");
		sb.append("bla 2\n");
		sb.append("# header 2\n");
		sb.append("bla 3\n");
		sb.append("## header 2.1\n");
		sb.append("bla 4\n");
		sb.append("## header 2.2\n");
		sb.append("bla 5\n");
		sb.append("### header 2.2.1\n");
		sb.append("bla 6\n");
		sb.append("# header 3\n");
		sb.append("bla 7\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expectedTOC = "<span><ul><li>header 1</li><li>header 2</li><ul><li>header 2.1</li><li>header 2.2</li><ul><li>header 2.2.1</li></ul></ul><li>header 3</li></ul></span>";
		String expectedMainText = "<h1>header 1</h1><p>bla 2</p><h1>header 2</h1><p>bla 3</p><h2>header 2.1</h2><p>bla 4</p><h2>header 2.2</h2><p>bla 5</p><h3>header 2.2.1</h3><p>bla 6</p><h1>header 3</h1><p>bla 7</p>";
		String expected = "<html><body><p><span>bla 1</span><br><span><b>Content:</b></span></p>" + expectedTOC + expectedMainText + "</body></html>";
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_withTOCStartingAtLevel2() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("bla 1\n");
		sb.append("*Content:*\n");
		sb.append("{TOC}\n");
		sb.append("## header 2.1\n");
		sb.append("bla 4\n");
		sb.append("## header 2.2\n");
		sb.append("bla 5\n");
		sb.append("### header 2.2.1\n");
		sb.append("bla 6\n");
		sb.append("# header 3\n");
		sb.append("bla 7\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expectedTOC = "<span><ul><ul><li>header 2.1</li><li>header 2.2</li><ul><li>header 2.2.1</li></ul></ul><li>header 3</li></ul></span>";
		String expectedMainText = "<h2>header 2.1</h2><p>bla 4</p><h2>header 2.2</h2><p>bla 5</p><h3>header 2.2.1</h3><p>bla 6</p><h1>header 3</h1><p>bla 7</p>";
		String expected = "<html><body><p><span>bla 1</span><br><span><b>Content:</b></span></p>" + expectedTOC + expectedMainText + "</body></html>";
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_normalNoHeader() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
//		log("==========================");
//		log("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_normalWithHeader() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		
		log("output with formatting:>>\n" + new BrightMarkdown().createHTML(input) + "\n<<");
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_table_normalNoHeader1Row() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
//		log("==========================");
//		log("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_normalWithHeaderButNoRows() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_emptyCellsMiddle() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1||c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));

		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td></td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_emptyCellsEnd() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td></td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void createHTML_table_missingCellsEnd() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td></td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_emptyCellBeginning() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td></td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void createHTML_table_extraCellsMiddle() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|c2-3|c2-4\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td><td></td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td>c2-3</td><td>c2-4</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td><td></td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_escapeCellSeparatorInTable() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|Hello the \\| is kept|c2-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>Hello the | is kept</td><td>c2-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_normal_escapeCellSeparator() throws Exception{
		String input = "# Title\nThis text is _italic_ and the \\| is kept. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This text is </span><i>italic</i><span> and the | is kept. Nice?</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_only1Row2Columns() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void createHTML_table_withFormatting() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|Hello *bold*|c2-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		log("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td><span>Hello </span><b>bold</b></td><td>c2-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_normal_underline() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is +underlined+. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><u>underlined</u><span>. Nice?</span></span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_normal_shortTextUnderline() throws Exception{
		String input = "This text is +underlined+. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>This text is </span><u>underlined</u><span>. Nice?</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_normal_BoldAndUnderline() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *+underlined & bold+*. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><b><u>underlined &amp; bold</u></b><span>. Nice?</span></span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_noMarkdownTag() throws Exception{
		String input = "{NOMARKDOWN}# Title\n* item 1\n* item 2\n* item 3\n\nThis text is _italic_.\n---\nNice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span># Title</span><br><span>* item 1</span><br><span>* item 2</span><br><span>* item 3</span><br><br><span>This text is _italic_.</span><br><span>---</span><br><span>Nice?</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_numberedListFormatNumberAndPeriod() throws Exception{
		String input = "# My List\n1. item 1\n2. item 2\n2. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>My List</h1><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_numberedListFormatDot() throws Exception{
		String input = "# My List\n. item 1\n. item 2\n. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>My List</h1><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_bulletPointsLevel3Complex() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - i1\n");
		sb.append(" -- i11\n");
		sb.append(" *** i111\n");
		sb.append(" -- i12\n");
		sb.append(" - i2\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>i1</li><ul><li>i11</li><ul><li>i111</li></ul><li>i12</li></ul><li>i2</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_numberedListSubBulletPointSubNumber() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("    - item 2.1\n");
		sb.append("    - item 2.2\n");
		sb.append("       . item 2.2.1\n");
		sb.append("  . item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ol><li>item 2.2.1</li></ol></ul><li>item 3</li></ol></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletListSubNumberSubBulletPoint() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    . item 2.2\n");
		sb.append("       - item 2.2.1\n");
		sb.append("  - item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li></ul></ol><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_mixedBulletPointAndNumberedList() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" . item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    - item 2.2\n");
		sb.append("    . item 2.3\n");
		sb.append("       - item 2.2.1\n");
		sb.append("  - item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li><li>item 2.3</li><ul><li>item 2.2.1</li></ul></ol><li>item 3</li></ul></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_codeBlock() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre><p>rest of the text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeBlockWithIndentedText() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\n  int a = 7;\n  int b = 5;\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>  int a = 7;<br/>  int b = 5;</span></code></pre><p>rest of the text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		log("Result (incl. new lines):\n" + new BrightMarkdown().createHTML(input));
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeBlockUnclosed() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeBlockOnly() throws Exception{
		String input = "```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre></body></html>";

		BMSection section = new BMSectionParserLogic().toMDSection(input);
		log("initial section:\n" + BMUtil.toString(section));
		log("==========================");
		new BMSectionParserLogic().parseCodeSections(section);
		log("after parseCodeSections:\n" + BMUtil.toString(section));
		log("==========================");
		
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeWithUnknownLanguage() throws Exception{
		String input = "```xyz\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>xyz<br/>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeBlockWithBracketsLikeALink() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nThis is text with [brackets][like a link]\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>This is text with [brackets][like a link]</span></code></pre><p>rest of the text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		log("Result (incl. new lines):\n" + new BrightMarkdown().createHTML(input));
		assertEquals(expected, result);
	}

	
	@Test
	public void createHTML_codeBlockJava() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("Title\n");
		sb.append(" - bullet 1\n");
		sb.append(" - bullet two\n");
		sb.append("more text\n");
		sb.append("```java\n");
		sb.append("if (x == 4) {\n");
		sb.append("    //commented out\n");
		sb.append("    int y = \"hi!\";\n");
		sb.append("    /*block\n");
		sb.append("    comment*/\n");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		sb.append("a = b;\n");
		sb.append("```\n");
		sb.append("rest of the text");
		
		String testName = "createHTML_codeBlockJava";
		String result = processAndProvideDebugInfo(sb, testName, OutputType.FULL_HTML_DOCUMENT);
		
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" 
		+ BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">if</span>"
				+ "<span> (x == 4) {<br/></span><span style=\"color:darkgreen\">//commented out</span><span><br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">int</span><span> y = </span><span style=\"color:blue\">\"hi!\"</span>"
				+ "<span>;<br/></span><span style=\"color:darkgreen\">/*block<br/>comment*/</span><span><br/>}<br/>"
				+ "<br/>a = b;</span></code></pre><p>rest of the text</p></body></html>";
		assertEquals(expected, result);
	}


	@Test
	public void createHTML_codeBlockXML() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```xml\n<tag1 x=\"1\">nice</tag1>\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String resultWithFormatting = new BrightMarkdown().createHTML(input);
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">&lt;tag1</span><span> x=</span><span style=\"color:blue\">\"1\"</span><span style=\"color:purple;font-weight:bold\">&gt;</span><span>nice</span><span style=\"color:purple;font-weight:bold\">&lt;/tag1&gt;</span></code></pre><p>rest of the text</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		log("==========================");
		log("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_codeBlockSql() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("- bullet 1\n");
		sb.append("- bullet 2\n");
		sb.append("´´´sql\n");
		sb.append("-- inserts\n");
		sb.append("insert into mytable(key, value) values (17, 'MyValue');\n");
		sb.append("INSERT INTO mytable(key, value) VALUES (18, 'MyOtherValue');\n");
		sb.append("/* some more \n");
		sb.append("documentation */\n");
		sb.append("´´´\n");
		sb.append("more text...\n");

		String input = sb.toString();

		String testName = "createHTML_codeBlockSql";

		String result = processAndProvideDebugInfo(input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Title</h1><ul><li>bullet 1</li><li>bullet 2</li></ul><pre style=\"background:lightgrey\"><code>"
				+ "<span><br/></span><span style=\"color:darkgreen\">-- inserts</span><span><br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">insert</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">into</span><span> mytable(</span>"
				+ "<span style=\"color:purple;font-weight:bold\">key</span><span>, value) </span>"
				+ "<span style=\"color:purple;font-weight:bold\">values</span><span> (17, </span>"
				+ "<span style=\"color:blue\">'MyValue'</span><span>);<br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">INSERT</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">INTO</span><span> mytable(</span>"
				+ "<span style=\"color:purple;font-weight:bold\">key</span><span>, value) </span>"
				+ "<span style=\"color:purple;font-weight:bold\">VALUES</span><span> (18, </span>"
				+ "<span style=\"color:blue\">'MyOtherValue'</span><span>);<br/></span>"
				+ "<span style=\"color:darkgreen\">/* some more <br/>documentation */</span>"
				+ "</code></pre><p>more text...</p></span>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_codeBlockSqlTwoBackticks() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("- bullet 1\n");
		sb.append("- bullet 2\n");
		sb.append("´´sql\n");
		sb.append("-- inserts\n");
		sb.append("insert into mytable(key, value) values (17, 'MyValue');\n");
		sb.append("INSERT INTO mytable(key, value) VALUES (18, 'MyOtherValue');\n");
		sb.append("/* some more \n");
		sb.append("documentation */\n");
		sb.append("´´\n");
		sb.append("more text...\n");

		String input = sb.toString();

		String testName = "createHTML_codeBlockSql";

		String result = processAndProvideDebugInfo(input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Title</h1><ul><li>bullet 1</li><li>bullet 2</li></ul><pre style=\"background:lightgrey\"><code>"
				+ "<span><br/></span><span style=\"color:darkgreen\">-- inserts</span><span><br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">insert</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">into</span><span> mytable(</span>"
				+ "<span style=\"color:purple;font-weight:bold\">key</span><span>, value) </span>"
				+ "<span style=\"color:purple;font-weight:bold\">values</span><span> (17, </span>"
				+ "<span style=\"color:blue\">'MyValue'</span><span>);<br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">INSERT</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">INTO</span><span> mytable(</span>"
				+ "<span style=\"color:purple;font-weight:bold\">key</span><span>, value) </span>"
				+ "<span style=\"color:purple;font-weight:bold\">VALUES</span><span> (18, </span>"
				+ "<span style=\"color:blue\">'MyOtherValue'</span><span>);<br/></span>"
				+ "<span style=\"color:darkgreen\">/* some more <br/>documentation */</span>"
				+ "</code></pre><p>more text...</p></span>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeBlockJavaWithSpecialSectionInStringAndComment() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("- bullet 1\n");
		sb.append("- bullet 2\n");
		sb.append("´´´java\n");
		sb.append("// Main Block: calling !!!hl!myMethod!!!:\n");
		sb.append("\n");
		sb.append("/* using the parameter\n");
		sb.append("   !!!ph hl!<user-name>!!! in a string\n");
		sb.append("*/\n");
		sb.append("result = myMethod(\"username:!!!ph hl!<user-name>!!!, token: 123\");\n");
		sb.append("return result;\n");
		sb.append("´´´\n");
		sb.append("more text...\n");

		String input = sb.toString();

		String testName = "createHTML_codeBlockJavaWithSpecialSectionInStringAndComment";

		String result = processAndProvideDebugInfo(input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Title</h1><ul><li>bullet 1</li><li>bullet 2</li></ul>"
				+ "<pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span style=\"color:darkgreen\">// Main Block: calling </span>"
				+ "<span style=\"font-weight: bold;background-color: #f7ea04;\">myMethod</span>"
				+ "<span style=\"color:darkgreen\">:</span><span><br/><br/></span>"
				+ "<span style=\"color:darkgreen\">/* using the parameter<br/>   </span>"
				+ "<span style=\"font-weight: bold;font-style: italic;background-color: #f7ea04;color: #0000ff;\">&lt;user-name&gt;</span>"
				+ "<span style=\"color:darkgreen\"> in a string<br/>*/</span><span><br/>result = myMethod(</span>"
				+ "<span style=\"color:blue\">\"username:</span>"
				+ "<span style=\"font-weight: bold;font-style: italic;background-color: #f7ea04;color: #0000ff;\">&lt;user-name&gt;</span>"
				+ "<span style=\"color:blue\">, token: 123\"</span><span>);<br/></span><span style=\"color:purple;font-weight:bold\">return</span>"
				+ "<span> result;</span></code></pre><p>more text...</p></span>";
		assertEquals(expected, result);
	}



	
	@Test
	public void createHTML_codeBlockJavaScript() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("- bullet 1\n");
		sb.append("- bullet 2\n");
		sb.append("´´´js\n");
		sb.append("// fetch exampmle:\n");
		sb.append("let myVal = 99;\n");
		sb.append("const myFunc = function(){\n");
		sb.append("   fetch(\"http://localhost:8080/api/contacts\", {\n");
		sb.append("      method: \"POST\",\n");
		sb.append("      headers: {\n");
		sb.append("         \"content-type\": \"application/json\"\n");
		sb.append("      },\n");
		sb.append("      body: JSON.stringify(contact),\n");
		sb.append("   }).then(response => resonse.json());\n");
		sb.append("}\n");
		sb.append("/* some more \n");
		sb.append("documentation */\n");
		sb.append("´´´\n");
		sb.append("more text...\n");
		
		String input = sb.toString();
		
		String testName = "createHTML_codeBlockJavaScript";
		
		String result = processAndProvideDebugInfo(input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Title</h1><ul><li>bullet 1</li><li>bullet 2</li></ul>"
				+ "<pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span style=\"color:darkgreen\">// fetch exampmle:</span><span><br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">let</span><span> myVal = 99;<br/>"
				+ "</span><span style=\"color:purple;font-weight:bold\">const</span><span> myFunc = </span>"
				+ "<span style=\"color:purple;font-weight:bold\">function</span><span>(){<br/>   fetch(</span>"
				+ "<span style=\"color:blue\">\"http://localhost:8080/api/contacts\"</span><span>, {<br/>  method: </span>"
				+ "<span style=\"color:blue\">\"POST\"</span><span>,<br/>  headers: {<br/> </span>"
				+ "<span style=\"color:blue\">\"content-type\"</span><span>: </span>"
				+ "<span style=\"color:blue\">\"application/json\"</span>"
				+ "<span><br/>  },<br/>  body: JSON.stringify(contact),<br/>   }).then(response =&gt; resonse.json());<br/>}<br/>"
				+ "</span><span style=\"color:darkgreen\">/* some more <br/>documentation */</span></code></pre>"
				+ "<p>more text...</p></span>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_codeBlockDoNotOverdetectKeywords() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("´´´java\n");
		sb.append("int hint = 1;\n");
		sb.append("int internet = 2;\n");
		sb.append("int printer = 3;\n");
		sb.append("´´´\n");
		sb.append("more text...\n");

		String input = sb.toString();

		String testName = "createHTML_codeBlockDoNotOverdetectKeywords";

		String result = processAndProvideDebugInfo(input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Title</h1><pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">int</span><span> hint = 1;<br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">int</span><span> internet = 2;<br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">int</span><span> printer = 3;</span></code>"
				+ "</pre><p>more text...</p></span>";
		assertEquals(expected, result);
	}


	@Test
	public void createHTML_table_rowBackground() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("{bc:red}c2-1|c2-2|c2-3{bc}\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("sections:\n" + BMUtil.toString(sections));
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr style=\"background-color:red\"><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_table_rowBackgroundNoEndTag() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("{bc:red}c2-1|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr style=\"background-color:red\"><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	
	@Test
	public void createHTML_table_rowBackgroundInterruption() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("{bc:green}c1-1|c1-2|{bc}c1-3\n");
		sb.append("{bc:red}c2-1|c2-2|c2-3\n");
		sb.append("{bc:yellow}c3-1|c3-{bc:blue}2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td style=\"background-color:green\">c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr style=\"background-color:red\"><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td style=\"background-color:yellow\">c3-1</td><td><span>c3-</span><span style=\"background-color:blue\">2</span></td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}


	
	@Test
	public void createHTML_table_cellBackground() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("{bc:red}c1-1{bc}|c1-2|c1-3\n");
		sb.append("c2-1|{bc:green}c2-2|c2-3\n");
		sb.append("c3-1|c3-2|{bc:blue}c3-3{bc}\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("sections:\n" + BMUtil.toString(sections));

		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td style=\"background-color:red\">c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td style=\"background-color:green\">c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td style=\"background-color:blue\">c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_table_cellBackgroundInterrupted() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|{bc:green}c2{bc:red}-2{bc}|c2-3\n");
		sb.append("c3-1|{bc:blue}c3-_2_{bc}|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("sections:\n" + BMUtil.toString(sections));

		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table class=\"brightmarkdown\">";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td><span style=\"background-color:green\">c2</span><span style=\"background-color:red\">-2</span></td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td style=\"background-color:blue\"><span>c3-</span><i><span style=\"background-color:blue\">2</span></i></td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointWithFormatting() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();

		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - items two-b\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");

		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll: sections:\n" + BMUtil.toString(sections));
		log("==========================");

		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("Result:\n" + result);
	}

	
	@Test
	public void createHTML_bulletPointListWithEmbeddedCodeBlockSimple() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - x```\n");
		sb.append("some code\n");
		sb.append("some more code\n");
		sb.append("```\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");

		String testName = "createHTML_higlightedTextInSingleLineCodeBlock";

		String result = processAndProvideDebugInfo(sb, testName, OutputType.FULL_HTML_DOCUMENT);

		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li></ul><li><i>item two</i></li>"
				+ "<ul><li>items two-a</li><li><span>x</span><pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span>some code<br/>some more code</span></code></pre></li></ul><li>item three</li></ul><p>More text...</p></body></html>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointListWithEmbeddedJavaCodeBlockSimple() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - x```java\n");
		sb.append("int x = 2;\n");
		sb.append("String y = \"hello\";\n");
		sb.append("```\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li>"
				+ "</ul><li><i>item two</i></li><ul><li>items two-a</li><li><span>x</span><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<code><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span><span> x = 2;<br/>String y = </span>"
				+ "<span style=\"color:blue\">\"hello\"</span><span>;</span></code></pre></li></ul><li>item three</li></ul>"
				+ "<p>More text...</p></body></html>";
		
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointListIteOnlyJavaCodeBlockMultiLine() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - ```java\n");
		sb.append("int x = 2;\n");
		sb.append("String y = \"hello\";\n");
		sb.append("```\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li>"
				+ "</ul><li><i>item two</i></li><ul><li>items two-a</li><li><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<code><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span><span> x = 2;<br/>"
				+ "String y = </span><span style=\"color:blue\">\"hello\"</span><span>;</span></code></pre></li></ul>"
				+ "<li>item three</li></ul><p>More text...</p></body></html>";
		
//		UtilForTest.writeDebugFile(resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointListItemOnlyJavaCodeBlockMultiLineButNoEndingLineBreak() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - ```java\n");
		sb.append("int x = 2;\n");
		sb.append("String y = \"hello\";```\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li>"
				+ "</ul><li><i>item two</i></li><ul><li>items two-a</li><li><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<code><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span><span> x = 2;<br/>"
				+ "String y = </span><span style=\"color:blue\">\"hello\"</span><span>;</span></code></pre></li></ul>"
				+ "<li>item three</li></ul><p>More text...</p></body></html>";
		
		TestingUtil.writeDebugFileAndResources("createHTML_bulletPointListItemOnlyJavaCodeBlockMultiLineButNoEndingLineBreak", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointListItenIndentedOnlyJavaCodeBlockMultiLine() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("        - ```java\n");
		sb.append("int x = 2;\n");
		sb.append("String y = \"hello\";\n");
		sb.append("```\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li>"
				+ "</ul><li><i>item two</i></li><ul><li>items two-a</li><ul><li><pre style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<code><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span><span> x = 2;<br/>"
				+ "String y = </span><span style=\"color:blue\">\"hello\"</span><span>;</span></code></pre></li></ul>"
				+ "</ul><li>item three</li></ul><p>More text...</p></body></html>";
		
//		UtilForTest.writeDebugFile(resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointListWithEmbeddedCodeBlockNoLineBreaks() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - with the code ```public static void main(String[] args)´´´ you can declare a *main* function\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li></ul><li>"
				+ "<i>item two</i></li><ul><li>items two-a</li><li><span>with the code </span><code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<span>public static void main(String[] args)</span></code><span> you can declare a </span><b>main</b>"
				+ "<span> function</span></li></ul><li>item three</li></ul><p>More text...</p></body></html>";
		
//		UtilForTest.writeDebugFile(resultRaw);
		assertEquals(expected, result);	
	}

	@Test
	public void createHTML_bulletPointListWithEmbeddedCodeBlockJavaNoLineBreaks() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - with the code ```java public static void main(String[] args)´´´ you can declare a *main* function\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul>"
				+ "<li>item one-a</li></ul><li><i>item two</i></li><ul><li>items two-a</li><li>"
				+ "<span>with the code </span><code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<span style=\"color:purple;font-weight:bold\">public</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">static</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">void</span><span> main(String[] args)</span></code>"
				+ "<span> you can declare a </span><b>main</b><span> function</span></li></ul><li>item three</li>"
				+ "</ul><p>More text...</p></body></html>";
		
//		UtilForTest.writeDebugFile(resultRaw);
		assertEquals(expected, result);	
	}
	
	@Test
	public void createHTML_bulletPointListWithMultipleEmbeddedCodeBlocksNoLineBreaks() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - with the code ```java public static void main(String[] args)´´´ you can declare a *main* function and with ´´´System.exit(0)´´´ the program stops\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li></ul>"
				+ "<li><i>item two</i></li><ul><li>items two-a</li><li><span>with the code </span>"
				+ "<code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><span style=\"color:purple;font-weight:bold\">public</span>"
				+ "<span> </span><span style=\"color:purple;font-weight:bold\">static</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">void</span><span> main(String[] args)</span></code>"
				+ "<span> you can declare a </span><b>main</b><span> function and with </span>"
				+ "<code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><span>System.exit(0)</span></code><span> the program stops</span>"
				+ "</li></ul><li>item three</li></ul><p>More text...</p></body></html>";
		
//		UtilForTest.writeDebugFile(resultRaw);
		assertEquals(expected, result);	
	}

	@Test
	public void createHTML_orderedListWithEmbeddedCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" . item *one*\n");
		sb.append("    . item one-a\n");
		sb.append(" . _item two_\n");
		sb.append("    . items two-a\n");
		sb.append("    . with the code ```java public static void main(String[] args)´´´ you can declare a *main* function and with ´´´System.exit(0)´´´ the program stops\n");
		sb.append(" . item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ol><li><span>item </span><b>one</b></li><ol><li>item one-a</li></ol>"
				+ "<li><i>item two</i></li><ol><li>items two-a</li><li><span>with the code </span>"
				+ "<code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><span style=\"color:purple;font-weight:bold\">public</span>"
				+ "<span> </span><span style=\"color:purple;font-weight:bold\">static</span><span> </span>"
				+ "<span style=\"color:purple;font-weight:bold\">void</span><span> main(String[] args)</span></code>"
				+ "<span> you can declare a </span><b>main</b><span> function and with </span>"
				+ "<code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><span>System.exit(0)</span></code><span> the program stops</span>"
				+ "</li></ol><li>item three</li></ol><p>More text...</p></body></html>";
		
		TestingUtil.writeDebugFileAndResources("createHTML_orderedListWithEmbeddedCodeBlock", resultRaw);
		assertEquals(expected, result);	
	}

	@Test
	public void createHTML_imageInList() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - item two-a\n");
		sb.append("    - item two-b the image: ![my img](test.jpg)\n");
		sb.append("    - item two-c\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);

		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li>"
				+ "</ul><li><i>item two</i></li><ul><li>item two-a</li><li><span>item two-b the image: </span>"
				+ "<img align=\"top\" alt=\"my img\" border=\"1mm\" src=\"test.jpg\" width=\"75%\">"
				+ "</li><li>item two-c</li></ul><li>item three</li></ul><p>More text...</p></body></html>";
		
		TestingUtil.writeDebugFileAndResources("createHTML_imageInList", resultRaw);
		assertEquals(expected, result);	
	}

	@Test
	public void createHTML_imageInListOnlyElementInBulletPoint() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Hello\n");
		sb.append(" - item *one*\n");
		sb.append("    - item one-a\n");
		sb.append(" - _item two_\n");
		sb.append("    - item two-a\n");
		sb.append("    - item two-b the image:\n");
		sb.append("    - ![my img](test.jpg)\n");
		sb.append("    - item two-d\n");
		sb.append(" - item three\n");
		sb.append("More text...\n");
		String input = sb.toString();
		log("input:\n" + input);
		
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><body><p>Hello</p><ul><li><span>item </span><b>one</b></li><ul><li>item one-a</li></ul>"
				+ "<li><i>item two</i></li><ul><li>item two-a</li><li>item two-b the image:</li><li>"
				+ "<img align=\"top\" alt=\"my img\" border=\"1mm\" src=\"test.jpg\" width=\"75%\"></li>"
				+ "<li>item two-d</li></ul><li>item three</li></ul><p>More text...</p></body></html>";
		
		TestingUtil.writeDebugFileAndResources("createHTML_imageInListOnlyElementInBulletPoint", resultRaw);
		assertEquals(expected, result);	
	}
	
	@Test
	public void createHTML_imageNormal() throws Exception{
		String input = "Title\n![my img](test.jpg)\nmore text";
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" alt=\"my img\" border=\"1mm\" src=\"test.jpg\" width=\"75%\"></span><br><span>more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_imageNoAlternativeText() throws Exception{
		String input = "Title\n!(test.jpg)\nmore text";
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" border=\"1mm\" src=\"test.jpg\" width=\"75%\"></span><br><span>more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		TestingUtil.writeDebugFileAndResources("createHTML_imageNoAlternativeText", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_imageNormalWidthInMM() throws Exception{
		String input = "Title\n![my img](test.jpg width=30mm)\nmore text";
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String resultWithFormatting = resultRaw;
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" alt=\"my img\" border=\"1mm\" src=\"test.jpg\" width=\"30mm\">"
				+ "</span><br><span>more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		log("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		TestingUtil.writeDebugFileAndResources("createHTML_imageNormalWidthInMM", resultRaw);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_imageNormalHeightInMM() throws Exception{
		String input = "Title\n![my img](test.jpg height=30mm)\nmore text";
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String resultWithFormatting = resultRaw;
		String expected = "<html><body><p><span>Title</span><br><span>"
				+ "<img align=\"top\" alt=\"my img\" border=\"1mm\" height=\"30mm\" src=\"test.jpg\">"
				+ "</span><br><span>more text</span></p></body></html>"; 
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		log("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		TestingUtil.writeDebugFileAndResources("createHTML_imageNormalHeightInMM", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_imageNormalWidthInPercent() throws Exception{
		String input = "Title\n![my img](test.jpg width=25%)\nmore text";
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String resultWithFormatting = resultRaw;
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" alt=\"my img\" border=\"1mm\" src=\"test.jpg\" width=\"25%\">"
				+ "</span><br><span>more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		log("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		TestingUtil.writeDebugFileAndResources("createHTML_imageNormalWidthInPercent", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_imageWidthHeightBorder() throws Exception{
		String input = "Title\n![my img](test.jpg width=300mm height=10mm border=5mm)\nmore text";
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String resultWithFormatting = resultRaw;
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" alt=\"my img\" border=\"5mm\" height=\"10mm\" src=\"test.jpg\" width=\"300mm\">"
				+ "</span><br><span>more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		log("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		TestingUtil.writeDebugFileAndResources("createHTML_imageWidthHeightBorder", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_imageSetBorder() throws Exception{
		String input = "Title\n![my img](test.jpg border=7mm)\nmore text";
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String resultWithFormatting = resultRaw;
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" alt=\"my img\" border=\"7mm\" src=\"test.jpg\" width=\"75%\">"
				+ "</span><br><span>more text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("expected:\n" + expected);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		log("==========================");
		log("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		TestingUtil.writeDebugFileAndResources("createHTML_imageSetBorder", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_imageName() throws Exception{
		String input = "Title\n!(test)\nmore text\n!(test2)\nrest of text";
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.addImageNameToPathMapping("test", "test.jpg");
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		String expected = "<html><body><p><span>Title</span><br><span><img align=\"top\" border=\"1mm\" src=\"test.jpg\" width=\"75%\">"
				+ "</span><br><span>more text</span><br><span><img align=\"top\" border=\"1mm\" src=\"test2\" width=\"75%\"></span><br>"
				+ "<span>rest of text</span></p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Parsed:\n" + BMUtil.toString(new BMSectionParserLogic().parseAll(input)));
		log("==========================");
		log("Result:\n" + result);
		TestingUtil.writeDebugFileAndResources("createHTML_imageName", resultRaw);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_typicalCaseFullDocument() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Bullet Points\n");
		sb.append(" - item *one*\n");
		sb.append("    - item {c:red}the red{c}\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - ```java\n");
		sb.append("int x = 2;\n");
		sb.append("if (z == 42){\n");
		sb.append("   processData();\n");
		sb.append("}\n");
		sb.append("String y = \"hello\";```\n");
		sb.append(" - item three\n");
		sb.append("    - ![test-img height=50mm]\n");
		sb.append("\n");
		sb.append("# Table:\n");
		sb.append("col 1|col 2\n");
		sb.append("=====\n");
		sb.append("one|a\n");
		sb.append("two|b\n");
		sb.append("three|c\n");
		sb.append("More text...\n");
		
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		brightMarkdown.addImageNameToPathMapping("test-img", "test.jpg");
		String resultRaw = brightMarkdown.createHTML(input);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<style>table.brightmarkdown{border-collapse: collapse;}table.brightmarkdown td {border: 1px solid black; padding: 3px;}"
				+ "table.brightmarkdown th {border: 1px solid black; padding: 3px;}table.brightmarkdown th {background-color: #a0a0a0;}"
				+ "table.brightmarkdown tr:nth-child(odd) {background-color: #d8d8d8;}table.brightmarkdown tr:nth-child(even) {background-color: #ffffff;}"
				+ "</style></head><body><h1>Bullet Points</h1><ul><li><span>item </span><b>one</b></li><ul><li><span>item </span>"
				+ "<span style=\"color:red\">the red</span></li></ul><li><i>item two</i></li><ul><li>items two-a</li><li>"
				+ "<pre style=\"background:lightgrey\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span>"
				+ "<span> x = 2;<br/></span><span style=\"color:purple;font-weight:bold\">if</span>"
				+ "<span> (z == 42){<br/>   processData();<br/>}<br/>String y = </span><span style=\"color:blue\">\"hello\"</span>"
				+ "<span>;</span></code></pre></li></ul><li>item three</li><ul><li><img align=\"top\" border=\"1mm\" height=\"50mm\" src=\"test.jpg\">"
				+ "</li></ul></ul><h1>Table:</h1><table class=\"brightmarkdown\"><tr><th>col 1</th><th>col 2</th></tr><tr><td>one</td><td>a</td>"
				+ "</tr><tr><td>two</td><td>b</td></tr><tr><td>three</td><td>c</td></tr></table><p>More text...</p></body></html>";
		
		TestingUtil.writeDebugFileAndResources("createHTML_typicalCaseFullDocument", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_typicalEmbeddableHtmlCode() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Bullet Points\n");
		sb.append(" - item *one*\n");
		sb.append("    - item {c:red}the red{c}\n");
		sb.append(" - _item two_\n");
		sb.append("    - items two-a\n");
		sb.append("    - ```java\n");
		sb.append("int x = 2;\n");
		sb.append("if (z == 42){\n");
		sb.append("   processData();\n");
		sb.append("}\n");
		sb.append("String y = \"hello\";```\n");
		sb.append(" - item three\n");
		sb.append("    - ![test-img height=50mm]\n");
		sb.append(" - item four contains the code ´´´java int x = \"hello\";´´´ within the text\n");
		sb.append("\n");
		sb.append("# Table:\n");
		sb.append("col 1|col 2\n");
		sb.append("=====\n");
		sb.append("one|a\n");
		sb.append("two|b\n");
		sb.append("three|c\n");
		sb.append("More text...\n");
		
		String input = sb.toString();
		log("input:\n" + input);
		
		BMSection processingStepResult = new BMSectionParserLogic().toMDSection(input);
		new BMSectionParserLogic().parseCodeSections(processingStepResult);
		log("==========================");
		log("processingStepResult:\n" + BMUtil.toString(processingStepResult));
		log("==========================");
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		brightMarkdown.addImageNameToPathMapping("test-img", "test.jpg");
		String resultRaw = brightMarkdown.createHTML(input, OutputType.EMBEDDABLE_HTML_CODE);
		String result = removeFormatting(resultRaw);
		
		log("==========================");
		log("resultRaw:\n" + resultRaw);
		log("==========================");
		log("Result:\n" + result);
		
		String expected = "<span><h1>Bullet Points</h1><ul><li><span>item </span><b>one</b></li><ul><li><span>item </span>"
				+ "<span style=\"color:red\">the red</span></li></ul><li><i>item two</i></li><ul><li>items two-a</li><li><span/>"
				+ "<pre style=\"background:lightgrey\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span>"
				+ "<span> x = 2;<br/></span><span style=\"color:purple;font-weight:bold\">if</span>"
				+ "<span> (z == 42){<br/>   processData();<br/>}<br/>String y = </span><span style=\"color:blue\">\"hello\"</span>"
				+ "<span>;</span></code></pre></li></ul><li>item three</li><ul><li><img align=\"top\" border=\"1mm\" height=\"50mm\" src=\"test.jpg\"/>"
				+ "</li></ul><li><span>item four contains the code </span><code style=\"background:lightgrey\">"
				+ "<span style=\"color:purple;font-weight:bold\">int</span><span> x = </span><span style=\"color:blue\">\"hello\"</span>"
				+ "<span>;</span></code><span> within the text</span></li></ul><p/><h1>Table:</h1><table class=\"brightmarkdown\">"
				+ "<tr><th>col 1</th><th>col 2</th></tr><tr><td>one</td><td>a</td></tr><tr><td>two</td><td>b</td></tr><tr><td>three</td>"
				+ "<td>c</td></tr></table><p>More text...</p></span>";
		
		TestingUtil.writeDebugFileAndResources("createHTML_typicalEmbeddableHtmlCode", resultRaw);
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_linkWithFormatting() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("This is a *link* to [Wikipedia][https://www.wikipedia.org/].\n");

		String input = sb.toString();
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);

		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		log("Result:\n" + result);
		TestingUtil.writeDebugFileAndResources("createHTML_linkWithFormatting", resultRaw);
		String expected = "<html><body><p><span>This is a </span><b>link</b><span> to </span><a href=\"https://www.wikipedia.org/\">Wikipedia</a><span>.</span></p></body></html>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_imageInParagraph() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("Small: ![img1.jpg height=10mm]\n");

		String input = sb.toString();
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);

		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		log("Result:\n" + result);
		TestingUtil.writeDebugFileAndResources("createHTML_imageInParagraph", resultRaw);

		String expected = "<html><body><p><span>Small: </span><img align=\"top\" border=\"1mm\" height=\"10mm\" src=\"img1.jpg\"></p></body></html>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_codeBlockWithoutLineBreaksInParagraph() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Test\n");
		sb.append("This is _some_ text.\n");
		sb.append("\n");
		sb.append("More *text* that contains the code ´´´java int x = \"hello\";´´´ within the *single* paragraph.\n");
		sb.append("End of text\n");

		String input = sb.toString();
		
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		String expected = "<html><body><h1>Test</h1><p><span><span>This is </span><i>some</i><span> text.</span></span><br><br><span>"
				+ "<span>More </span><b>text</b><span> that contains the code </span><code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
						+ "<span style=\"color:purple;font-weight:bold\">int</span><span> x = </span><span style=\"color:blue\">\"hello\"</span>"
						+ "<span>;</span></code><span> within the </span><b>single</b><span> paragraph.</span></span><br><span>End of text</span>"
						+ "</p></body></html>";
		log("input:\n" + input);
		log("==========================");
		log("Result:\n" + result);
		
		TestingUtil.writeDebugFileAndResources("createHTML_codeBlockWithoutLineBreaksInParagraph", resultRaw);
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_SingleLineParagraphWithCodeBlock() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("It is also possible to have code blocks like ´´´java x=\"hello\";´´´ or longer code blocks such as\n");

		String input = sb.toString();
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		log("Result:\n" + result);
		TestingUtil.writeDebugFileAndResources("createHTML_SingleLineParagraphWithCodeBlock", resultRaw);

		String expected = "<html><body><p>It is also possible to have code blocks like <code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\">"
				+ "<span>x=</span><span style=\"color:blue\">\"hello\"</span><span>;</span></code>"
				+ "<span> or longer code blocks such as</span></p></body></html>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_SingleLineParagraphWithFormattingAndCodeBlock() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("It is _also_ possible to have *code blocks* like ´´´java x=\"hello\";´´´ or _longer_ code blocks such as\n");
		
		String input = sb.toString();
		BMSection sections = new BMSectionParserLogic().parseAll(input);
		log("==========================");
		log("parseAll sections:\n" + BMUtil.toString(sections));
		log("==========================");
		String resultRaw = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(resultRaw);
		log("Result:\n" + result);
		TestingUtil.writeDebugFileAndResources("createHTML_SingleLineParagraphWithFormattingAndCodeBlock", resultRaw);
		
		String expected = "<html><body><p><span>It is </span><i>also</i><span> possible to have </span><b>code blocks</b>"
				+ "<span> like </span><code style=\"" + BMHtmlCreator.CODE_BOX_STYLE + "\"><span>x=</span><span style=\"color:blue\">\"hello\"</span>"
				+ "<span>;</span></code><span> or </span><i>longer</i><span> code blocks such as</span></p></body></html>"; 
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_higlightedTextInCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append("```java\n");
		sb.append("int x = 2;\n");
		sb.append("int y = processData(x, !!!hl!true!!!);\n");
		sb.append("return y;```\n");
		sb.append("More text...\n");
		String input = sb.toString();
		
		String testName = "createHTML_higlightedTextInCodeBlock";

		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><pre style=\"background:lightgrey\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">int"
				+ "</span><span> x = 2;<br/></span><span style=\"color:purple;font-weight:bold\">int</span><span> y = processData(x, </span>"
				+ "<span style=\"font-weight: bold;background-color: #f7ea04;\">true</span><span>);<br/></span><span style=\"color:purple;font-weight:bold\">return</span>"
				+ "<span> y;</span></code></pre><p>More text...</p></span>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_placeholderTextInCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append("```\n");
		sb.append("send -m 'hello' -t '!!!ph!<token>!!!'\n");
		String input = sb.toString();
		
		String testName = "createHTML_placeholderTextInCodeBlock";
		
		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span>send -m 'hello' -t '</span><span style=\"font-weight: bold;font-style: italic;color: " 
				+ BMConstants.CODE_BLOCK_PLACEHOLDER_FOREGROUND_COLOR + ";\">&lt;token&gt;</span><span>'</span></code></pre></span>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_infoTextInCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append("```java\n");
		sb.append("int x = myMethod(1000!!!info!=duration in millis!!!, true);\n");
		sb.append("return x;\n");
		sb.append("´´´\n");
		String input = sb.toString();
		
		String testName = "createHTML_infoTextInCodeBlock";
		
		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">int</span><span> x = myMethod(1000</span>"
				+ "<span style=\"font-style: italic;background-color: #efefef;color: #4b2eff;\">=duration in millis</span>"
				+ "<span>, </span><span style=\"color:purple;font-weight:bold\">true</span><span>);<br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">return</span><span> x;</span></code></pre></span>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_elipse() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append("```java\n");
		sb.append("//init\n");
		sb.append("import x.y.z;\n");
		sb.append("!!!...\n");
		sb.append("\n");
		sb.append("int y = processData(x);\n");
		sb.append("return y;```\n");
		sb.append("More text...\n");
		String input = sb.toString();
		
		String testName = "createHTML_elipse";
		
		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><pre style=\"background:lightgrey\"><code><span><br/></span><span style=\"color:darkgreen\">//init</span>"
				+ "<span><br/></span><span style=\"color:purple;font-weight:bold\">import</span><span> x.y.z;<br/></span>"
				+ "<span style=\"font-weight: bold;font-style: italic;background-color: lightgreen;color: grey;\">[...]</span><span><br/><br/>"
				+ "</span><span style=\"color:purple;font-weight:bold\">int</span><span> y = processData(x);<br/></span>"
				+ "<span style=\"color:purple;font-weight:bold\">return</span><span> y;</span></code></pre><p>More text...</p></span>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_escapeExclamationMarksInCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append("```\n");
		sb.append("info: A test!!!\\!!!!\n");
		sb.append("do xyz```\n");
		sb.append("More text...\n");
		String input = sb.toString();
		
		String testName = "createHTML_escapeExclamationMarksInCodeBlock";
		
		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><pre style=\"background:lightgrey\"><code><span><br/></span><span>info: A test</span>"
				+ "<span>!!!!</span><span><br/>do xyz</span></code></pre><p>More text...</p></span>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_specialFormattingInCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append("```java\n");
		sb.append("String x = !!!b i bc:pink u c:blue!theSpecialMethod()!!!;\n");
		sb.append("return x;```\n");
		sb.append("More text...\n");
		String input = sb.toString();
		
		String testName = "createHTML_specialFormattingInCodeBlock";
		
		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><pre style=\"background:lightgrey\"><code><span><br/></span>"
				+ "<span>String x = </span><span style=\"font-weight: bold;font-style: italic;text-decoration: underline;background-color: pink;color: blue;\">"
				+ "theSpecialMethod()</span><span>;<br/></span><span style=\"color:purple;font-weight:bold\">return</span>"
				+ "<span> x;</span></code></pre><p>More text...</p></span>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_higlightedTextInSingleLineCodeBlock() throws Exception {
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		
		StringBuilder sb = new StringBuilder();
		sb.append("# Chapter\n");
		sb.append(" - one\n");
		sb.append(" - use method call ```java processData(x, !!!hl!true!!!);´´´\n");
		sb.append("More text...\n");
		String input = sb.toString();
		
		String testName = "createHTML_higlightedTextInSingleLineCodeBlock";

		String result = processAndProvideDebugInfo(brightMarkdown, input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Chapter</h1><ul><li>one</li><li><span>use method call </span><code style=\"background:lightgrey\">"
				+ "<span>processData(x, </span><span style=\"font-weight: bold;background-color: #f7ea04;\">true</span><span>);</span>"
				+ "</code></li></ul><p>More text...</p></span>";
		assertEquals(expected, result);
	}

	@Test
	public void createHTML_unclosedParenthesis() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# Title\n");
		sb.append("fetch(\n");
		
		String input = sb.toString();
		
		String testName = "createHTML_unclosedParenthesis";
		
		String result = processAndProvideDebugInfo(input, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><h1>Title</h1><p>fetch(</p></span>";
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_bulletPointsParenthesisCodeBlocks() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - creating a app:\n");
		sb.append("    . run ´´´npm install´´´ (or ´´´npm i´´´) to install all dependencies\n");

		String testName = "createHTML_bulletPointsParenthesisCodeBlocks";
		
		String result = processAndProvideDebugInfo(sb, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><ul><li>creating a app:</li><ol><li><span>run </span><code style=\"background:lightgrey\">"
				+ "<span>npm install</span></code><span> (or </span><code style=\"background:lightgrey\"><span>npm i</span>"
				+ "</code><span>) to install all dependencies</span></li></ol></ul></span>"; 
		assertEquals(expected, result);
	}
	
	@Test
	public void createHTML_periodAfterCodeBlock() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item one\n");
		sb.append("     - sub item one-one\n");
		sb.append("     - sub item one-two\n");
		sb.append(" - column ´´´bla´´´. more text\n");
		
		String testName = "createHTML_periodAfterCodeBlock";
		
		String result = processAndProvideDebugInfo(sb, testName, OutputType.EMBEDDABLE_HTML_CODE);
		
		String expected = "<span><ul><li>item one</li><ul><li>sub item one-one</li><li>sub item one-two</li></ul>"
				+ "<li><span>column </span><code style=\"background:lightgrey\"><span>bla</span></code>"
				+ "<span>. more text</span></li></ul></span>";
		assertEquals(expected, result);
	}
	


}