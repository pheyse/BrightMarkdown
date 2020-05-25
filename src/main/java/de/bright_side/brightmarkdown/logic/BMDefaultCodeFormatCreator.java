package de.bright_side.brightmarkdown.logic;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import de.bright_side.brightmarkdown.model.BMCodeFormat;

public class BMDefaultCodeFormatCreator {

	public Map<String, BMCodeFormat> createCodeFormats() {
		Map<String, BMCodeFormat> result = new TreeMap<String, BMCodeFormat>();
		result.put("", createEmptyFormat());
		result.put("java", createJavaFormat());
		result.put("xml", createXMLFormat());
		result.put("html", createXMLFormat());
		result.put("js", createJavaScriptFormat());
		result.put("javascript", createJavaScriptFormat());
		result.put("kt", createKotlinFormat());
		result.put("kotlin", createKotlinFormat());
		result.put("ts", createTypeScriptFormat());
		result.put("typescript", createTypeScriptFormat());
		result.put("scala", createScalaFormat());
		result.put("sql", createSqlFormat());
		
		return result;
	}

	private BMCodeFormat createKotlinFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"")));
		result.setLineCommentStart("//");
		result.setEscapeCharacter("\\");
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>(Arrays.asList("as", "break", "class", "continue", "do", "else", "false", "for", "fun"
				, "in", "if", "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try"
				, "typealias", "typeof", "val", "var", "when", "while", "by", "catch", "constructor", "delegate", "dynamic", "field"
				, "file", "finally", "get", "import", "init", "param", "property", "receiver", "set", "setparam", "where", "actual"
				, "abstract", "annotation", "companion", "const", "crossinline", "data", "enum", "expect", "external", "final"
				, "infix", "inline", "inner", "internal", "lateinit", "noinline", "open", "operator", "out", "override", "private"
				, "protected", "public", "reified", "sealed", "suspend", "tailrec", "vararg", "field", "it")));
		result.setTagStarts(new TreeSet<String>(Arrays.<String>asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	private BMCodeFormat createSqlFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("'")));
		result.setLineCommentStart("--");
		result.setEscapeCharacter(null);
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setKeywordsIgnoreCase(true);
		result.setKeywords(new TreeSet<String>(Arrays.asList("ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "BACKUP", "BETWEEN"
				, "BY", "CASE", "CHECK", "COLUMN", "CONSTRAINT", "CREATE", "DATABASE", "DEFAULT", "DELETE", "DESC", "DISTINCT"
				, "DROP", "EXEC", "EXISTS", "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "IN", "INDEX", "INNER", "INSERT", "INTO"
				, "IS", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "NOT", "NULL", "OR", "ORDER", "OUTER", "PRIMARY", "PROCEDURE"
				, "REPLACE", "RIGHT", "ROWNUM", "SELECT", "SET", "TABLE", "TOP", "TRUNCATE", "UNION", "UNIQUE", "UPDATE", "VALUES"
				, "VIEW", "WHERE")));
		result.setTagStarts(new TreeSet<String>(Arrays.<String>asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	private BMCodeFormat createJavaFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"")));
		result.setLineCommentStart("//");
		result.setEscapeCharacter("\\");
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>(Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "goto"
				, "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected"
				, "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch"
				, "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long"
				, "strictfp", "volatile", "const", "float", "native", "super", "while", "true", "false")));
		result.setTagStarts(new TreeSet<String>(Arrays.<String>asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	private BMCodeFormat createScalaFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"")));
		result.setLineCommentStart("//");
		result.setEscapeCharacter("\\");
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>(Arrays.asList("abstract", "case", "catch", "class", "def", "do", "else", "extends"
				, "false", "final", "finally", "for", "forSome", "if", "implicit", "import", "lazy", "match", "new", "null", "object"
				, "override", "package", "private", "protected", "return", "sealed", "super", "this", "throw", "trait", "true", "try"
				, "type", "val", "var", "while", "with", "yield")));
		result.setTagStarts(new TreeSet<String>(Arrays.<String>asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	private BMCodeFormat createJavaScriptFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"", "´")));
		result.setLineCommentStart("//");
		result.setEscapeCharacter("\\");
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>(Arrays.asList("abstract", "arguments", "await", "boolean", "break", "byte", "case", "catch"
				, "char", "class", "const", "continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval"
				, "export", "extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements", "import"
				, "in", "instanceof", "int", "interface", "let", "long", "native", "new", "null", "package", "private", "protected"
				, "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient"
				, "true", "try", "typeof", "var", "void", "volatile", "while", "with", "yield", "abstract", "boolean", "byte", "char"
				, "double", "final", "float", "goto", "int", "long", "native", "short", "synchronized", "throws", "transient"
				, "volatile", "prototype", "function", "Array", "Date", "eval", "hasOwnProperty", "Infinity", "isFinite", "isNaN"
				, "isPrototypeOf", "Math", "NaN", "Number", "Object", "String", "toString", "undefined", "valueOf")));
		result.setTagStarts(new TreeSet<String>(Arrays.<String>asList()));
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	private BMCodeFormat createTypeScriptFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>(Arrays.asList("\"", "´")));
		result.setLineCommentStart("//");
		result.setEscapeCharacter("\\");
		result.setBlockCommentStart("/*");
		result.setBlockCommentEnd("*/");
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>(Arrays.asList("break", "as", "any", "case", "implements", "boolean", "catch", "interface"
				, "constructor", "class", "let", "declare", "const", "package", "get", "continue", "private", "module", "debugger"
				, "protected", "require", "default", "public", "number", "delete", "static", "set", "do", "yield", "string", "else"
				, "symbol", "enum", "type", "export", "from", "extends", "of", "false", "finally", "for", "function", "if", "import"
				, "in", "instanceof", "new", "null", "return", "super", "switch", "this", "throw", "true", "try", "typeof", "var"
				, "void", "while", "with", "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete"
				, "do", "else", "enum", "export", "extends", "false", "finally", "for", "function", "if", "import", "in", "instanceof"
				, "new", "null", "return", "super", "switch", "this", "throw", "true", "try", "typeof", "var", "void", "while", "with"
				, "as", "implements", "interface", "let", "package", "private", "protected", "public", "static", "yield", "any"
				, "boolean", "constructor", "declare", "get", "module", "require", "number", "set", "string", "symbol", "type", "from"
				, "of", "namespace", "async", "await")));
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
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>());
		result.setTagStarts(new TreeSet<String>(Arrays.asList("<", "</")));
		result.setTagEnds(new TreeSet<String>(Arrays.asList(">", "/>")));
		return result;
	}

	private BMCodeFormat createEmptyFormat() {
		BMCodeFormat result = new BMCodeFormat();
		result.setStringIndicators(new TreeSet<String>());
		result.setLineCommentStart(null);
		result.setEscapeCharacter(null);
		result.setBlockCommentStart(null);
		result.setBlockCommentEnd(null);
		result.setKeywordsIgnoreCase(false);
		result.setKeywords(new TreeSet<String>());
		result.setTagStarts(new TreeSet<String>());
		result.setTagEnds(new TreeSet<String>());
		return result;
	}
	
	
}
