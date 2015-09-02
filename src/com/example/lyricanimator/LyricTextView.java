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

/** 
* @ClassName: LyricTextView 
* @Description: KTV歌词播放效果
* @author TianYu 田雨
* @date 2015年9月2日 下午12:07:45 
*  本类接收两个集合作为参数，使用setLineAndDurations方法设置数据
*  播放过程中可以使用pauseSwitch进行暂停和播放的切换
*/
public class LyricTextView extends TextView {

	private static final String TAG = "MyTextView";

	private BitmapShader mBitmapShader;

	// @Fields maskWidth :指定略过的阴影
	private float maskWidth;

	// 资源文件
	private Drawable shadow;

	// 歌词正文
	private String line;
	// 歌词正文集合
	private ArrayList<String> words;

	// 歌词中每个字的持续时间
	private ArrayList<Long> durations;
	
	//每个字每毫秒前进多少像素
	private ArrayList<Float> speed = new ArrayList<>();

	//控制歌词遮挡动态显示的线程
	private Thread maskThread;
	
	//暂停tag,true为暂停，false为继续
	private boolean pauseTag;

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
		shadow = getResources().getDrawable(R.drawable.music_shadow);
		createShader(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		String text = this.getText().toString();
		float width = getPaint().measureText(text) + getPaddingLeft() + getPaddingRight();
		
		if(width > this.getWidth()) {
			setMeasuredDimension((int) width, getMeasuredHeight() );
			this.setWidth((int) width);
		}
		Log.e(TAG, "measureWidth:"+width+"textView width:"+this.getWidth()+"|textview height:" + this.getHeight()); 
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
//		this.setX(0);
		Log.e(TAG, "textView:"+ this.getX()); 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		

		// Log.e(TAG, "MaskWidth:"+ maskWidth);
		Matrix shaderMatrix = new Matrix();
		shaderMatrix.setTranslate(0, shadow.getIntrinsicHeight());
		mBitmapShader.setLocalMatrix(shaderMatrix);
	}

	public float getMaskWidth() {
		return maskWidth;
	}

	public void setMaskWidth(float maskWidth) {
		this.maskWidth = maskWidth;
		if(maskWidth < this.getWidth()) {
			
			createShader((int) maskWidth);
			invalidate();
		}else {
			createShader(this.getWidth());
			scrollTo((int) (maskWidth - this.getWidth()), 0);
			invalidate(); 
		}
		
	}


	public void setLine(String line) {
		this.line = line;
	}

	public void setDurations(ArrayList<Long> durations) {
		this.durations = durations;
	}
	
	public void pauseSwitch() {
		if(pauseTag) {
			pauseTag = false;
		}else {
			pauseTag = true;
		}
	}

	/** 
	* @Title: setLineAndDuratios 
	* @Description: 设置这个空间的基本数据
	* @param @param line 歌词
	* @param @param words 歌词的集合,对应durations中的每个时间
	* @param @param durations 歌词中每个字的时间，对应words中的每个字符串
	* @return void
	* @throws InterruptedException
	*/
	public void setLineAndDurations(String line, ArrayList<String> words,ArrayList<Long> durations) {
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
		measureWords();
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
				try {
					//处理每个词
					for (int i = 0; !isInterrupted() && i < words.size(); i++) {
						//每个词中根据算好的速度每10毫秒刷新
						for (int j = 0; j < durations.get(i)/10; j++) {
							//当前暂停
							if(pauseTag) {
								j--;
							}
							//当前处于播放状态
							else {
								
								t += speed.get(i) * 10;
							}
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

		Bitmap bitmap = Bitmap.createBitmap(1920, shadow.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(getCurrentTextColor());
		shadow.setBounds(0, 0, width, 100);
		shadow.draw(canvas);

		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
		getPaint().setShader(mBitmapShader);

	}
	
	private void measureWords() {
		for (int i = 0; i < words.size(); i++) {
			float pix = getPaint().measureText(words.get(i));
			speed.add(pix / durations.get(i));
		}
	}
	

}
