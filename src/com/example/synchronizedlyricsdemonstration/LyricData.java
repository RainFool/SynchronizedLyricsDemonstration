package com.example.synchronizedlyricsdemonstration;

import java.util.ArrayList;

public class LyricData {
	/**
	 * @Fields resultDurations :保存所有的行的时间信息,嵌套的集合存放了对应歌词正文每个字的持续时间
	 */
	public ArrayList<ArrayList<Long>> resultDurations;
	/**
	 * @Fields resultWords :保存所有的歌词正文信息，即用户看见的文字
	 */
	public ArrayList<ArrayList<String>> resultWords;
	/** 
	* @Fields lines :保存所有歌词正文信息，与resultWords不同的是，这个变量存储了每一行歌词作为一个String
	*/
	public ArrayList<String> lines;
	/**
	 * @Fields lineStart:每一行的开始时间，即每一行出现的第一个时间的字符串表达形式
	 */
	public ArrayList<String> lineStart;
	/** 
	* @Fields lineStartTime :每一行歌词开始时间
	*/
	public ArrayList<Long> lineStartTime;

	public LyricData() {
		resultDurations = new ArrayList<>();
		resultWords = new ArrayList<>();
		lines = new ArrayList<>();
		lineStart = new ArrayList<>();
		lineStartTime = new ArrayList<>();
	}
}