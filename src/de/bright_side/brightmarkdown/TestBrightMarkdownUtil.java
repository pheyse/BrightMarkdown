package de.bright_side.brightmarkdown;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.bright_side.brightmarkdown.BrightMarkdownUtil.PosAndTag;

public class TestBrightMarkdownUtil {

	@Test
	public void test_findNext_firstItem() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		String text = "My tag1 text";
		PosAndTag result = new BrightMarkdownUtil().findNext(text, 0, tags);
		assertEquals(3, result.pos);
		assertEquals("tag1", result.tag);
	}

	@Test
	public void test_findNext_noMatches() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		String text = "My tag99 string";
		PosAndTag result = new BrightMarkdownUtil().findNext(text, 0, tags);
		assertEquals(null, result);
	}

	@Test
	public void test_findNext_secondItem() {
		List<String>tags = Arrays.asList("tag1", "t2", "x");
		String text = "My nice t2 tag1 text";
		PosAndTag result = new BrightMarkdownUtil().findNext(text, 0, tags);
		assertEquals(8, result.pos);
		assertEquals("t2", result.tag);
	}
	
	@Test
	public void test_findNext_longerTagThatOccursFirst() {
		List<String>tags = Arrays.asList("taglong", "tag", "t2", "x");
		String text = "My nice taglong text";
		PosAndTag result = new BrightMarkdownUtil().findNext(text, 0, tags);
		assertEquals(8, result.pos);
		assertEquals("taglong", result.tag);
	}
	
	@Test
	public void test_findNext_longerTagThatOccursSecond() {
		List<String>tags = Arrays.asList("tag", "taglong", "t2", "x");
		String text = "My nice taglong text";
		PosAndTag result = new BrightMarkdownUtil().findNext(text, 0, tags);
		assertEquals(8, result.pos);
		assertEquals("taglong", result.tag);
	}
	

}
