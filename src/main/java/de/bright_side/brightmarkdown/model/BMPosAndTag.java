package de.bright_side.brightmarkdown.model;

public class BMPosAndTag {
	private int pos;
	private String tag;
	
	public BMPosAndTag() {
	}

	public BMPosAndTag(int pos, String tag) {
		super();
		this.pos = pos;
		this.tag = tag;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String toString() {
		String useTag = "(none)";
		if (tag != null) {
			useTag = tag.replace("\n", "\\n");
		}
		return "BMPosAndTag(pos = " + pos + ", tag = \"" + useTag + "\")";
	}
}
