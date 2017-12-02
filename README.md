# BrightMarkdown
BrightMarkdown is a light weight java library to process markdown text.
Created 2017 by Philip Heyse

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
 - code blocks

## Usage
```java
String input = "# Title\n* item 1\n* item 2\n* item 3\n\nThis text is **bold**.";
String html = new BrightMarkdown().createHTML(input);
```
 
## Change History
 - Version 1.1.0 (2017-11-19)
   - ignore markers for formatting like '_' if they occur within a word
   - set font sizes of headings via method setFontSizeInMM 
 - Version 1.1.1 (2017-12-02)
   - function to read deepest heading level 
 
 

