package de.bright_side.brightmarkdown;

import java.util.Collection;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

public class BrightMarkdownUtil {

	protected PosAndTag findNext(String text, int startPos, Collection<String> tags) {
		PosAndTag result = null;
		for (String i: tags) {
			int pos = text.indexOf(i, startPos);
			if (pos >= 0) {
				if ((result == null) || (pos < result.pos)) {
					result = new PosAndTag();
					result.pos = pos;
					result.tag = i;
				} else if ((pos == result.pos) && (result.tag.length() < i.length())) { //: check if match with longer tag
					result = new PosAndTag();
					result.pos = pos;
					result.tag = i;
				}
			}
		}
		return result;
	}
	
	public static BrightMarkdownSection createSection(BrightMarkdownSection parent, MDType type, String rawText){
		BrightMarkdownSection result = new BrightMarkdownSection();
		result.setParent(parent);
		result.setType(type);
		result.setRawText(rawText);
		return result;
	}

	
	protected class PosAndTag{
		int pos;
		String tag;
		public String toString() {
			return "PosAndTAg(pos = " + pos + ", tag = \"" + tag + "\")";
		}
	}


	public static boolean isNumber(String text) {
		try {
			Double.valueOf(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
