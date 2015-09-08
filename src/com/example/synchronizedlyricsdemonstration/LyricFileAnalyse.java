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

	/**
	 * @Fields resultDurations :保存所有的行的时间信息,嵌套的集合存放了对应歌词正文每个字的持续时间
	 */
	ArrayList<ArrayList<Long>> resultDurations;

	/**
	 * @Fields resultWords :保存所有的歌词正文信息，即用户看见的文字
	 */
	ArrayList<ArrayList<String>> resultWords;
	
	/** 
	* @Fields lines :保存所有歌词正文信息，与resultWords不同的是，这个变量存储了每一行歌词作为一个String
	*/ 
	ArrayList<String> lines;

	/**
	 * @Fields lineStart:每一行的开始时间，即每一行出现的第一个时间的字符串表达形式
	 */
	ArrayList<String> lineStart = new ArrayList<>();
	
	/** 
	* @Fields lineStartTime :每一行歌词开始时间
	*/ 
	ArrayList<Long> lineStartTime;

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
		this.lyricFile = lyricFile;
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
		return resultDurations;
	}

	public ArrayList<ArrayList<String>> getResultWords() {
		return resultWords;
	}

	public ArrayList<String> getLineStart() {
		return lineStart;
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public ArrayList<Long> getLineStartTime() {
		ArrayList<String> start = getLineStart();
		lineStartTime = new ArrayList<>();
		for(String i : start) {
			lineStartTime.add(timeParse(i));
		}
		return lineStartTime;
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
			resultDurations = new ArrayList<>();
			resultWords = new ArrayList<>();
			
			// 从文件中取出的原始的一行数据
			String rawLine = null;

			while ((rawLine = br.readLine()) != null) {

				// 每一行处理后存放时间的集合
				ArrayList<Long> lineDurations = new ArrayList<>();
				// 每一行处理后存放正文的集合
				ArrayList<String> lineWords = new ArrayList<>();
				lineHandle(rawLine, lineDurations, lineWords);
				
				resultDurations.add(lineDurations);
				resultWords.add(lineWords);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void lineHandle(String rawLine, ArrayList<Long> lineDurations, ArrayList<String> lineWords) {

		String[] data = rawLine.split("\\|");

		// 如果没有“|”符号,且有数据，就将时间设为起始时间，并将字符串内的"["去掉返回
		if (data.length == 1) {
			lineStart.add("[00:00:00]");
			lineWords.add(rawLine.replace("[", "").replace("]", ""));
			return;
		}

		// 以下是正常行的处理，先加入每一行的起始时间,并将时间的“[”和“]”去掉
		lineStart.add(data[0].replace("[", "").replace("]", ""));

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
		lines = new ArrayList<>();
		for(int i = 0;i < resultWords.size();i ++) {
			StringBuffer sb = new StringBuffer();
			for(int j = 0; j < resultWords.get(i).size(); j ++) {
				sb.append(resultWords.get(i).get(j));
			}
			lines.add(sb.toString());
		}
	}
}
