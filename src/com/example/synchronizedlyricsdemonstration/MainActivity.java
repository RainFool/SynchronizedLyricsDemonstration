package com.example.synchronizedlyricsdemonstration;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
	private static final int MUSIC_PLAY = 0;
	private static final int MUSIC_PAUSE = 1;
	
	private static final int REFRESH_DELAY = 100;

	private LyricData lyricData = new LyricData();

	public LyricTextView mLyricsTextView;
	public LinearLayout mLayoutLyricContent,mLayoutControlContent;
	public TextView mTextView1, mTextView2, mTextView3, mTextView4, mTextView5, mTextView6, mTextView7,
			mTextViewNextLine;
	public TextView mTextViewTimer;
	public ImageView mImageViewPlayOrPause,mImageViewMode,mImageViewFavor,mImageViewSing,mImageViewAdd;
	public ProgressBar mProgressBarWider,mProgressBarThinner;
	
	private LyricTextViewWapper wapper;
	
	private int musicState = MUSIC_PLAY;
	int currentLineNumber = -1;

	long timestamp = 40000;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MUSIC_PLAY:
				Message message = new Message();
				message.what = MUSIC_PLAY;
				wapper.setTimestamp(timestamp += REFRESH_DELAY);
				if (currentLineNumber != wapper.getLineNumber()) {
					updateTextViews();
				}
				sendEmptyMessageDelayed(0, REFRESH_DELAY);
				break;
			case MUSIC_PAUSE:
				removeMessages(MUSIC_PLAY);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawableResource(R.drawable.background);

		init();
		
		initData();

		mLyricsTextView.setMaxLyricWidth((int) getResources().getDimension(R.dimen.music_lyric_width));

		wapper = new LyricTextViewWapper(mLyricsTextView, lyricData.lineStartTime, lyricData.lines, lyricData.resultDurations, lyricData.resultWords);

		handler.sendEmptyMessage(MUSIC_PLAY);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(mLayoutControlContent.getVisibility() == View.INVISIBLE) {
				mLayoutControlContent.setVisibility(View.VISIBLE);
				mProgressBarWider.setVisibility(View.INVISIBLE);
				mProgressBarThinner.setVisibility(View.VISIBLE);
				mLayoutLyricContent.setY(mLayoutLyricContent.getY() - mProgressBarThinner.getHeight() - mLayoutControlContent.getHeight());
				mImageViewPlayOrPause.requestFocus();
			} else {
				Log.e(TAG, "enter");
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			//TODO
			break;
		case KeyEvent.KEYCODE_BACK:
			if(mLayoutControlContent.getVisibility() == View.VISIBLE) {
				mLayoutControlContent.setVisibility(View.INVISIBLE);
				mProgressBarThinner.setVisibility(View.INVISIBLE);
				mProgressBarWider.setVisibility(View.VISIBLE);
				mLayoutLyricContent.setY(mLayoutLyricContent.getY() + mProgressBarThinner.getHeight() + mLayoutControlContent.getHeight());
				return true;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
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
		LyricFileAnalyse lf = new LyricFileAnalyse(is);

		lyricData.resultWords = lf.getResultWords();
		lyricData.resultDurations = lf.getResultDurations();
		lyricData.lines = lf.getLines();
		lyricData.lineStart = lf.getLineStart();
		lyricData.lineStartTime = lf.getLineStartTime();
	}

	private void init() {
		mLayoutLyricContent = (LinearLayout) findViewById(R.id.music_lyricContent);
		mLayoutControlContent = (LinearLayout) findViewById(R.id.music_controlContent);
		
		mTextView1 = (TextView) findViewById(R.id.music_textView_lyrics1);
		mTextView2 = (TextView) findViewById(R.id.music_textView_lyrics2);
		mTextView3 = (TextView) findViewById(R.id.music_textView_lyrics3);
		mTextView4 = (TextView) findViewById(R.id.music_textView_lyrics4);
		mTextView5 = (TextView) findViewById(R.id.music_textView_lyrics5);
		mTextView6 = (TextView) findViewById(R.id.music_textView_lyrics6);
		mTextView7 = (TextView) findViewById(R.id.music_textView_lyrics7);
		mLyricsTextView = (LyricTextView) findViewById(R.id.music_lyricTextView);
		mTextViewNextLine = (TextView) findViewById(R.id.music_textView_nextLine);
		
		mTextViewTimer = (TextView) findViewById(R.id.music_control_timer);
		mImageViewPlayOrPause = (ImageView) findViewById(R.id.music_control_playOrPause);
		mImageViewMode = (ImageView) findViewById(R.id.music_control_mode);
		mImageViewFavor = (ImageView) findViewById(R.id.music_control_favor);
		mImageViewSing = (ImageView) findViewById(R.id.music_control_sing);
		mImageViewAdd = (ImageView) findViewById(R.id.music_control_add);
		
		mProgressBarWider = (ProgressBar) findViewById(R.id.music_progressbarWider);
		mProgressBarThinner = (ProgressBar) findViewById(R.id.music_progressbarThinner);
		
		mImageViewPlayOrPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(musicState == MUSIC_PLAY) {
					musicState = MUSIC_PAUSE;
					mImageViewPlayOrPause.setImageResource(R.drawable.music_control_pause);
				}else {
					musicState = MUSIC_PLAY;
					mImageViewPlayOrPause.setImageResource(R.drawable.music_control_play);
				}
			}
		});
	}
	
	private void updateTextViews() {
		currentLineNumber = wapper.getLineNumber();
		mTextView1.setText((currentLineNumber - 7 < 0) ? " " : lyricData.lines.get(currentLineNumber - 7));
		mTextView2.setText((currentLineNumber - 6 < 0) ? " " : lyricData.lines.get(currentLineNumber - 6));
		mTextView3.setText((currentLineNumber - 5 < 0) ? " " : lyricData.lines.get(currentLineNumber - 5));
		mTextView4.setText((currentLineNumber - 4 < 0) ? " " : lyricData.lines.get(currentLineNumber - 4));
		mTextView5.setText((currentLineNumber - 3 < 0) ? " " : lyricData.lines.get(currentLineNumber - 3));
		mTextView6.setText((currentLineNumber - 2 < 0) ? " " : lyricData.lines.get(currentLineNumber - 2));
		mTextView7.setText((currentLineNumber - 1 < 0) ? " " : lyricData.lines.get(currentLineNumber - 1));
		mTextViewNextLine.setText((currentLineNumber + 1 > lyricData.lines.size() - 1) ? new String(" ")
				: lyricData.lines.get(currentLineNumber + 1));
	}
	
	private void pause() {
		Log.e(TAG, "music paused");
		handler.sendEmptyMessage(MUSIC_PAUSE);
	}
	private void resume() {
		Log.e(TAG, "Music resumed");
		timestamp = 0;
	}
	

}
