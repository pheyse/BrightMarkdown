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

