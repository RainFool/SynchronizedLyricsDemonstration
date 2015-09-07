package com.example.synchronizedlyricsdemonstration;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class LyricTextViewWapper {

	private static final String TAG = "LyricTextViewWapper";

	private LyricTextView mLyricTextView;
	
	//歌曲进行到了哪一行
	private int mLineNumber = -1;

	// 歌曲时间进行到了哪一个时间点
	private long timestamp;
	//已经完全经过的文字的宽度，第i项表示已经经过了i-1个文字
	ArrayList<Float> passedWidths;
	//每个字上，maskWidth应该具有的增长速度
	ArrayList<Float> speeds;

	// 每一行起始时间的集合
	List<Long> lineStartTime;
	// 歌词集合
	List<String> lines;
	// 每一行每一个字的持续时间的集合
	List<ArrayList<Long>> resultDurations;
	// 每一行每一个字集合的集合
	List<ArrayList<String>> resultWords;

	// 每一行每一个字的持续时间
	ArrayList<Long> durations;
	// 每一行每一个字集合
	ArrayList<String> words;

	Context context;
	public LyricTextViewWapper(LyricTextView lyricTextView, List<Long> lineStartTime, List<String> lines,
			List<ArrayList<Long>> resultDurations, List<ArrayList<String>> resultWords) {
		super();
		this.context = context;
		this.lineStartTime = lineStartTime;
		this.lines = lines;
		this.resultDurations = resultDurations;
		this.resultWords = resultWords;
		this.mLyricTextView  = lyricTextView;
	}

	public void setTimestamp(long timestemp) {
		//歌词在第几行的临时变量，主要用于查看是否改变了控件内的内容
		int tempLineNumber = mLineNumber; 
		
		long lineTimestemp = checkToWhichLine(timestemp);


		// 如果行号变化了，则应该重新设定文本和时间值，并且进行耗时的measure操作，存储在集合中
		if (tempLineNumber != mLineNumber) {

			changeLine();

		}

		//处理当前行，并设置mask宽度
		handleLine(lineTimestemp);
	}

	private void handleLine(long lineTimestemp) {

		// 设置没有时间的特殊行
		if (durations.isEmpty()) {
			Log.e(TAG, "这是一个特殊行" + "\nwords:" + words.toString() + "\nDurations:" + durations.toString());
		}
		// 设置完行，现在设置指定行中的已经经过了几个字
		else if (words.size() != durations.size()) {
			Log.e(TAG, "歌词与时间不对应:" + "\nwords:" + words.toString() + "\nDurations:" + durations.toString());
		} else {
//			Log.e(TAG, "处理正常行");
			long tempLong = 0;
			for (int i = 0; i < words.size(); i++) {
				
				if(lineTimestemp - tempLong >=durations.get(i)) {
					tempLong += durations.get(i);
				}else {
					// 已经经过了第i-1个字的宽度
					float passedWidth = passedWidths.get(i);
					// 第i个字的速度
					float speed = speeds.get(i);
//					Log.e(TAG, "speed:" + speed);
					// 目前在第i个字上的宽度
					float currentWidth = speed * (lineTimestemp - tempLong);
					// 设定实际宽度
//					Log.e(TAG, "实际宽度："+ passedWidth + currentWidth);
					mLyricTextView.setProgress((int) (passedWidth + currentWidth));
					break;
				}
			}
		}
	}

	//如果行号变化了，执行以下操作
	private void changeLine() {
		int tempLineNumber;
		// Log.e(TAG, "linenumber:" + tempLineNumber + "|mLinNumber:" +
		// mLineNumber);
		tempLineNumber = mLineNumber;
		mLyricTextView.setLine(lines.get(mLineNumber));
		this.words = resultWords.get(mLineNumber);
		this.durations = resultDurations.get(mLineNumber);
		// 测量经过的文字宽度,并放入集合
		passedWidths = new ArrayList<>();
		speeds = new ArrayList<>();
		StringBuffer tempStringBuffer = new StringBuffer();
		for (int i = 0; i < words.size(); i++) {

			
			float speed= -1;
			if (!durations.isEmpty()) {
				speed = mLyricTextView.getPaint().measureText(words.get(i)) / durations.get(i);
			} else {
				speed = 10;
			}
			speeds.add(speed);

			float passedWidth = mLyricTextView.getPaint().measureText(tempStringBuffer.toString());
			passedWidths.add(passedWidth);
			tempStringBuffer.append(words.get(i));
		}
		// Log.e(TAG, "words:\n" +words.toString() + "\npassed:\n" +
		// passedWidths.toString() + "\nspeed:\n" + speeds.toString());
	}

	// 让时间戳跟每一行的起始时间去比较，如果在这个区间，当前的歌词进行到哪一行也随之确定
	private long checkToWhichLine(long timestamp) {
		// 当前行时间进行到何时
		long lineTimestemp = 0;
		if (lineStartTime.size() != lines.size()) {
			return -1;
		}
		for (int i = 0; i < lineStartTime.size(); i++) {
			if (i == lineStartTime.size() - 1 && timestamp >= lineStartTime.get(i)) {
				lineTimestemp = timestamp - lineStartTime.get(i);
				mLineNumber = i;
				break;
			} else if (timestamp >= lineStartTime.get(i) && timestamp <= lineStartTime.get(i + 1)) {
				lineTimestemp = timestamp - lineStartTime.get(i);
				mLineNumber = i;
				break;
			} else {
			}
			if (i == lineStartTime.size() - 1) {
				Log.e(TAG, "未找到指定行.");
			}
		}
		return lineTimestemp;
	}

}
