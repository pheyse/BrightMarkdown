package de.bright_side.brightmarkdown;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class BrightMarkdownCodeFormatDefinition {

	public Map<String, BrightMarkdownCodeFormat> createCodeFormats() {
		Map<String, BrightMarkdownCodeFormat> result = new TreeMap<String, BrightMarkdownCodeFormat>();
		result.put("java", createJavaFormat());
		result.put("xml", createXMLFormat());
		
		return result;
	}

	private BrightMarkdownCodeFormat createJavaFormat() {
		BrightMarkdownCodeFormat result = new BrightMarkdownCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"")));
		result.setLineCommentStart("//");
		result.setEscapeCharacter("\\");
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setLineCommentStart("//");
		result.setKeywords(new TreeSet<String>(Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "goto"
				, "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected"
				, "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch"
				, "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long"
				, "strictfp", "volatile", "const", "float", "native", "super", "while")));
		result.setTagStarts(new TreeSet<String>(Arrays.asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	
	private BrightMarkdownCodeFormat createXMLFormat() {
		BrightMarkdownCodeFormat result = new BrightMarkdownCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"", "'")));
		result.setLineCommentStart(null);
		result.setEscapeCharacter(null);
		result.setBlockCommentStart("<!--");
		result.setBlockCommentEnd("-->");
		result.setLineCommentStart(null);
		result.setKeywords(new TreeSet<String>());
		result.setTagStarts(new TreeSet<String>(Arrays.asList("<", "</")));
		result.setTagEnds(new TreeSet<String>(Arrays.asList(">", "/>")));
		return result;
	}

	
}
