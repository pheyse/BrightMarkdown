package de.bright_side.brightmarkdown;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;

import org.junit.Test;

import de.bright_side.brightmarkdown.BrightMarkdown.FormattingItem;

/**
 * 
 * @author Philip Heyse
 *
 */
public class TestBrightMarkdown {
	@Test
	public void test_createHTML_printDocumentation() throws Exception{
		System.out.println("Documentation:\n" + new BrightMarkdown().getDocumentationAsHTML());
	}

	@Test
	public void testParseAllSimple(){
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is **bold**. Nice?";
		BrightMarkdownSection result = new BrightMarkdown().parseAll(input);
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + new BrightMarkdown().toString(result));
	}
	
	@Test
	public void test_createHTML_normal() throws Exception{
		String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is **bold**. Nice?";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul><p><br><span><span>This text is </span><b>bold</b><span>. Nice?</span></span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_specialCharacters() throws Exception{
		String input = "# Title\nHello! Special: <>&?";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p>Hello! Special: &lt;&gt;&amp;?</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_noText() throws Exception{
		String input = "";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_noMarkdown() throws Exception{
		String input = "Simple Text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Simple Text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_onlyBold() throws Exception{
		String input = "**Only Bold**";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><b>Only Bold</b></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_textAndStrikeThrough() throws Exception{
		String input = "Hello ~~strike through~~ there";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>Hello </span><strike>strike through</strike><span> there</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_onlyHeading() throws Exception{
		String input = "## my title";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h2>my title</h2></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_headingsOnDifferentLevels() throws Exception{
		String input = "# my title 1\n## my title 2\n### my title 3\n# other";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>my title 1</h1><h2>my title 2</h2><h3>my title 3</h3><h1>other</h1></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_headingWithPartItalic() throws Exception{
		String input = "## Title with *italic* part\nbla";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h2><span>Title with </span><i>italic</i><span> part</span></h2><p>bla</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_fullTextBulletPoints() throws Exception{
		String input = "* item 1\n* item 2\n. item 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><ul><li>item 1</li><li>item 2</li><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_bulletPointsWithPartBold() throws Exception{
		String input = "* item 1\n* item 2 with **bold** text\n. item 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><ul><li>item 1</li><li><span>item 2 with </span><b>bold</b><span> text</span></li><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_numberedList() throws Exception{
		String input = "# My List\n1. item 1\n2. item 2\n2. item 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>My List</h1><ol><li>item 1</li><li>item 2</li><li>item 3</li></ol></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_plainTextBulletPointsPlainText() throws Exception{
		String input = "Some plain text\n* item 1\n* item 2 with text\n. item 3\nmore plain text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Some plain text</p><ul><li>item 1</li><li>item 2 with text</li><li>item 3</li></ul><p>more plain text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_checkBoxes() throws Exception{
		String input = "Some plain text\n[] box 1\n[x] box 2\n[ ] box 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Some plain text</p><input disabled=\"true\" type=\"checkbox\">box 1</input><br><input checked=\"true\" disabled=\"true\" type=\"checkbox\">box 2</input><br><input disabled=\"true\" type=\"checkbox\">box 3</input><br></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_checkBoxesWithDifferentSyntax() throws Exception{
		String input = "Some plain text\n - [] box 1\n - [x] box 2\n - [ ] box 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Some plain text</p><input disabled=\"true\" type=\"checkbox\">box 1</input><br><input checked=\"true\" disabled=\"true\" type=\"checkbox\">box 2</input><br><input disabled=\"true\" type=\"checkbox\">box 3</input><br></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_bulletPointsOnDifferentLevelsNormal() throws Exception{
		String input = "Hello\n* item 1\n* item 2\n** item 2.1\n** item 2.2\n* item 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_bulletPointsLevelDownAndTwoLevelsUp() throws Exception{
		String input = "Hello\n* item 1\n* item 2\n** item 2.1\n** item 2.2\n*** item 2.2.1\n*** item 2.2.2\n* item 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Hello</p><ul><li>item 1</li><li>item 2</li><ul><li>item 2.1</li><li>item 2.2</li><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_bulletPointsBeginningWithLevel3() throws Exception{
		String input = "Hello\n*** item 2.2.1\n*** item 2.2.2\n* item 3";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Hello</p><ul><ul><ul><li>item 2.2.1</li><li>item 2.2.2</li></ul></ul><li>item 3</li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	
	@Test
	public void test_createHTML_horizontalRuleInText() throws Exception{
		String input = "Some plain text\n --------\nother text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Some plain text</p><hr><p>other text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_almostHorizontalRulesInText() throws Exception{
		String input = "Some plain text\n __\n\n ___x\n\n ___\nother text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>Some plain text</span><br><span><span>__</span></span><br><br><span><span>__</span><span>_x</span></span><br></p><hr><p>other text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyBold() throws Exception{
		String input = "Some plain text\n__x\nother text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>Some plain text</span><br><span><span>__</span><span>x</span></span><br><span>other text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyItalic() throws Exception{
		String input = "__";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>__</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkInText() throws Exception{
		String input = "this is a [link](www.wikipedia.de) and more text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\">link</a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkOnly() throws Exception{
		String input = "[link](www.wikipedia.de)";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><a href=\"www.wikipedia.de\">link</a></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkInBulletPoints() throws Exception{
		String input = "Title\n - bullet 1 with [link](www.wikipedia.de)\n - bullet two\nmore text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Title</p><ul><li><span>bullet 1 with </span><a href=\"www.wikipedia.de\">link</a></li><li>bullet two</li></ul><p>more text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_linkWithFormattedText() throws Exception{
		String input = "this is a [link with **formatted** text](www.wikipedia.de) and more text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>this is a </span><a href=\"www.wikipedia.de\"><span>link with </span><b>formatted</b><span> text</span></a><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_almostLink() throws Exception{
		String input = "this is a not a [link www.wikipedia.de) and more text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p><span>this is a not a </span><span>[link www.wikipedia.de)</span><span> and more text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_escapeChars() throws Exception{
		String input = "# Title\nThis text is **bold**. And this *italic* and this is \\*escaped\\*. Nice?";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p><span>This text is </span><b>bold</b><span>. And this </span><i>italic</i><span> and this is *escaped*. Nice?</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_codeBlock() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```\nrest of the text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre><code>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;<br/></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockWithIndentedText() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\n    int a = 7;\n    int b = 5;\n```\nrest of the text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre><code>some source code<br/>next line<br/>    int a = 7;<br/>    int b = 5;<br/></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("Result (incl. new lines):\n" + new BrightMarkdown().createHTML(input));
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockUnclosed() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nint a = 7;\nint b = 5;";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre><code>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;<br/></code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockOnly() throws Exception{
		String input = "```\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><pre><code>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;<br/></code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeWithIgnoredLanguage() throws Exception{
		String input = "```java\nsome source code\nnext line\nint a = 7;\nint b = 5;\n```";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><pre><code>some source code<br/>next line<br/>int a = 7;<br/>int b = 5;<br/></code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_codeBlockWithBracketsLikeALink() throws Exception{
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n```\nsome source code\nnext line\nThis is text with [brackets][like a link]\n```\nrest of the text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><p>Title</p><ul><li>bullet 1</li><li>bullet two</li></ul><p>more text</p><pre><code>some source code<br/>next line<br/>This is text with [brackets][like a link]<br/></code></pre><p>rest of the text</p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("Result (incl. new lines):\n" + new BrightMarkdown().createHTML(input));
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
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		
		String expected = "<html><body><h1>Fonotes</h1><p><span>2016-2017 by Philip Heyse - www.bright-side.de</span><br></p><h2>Used libraries and licences:</h2><ul><li>Fliesen UI: Apache V2</li>"
				+ "<li>GSON (https://github.com/google/gson): Apache V2</li><li>Jetty (http://www.eclipse.org/jetty/): Apache V2</li></ul><p><br><br></p><h2>Apache V2:</h2>"
				+ "<pre><code>" + codeBlockText.replace("\n", "<br/>") + "</code></pre></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("expected:\n" + expected);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		System.out.println("==========================");
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_ignoreFormattingWithinWord() throws Exception{
		String input = "# Title\nThis is time 2017_11_19__08_11 and only _this_ is formatted";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p><span>This is time 2017_11_19__08_11 and only </span><i>this</i><span> is formatted</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_ignoreSingleFormattingWithinWord() throws Exception{
		String input = "# Title\nThere is a file_name in the text";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p><span>There is a file_name in the text</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_ignoreFormattingInWordWith2CharIndicator() throws Exception{
		String input = "# Title\nThis is non__formatted_text and this is __formatted__ text.";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p><span>This is non__formatted_text and this is </span><b>formatted</b><span> text.</span></p></body></html>";
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
		String result = markdown.createHTML(input).replace("\r", "").replace("\n", "");
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
		String input = "Title\n - bullet 1\n - bullet two\nmore text\n**rest** of the text";
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
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span>Line 2</span><br><br><span>Line 3 (after empty line)</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}

	@Test
	public void test_createHTML_emptyLinesAndMoreElements() throws Exception{
		String input = "# Title\nLine 1\nLine 2\n\nLine 3 (after empty line)\n * item 1\n * item 2\nmore text in paragraph\nlast line";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><p><span>Line 1</span><br><span>Line 2</span><br><br><span>Line 3 (after empty line)</span></p><ul><li>item 1</li><li>item 2</li></ul><p><span>more text in paragraph</span><br><span>last line</span></p></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	
	@Test
	public void test_createHTML_emptyLinesAndMoreElementsWithFormatting() throws Exception{
		String input = "# Title\nLine 1\n_Line_ 2\n\nLine 3 (after **empty** line)\n * item 1\n * item 2\nmore text in paragraph\nlast line";
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
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
		sb.append(" - **bold _italic!_ rest of bold** unformatted\n");
		String input = sb.toString();
		String result = new BrightMarkdown().createHTML(input).replace("\r", "").replace("\n", "");
		String expected = "<html><body><h1>Title</h1><ul><li>item 1 \"special\" text</li><li>item 2</li><li>item 3</li><li>item 4 with \"quotes\"</li><li><b><span>bold </span><i>italic!</i><span> rest of bold</span></b><span> unformatted</span></li></ul></body></html>";
		System.out.println("input:\n" + input);
		System.out.println("==========================");
		System.out.println("Result:\n" + result);
		assertEquals(expected, result);
	}
	

	
}


