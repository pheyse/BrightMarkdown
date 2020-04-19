package de.bright_side.brightmarkdown.logic;

import java.util.Collection;
import java.util.List;

import de.bright_side.brightmarkdown.model.BMPosAndTag;

public class BMTextParserLogic {

	public BMPosAndTag findNext(String text, int startPos, Collection<String> tags) {
		BMPosAndTag result = null;
		for (String i: tags) {
			int pos = text.indexOf(i, startPos);
			if (pos >= 0) {
				if ((result == null) || (pos < result.getPos())) {
					result = new BMPosAndTag();
					result.setPos(pos);
					result.setTag(i);
				} else if ((pos == result.getPos()) && (result.getTag().length() < i.length())) { //: check if match with longer tag
					result = new BMPosAndTag();
					result.setPos(pos);
					result.setTag(i);
				}
			}
		}
		return result;
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

}
