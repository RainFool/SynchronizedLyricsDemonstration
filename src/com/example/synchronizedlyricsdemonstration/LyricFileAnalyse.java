package com.example.synchronizedlyricsdemonstration;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.Log;

/** 
* @ClassName: LyricsFileAnalyse 
* @Description: 将歌词文件转换为一个存储歌词的集合，和一个存储对应歌词中每个字持续时间的集合
* @author TianYu 田雨
* @date 2015年8月31日 下午6:08:47 
*  在构造方法中传入File类，然后调用两个getResult...方法即可
*/
public class LyricFileAnalyse {
	
	private static final String TAG = "LyricsFileAnalyse";

	/**
	 * @Fields lyricFile : 调用方法时传入的文件
	 */
	File lyricFile;

	LyricData lyricData = new LyricData();

	public LyricFileAnalyse(File lyricFile) {
		this.lyricFile = lyricFile;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(lyricFile),"UTF-8"));
			lyricFileAnalyse(br);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		setLines();
	}
	
	public LyricFileAnalyse(InputStream is) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			lyricFileAnalyse(br);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		setLines();
	}

	public ArrayList<ArrayList<Long>> getResultDurations() {
		return lyricData.resultDurations;
	}

	public ArrayList<ArrayList<String>> getResultWords() {
		return lyricData.resultWords;
	}

	public ArrayList<String> getLineStart() {
		return lyricData.lineStart;
	}

	public ArrayList<String> getLines() {
		return lyricData.lines;
	}

	public ArrayList<Long> getLineStartTime() {
		ArrayList<String> start = getLineStart();
		lyricData.lineStartTime = new ArrayList<>();
		for(String i : start) {
			lyricData.lineStartTime.add(timeParse(i));
		}
		return lyricData.lineStartTime; 
	}


	/** 
	* @Title: lyricFileAnalyse 
	* @Description: 将文件中的歌词和时间提取到两个集合中，同时将每一行的开始时间也提取出
	* @param @param lyricFile
	* @return void
	* @throws 
	*/
	public void lyricFileAnalyse(BufferedReader br) {
		try {
			lyricData.resultDurations = new ArrayList<>();
			lyricData.resultWords = new ArrayList<>();
			
			// 从文件中取出的原始的一行数据
			String rawLine = null;

			while ((rawLine = br.readLine()) != null) {

				// 每一行处理后存放时间的集合
				ArrayList<Long> lineDurations = new ArrayList<>();
				// 每一行处理后存放正文的集合
				ArrayList<String> lineWords = new ArrayList<>();
				
				if(rawLine.contains("<") && rawLine.contains(">")) {
					lineHandleLRC(rawLine, lineDurations, lineWords);
				}else {
					
					lineHandleLYC(rawLine, lineDurations, lineWords);
				}
				
				lyricData.resultDurations.add(lineDurations);
				lyricData.resultWords.add(lineWords);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//LYC歌词文件处理
	private void lineHandleLYC(String rawLine, ArrayList<Long> lineDurations, ArrayList<String> lineWords) {

		String[] data = rawLine.split("\\|");

		// 如果没有“|”符号,且有数据，就将时间设为起始时间，并将字符串内的"["去掉返回
		if (data.length == 1) {
			lyricData.lineStart.add("[00:00:00]");
			lineWords.add(rawLine.replace("[", "").replace("]", ""));
			return;
		}

		// 以下是正常行的处理，先加入每一行的起始时间,并将时间的“[”和“]”去掉
		lyricData.lineStart.add(data[0].replace("[", "").replace("]", ""));

		// 循环处理正常数据，一行被分割后，数组中偶数为时间，先存放到临时集合里,奇数为正文存放在lineWords中
		ArrayList<String> times = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {

			if (i % 2 == 0) {
				times.add(data[i]);
			} else if (i % 2 == 1) {
				lineWords.add(data[i]);
			}
		}

		// 正文处理完毕，现在将临时集合中的数据转换为每个字的持续时间
		long[] nums = new long[times.size()];
		for (int i = 0; i < times.size(); i++) {
			nums[i] = timeParse(times.get(i));
		}
		for(int i = 1;i <nums.length; i ++) {
			lineDurations.add(nums[i] - nums[i-1]);
		}
	}

	//LRC歌词文件处理
		private void lineHandleLRC(String rawLine,  ArrayList<Long> lineDurations, ArrayList<String> lineWords) {
			char[] data = rawLine.toCharArray();
			String lineStart = null;
			for(int i = 0;i < data.length; i ++) {
				if("[".equals(data[i] + "")) {
					for(int j = i + 1;j < data.length; j ++) {
						if("]".equals(data[j] + "")) {
							lineStart = rawLine.substring(i, j+1);
							lyricData.lineStart.add(lineStart);
							String temp = lineStart.replace("[", "").replace("]", "");
							lyricData.lineStartTime.add(Long.parseLong(temp.split(",")[0]));
							i = j;
							break;
						}
					}
				}else if("<".equals(data[i] + "")) {
					for(int j = i + 1;j < data.length;j ++) {
						if(">".equals(data[j] + "")) {
							String temp = rawLine.substring(i,j + 1);
							lineDurations.add(Long.parseLong(temp.split(",")[1]));
							i = j;
							break;
						}
					}
				} else {
					lineWords.add(data[i] + "");
				}
				
			}
		}
	
	// 将时间字符串转换为long型数值
	private long timeParse(String time) {

		time = time.replace("[", "").replace("]", "");
		String[] ss = time.split(":");
		if (ss.length != 3) {
			Log.e(TAG, "解析文件时发现时间格式不对！");
			return -1;
		}
		long result = Long.parseLong(ss[0]) * 60 * 1000 + Long.parseLong(ss[1]) * 1000 + Long.parseLong(ss[2]);
		return result;
	}
	
	//初始化lines
	private void setLines() {
		lyricData.lines = new ArrayList<>();
		for(int i = 0;i < lyricData.resultWords.size();i ++) {
			StringBuffer sb = new StringBuffer();
			for(int j = 0; j < lyricData.resultWords.get(i).size(); j ++) {
				sb.append(lyricData.resultWords.get(i).get(j));
			}
			lyricData.lines.add(sb.toString());
		}
	}
}
