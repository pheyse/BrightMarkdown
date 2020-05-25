package de.bright_side.brightmarkdown.logic;

public class BMDocumentationCreator {
	public String getDocumentationAsMarkdown() {
		StringBuilder sb = new StringBuilder();
		add(sb, "# Syntax");
		add(sb, "## Headings");
		add(sb, "* \\# heading level 1");
		add(sb, "* \\#\\# heading level 2");
		add(sb, "* \\#\\#\\# heading level 3");
		add(sb, "* \\#\\#\\#\\# heading level 4");
		add(sb, "* \\#\\#\\#\\#\\# heading level 5");
		add(sb, "");
		add(sb, "## Bullet Point Lists");
		add(sb, "* \\* bullet point item level 1");
		add(sb, "* \\*\\* bullet point item level 2");
		add(sb, "* \\*\\*\\* bullet point item level 3");
		add(sb, "* \\*\\*\\*\\* bullet point item level 4");
		add(sb, "* \\*\\*\\*\\*\\* bullet point item level 5");
		add(sb, "* Instead of \\* you can also use \\-, \\o or \\-");
		add(sb, "* Instead of multiple markers like \\*\\* you can also indent by three or more spaces than the previous item");
		add(sb, "");
		add(sb, "## Numbered list");
		add(sb, "* start line with a \".\" followed by at least one space ");
		add(sb, "* like bullet point lists you can also indent by three or more spaces than the previous item for sub lists");
		add(sb, "* you can also use \"1.\", \"2.\" etc. Which number is used does not matter. This does not work for sub lists though.");
		add(sb, "");
		add(sb, "## Formatting");
		add(sb, "* use \\_text\\_ to write text in _italic_");
		add(sb, "* use \\*text\\* to write text in *bold*");
		add(sb, "* use \\+text\\+ to write text in +underlined+");
		add(sb, "* use \\~text\\~ to write text in ~strike through~");
		add(sb, "");
		add(sb, "## Colors");
		add(sb, "* put text between \\{color:_*value*_} and \\{color} to set a text {c:red}color{c}");
		add(sb, "* put text between \\{bg-color:_*value*_} and \\{bg-color} to set a text {bc:yellow}background color{bc}");
		add(sb, "* instead of \"color\" and \"bg-color\" you can also use \"c\" and \"bc\"");
		add(sb, "* possible color _*values*_: \"black\", \"blue\", \"brown\", \"cyan\", \"gold\", \"gray\", \"grey\", \"green\", \"lightgray\", \"lightgrey\", \"darkgray\", \"darkgrey\", \"magenta\", \"red\", \"teal\", \"white\", \"yellow\", \"pink\"");
		add(sb, "* you can also specify a color as a hex value like #ffaa00");
		add(sb, "");
		add(sb, "## Links");
		add(sb, "* \\[my link label\\]\\[www.wikipedia.de\\]");
		add(sb, "* instead of \"\\[\" and \"\\]\" you can also use \"\\(\" and \"\\)\"");
		add(sb, "");
		add(sb, "## Images");
		add(sb, "* !\\[alt text\\]\\[http://path/to/image.png\\]");
		add(sb, "* instead of \"\\[\" and \"\\]\" you can also use \"\\(\" and \"\\)\"");
		add(sb, "* the 'alt text' part is optional, so you can also write !\\[http://path/to/image.png\\]");
		add(sb, "* you can also specify the width and/or height and/or border size of the image like this: !\\[alt text\\]\\[http://path/to/image.png *width=20mm height=10mm border=3mm* \\]");
		add(sb, "* when specifying width, height and/or border you can use the units \"%\" (percent of parent element), \"px\" (pixels), \"mm\" (milimeters) and \"in\" (inches). You can also leave out the unit for \"px\".");
		add(sb, "* if nothing is specified a border of 1mm and a with of 75% are used");
		add(sb, "");
		add(sb, "## Checkbox lists");
		add(sb, "* Start the line with \\[x\\] for a checked box and \\[\\] for an unchecked box)");
		add(sb, "");
		add(sb, "## Horizontal rule");
		add(sb, "* have a line that contains of 3 or more \\*\\*\\*");
		add(sb, "* instead of \\* you can also use \\_, \\-, \\= or \\#");
		add(sb, "");
		add(sb, "## Tables");
		add(sb, "* use the \\| character to separate cells");
		add(sb, "* place a few \\- chars underneath the first row to make it a header row");
		add(sb, "* place \\{bg-color:_*value*_\\} or \\{bc:_*value*_\\} at the beginning of a row to set the row background or at the beginning of a cell to set the cell background");
		add(sb, "");
		add(sb, "## Escaping special characters");
		add(sb, "* Place a \\\\ before a special character like \\* to escape it (ignore for processing)");
		add(sb, "");
		add(sb, "## Code blocks");
		add(sb, "* place a line of two or three \\` or \\´ before and after the text to indicate a code block");
		add(sb, "* code blocks may also occur in a text paragraph or a bullet point item (and they even may have line breaks)");
		add(sb, "* write the format (case is ignored) behind the indicator for syntax highlighting. Example \"\\`\\`java\"");
		add(sb, "* these formats are available: xml, java, html, javascript (short: js), kotlin (short: kt), typeScript (short: ts), scala, sql");
		add(sb, "");
		add(sb, "## Special sections in code blocks");
		add(sb, "* It is also possible to add special formatting sections within(!) code blocks to highlight passages, add additional information, indicate placeholders, etc.");
		add(sb, "* Syntax: !!!{c:blue}_<format>_{c}!{c:blue}_<text>_{c}!!!. Example \\´\\´my code block !!!hl!highlighted text!!! more code\\´\\´");
		add(sb, "* Formatting options: 'hl' to highlight text, 'ph' to indicate a placeholder, 'info' to indicate an info-text, 'c:_{c:blue}<color-name>{c}_' "
				+ "or 'color:_{c:blue}<color-name>{c}_' for colors, 'bc:_{c:blue}<color>{c}_' or 'bg-color:_{c:blue}<color>{c}_' for background colors,"
				+ " 'b' for bold, 'i' for italic and 'u' for underline");
		add(sb, "* Elipse (='...'): write '!!!...'. Example: \\´\\´my code !!!... more code\\´\\´ ");
		add(sb, "* Escaping '!!!': write '!!!\\' followed by the '!' you want to show. Example to show '!!!!': \\´\\´my code !!!\\!!!! more code\\´\\´");
		add(sb, "");
		add(sb, "## Table of contents");
		add(sb, "* have a line with only the text \\{TOC}");
		add(sb, "");
		add(sb, "## Disable processing");
		add(sb, "* place \\{NOMARKDOWN} at the beginning of the text");
		add(sb, "");
		
		return sb.toString();
	}
	
	private void add(StringBuilder sb, String text){
		sb.append(text + "\n");
	}
	

}
