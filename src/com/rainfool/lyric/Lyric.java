package com.rainfool.lyric;

import java.util.ArrayList;

import android.util.Log;

public class Lyric implements ILyric {

	private static final String TAG = "Lyric";

	// 此文件的获取渠道
	public String url;

	/*
	 * 每一个歌词文件都有特殊行，包含着专辑、作词人等信息，以下属性为存储特殊行信息的成员变量
	 */
	// 本歌所在的唱片集
	public String al;
	// 歌词作者
	public String ar;
	// 此文件的创作者
	public String by;
	// 歌词标题
	public String ti;
	// 程序的版本
	public String ve;

	/*
	 * 接下来是正文信息，以两个二维数组储存数据，timestamps存每一个歌词的时间戳，words存歌词的内容
	 */
	public ArrayList<ArrayList<Long>> timestamps;
	public ArrayList<ArrayList<String>> words;

	/*
	 * 记录当期的position
	 */
	private int currentRow;
	private int currentColumn;

	public Lyric() {
	}
	// public Lyric(ArrayList<ArrayList<Long>>
	// timestamps,ArrayList<ArrayList<String>> words) {
	// this.timestamps = timestamps;
	// this.words = words;
	// }

	@Override
	public String getWord(long timestamp) {
		findOutPosition(timestamp);
		return words.get(currentRow).get(currentColumn);
	}

	@Override
	public long getDuration(long timestamp) {
		findOutPosition(timestamp);
		return timestamps.get(currentRow).get(currentColumn+1) - timestamps.get(currentRow).get(currentColumn);
	}

	@Override
	public String getLine(long timestamp) {
		findOutPosition(timestamp);
		ArrayList<String> rowWords = words.get(currentRow);
		StringBuffer result = new StringBuffer();
		for(String s : rowWords) {
			result.append(s);
		}
		return result.toString();
	}

	@Override
	public String getPassedWords(long timestamp) {
		findOutPosition(timestamp);
		StringBuffer result = new StringBuffer();
		for(int i = 0;i < currentColumn;i++) {
			result.append(words.get(currentRow).get(i));
		}
		return result.toString();
	}

	@Override
	public long getPassedDuration(long timestamp) {
		findOutPosition(timestamp);
		long result = 0;
		
		ArrayList<Long> rowTimestamps = timestamps.get(currentRow);
		long startTimestamp = rowTimestamps.get(0);
		
		if(currentColumn == 0) {
			return timestamp - startTimestamp;
		}
		int i = 1;
		for(;i < currentColumn;i ++) {
			result += (rowTimestamps.get(i) - rowTimestamps.get(i - 1));
			
		}
		result += (timestamp - rowTimestamps.get(i));
		return result;
	}

	@Override
	public String toString() {
		return "Lyric [url=" + url + ", al=" + al + ", ar=" + ar + ", by=" + by + ", ti=" + ti + ", ve=" + ve
				+ ", timestamps=" + timestamps + ", words=" + words + ", currentRow=" + currentRow + ", currentColumn="
				+ currentColumn + "]";
	}

	/*
	 * O(n*logn)
	 */
	private void findOutPosition(long timestamp) {
		for (int i = 0; i < timestamps.size(); i++) {
			ArrayList<Long> rowTimestamp = timestamps.get(i);
			if (i == timestamps.size() - 1 && timestamp > rowTimestamp.get(0)) {
				currentRow = i;
				currentColumn = findOutColumn(timestamp, rowTimestamp);
			} else if (timestamp >= rowTimestamp.get(0) && timestamp < timestamps.get(i + 1).get(0)) {
				currentRow = i;
				currentColumn = findOutColumn(timestamp, rowTimestamp);
				break;
			}
		}
	}

	private int findOutColumn(long timestamp, ArrayList<Long> rowTimestamp) {
		for (int j = 0; j < rowTimestamp.size(); j++) {
			if (j == rowTimestamp.size() - 1 && timestamp > rowTimestamp.get(j)) {
				Log.e(TAG, "Out of bound of Timestamp size:" + j);
				return -1;
			} else if (timestamp >= rowTimestamp.get(j) && timestamp < rowTimestamp.get(j + 1)) {
				return j;
			}
		}
		return -1;
	}
}
