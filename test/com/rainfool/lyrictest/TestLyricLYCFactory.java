package com.rainfool.lyrictest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rainfool.lyric.ILyric;
import com.rainfool.lyric.Lyric;
import com.rainfool.lyric.LyricFactory;
import com.rainfool.lyric.LyricLYCFactory;

import junit.framework.Assert;

public class TestLyricLYCFactory {
	LyricFactory factory;
	@Before
	public void setUp() {
		factory = new LyricLYCFactory();
	}
	@After
	public void tearDown() {
		factory = null;
	}
	@Test
	public void testLyric() {
		ILyric lyric = factory.createLyric("http://slave.homed.me:13160/hdfsdownload/music/song/500000040/1.lyc");
		Assert.assertNotNull(lyric);
		System.out.println(lyric.toString());
	}
	
}
