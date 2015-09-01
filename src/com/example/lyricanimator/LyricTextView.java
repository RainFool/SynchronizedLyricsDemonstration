package com.example.lyricanimator;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class LyricTextView extends TextView {

	private static final String TAG = "MyTextView";

	BitmapShader mBitmapShader;

	// @Fields maskWidth :指定略过的阴影
	float maskWidth;

	// 资源文件
	Drawable shadow;

	// 歌词正文
	String line;
	// 歌词正文集合
	ArrayList<String> words;

	// 歌词中每个字的持续时间
	ArrayList<Long> durations;
	
	//每个字每毫秒前进多少像素
	ArrayList<Float> speed = new ArrayList<>();

	Thread maskThread;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				setMaskWidth(Float.parseFloat((String.valueOf(msg.obj))));
			}
		};
	};

	public LyricTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LyricTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LyricTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		shadow = getResources().getDrawable(R.drawable.shadow);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		String text = this.getText().toString();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Log.e(TAG, "MaskWidth:"+ maskWidth);
		Matrix shaderMatrix = new Matrix();
		shaderMatrix.setTranslate(0, shadow.getIntrinsicHeight());
		mBitmapShader.setLocalMatrix(shaderMatrix);
		super.onDraw(canvas);
	}

	public float getMaskWidth() {
		return maskWidth;
	}

	public void setMaskWidth(float maskWidth) {
		this.maskWidth = maskWidth;
		createShader((int) maskWidth);
		invalidate();
	}

	public void setMaskPercent(float percent) {
		// this.maskWidth =
	}

	public void setLine(String line) {
		this.line = line;
	}

	public void setDurations(ArrayList<Long> durations) {
		this.durations = durations;
	}

	public void setLineAndDuratios(String line, ArrayList<String> words,ArrayList<Long> durations) {
		this.line = line;
		this.durations = durations;
		this.words = words;

		if (words.size() != durations.size()) {
			Log.e(TAG, "歌词集合与时间集合不符：" + "\n歌词集合：" + words.toString() + "\n时间集合：" + durations.toString());
			return;
		}else {
			
		}

		// 同时设置这两个意味着需要结束当前线程
		if (maskThread != null) {
			maskThread.interrupt();
			maskThread = null;
			Log.e(TAG, "Thread ：上一个线程已经中断");
		}

		this.setText(line);
		measureWord();
		initMaskThread();
		if (maskThread != null) {
			maskThread.start();
		}

	}

	private void initMaskThread() {
		maskThread = new Thread() {
			float t = 0;
			@Override
			public void run() {
				Log.e(TAG, "speed:" + speed);
				try {
					//处理每个词
					for (int i = 0; !isInterrupted() && i < words.size(); i++) {
						//每个词中根据算好的速度每10毫秒刷新
						for (int j = 0; j < durations.get(i)/10; j++) {
							t += speed.get(i) * 10;
							Log.e(TAG, "t:" + t);
							Thread.sleep(10);
							Message msg = new Message();
							msg.what = 0;
							msg.obj = t;
							handler.sendMessage(msg);
						}

					}
					// 循环结束中断线程
					interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		};
	}

	private void createShader(int width) {

		Bitmap bitmap = Bitmap.createBitmap(1280, shadow.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(getCurrentTextColor());
		shadow.setBounds(0, 0, width, 100);
		shadow.draw(canvas);

		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
		getPaint().setShader(mBitmapShader);

	}
	
	private void measureWord() {
		for (int i = 0; i < words.size(); i++) {
			float pix = getPaint().measureText(words.get(i));
			speed.add(pix / durations.get(i));
		}
	}
	class LyricAsyncTask extends AsyncTask<ArrayList<Float>, Float, Void> {

		@Override
		protected Void doInBackground(ArrayList<Float>... params) {
			
			return null;
		}
		
	}
}
