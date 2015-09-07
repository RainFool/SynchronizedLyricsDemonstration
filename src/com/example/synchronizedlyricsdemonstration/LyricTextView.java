package com.example.synchronizedlyricsdemonstration;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TextView;

public class LyricTextView extends TextView {
	private Shader shader;
	private Matrix shaderMatrix = new Matrix();

	private int activeColor;

	private int inactiveColor;

	private int progress;
	
	private String line;
	
	private int maxLyricWidth;
	
	public LyricTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LyricTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LyricTextView(Context context) {
		super(context);
		init();
	}

	private void init() {

		this.setColors(Color.parseColor("#0eecef"), Color.parseColor("#f9df00"));
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
	 *            in pixel
	 */
	public void setProgress(int progress) {
		this.progress = progress;
		if (progress > this.getWidth()) {
			scrollTo((int) (progress - this.getWidth()), 0);
		}
		invalidate();
	}

	public void setLine(String line) {
		this.line = line;
		this.setText(line);
	}
	
	public void setMaxLyricWidth(int maxLyricWidth) {
		this.maxLyricWidth = maxLyricWidth;
	}

	/**
	 * Create the shader draw the wave with current color for a background repeat the bitmap
	 * horizontally, and clamp colors vertically
	 */
	private void createShader() {
		shader = new LinearGradient(0, 0,1, 0, activeColor, inactiveColor, TileMode.CLAMP);
		getPaint().setShader(shader);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		String text = this.getText().toString();
		float width = getPaint().measureText(text) + getPaddingLeft() + getPaddingRight();

		if(width > maxLyricWidth) {
			width = maxLyricWidth;
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

		super.onDraw(canvas);
	}
}
