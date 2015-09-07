package com.example.synchronizedlyricsdemonstration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private List<ArrayList<String>> resultWords;
	private ArrayList<ArrayList<Long>> resultDurations;
	private List<String> lines;
	private List<String> lineStart;
	private List<Long> lineStartTime;

	private LyricTextView mLyricsTextView;
	private Button mButtonPause;

	public TextView mTextView1, mTextView2, mTextView3, mTextView4, mTextView5, mTextView6, mTextView7,
			mTextViewNextLine;

	int currentLineNumber = -1;

	long timestamp = 40000;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				Message message = new Message();
				message.what = 0;
				wapper.setTimestamp(timestamp += 100);
				if (currentLineNumber != wapper.getLineNumber()) {
					updateTextViews();
				}
				sendEmptyMessageDelayed(0, 100);
			}
		}
	};
	private LyricTextViewWapper wapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawableResource(R.drawable.background);

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
			}
		});

		initData();

		mLyricsTextView.setMaxLyricWidth(1180);

		wapper = new LyricTextViewWapper(mLyricsTextView, lineStartTime, lines, resultDurations, resultWords);

		Message message = new Message();
		message.what = 0;
		message.obj = 0;
		handler.sendMessage(message);

	}
	
	/**
	 * @Title: initData @Description:取出歌词文件并装载到对应的集合内 @param @return
	 * void @throws
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

	private void updateTextViews() {
		currentLineNumber = wapper.getLineNumber();
		mTextView1.setText((currentLineNumber - 7 < 0) ? " " : lines.get(currentLineNumber - 7));
		mTextView2.setText((currentLineNumber - 6 < 0) ? " " : lines.get(currentLineNumber - 6));
		mTextView3.setText((currentLineNumber - 5 < 0) ? " " : lines.get(currentLineNumber - 5));
		mTextView4.setText((currentLineNumber - 4 < 0) ? " " : lines.get(currentLineNumber - 4));
		mTextView5.setText((currentLineNumber - 3 < 0) ? " " : lines.get(currentLineNumber - 3));
		mTextView6.setText((currentLineNumber - 2 < 0) ? " " : lines.get(currentLineNumber - 2));
		mTextView7.setText((currentLineNumber - 1 < 0) ? " " : lines.get(currentLineNumber - 1));
		mTextViewNextLine.setText((currentLineNumber + 1 > lines.size() - 1) ? new String(" ")
				: lines.get(currentLineNumber + 1));
	}

}
