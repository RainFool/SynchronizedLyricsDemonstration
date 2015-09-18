package com.rainfool.lyric;

import java.util.HashMap;

/*
 * Autor:rainfool
 */
public interface ILyric {
	
	/* 
	 * Get specified word by timestamp
	 */
	public String getWord(long timestamp);
	
	/*
	 * Get specified duration, which has a corresponding word can be got by method getWord
	 */
	public long getDuration(long timestamp);
	
	/*
	 * Get current line
	 */
	public String getLine(long timestamp);
	
	/*
	 * return passed words in current,doesn't count current word
	 */
	public String getPassedWords(long timestamp);
	
	/*
	 * return passed duration in current line,doesn't count passed time of current word
	 */
	public long getPassedDuration(long timestamp);
	
}
