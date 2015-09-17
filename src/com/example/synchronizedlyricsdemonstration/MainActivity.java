package com.example.synchronizedlyricsdemonstration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	
	/** 
	* 播放状态。取值：０正在播放，１暂停，２停止
	*/ 
	private static final int MUSIC_PLAY = 0;
	private static final int MUSIC_PAUSE = 1;
	private static final int MUSIC_STOP = 2;

	/** 
	* 刷新延迟
	*/ 
	private static final int REFRESH_DELAY = 100;
	
	/** 
	* 播放模式。取值：0随机播放，1单曲循环，2列表循环
	*/ 
	private static final int PLAY_MODE_RANDOM = 0;
	private static final int PLAY_MODE_SINGLE = 1;
	private static final int PLAY_MODE_LOOP = 2;

	/** 
	* @Fields lyricData : 歌词数据对象
	*/ 
	private LyricData lyricData = new LyricData();

	/** 
	* @Fields wapper : 歌词控件的包装类，主要会设置一些歌词信息
	*/ 
	private LyricTextViewWapper wapper;
	
	/** 
	* @Fields mediaPlayer : 媒体文件播放类
	*/ 
	private MediaPlayer mediaPlayer;
	
	public LyricTextView mLyricsTextView;
	public LinearLayout mLayoutLyricContent, mLayoutControlContent;
	public TextView mTextView1, mTextView2, mTextView3, mTextView4, mTextView5, mTextView6, mTextView7,
			mTextViewNextLine;
	public TextView mTextViewTimer;
	public ImageView mImageViewPlayOrPause, mImageViewMode, mImageViewFavor, mImageViewSing, mImageViewAdd;
	public ProgressBar mProgressBarWider, mProgressBarThinner;


	//歌曲状态，默认为正在播放
	private int musicState = MUSIC_PLAY;
	//播放模式，默认为列表循环
	private int playMode = PLAY_MODE_LOOP;
	//当前播放到哪行
	int currentLineNumber = -1;

	long timestamp = 0;
	//主要用来更新歌词控件和进度条UI
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			
			//waht值为音乐播放状态，对应不同的处理
			switch (msg.what) {
			case MUSIC_PLAY:
				Log.e(TAG, "playing");
				if (!mediaPlayer.isPlaying()) {
					mediaPlayer.start();
				}
				Message message = new Message();
				message.what = MUSIC_PLAY;
				wapper.setTimestamp(timestamp = mediaPlayer.getCurrentPosition());
				mTextViewTimer.setText(formatMusicTime(mediaPlayer.getCurrentPosition()) + "/"
						+ formatMusicTime(mediaPlayer.getDuration()));
				mProgressBarWider.setProgress((int) timestamp);
				mProgressBarThinner.setProgress(mediaPlayer.getCurrentPosition());
				if (currentLineNumber != wapper.getLineNumber()) {
					updateLyricTextViews();
				}
				sendEmptyMessageDelayed(0, REFRESH_DELAY);
				break;
			case MUSIC_PAUSE:
				mediaPlayer.pause();
				removeMessages(MUSIC_PLAY);
				break;
			case MUSIC_STOP:
				mediaPlayer.stop();
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
		initMediaData();

		initLyricData();
		
		seek(210000);
		
		
//		wapper = new LyricTextViewWapper(mLyricsTextView, lyricData.lineStartTime, lyricData.lines,
//				lyricData.resultDurations, lyricData.resultWords);
		wapper = new LyricTextViewWapper(mLyricsTextView, lyricData);
		handler.sendEmptyMessage(MUSIC_PLAY);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			//如果控制部分不可见，则执行以下逻辑
			if (mLayoutControlContent.getVisibility() == View.INVISIBLE) {
				mLayoutControlContent.setVisibility(View.VISIBLE);
				mProgressBarWider.setVisibility(View.INVISIBLE);
				mProgressBarThinner.setVisibility(View.VISIBLE);
				mLayoutLyricContent.setY(mLayoutLyricContent.getY() - mProgressBarThinner.getHeight()
						- mLayoutControlContent.getHeight());
				mImageViewPlayOrPause.requestFocus();
			} else {
				Log.e(TAG, "enter");
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			// TODO
			break;
		case KeyEvent.KEYCODE_BACK:
			//如果控制部分可见，则按下返回键只回到播放界面
			if (mLayoutControlContent.getVisibility() == View.VISIBLE) {
				mLayoutControlContent.setVisibility(View.INVISIBLE);
				mProgressBarThinner.setVisibility(View.INVISIBLE);
				mProgressBarWider.setVisibility(View.VISIBLE);
				mLayoutLyricContent.setY(mLayoutLyricContent.getY() + mProgressBarThinner.getHeight()
						+ mLayoutControlContent.getHeight());
				return true;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mediaPlayer != null) {
			mediaPlayer.release();
		}
	}
	// 初始化歌词数据
	private void initLyricData() {
		// 取出歌词文件
		InputStream is = null;
		try {
			is = getAssets().open("1.lyc");
//			is = getAssets().open("2.lrc");
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
		mLyricsTextView.setMaxLyricWidth((int) getResources().getDimension(R.dimen.music_lyric_width));
		mTextViewNextLine = (TextView) findViewById(R.id.music_textView_nextLine);

		mTextViewTimer = (TextView) findViewById(R.id.music_control_timer);

		mImageViewPlayOrPause = (ImageView) findViewById(R.id.music_control_playOrPause);
		mImageViewPlayOrPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (musicState == MUSIC_PLAY) {
					pause();
					musicState = MUSIC_PAUSE;
					mImageViewPlayOrPause.setImageResource(R.drawable.music_control_play);
				} else {
					start();
					musicState = MUSIC_PLAY;
					mImageViewPlayOrPause.setImageResource(R.drawable.music_control_pause);
				}
			}
		});

		mImageViewMode = (ImageView) findViewById(R.id.music_control_mode);
		mImageViewMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playMode = (playMode + 1) % 3;
				switch (playMode) {
				case PLAY_MODE_RANDOM:
					Log.e(TAG, "mode random");
					mediaPlayer.setLooping(false);
					mImageViewMode.setImageResource(R.drawable.music_control_mode_random);
					break;
				case PLAY_MODE_SINGLE:
					Log.e(TAG, "mode sngle");
					mediaPlayer.setLooping(true);
					mImageViewMode.setImageResource(R.drawable.music_control_mode_single);
					break;
				case PLAY_MODE_LOOP:
					Log.e(TAG, "mode loop");
					mediaPlayer.setLooping(false);
					mImageViewMode.setImageResource(R.drawable.music_control_mode_loop);
					break;
				default:
					break;
				}
			}
		});

		mImageViewFavor = (ImageView) findViewById(R.id.music_control_favor);
		mImageViewFavor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Toast(MainActivity.this).setText("已收藏");
			}
		});
		mImageViewSing = (ImageView) findViewById(R.id.music_control_sing);
		mImageViewAdd = (ImageView) findViewById(R.id.music_control_add);

		mProgressBarWider = (ProgressBar) findViewById(R.id.music_progressbar);
		mProgressBarThinner = (ProgressBar) findViewById(R.id.music_progressbarThinner);
	}

	private void initMediaData() {
//		mediaPlayer = new MediaPlayer();
		mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("http://slave.homed.me:13160/hdfsdownload/music/song/500000019/128.mp3"));
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				stop();
			}
		});
		if (mediaPlayer != null) { 
			mProgressBarWider.setMax(mediaPlayer.getDuration());
			mProgressBarThinner.setMax(mediaPlayer.getDuration());
		}

		try {
			File file = new File(Environment.getExternalStorageDirectory() + "/Music/", "ziyouzizai.mp3");
//			mediaPlayer.setDataSource(file.getPath()); 
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

	private void updateLyricTextViews() {

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

	private void start() {
		Log.e(TAG, "start");
		handler.sendEmptyMessage(0);
	}

	private void pause() {
		Log.e(TAG, "music paused");
		handler.sendEmptyMessage(MUSIC_PAUSE);
	}

	private void resume() {
		Log.e(TAG, "Music resumed");
		timestamp = 0;
	}
	private void stop() {
		Log.e(TAG, "Music Stoped");
		mProgressBarThinner.setProgress(mProgressBarThinner.getMax());
		mProgressBarWider.setProgress(mProgressBarWider.getMax());
		handler.sendEmptyMessage(MUSIC_STOP);
	}
	private void seek(int timestamp) {
		this.timestamp = timestamp;
		mediaPlayer.seekTo(timestamp);
	}

	private String formatMusicTime(long timestamp) {
		int second = (int) (timestamp / 1000 % 60);
		int minute = (int) (timestamp / 1000 / 60);
		return String.format("%02d:%02d", minute, second);
	}

}
