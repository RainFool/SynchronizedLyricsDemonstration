package com.example.synchronizedlyricsdemonstration;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

	private static final String TAG = "LyricTextView";

	private BitmapShader mBitmapShader;

	// @Fields maskWidth :指定略过的阴影
	private float maskWidth;

	// 资源文件
	private Drawable shadow;
	Bitmap bitmap;
	Canvas canvas;

	// 歌词正文
	private String line;
	
	//每一行的开始时间
	private ArrayList<Long> lineStartTime;
	
	//最大尺寸
	private int lyricMaxWidth;

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
		bitmap = Bitmap.createBitmap(1920, shadow.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
		createShader(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		String text = this.getText().toString();
		float width = getPaint().measureText(text) + getPaddingLeft() + getPaddingRight();
		if(width > lyricMaxWidth) {
			width = lyricMaxWidth;
		}
		setMeasuredDimension((int) width, getMeasuredHeight());
		this.setWidth((int) width);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		 Log.e(TAG, "MaskWidth:"+ maskWidth + "|this.getWidth:" + this.getWidth());
		Matrix shaderMatrix = new Matrix();
		shaderMatrix.setTranslate(0, shadow.getIntrinsicHeight());
		mBitmapShader.setLocalMatrix(shaderMatrix); 
	}
 
	public float getMaskWidth() {
		return maskWidth;
	}
	
	/** 
	* @Title: setMaskWidth 
	* @Description: TODO根据宽度绘制遮挡效果
	* @param @param maskWidth
	* @return void
	* @throws 
	*/
	public void setMaskWidth(float maskWidth) {
		this.maskWidth = maskWidth;
//		Log.e(TAG, "this width:"+ this.getWidth());
		if(maskWidth < this.getWidth()) {
			createShader((int) maskWidth);
			invalidate(); 
//			Log.e(TAG, "maskWidth:" + maskWidth + "|this.width:" + this.getWidth());
		}else {
			createShader((int) maskWidth);
			scrollTo((int) (maskWidth - this.getWidth()), 0); 
			invalidate(); 
		}
		
	}


	public void setLine(String line) {
		this.line = line;
		this.setText(line);
	}
	
	public void setLineStartTime(ArrayList<Long> lineStartTime) {
		this.lineStartTime = lineStartTime;
	}

	public int getLyricMaxWidth() {
		return lyricMaxWidth;
	}

	public void setLyricMaxWidth(int lyricMaxWidth) {
		this.lyricMaxWidth = lyricMaxWidth;
	}

	private void createShader(int width) {
		canvas.drawColor(getCurrentTextColor());
		shadow.setBounds(0, 0, width, 100);
		shadow.draw(canvas);
		
		getPaint().setShader(mBitmapShader);
	}
	

}
