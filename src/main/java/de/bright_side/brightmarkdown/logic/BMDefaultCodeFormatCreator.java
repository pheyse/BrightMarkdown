package de.bright_side.brightmarkdown.logic;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import de.bright_side.brightmarkdown.model.BMCodeFormat;

public class BMDefaultCodeFormatCreator {

	public Map<String, BMCodeFormat> createCodeFormats() {
		Map<String, BMCodeFormat> result = new TreeMap<String, BMCodeFormat>();
		result.put("java", createJavaFormat());
		result.put("xml", createXMLFormat());
		
		return result;
	}

	private BMCodeFormat createJavaFormat() {
		BMCodeFormat result = new BMCodeFormat();
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
		result.setTagStarts(new TreeSet<String>(Arrays.<String>asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	
	private BMCodeFormat createXMLFormat() {
		BMCodeFormat result = new BMCodeFormat();
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
