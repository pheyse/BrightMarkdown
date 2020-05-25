package de.bright_side.brightmarkdown.logic;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.bright_side.brightmarkdown.model.BMPosAndTag;

public class BMTextParserLogic {
	interface MatchConfirmer {
		boolean confirmMatch(String text, int pos, String tag);
	}
	
	public BMPosAndTag findNext(String text, int startPos, Collection<String> tags) {
		return findNext(text, startPos, false, tags, null);
	}
	
	public BMPosAndTag findNext(String text, int startPos, boolean ignoreCase, Collection<String> tags) {
		return findNext(text, startPos, ignoreCase, tags, null);
	}
	
	/**
	 * 
	 * @return BMPosAndTag containing first matching tag and its position or null if there are no matching tags
	 */
	public BMPosAndTag findNext(String text, int startPos, boolean ignoreCase, Collection<String> tags, MatchConfirmer matchConfirmer) {
		BMPosAndTag result = null;
		String useText = applyIgnoreCase(text, ignoreCase);
		for (String i: tags) {
//			boolean matchConfirmerDeclined = false;
			String searchItem;
			if (ignoreCase) {
				searchItem = i.toUpperCase();
			} else {
				searchItem = i;
			}
			int pos = useText.indexOf(searchItem, startPos);
			while (pos >= 0){
				boolean match = false;
				if ((result == null) || (pos < result.getPos())) {
					match = true;
				} else if ((pos == result.getPos()) && (result.getTag().length() < i.length())) { //: check if match with longer tag
					match = true;
				} else {
					pos = -1; // set to < 0 to end the loop
				}
				
				if ((match) && (matchConfirmer != null)) {
					match = matchConfirmer.confirmMatch(text, pos, i);
					if (!match) {
						//: the matchConfirmer declined. Find next potential match
//						matchConfirmerDeclined = true;
						pos = useText.indexOf(searchItem, pos + 1);
					}
				}
				
				if (match) {
					result = new BMPosAndTag();
					result.setPos(pos);
					result.setTag(i);
					pos = -1; // set to < 0 to end the loop
				}
				
			}
		}
		return result;
	}
	
	public char getCharBeforeOrDefault(String string, int pos, char defaultChar) {
		if ((pos <= 0) || (pos >= string.length())) {
			return defaultChar;
		}
		return string.charAt(pos - 1);
	}
	
	public char getCharAfterOrDefault(String string, int pos, char defaultChar) {
		if ((pos + 1 < 0) || (pos + 1 >= string.length())) {
			return defaultChar;
		}
		return string.charAt(pos + 1);
	}
	
	private String applyIgnoreCase(String string, boolean ignoreCase) {
		if (!ignoreCase) {
			return string;
		}
		return string.toUpperCase();
	}
	
	public BMPosAndTag findNext(String text, int startPos, String ... tags) {
		return findNext(text, startPos, Arrays.asList(tags));
	}
	
	protected int findPosAfterLeadindSpaces(String rawText, int startPos) {
		int result = 0;
		int start = startPos + 1;
		if (rawText.length() <= start){
			return start;
		}
		result = start;
		for (char i: rawText.substring(start).toCharArray()){
			if (i == ' '){
				result ++;
			} else {
				return result;
			}
		}
		return result;
	}

	public BMPosAndTag findNextSkipSpaces(String rawText, int startPos) {
		int pos = startPos;
		int length = rawText.length();
				
		while(pos < length) {
			char c = rawText.charAt(pos); 
			if (c != ' ') {
				return new BMPosAndTag(pos, "" + c);
			}
			pos ++;
		}
		
		return null;
	}
	
	public String readUntilCharOrEnd(String rawText, int startPos, char charToReadUntil) {
		int pos = startPos;
		int length = rawText.length();
		
		while(pos < length) {
			char c = rawText.charAt(pos);
			if (c == charToReadUntil) {
				return rawText.substring(startPos, pos);
			}
			pos ++;
		}
		
		return rawText.substring(startPos);
	}
	
	public String readUntilCharOrEnd(String rawText, int startPos, List<Character> charsToReadUntil) {
		int pos = startPos;
		int length = rawText.length();
		
		while(pos < length) {
			char c = rawText.charAt(pos);
			for (Character charToFind: charsToReadUntil) {
				if (c == charToFind) {
					return rawText.substring(startPos, pos);
				}
			}
			pos ++;
		}
		
		return rawText.substring(startPos);
	}
	
	public String removeLeadingChars(String rawText, int startPos, List<Character> charsToRemove) {
		int pos = startPos;
		int length = rawText.length();
		
		while(pos < length) {
			char c = rawText.charAt(pos);
			if (!charsToRemove.contains(c)) {
				return rawText.substring(pos);
			}
			pos ++;
		}
		
		return rawText;
	}

	public String removeLeadingTextIfFound(String rawText, String textToRemoveIfFound) {
		if (rawText == null) {
			return null;
		}
		if (rawText.startsWith(textToRemoveIfFound)) {
			return rawText.substring(textToRemoveIfFound.length());
		}
		return rawText;
	}
	
	/**
	 * reads in the given raw text either until the end of the raw text was reached or the amount charsToRead has been read
	 * @return
	 */
	public String readUntilLengthOrEnd(String rawText, int startPos, int length) {
		int textLength = rawText.length();
		
		if (length <= 0) {
			return "";
		}
		if (startPos >= textLength) {
			return "";
		}
		if (startPos + length >= textLength) {
			return rawText.substring(startPos);
		}
		
		return rawText.substring(startPos, startPos + length);
	}

	/**
	 * 
	 * @param rawText
	 * @param startPos
	 * @param charToSkip
	 * @return position where the next char is different than charToSkip
	 */
	protected int posAfterCharOccurences(String rawText, int startPos, char charToSkip) {
		int result = 0;
		int start = startPos;
		if (rawText.length() <= start){
			return start;
		}
		result = start;
		for (char i: rawText.substring(start).toCharArray()){
			if (i == charToSkip){
				result ++;
			} else {
				return result;
			}
		}
		return result;
	}


}
