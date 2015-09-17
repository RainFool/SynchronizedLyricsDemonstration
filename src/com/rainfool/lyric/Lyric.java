package com.rainfool.lyric;

import java.util.ArrayList;

public class Lyric implements ILyric {
	
	//此文件的获取渠道
	public String url;

	/*
	 * 每一个歌词文件都有特殊行，包含着专辑、作词人等信息，以下属性为存储特殊行信息的成员变量
	 */
	//本歌所在的唱片集
	public String al;
	//歌词作者
	public String ar;
	//此文件的创作者
	public String by;
	//歌词标题
	public String ti;
	//程序的版本
	public String ve;
	
	/*
	 * 接下来是正文信息，以两个二维数组储存数据，timestamps存每一个歌词的时间戳，words存歌词的内容
	 */
	public ArrayList<ArrayList<Long>> timestamps;
	public ArrayList<ArrayList<String>> words;
	
	private int currentRow;
	private int currentColumn;
	
	public Lyric() {
	}
//	public Lyric(ArrayList<ArrayList<Long>> timestamps,ArrayList<ArrayList<String>> words) {
//		this.timestamps = timestamps;
//		this.words = words;
//	}
	
	@Override
	public String getWord(long timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDuration(long timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLine(long startTimestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassWords(long timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getPassDuration(long timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

}
