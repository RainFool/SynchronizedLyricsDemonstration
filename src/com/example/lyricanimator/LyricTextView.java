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
* @Description: KTV��ʲ���Ч��
* @author TianYu ����
* @date 2015��9��2�� ����12:07:45 
*  �����������������Ϊ������ʹ��setLineAndDurations������������
*  ���Ź����п���ʹ��pauseSwitch������ͣ�Ͳ��ŵ��л�
*/
public class LyricTextView extends TextView {

	private static final String TAG = "MyTextView";

	private BitmapShader mBitmapShader;

	// @Fields maskWidth :ָ���Թ�����Ӱ
	private float maskWidth;

	// ��Դ�ļ�
	private Drawable shadow;

	// �������
	private String line;
	// ������ļ���
	private ArrayList<String> words;

	// �����ÿ���ֵĳ���ʱ��
	private ArrayList<Long> durations;
	
	//ÿ����ÿ����ǰ����������
	private ArrayList<Float> speed = new ArrayList<>();

	//���Ƹ���ڵ���̬��ʾ���߳�
	private Thread maskThread;
	
	//��ͣtag,trueΪ��ͣ��falseΪ����
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
	* @Description: ��������ռ�Ļ�������
	* @param @param line ���
	* @param @param words ��ʵļ���,��Ӧdurations�е�ÿ��ʱ��
	* @param @param durations �����ÿ���ֵ�ʱ�䣬��Ӧwords�е�ÿ���ַ���
	* @return void
	* @throws InterruptedException
	*/
	public void setLineAndDurations(String line, ArrayList<String> words,ArrayList<Long> durations) {
		this.line = line;
		this.durations = durations;
		this.words = words;

		if (words.size() != durations.size()) {
			Log.e(TAG, "��ʼ�����ʱ�伯�ϲ�����" + "\n��ʼ��ϣ�" + words.toString() + "\nʱ�伯�ϣ�" + durations.toString());
			return;
		}else {
			
		}

		// ͬʱ������������ζ����Ҫ������ǰ�߳�
		if (maskThread != null) {
			maskThread.interrupt();
			maskThread = null;
			Log.e(TAG, "Thread ����һ���߳��Ѿ��ж�");
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
					//����ÿ����
					for (int i = 0; !isInterrupted() && i < words.size(); i++) {
						//ÿ�����и�����õ��ٶ�ÿ10����ˢ��
						for (int j = 0; j < durations.get(i)/10; j++) {
							//��ǰ��ͣ
							if(pauseTag) {
								j--;
							}
							//��ǰ���ڲ���״̬
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
					// ѭ�������ж��߳�
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
