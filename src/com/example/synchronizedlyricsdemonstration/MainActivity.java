package com.example.synchronizedlyricsdemonstration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	int mLineNumber;
	ArrayList<Long> durations;
	ArrayList<String> words;

	
	
	
	private List<ArrayList<String>> resultWords;
	private ArrayList<ArrayList<Long>> resultDurations;
	private List<String> lines;
	private List<String> lineStart;
	private List<Long> lineStartTime;

	private LyricTextView mLyricsTextView;
	private Button mButtonPause;

	public TextView mTextView1, mTextView2, mTextView3, mTextView4, mTextView5, mTextView6, mTextView7,
			mTextViewNextLine;
	
	long timestamp = 30000;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				//继续播放
				Message message = new Message();
				message.what = 0;
				long front = System.currentTimeMillis();
				wapper.setTimestamp(timestamp += 100);
//				Log.e("Main", System.currentTimeMillis() - front + "");
//				Log.i("Main", ">>>>" + (timestamp += 10));
				
				//设置其他文字控件
				sendEmptyMessageDelayed(0, 100); 
				break;
			case 1:
				//暂停
				removeMessages(0);
			default:
				break;
			}

		}
	};
	private LyricTextViewWapper wapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView1 = (TextView) findViewById(R.id.music_textView_lyrics1);
		mTextView2 = (TextView) findViewById(R.id.music_textView_lyrics2);
		mTextView3 = (TextView) findViewById(R.id.music_textView_lyrics3);
		mTextView4 = (TextView) findViewById(R.id.music_textView_lyrics4);
		mTextView5 = (TextView) findViewById(R.id.music_textView_lyrics5);
		mTextView6 = (TextView) findViewById(R.id.music_textView_lyrics6);
		mTextView7 = (TextView) findViewById(R.id.music_textView_lyrics7);
		mLyricsTextView = (LyricTextView) findViewById(R.id.music_lyricTextView);
		mTextViewNextLine = (TextView) findViewById(R.id.music_textView_nextLine);

		mButtonPause = (Button) findViewById(R.id.button_pause);
		mButtonPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(1);
			}
		});

		initData();
<<<<<<< HEAD
		int screenWidth = getScreenWidth();
		mLyricsTextView.setLyricMaxWidth(screenWidth);
=======
>>>>>>> parent of f8f68e0... 修复第一行不能播放的bug
		
		wapper = new LyricTextViewWapper(mLyricsTextView,lineStartTime, lines, resultDurations, resultWords);

		Message message = new Message();
		message.what = 0;
		message.obj = 0;
		handler.sendMessage(message);
		
		
		
		
	}

	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	/**
	 * @Title: initData
	 * @Description:取出歌词文件并装载到对应的集合内
	 *  @param 
	 *  @return void 
	 *  @throws
	 */
	private void initData() {
		// 取出歌词文件
		InputStream is = null;
		try {
			is = getAssets().open("1.lyc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		LyricsFileAnalyse lf = new LyricsFileAnalyse(is);

		resultWords = lf.getResultWords();
		resultDurations = lf.getResultDurations();
		lines = lf.getLines();
		lineStart = lf.getLineStart();
		lineStartTime = lf.getLineStartTime();
	}
	
	
	public void setTimestamp(long timestemp) {
		// 歌词在第几行的临时变量，主要用于查看是否改变了控件内的内容
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
			// Log.e(TAG, "处理正常行");
			long tempLong = 0;
			for (int i = 0; i < words.size(); i++) {

				if (lineTimestemp - tempLong >= durations.get(i)) {
					tempLong += durations.get(i);
				} else {
					// 已经经过了第i-1个字的宽度
					float passedWidth = passedWidths.get(i);
					// 第i个字的速度
					float speed = speeds.get(i);
					// Log.e(TAG, "speed:" + speed);
					// 目前在第i个字上的宽度
					float currentWidth = speed * (lineTimestemp - tempLong);
					// 设定实际宽度
					// Log.e(TAG, "实际宽度："+ passedWidth + currentWidth);
					mLyricTextView.setMaskWidth(passedWidth + currentWidth);
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
