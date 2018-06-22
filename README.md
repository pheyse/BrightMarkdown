# BrightMarkdown
BrightMarkdown is a light weight java library to process markdown text.
Created 2017-2018 by Philip Heyse

## License
Apace V2

## Features
 - headings
 - bullet point lists
 - numbered lists
 - links
 - formatting: bold, italic, strike through
 - checkbox lists
 - horizontal rules
 - code blocks incl. syntax highlighting for Java and XML
 - images
 - foregorund and background color
 

## Usage
```java
String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is *bold*.";
String html = new BrightMarkdown().createHTML(input);

String documentation = new BrightMarkdown().getDocumentationAsHTML();
```
 
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
 
 

