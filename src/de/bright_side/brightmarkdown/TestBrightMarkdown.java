package de.bright_side.brightmarkdown;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import de.bright_side.brightmarkdown.BrightMarkdown.FormattingItem;
import de.bright_side.brightmarkdown.BrightMarkdownHTMLCreator.LevelAndTitle;
import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

/**
 * 
 * @author Philip Heyse
 *
 */
public class TestBrightMarkdown {
	private static final String TABLE_STYLE = "<style>table {border-collapse: collapse;}td, th {border: 1px solid black; padding: 3px;}th {background-color: #a0a0a0;}tr:nth-child(odd) {background-color: #d8d8d8;}tr:nth-child(even) {background-color: #ffffff;}</style>";

	private String removeFormatting(String htmlString) {
		return htmlString.replace("\r", "").replace("\n", "").replace("    ", "");
	}	

	@Test
	public void test_createHTML_printDocumentation() throws Exception{
		System.out.println("Documentation:\n" + new BrightMarkdown().getDocumentationAsHTML());
	}

	
	@Test
	public void testParseAllSimple(){
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*. Nice?";
		BrightMarkdownSection result = new BrightMarkdown().parseAll(input);
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + new BrightMarkdown().toString(result));
	}
	
	@Test
	public void test_createHTML_normal_bold() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><b>bold</b><span>. Nice?</span></span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_normal_italic() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is _italic_. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><i>italic</i><span>. Nice?</span></span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	
	@Test
	public void test_createHTML_specialCharacters() throws Exception{
		String input = "# Title\nHello! Special: <>&?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p>Hello! Special: &lt;&gt;&amp;?</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_noText() throws Exception{
		String input = "";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_noMarkdown() throws Exception{
		String input = "Simple Text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Simple Text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_onlyBold() throws Exception{
		String input = "*Only Bold*";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><b>Only Bold</b></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_textAndStrikeThrough() throws Exception{
		String input = "Hello ~strike through~ there";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Hello </span><strike>strike through</strike><span> there</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_onlyHeading() throws Exception{
		String input = "## my title";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h2>my title</h2></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_headingsOnDifferentLevels() throws Exception{
		String input = "# my title 1\n## my title 2\n### my title 3\n# other";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>my title 1</h1><h2>my title 2</h2><h3>my title 3</h3><h1>other</h1></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_headingWithPartItalic() throws Exception{
		String input = "## Title with _italic_ part\nbla";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h2><span>Title with </span><i>italic</i><span> part</span></h2><p>bla</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_fullTextBulletPoints() throws Exception{
		String input = "* item 1\n* item 2\n. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_bulletPointsWithPartBold() throws Exception{
		String input = "* item 1\n* item 2 with *bold* text\n. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li><span>item 2 with </span><b>bold</b><span> text</span></li><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_plainTextBulletPointsPlainText() throws Exception{
		String input = "Some plain text\n* item 1\n* item 2 with text\n. item 3\nmore plain text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><ul><li>item 1</li><li>item 2 with text</li><li>item 3</li></ul><p>more plain text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_checkBoxes() throws Exception{
		String input = "Some plain text\n[] box 1\n[x] box 2\n[ ] box 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><input disabled=\"true\" type=\"checkbox\">box 1</input><br><input checked=\"true\" disabled=\"true\" type=\"checkbox\">box 2</input><br><input disabled=\"true\" type=\"checkbox\">box 3</input><br></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_checkBoxesWithDifferentSyntax() throws Exception{
		String input = "Some plain text\n - [] box 1\n - [x] box 2\n - [ ] box 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><input disabled=\"true\" type=\"checkbox\">box 1</input><br><input checked=\"true\" disabled=\"true\" type=\"checkbox\">box 2</input><br><input disabled=\"true\" type=\"checkbox\">box 3</input><br></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_bulletPointsOnDifferentLevelsNormal() throws Exception{
		String input = "Hello\n* item 1\n* item 2\n** item 2.1\n** item 2.2\n* item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_bulletPointsLevelDownAndTwoLevelsUp() throws Exception{
		String input = "Hello\n* item 1\n* item 2\n** item 2.1\n** item 2.2\n*** item 2.2.1\n*** item 2.2.2\n* item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_bulletPointsLevel3WithDashes() throws Exception{
		String input = "Hello\n - item 1\n - item 2\n -- item 2.1\n -- item 2.2\n --- item 2.2.1\n --- item 2.2.2\n - item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_bulletPointsBeginningWithLevel3() throws Exception{
		String input = "Hello\n*** item 2.2.1\n*** item 2.2.2\n* item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Hello</p><ul><ul><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_horizontalRuleInText() throws Exception{
		String input = "Some plain text\n --------\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Some plain text</p><hr><p>other text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_almostHorizontalRulesInText() throws Exception{
		String input = "Some plain text\n __\n\n ___x\n\n ___\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Some plain text</span><br><span>__</span><br><br><span><i>x</i></span><br></p><hr><p>other text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyBold() throws Exception{
		String input = "Some plain text\n**x\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Some plain text</span><br><span>**x</span><br><span>other text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyItalic() throws Exception{
		String input = "Some plain text\n__x\nother text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>Some plain text</span><br><span>__x</span><br><span>other text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyItalicOnly() throws Exception{
		String input = "__";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>__</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkInTextSquareRoundBrackets() throws Exception{
		String input = "this is a [link](www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkInTextSquareBrackets() throws Exception{
		String input = "this is a [link][www.wikipedia.de] and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_linkInTextRoundBrackets() throws Exception{
		String input = "this is a (link)(www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkInTextRoundAndSquareBrackets() throws Exception{
		String input = "this is a (link)[www.wikipedia.de] and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	

	
	@Test
	public void test_createHTML_linkOnly() throws Exception{
		String input = "[link](www.wikipedia.de)";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><a href=\"www.wikipedia.de\">link</a></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkInBulletPoints() throws Exception{
		String input = "Title\n - bullet 1 with [link](www.wikipedia.de)\n - bullet two\nmore text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li><span>bullet 1 with </span><a href=\"www.wikipedia.de\">link</a></li><li>bullet two</li></ul><p>more text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkWithFormattedText() throws Exception{
		String input = "this is a [link with *formatted* text](www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\"><span>link with </span><b>formatted</b><span> text</span></a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_almostLink() throws Exception{
		String input = "this is a not a [link www.wikipedia.de) and more text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>this is a not a [link www.wikipedia.de) and more text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_escapeChars() throws Exception{
		String input = "# Title\nThis text is *bold*. And this _italic_ and this is \\*escaped\\*. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This text is </span><b>bold</b><span>. And this </span><i>italic</i><span> and this is *escaped*. Nice?</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_aboutText() throws Exception{
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
		sb.append("                                 Apache License\n");
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
				+ "<pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>" + codeBlockTextUse + "</span></code></pre></body></html>";
		expected = removeFormatting(expected);
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("expected:\n" + expected);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("==========================");
		assertEquals(expected.replace(">", ">\n"), result.replace(">", ">\n"));
	}
	
	@Test
	public void test_createHTML_ignoreFormattingWithinWord() throws Exception{
		String input = "# Title\nThis is time 2017_11_19__08_11 and only _this_ is formatted";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This is time 2017_11_19__08_11 and only </span><i>this</i><span> is formatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_ignoreSingleFormattingWithinWord() throws Exception{
		String input = "# Title\nThere is a file_name in the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p>There is a file_name in the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_ignoreFormattingInWordWith2CharIndicator() throws Exception{
		String input = "# Title\nThis is non__formatted_text and this is _formatted_ text.";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This is non__formatted_text and this is </span><i>formatted</i><span> text.</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_fontSizes() throws Exception{
		String input = "# Title\n## Title 2\nText";
		BrightMarkdown markdown = new BrightMarkdown();
		markdown.setFontSizeInMM(FormattingItem.H1, 40);
		markdown.setFontSizeInMM(FormattingItem.H2, 8);
		String result = removeFormatting(markdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><style>H1{font-size:40mm;}H2{font-size:8mm;}</style></head><body><h1>Title</h1><h2>Title 2</h2><p>Text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_getDeepestHeading_normal() throws Exception{
		String input = "# Title\n## Title 2\nText";
		BrightMarkdown markdown = new BrightMarkdown();
		int result = markdown.getDeepestHeading(input);
		int expected = 2;
		assertEquals(expected, result);
	}
	
	@Test
	public void test_getDeepestHeading_noHeadings() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n*rest* of the text";
		BrightMarkdown markdown = new BrightMarkdown();
		int result = markdown.getDeepestHeading(input);
		int expected = 0;
		assertEquals(expected, result);
	}
	
	@Test
	public void test_getDeepestHeading_noText() throws Exception{
		String input = "";
		BrightMarkdown markdown = new BrightMarkdown();
		int result = markdown.getDeepestHeading(input);
		int expected = 0;
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_emptyLines() throws Exception{
		String input = "# Title\nLine 1\nLine 2\n\nLine 3 (after empty line)";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span>Line 2</span><br><br><span>Line 3 (after empty line)</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_emptyLinesAndMoreElements() throws Exception{
		String input = "# Title\nLine 1\nLine 2\n\nLine 3 (after empty line)\n * item 1\n * item 2\nmore text in paragraph\nlast line";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span>Line 2</span><br><br><span>Line 3 (after empty line)</span></p><ul><li>item 1</li><li>item 2</li></ul><p><span>more text in paragraph</span><br><span>last line</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyLinesAndMoreElementsWithFormatting() throws Exception{
		String input = "# Title\nLine 1\n_Line_ 2\n\nLine 3 (after *empty* line)\n * item 1\n * item 2\nmore text in paragraph\nlast line";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span><i>Line</i><span> 2</span></span><br><br><span><span>Line 3 (after </span><b>empty</b><span> line)</span></span></p><ul><li>item 1</li><li>item 2</li></ul><p><span>more text in paragraph</span><br><span>last line</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_nestedFormatting() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_nestedFormattingItalicInBold() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("*bold _italic!_ rest of bold* unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><b>bold </b><b><i>italic!</i></b><b> rest of bold</b><span> unformatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_nestedFormattingColorInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic {c:red}red!{c} rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><i><span style=\"color:red\">red!</span></i><i> rest of italic</i><span> unformatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_nestedFormattingBackgroundColorInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic {bc:red}red!{bc} rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><i><span style=\"background-color:red\">red!</span></i><i> rest of italic</i><span> unformatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_nestedFormattingBoldInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic *bold!* rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><b><i>bold!</i></b><i> rest of italic</i><span> unformatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_nestedFormattingBoldAndUnderlineInItalic() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("_italic *bold! +and underline+* rest of italic_ unformatted\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><i>italic </i><b><i>bold! </i></b><b><i><u>and underline</u></i></b><i> rest of italic</i><span> unformatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_findMatchingLevel_normal() throws Exception{
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		levelToIndentMap.put(1, 0);
		levelToIndentMap.put(2, 3);
		levelToIndentMap.put(3, 7);
		levelToIndentMap.put(4, 10);
		Integer result = new BrightMarkdown().findMatchingLevel(levelToIndentMap, 8);
		assertEquals(3, result.intValue());
	}

	@Test
	public void test_findMatchingLevel_lessIndentThanLevel1() throws Exception{
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		levelToIndentMap.put(1, 2);
		levelToIndentMap.put(2, 5);
		levelToIndentMap.put(3, 8);
		levelToIndentMap.put(4, 12);
		Integer result = new BrightMarkdown().findMatchingLevel(levelToIndentMap, 1);
		assertEquals(1, result.intValue());
	}
	
	@Test
	public void test_findMatchingLevel_directMatch() throws Exception{
		Map<Integer, Integer> levelToIndentMap = new TreeMap<Integer, Integer>();
		levelToIndentMap.put(1, 2);
		levelToIndentMap.put(2, 5);
		levelToIndentMap.put(3, 8);
		levelToIndentMap.put(4, 12);
		Integer result = new BrightMarkdown().findMatchingLevel(levelToIndentMap, 8);
		assertEquals(3, result.intValue());
	}

	@Test
	public void test_findPosAfterLeadindSpaces_multipleCases() throws Exception{
		BrightMarkdown md = new BrightMarkdown();
		assertEquals(1, md.findPosAfterLeadindSpaces("*", 0));
		assertEquals(2, md.findPosAfterLeadindSpaces("* ", 0));
		assertEquals(2, md.findPosAfterLeadindSpaces("* text", 0));
		assertEquals(3, md.findPosAfterLeadindSpaces(" * text", 1));
		assertEquals(5, md.findPosAfterLeadindSpaces(" *   text", 1));
		assertEquals(6, md.findPosAfterLeadindSpaces("  *   text", 2));
		assertEquals(4, md.findPosAfterLeadindSpaces("  - item 3", 2));
		assertEquals(5, md.findPosAfterLeadindSpaces(" *   ", 1));
	}

	@Test
	public void test_readListItemIndet_multipleCases() throws Exception{
		List<String> icl = BrightMarkdown.BULLET_POINT_INDICATORS_CHARS_LIST;
		BrightMarkdown md = new BrightMarkdown();
		assertEquals(0, md.readListItemIndet("*", icl).intValue());
		assertEquals(0, md.readListItemIndet("* ", icl).intValue());
		assertEquals(0, md.readListItemIndet("* text", icl).intValue());
		assertEquals(1, md.readListItemIndet(" * text", icl).intValue());
		assertEquals(1, md.readListItemIndet(" *   text", icl).intValue());
		assertEquals(2, md.readListItemIndet("  *   text", icl).intValue());
		assertEquals(1, md.readListItemIndet(" *   ", icl).intValue());
		assertEquals(null, md.readListItemIndet("**", icl));
		assertEquals(null, md.readListItemIndet("** text", icl));
		assertEquals(null, md.readListItemIndet(" ** text", icl));
		assertEquals(null, md.readListItemIndet("x", icl));
		assertEquals(null, md.readListItemIndet(" x", icl));
		assertEquals(4, md.readListItemIndet("    - item 2.1", icl).intValue());
		assertEquals(null, md.readListItemIndet("*Only Bold*", icl));
	}
	
	@Test
	public void test_createHTML_listEntryByLevelOnlyLevel1() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("  - item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_listEntryByLevel2Levels() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - item 1\n");
		sb.append(" - item 2\n");
		sb.append("    - item 2.1\n");
		sb.append("    - item 2.2\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li></ul></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}


	
	@Test
	public void test_createHTML_listEntryByLevelTextListTextListText() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_listEntryByLevelUp2Levels() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_listEntryByLevelUp2LevelsButNotToTop() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_listEntryByLevelUp2LevelsButNotToTopNoLeadindSpace() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_listEntryByLevelMultipleLevelsWith1SpaceOff() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_listEntryByLevelMultipleLevelsWith2SpacesOff() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	
	@Test
	public void test_createHTML_numberedListEntryByLevelOnlyLevel1() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("  . item 3\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_numberedListEntryByLevel2Levels() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" . item 1\n");
		sb.append(" . item 2\n");
		sb.append("    . item 2.1\n");
		sb.append("    . item 2.2\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ol><li>item 1</li><li>item 2</li><ol><li>item 2.1</li><li>item 2.2</li></ol></ol></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_numberedListEntryByLevelTextListTextListText() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_numberedListEntryByLevelUp2Levels() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_numberedListEntryByLevelUp2LevelsButNotToTop() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedListEntryByLevelUp2LevelsButNotToTopNoLeadindSpace() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedListEntryByLevelMultipleLevelsWith1SpaceOff() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedListEntryByLevelMultipleLevelsWith2SpacesOff() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}


	@Test
	public void test_getHeadingItems_normal() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# heading 1\n");
		sb.append("# heading 2\n");
		sb.append("## heading 2.1\n");
		sb.append("### heading 2.1.1\n");
		sb.append("# heading 3\n");
		String input = sb.toString();
		List<LevelAndTitle> result = new BrightMarkdownHTMLCreator(false, null).getHeadingItems(new BrightMarkdown().parseAll(input));
		assertEquals(5, result.size());
		assertEquals(1, result.get(0).getLevel());
		assertEquals("heading 1", result.get(0).getTitle());
		assertEquals(1, result.get(1).getLevel());
		assertEquals("heading 2", result.get(1).getTitle());
		assertEquals(2, result.get(2).getLevel());
		assertEquals("heading 2.1", result.get(2).getTitle());
		assertEquals(3, result.get(3).getLevel());
		assertEquals("heading 2.1.1", result.get(3).getTitle());
		assertEquals(1, result.get(4).getLevel());
		assertEquals("heading 3", result.get(4).getTitle());
	}

	@Test
	public void test_getHeadingItems_withFormatting() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("# heading 1\n");
		sb.append("# heading 2 is *very* nice\n");
		sb.append("## heading 2.1\n");
		sb.append("### heading 2.1.1\n");
		sb.append("# heading 3\n");
		String input = sb.toString();
		List<LevelAndTitle> result = new BrightMarkdownHTMLCreator(false, null).getHeadingItems(new BrightMarkdown().parseAll(input));
		assertEquals(5, result.size());
		assertEquals(1, result.get(0).getLevel());
		assertEquals("heading 1", result.get(0).getTitle());
		assertEquals(1, result.get(1).getLevel());
		assertEquals("heading 2 is very nice", result.get(1).getTitle());
		assertEquals(2, result.get(2).getLevel());
		assertEquals("heading 2.1", result.get(2).getTitle());
		assertEquals(3, result.get(3).getLevel());
		assertEquals("heading 2.1.1", result.get(3).getTitle());
		assertEquals(1, result.get(4).getLevel());
		assertEquals("heading 3", result.get(4).getTitle());
	}
	
	@Test
	public void test_getHeadingItems_noHeadings() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("This is just some text\n");
		String input = sb.toString();
		List<LevelAndTitle> result = new BrightMarkdownHTMLCreator(false, null).getHeadingItems(new BrightMarkdown().parseAll(input));
		assertEquals(0, result.size());
	}

	@Test
	public void test_createHTML_withTOC() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("expected:\n" + expected);
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_withTOCStartingAtLevel2() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("expected:\n" + expected);
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_table_normalNoHeader() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
//		System.out.println("==========================");
//		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_normalWithHeader() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_table_normalNoHeader1Row() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
//		System.out.println("==========================");
//		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_normalWithHeaderButNoRows() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_emptyCellsMiddle() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1||c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));

		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td></td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_emptyCellsEnd() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td></td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_table_missingCellsEnd() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td></td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_emptyCellBeginning() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td></td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_table_extraCellsMiddle() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|c2-2|c2-3|c2-4\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td><td></td></tr>";
		expected += "<tr><td>c2-1</td><td>c2-2</td><td>c2-3</td><td>c2-4</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td><td></td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_escapeCellSeparatorInTable() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|Hello the \\| is kept|c2-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td>Hello the | is kept</td><td>c2-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_normal_escapeCellSeparator() throws Exception{
		String input = "# Title\nThis text is _italic_ and the \\| is kept. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><p><span>This text is </span><i>italic</i><span> and the | is kept. Nice?</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_only1Row2Columns() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_table_withFormatting() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|Hello *bold*|c2-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		

		System.out.println("input:\n" + input);
		
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td><span>Hello </span><b>bold</b></td><td>c2-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_normal_underline() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is +underlined+. Nice?";
		String result = removeFormatting(new BrightMarkdown(true).createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><u>underlined</u><span>. Nice?</span></span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_normal_shortTextUnderline() throws Exception{
		String input = "This text is +underlined+. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span>This text is </span><u>underlined</u><span>. Nice?</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_normal_BoldAndUnderline() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *+underlined & bold+*. Nice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><b><u>underlined &amp; bold</u></b><span>. Nice?</span></span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_escapeSpecialCharacters_normal() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is _italic_.\n---\nNice?";
		String result = new BrightMarkdown().escapeSpecialCharacters(input).replace("\r", "").replace("\n", "");
		String expected = "\\# Title\\* item 1\\* item 2\\* item 3This text is \\_italic\\_\\.\\-\\-\\-Nice?";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_noMarkdownTag() throws Exception{
		String input = "{NOMARKDOWN}# Title\n* item 1\n* item 2\n* item 3\n\nThis text is _italic_.\n---\nNice?";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p><span># Title</span><br><span>* item 1</span><br><span>* item 2</span><br><span>* item 3</span><br><br><span>This text is _italic_.</span><br><span>---</span><br><span>Nice?</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedListFormatNumberAndPeriod() throws Exception{
		String input = "# My List\n1. item 1\n2. item 2\n2. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>My List</h1><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedListFormatDot() throws Exception{
		String input = "# My List\n. item 1\n. item 2\n. item 3";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><h1>My List</h1><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_bulletPointsLevel3Complex() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(" - i1\n");
		sb.append(" -- i11\n");
		sb.append(" *** i111\n");
		sb.append(" -- i12\n");
		sb.append(" - i2\n");
		String input = sb.toString();
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><ul><li>i1</li><ul><li>i11</li><ul><li>i111</li></ul><li>i12</li></ul><li>i2</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedListSubBulletPointSubNumber() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_bulletListSubNumberSubBulletPoint() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_mixedBulletPointAndNumberedList() throws Exception{
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
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_codeBlock() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockWithIndentedText() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\n  int a = 7;\n  int b = 5;\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>  int a = 7;<br/>  int b = 5;</span></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("Result (incl. new lines):\n" + new BrightMarkdown().createHTML(input));
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockUnclosed() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockOnly() throws Exception{
		String input = "```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeWithUnknownLanguage() throws Exception{
		String input = "```xyz\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;</span></code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockWithBracketsLikeALink() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nThis is text with [brackets][like a link]\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span>some source code<br/>next line<br/>This is text with [brackets][like a link]</span></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("Result (incl. new lines):\n" + new BrightMarkdown().createHTML(input));
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_codeBlockJava() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```java\nif (x == 4) {\n    //commented out\n    int y = \"hi!\";\n    /*block\n    comment*/\n}\n\na = b;\n```\nrest of the text";
		String resultWithFormatting = new BrightMarkdown().createHTML(input);
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"" + BrightMarkdownHTMLCreator.CODE_BOX_STYLE + "\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">if</span><span> (x == 4) {<br/></span><span style=\"color:darkgreen\">//commented out</span><span><br/></span><span style=\"color:purple;font-weight:bold\">int</span><span> y = </span><span style=\"color:blue\">\"hi!\"</span><span>;<br/></span><span style=\"color:darkgreen\">/*block<br/>comment*/</span><span><br/>}<br/><br/>a = b;</span></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("==========================");
		System.out.println("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		assertEquals(expected, result);
	}


	@Test
	public void test_createHTML_codeBlockXML() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```xml\n<tag1 x=\"1\">nice</tag1>\n```\nrest of the text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String resultWithFormatting = new BrightMarkdown().createHTML(input);
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre style=\"background:lightgrey\"><code><span><br/></span><span style=\"color:purple;font-weight:bold\">&lt;tag1</span><span> x=</span><span style=\"color:blue\">\"1\"</span><span style=\"color:purple;font-weight:bold\">&gt;</span><span>nice</span><span style=\"color:purple;font-weight:bold\">&lt;/tag1&gt;</span></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("==========================");
		System.out.println("==========================");
		System.out.println("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		assertEquals(expected, result);
	}

	
	@Test
	public void test_isValidSize_multiple() throws Exception{
		assertEquals(true, new BrightMarkdown().isValidSize("10"));
		assertEquals(true, new BrightMarkdown().isValidSize("10px"));
		assertEquals(true, new BrightMarkdown().isValidSize("10in"));
		assertEquals(true, new BrightMarkdown().isValidSize("10mm"));
		assertEquals(true, new BrightMarkdown().isValidSize("10.3mm"));
		assertEquals(true, new BrightMarkdown().isValidSize("1mm"));
		assertEquals(false, new BrightMarkdown().isValidSize("1mx"));
		assertEquals(false, new BrightMarkdown().isValidSize("mm"));
		assertEquals(false, new BrightMarkdown().isValidSize("mmx"));
	}

	@Test
	public void test_readImageLocationAndSize_widthAndHeight() throws Exception{
		String input = "myimg.png width=10mm height=7mm";
		BrightMarkdownSection section = new BrightMarkdownSection();
		new BrightMarkdown().readImageLocationAndSize(section, input);
		
		assertEquals("myimg.png", section.getLocation());
		assertEquals("10mm", section.getImageWidth());
		assertEquals("7mm", section.getImageHeight());
	}

	@Test
	public void test_readImageLocationAndSize_widthAndHeightMultipleSpace() throws Exception{
		String input = "myimg.png   width=10mm   height=7mm";
		BrightMarkdownSection section = new BrightMarkdownSection();
		new BrightMarkdown().readImageLocationAndSize(section, input);
		
		assertEquals("myimg.png", section.getLocation());
		assertEquals("10mm", section.getImageWidth());
		assertEquals("7mm", section.getImageHeight());
	}
	
	@Test
	public void test_readImageLocationAndSize_width() throws Exception{
		String input = "myimg.png width=10mm ";
		BrightMarkdownSection section = new BrightMarkdownSection();
		new BrightMarkdown().readImageLocationAndSize(section, input);
		
		assertEquals("myimg.png", section.getLocation());
		assertEquals("10mm", section.getImageWidth());
		assertEquals(null, section.getImageHeight());
	}
	
	
	@Test
	public void test_createHTML_imageNormal() throws Exception{
		String input = "Title\n![my img](image.png)\nmore text";
		String result = removeFormatting(new BrightMarkdown(true).createHTML(input));
		String expected = "<html><body><p><span>Title</span><br><span><img alt=\"my img\" src=\"image.png\"></span><br><span>more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_imageNormalRisized() throws Exception{
		String input = "Title\n![my img](image.png width=10mm)\nmore text";
		String result = removeFormatting(new BrightMarkdown().createHTML(input));
		String resultWithFormatting = new BrightMarkdown().createHTML(input);
		String expected = "<html><body><p><span>Title</span><br><span><img alt=\"my img\" src=\"image.png\" width=\"10mm\"></span><br><span>more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("expected:\n" + expected);
		System.out.println("==========================");
		System.out.println("Parsed:\n" + new BrightMarkdown().toString(new BrightMarkdown().parseAll(input)));
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("==========================");
		System.out.println("Result with formatting:\n>>\n" + resultWithFormatting + "\n<<");
		assertEquals(expected, result);
	}

	@Test
	public void test_parseAll_table_backgroundAtStartAndEndTableRow() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("{bc:red}c2-1|c2-2|c2-3{bc}\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);

		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.setLogginActive(true);
		
		BrightMarkdownSection result = brightMarkdown.parseAll(input);
		System.out.println("==========================");
		System.out.println("sections:\n" + brightMarkdown.toString(result));

		BrightMarkdownSection section = result;
		assertEquals(1, section.getChildren().size());
		section = result.getChildren().get(0);
		
		System.out.println("==========================");
		System.out.println("table row sections:\n" + brightMarkdown.toString(section));

		
		assertEquals(MDType.TABLE_ROW, section.getType());
		assertEquals("red", section.getBackgroundColor());
		assertEquals(3, section.getChildren().size());
		
		assertEquals(1, section.getChildren().get(0).getChildren().size());
		assertEquals(0, BrightMarkdownUtil.countChildren(section.getChildren().get(0).getChildren().get(0)));
		assertEquals(0, BrightMarkdownUtil.countChildren(section.getChildren().get(1)));
		assertEquals(2, section.getChildren().get(2).getChildren().size());
		assertEquals(0, BrightMarkdownUtil.countChildren(section.getChildren().get(2).getChildren().get(0)));
		assertEquals(0, BrightMarkdownUtil.countChildren(section.getChildren().get(2).getChildren().get(1)));
		
		assertEquals(true, BrightMarkdownUtil.isEmptyOrNull(section.getChildren().get(0).getRawText()));
		assertEquals("c2-1", section.getChildren().get(0).getChildren().get(0).getRawText());
		
		assertEquals("c2-2", section.getChildren().get(1).getRawText());
		
		assertEquals(true, BrightMarkdownUtil.isEmptyOrNull(section.getChildren().get(2).getRawText()));
		assertEquals("c2-3", section.getChildren().get(2).getChildren().get(0).getRawText());
	}

	
	@Test
	public void test_createHTML_table_rowBackground() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("{bc:red}c2-1|c2-2|c2-3{bc}\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.setLogginActive(true);
		
		BrightMarkdownSection sections = brightMarkdown.parseAll(input);
		System.out.println("==========================");
		System.out.println("sections:\n" + brightMarkdown.toString(sections));

		
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr style=\"background-color:red\"><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_table_rowBackgroundNoEndTag() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("{bc:red}c2-1|c2-2|c2-3\n");
		sb.append("c3-1|c3-2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.setLogginActive(true);
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr style=\"background-color:red\"><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	
	@Test
	public void test_createHTML_table_rowBackgroundInterruption() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("{bc:green}c1-1|c1-2|{bc}c1-3\n");
		sb.append("{bc:red}c2-1|c2-2|c2-3\n");
		sb.append("{bc:yellow}c3-1|c3-{bc:blue}2|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.setLogginActive(true);
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td style=\"background-color:green\">c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr style=\"background-color:red\"><td>c2-1</td><td>c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td style=\"background-color:yellow\">c3-1</td><td><span>c3-</span><span style=\"background-color:blue\">2</span></td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}


	
	@Test
	public void test_createHTML_table_cellBackground() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("{bc:red}c1-1{bc}|c1-2|c1-3\n");
		sb.append("c2-1|{bc:green}c2-2|c2-3\n");
		sb.append("c3-1|c3-2|{bc:blue}c3-3{bc}\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.setLogginActive(true);
		
		BrightMarkdownSection sections = brightMarkdown.parseAll(input);
		System.out.println("==========================");
		System.out.println("sections:\n" + brightMarkdown.toString(sections));

		
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td style=\"background-color:red\">c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td style=\"background-color:green\">c2-2</td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td>c3-2</td><td style=\"background-color:blue\">c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_table_cellBackgroundInterrupted() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("My Table:\n");
		sb.append("h-1|h-2|h-3\n");
		sb.append("------\n");
		sb.append("c1-1|c1-2|c1-3\n");
		sb.append("c2-1|{bc:green}c2{bc:red}-2{bc}|c2-3\n");
		sb.append("c3-1|{bc:blue}c3-_2_{bc}|c3-3\n");
		sb.append("More text...\n");
		String input = sb.toString();
		System.out.println("input:\n" + input);
		
		BrightMarkdown brightMarkdown = new BrightMarkdown();
		brightMarkdown.setLogginActive(true);
		
		BrightMarkdownSection sections = brightMarkdown.parseAll(input);
		System.out.println("==========================");
		System.out.println("sections:\n" + brightMarkdown.toString(sections));
		
		
		
		String result = removeFormatting(brightMarkdown.createHTML(input));
		String expected = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		expected += TABLE_STYLE + "</head>";
		expected += "<body><p>My Table:</p><table>";
		expected += "<tr><th>h-1</th><th>h-2</th><th>h-3</th></tr>";
		expected += "<tr><td>c1-1</td><td>c1-2</td><td>c1-3</td></tr>";
		expected += "<tr><td>c2-1</td><td><span style=\"background-color:green\">c2</span><span style=\"background-color:red\">-2</span></td><td>c2-3</td></tr>";
		expected += "<tr><td>c3-1</td><td style=\"background-color:blue\"><span>c3-</span><i><span style=\"background-color:blue\">2</span></i></td><td>c3-3</td></tr>";
		expected += "</table><p>More text...</p></body></html>";
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	
	
}


