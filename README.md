# BrightMarkdown
BrightMarkdown is a light weight java library to process markdown text.
Created 2017-2019 by Philip Heyse

## License
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Features
 - headings
 - formatting: bold, italic, strike through
 - foreground and background color
 - bullet point lists
 - numbered lists
 - images
 - links
 - checkbox lists
 - horizontal rules
 - code blocks incl. syntax highlighting for Java and XML
 - tables 

## Usage

### Including via Maven
```xml
[...]
		<dependency>
			<groupId>de.bright-side.brightmarkdown</groupId>
			<artifactId>brightmarkdown</artifactId>
			<version>1.6.1</version>
		</dependency>
[...]
```

### Including via Gradle
```
dependencies {
    implementation 'de.bright-side.brightmarkdown:brightmarkdown:1.6.1'
}
```

### Java 8 Legacy version
For Java 8 please use version "1.6.1-legacy-java8"

### get documentation as HTML
```java
String html = new BrightMarkdown().createHTML(input);
```

### create full HTML document
```java
String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*.";
String html = new BrightMarkdown().createHTML(input);
```

### create HTML code to be embedded in web page document (e.g. without the <html> and <body> tags) 
```java
String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*.";
String embeddableHtml = brightMarkdown.createHTML(input, OutputType.EMBEDDABLE_HTML_CODE);
```

The CSS for a web page where the element has the name 'my-markdown' could be this:
```html
table.brightmarkdown{border-collapse: collapse;}
table.brightmarkdown td {border: 1px solid black; padding: 3px;}
table.brightmarkdown th {border: 1px solid black; padding: 3px;}
table.brightmarkdown th {background-color: #a0a0a0;}
table.brightmarkdown tr:nth-child(odd) {background-color: #d8d8d8;}
table.brightmarkdown tr:nth-child(even) {background-color: #ffffff;}
.my-markdown img{border-style: solid;}
.my-markdown h1{font-size:8mm;}
.my-markdown h2{font-size:7mm;}
.my-markdown h3{font-size:6mm;}
.my-markdown h4{font-size:5mm;}
```

## Syntax

### Headings
 - \# heading level 1
 - \## heading level 2
 - \### heading level 3
 - \#### heading level 4
 - \##### heading level 5

### Bullet Point Lists
 - \* bullet point item level 1
 - \*\* bullet point item level 2
 - \*\*\* bullet point item level 3
 - \*\*\*\* bullet point item level 4-
 - \*\*\*\*\* bullet point item level 5
 - Instead of \* you can also use \-, o or \-
 - Instead of multiple markers like \*\* you can also indent by three or more spaces than the previous item

### Numbered list
 - start line with a "." followed by at least one space
 - like bullet point lists you can also indent by three or more spaces than the previous item for sub lists
 - you can also use "1.", "2." etc. Which number is used does not matter. This does not work for sub lists though.

### Formatting
  - use \_text\_ to write text in italic
  - use \*text\* to write text in bold
  - use \+text\+ to write text in underlined
  - use \~text\~ to write text in strike through

### Colors
 - put text between {color:value} and {color} to set a text color
 - put text between {bg-color:value} and {bg-color} to set a text background color
 - instead of "color" and "bg-color" you can also use "c" and "bc"
 - possible color values: "black", "blue", "brown", "cyan", "gold", "gray", "grey", "green", "lightgray", "lightgrey", "darkgray", "darkgrey", "magenta", "red", "teal", "white", "yellow", "pink"
 - you can also specify a color as a hex value like #ffaa00

### Links
 - \[my link label]\[www.wikipedia.de]
 - instead of "\[" and "]" you can also use "(" and ")"

### Images
 - !\[alt text]\[http://path/to/image.png]
 - instead of "\[" and "]" you can also use "(" and ")"
 - the 'alt text' part is optional, so you can also write !\[http://path/to/image.png]
 - you can also specify the width and/or height and/or border size of the image like this: !\[alt text]\[http://path/to/image.png width=20mm height=10mm border=3mm ]
 - when specifying width, height and/or border you can use the units "%" (percent of parent element), "px" (pixels), "mm" (milimeters) and "in" (inches). You can also leave out the unit for "px".
 - if nothing is specified a border of 1mm and a with of 75% are used

### Checkbox lists
 - Start the line with \[x] for a checked box and \[] for an unchecked box)

### Horizontal rule
 - have a line that contains of 3 or more ***
 - instead of * you can also use _, -, = or #

### Tables:
 - use the | character to separate cells
 - place a few - chars underneath the first row to make it a header row
 - place {bg-color:value} or {bc:value} at the beginning of a row to set the row background or at the beginning of a cell to set the cell background

### Escaping special characters
 - Place a \\ before a special character like \* to escape it (ignore for processing)

### Code blocks
 - Place a line of \`\`\` or ´´´ before and after the text to indicate a code block
 - write the format (java or xml) behind the indicator for syntax highlighting. Example "\`\`\`java"

### Table of contents
 - have a line with only the text {TOC}

### Disable processing
 - place {NOMARKDOWN} at the beginning of the text


## Example
![Example](https://github.com/pheyse/BrightMarkdown/blob/master/examples/demo.png "Example")


 
## Change History
 - Version 1.1.0 (2017-11-19)
   - ignore markers for formatting like '_' if they occur within a word
   - set font sizes of headings via method setFontSizeInMM 
 - Version 1.1.1 (2017-12-02)
   - function to read deepest heading level 
 - Version 1.1.2 (2017-12-08)
   - Bug fixes / enhancements: empty lines, nested text format fix
 - Version 1.1.3 (2018-01-05)
   - Bug fix for bullet point level up
 - Version 1.2.0 (2018-01-20)
   - Simplified formatting, list levels by indent, TOC
 - Version 1.3.0 (2018-03-03)
   - added table feature
   - added underline formatting
   - tag to disable parsing
 - Version 1.4.0 (2018-06-21)
   - Images
   - combined numbered and bullet point lists with indents
   - text foreground and background color
   - source code formatting: Java and XML
   - nested formatting of bold, italic, underline and strikethrough in any order
 - Version 1.5.0 (2019-04-02)
   - Background color for table rows and table cells
 - Version 1.5.1 (2019-04-10)
   - Bugfix so that HTML creation also works on Android
 - Version 1.5.2 (2019-08-09)
   - Bugfix so that HTML table styles use their own CSS class
 - Version 1.6.0 (2020-04-17): 
   - Refactoring for smaller Java classes
   - OutputType to allow choice EMBEDDABLE_HTML_CODE to create the only the code inside the body tag
   - Code blocks in lists
   - image name place holders
   - enhanced image tag: percentage size, border and default width, optional alternative text
   - testing: using JUnit 5, renamed test classes
 - Version 1.6.1 (2020-04-26):
   - bugfix for empty input and output type EMBEDDABLE_HTML_CODE
 - Version 1.7.0 (2020-05-25):
   - syntax highlighting for SQL, Kotlin, JavaScript, TypeScript
   - bugfix over-detecting keywords in syntax highlighting
   - bugfix '.' after code block
   - bugfix NPE for unclosed parenthesis
   - special sections in code blocks to format text for highlighting, indicating placeholders or infos
   - double backticks to indicate code blocks