package com.example.synchronizedlyricsdemonstration;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * @ClassName: LyricTextView
 * @Description: KTV歌词播放效果
 * @author TianYu 田雨
 * @date 2015年9月2日 下午12:07:45 本类接收两个集合作为参数，使用setLineAndDurations方法设置数据
 *       播放过程中可以使用pauseSwitch进行暂停和播放的切换
 */
public class LyricTextView_back extends TextView {

	public LyricTextView_back(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = "LyricTextView";
	private Shader shader;
	private Matrix shaderMatrix = new Matrix();

	private int activeColor;

	private int inactiveColor;

	private int progress;

	// @Fields maskWidth :指定略过的阴影
	private float maskWidth;

	// 歌词正文
	private String line;
	// 歌词正文集合
	private ArrayList<String> words;

	// 歌词中每个字的持续时间
	private ArrayList<Long> durations;


	// 每一行的开始时间
	private ArrayList<Long> lineStartTime;

	// 每一行显示的最大宽度
	private int LyricMaxWidth;

//	public LyricTextView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		init();
//	}
//
//	public LyricTextView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		init();
//	}
//
//	public LyricTextView(Context context) {
//		super(context);
//		init();
//	}

	private void init() {

		this.setColors(Color.parseColor("#0eecef"), Color.parseColor("#f9df00"));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		String text = this.getText().toString();
		float width = getPaint().measureText(text) + getPaddingLeft() + getPaddingRight();

		if(width > LyricMaxWidth) {
			width = LyricMaxWidth;
		}
		
		setMeasuredDimension((int) width, getMeasuredHeight());
		this.setWidth((int) width);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (shader == null) {
			createShader();
		}
		shaderMatrix.setTranslate(progress, 0);

		// assign matrix to invalidate the shader
		shader.setLocalMatrix(shaderMatrix);
		Log.e(TAG, "onDraw");
		super.onDraw(canvas);
	}

	public void setColors(int active, int inactive) {
		this.activeColor = active;
		this.inactiveColor = inactive;
		shader = null;
		invalidate();
	}

	/**
	 * 
	 * @param progress
	 *  float pixel
	 */
	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}

	/**
	 * @Title: setMaskWidth @Description: TODO根据宽度绘制遮挡效果 @param @param
	 * maskWidth @return void @throws
	 */
	public void setMaskWidth(float maskWidth) {
		this.maskWidth = maskWidth;
		// Log.e(TAG, "this width:"+ this.getWidth());
		if (maskWidth < this.getWidth()) {
			setProgress((int)maskWidth);
			invalidate();
			// Log.e(TAG, "maskWidth:" + maskWidth + "|this.width:" +
			// this.getWidth());
		} else {
			setProgress((int)maskWidth);
			scrollTo((int) (maskWidth - this.getWidth()), 0);
			invalidate();
		}

	}

	public int getLyricMaxWidth() {
		return LyricMaxWidth;
	}

	public void setLyricMaxWidth(int lyricMaxWidth) {
		LyricMaxWidth = lyricMaxWidth;
	}

	public void setLine(String line) {
		this.line = line;
		this.setText(line);
		invalidate();
	}

	/**
	 * Create the shader draw the wave with current color for a background repeat the bitmap
	 * horizontally, and clamp colors vertically
	 */
	private void createShader() {
		shader = new LinearGradient(0, 0,1, 0, activeColor, inactiveColor, TileMode.CLAMP);
		getPaint().setShader(shader);
	}

}
