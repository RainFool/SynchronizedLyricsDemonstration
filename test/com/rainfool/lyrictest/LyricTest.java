package com.rainfool.lyrictest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rainfool.lyric.ILyric;
import com.rainfool.lyric.Lyric;
import com.rainfool.lyric.LyricFactory;
import com.rainfool.lyric.LyricLYCFactory;

import junit.framework.Assert;

public class LyricTest {
	ILyric lyric;
	@Before
	public void setUp() {
		lyric = new LyricLYCFactory().createLyric("http://slave.homed.me:13160/hdfsdownload/music/song/500000040/1.lyc");
	}
	@After
	public void tearDown() {
		lyric = null;
	}
	@Test
	public void testLyric() {
		long timestamp = 0;
		System.out.println(lyric.getLine(timestamp));
//		Assert.assertNotNull(lyric);
	}
}
