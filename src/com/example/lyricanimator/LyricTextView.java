package com.example.lyricanimator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


/** 
* @ClassName: LyricTextView 
* @Description: 自定义的滚动歌词显示控件
* @author TianYu 田雨
* @date 2015年8月31日 下午3:08:24 
*  在xml中直接声明
*  需要使用一个drawable资源文件，用来规定滚动歌词中遮挡歌词的颜色
*  需要使用setDurations()来设定每个字的持续时间
*  TODO 英文歌词移动的像素值暂定为汉字的1/2
*/
public class LyricTextView extends TextView {

    public interface AnimationSetupCallback {
        public void onSetupAnimation(LyricTextView titanicTextView);
    }

    // callback fired at first onSizeChanged
    private AnimationSetupCallback animationSetupCallback;
    // wave shader coordinates
    private float maskX, maskY;
    // if true, the shader will display the wave
    private boolean sinking;
    // true after the first onSizeChanged
    private boolean setUp;

    private BitmapShader shader;
    // shader matrix
    private Matrix shaderMatrix;
    // wave drawable
    private Drawable wave;
    // (getHeight() - waveHeight) / 2
    private float offsetY;
    
    /** 
    * @Fields durations : 歌词参数，为每一个字的持续时间,单位毫秒
    */ 
    private List<Long> durations;
    
    

    public LyricTextView(Context context) {
        super(context);
        init();
    }

    public LyricTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    

    private void init() {
        shaderMatrix = new Matrix();
    }

    public AnimationSetupCallback getAnimationSetupCallback() {
        return animationSetupCallback;
    }

    public void setAnimationSetupCallback(AnimationSetupCallback animationSetupCallback) {
        this.animationSetupCallback = animationSetupCallback;
    }

    public float getMaskX() {
        return maskX;
    }

    public void setMaskX(float maskX) {
        this.maskX = maskX;
        invalidate();
    }

    public float getMaskY() {
        return maskY;
    }

    public void setMaskY(float maskY) {
        this.maskY = maskY;
        invalidate();
    }

    public boolean isSinking() {
        return sinking;
    }

    public void setSinking(boolean sinking) {
        this.sinking = sinking;
    }

    public boolean isSetUp() {
        return setUp;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        createShader();
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        createShader();
    }

    public List<Long> getDurations() {
    	return durations;
    }
    public void setDurations(List<Long> durations) {
		this.durations = durations;
	}
    

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        createShader();

        if (!setUp) {
            setUp = true;
            if (animationSetupCallback != null) {
                animationSetupCallback.onSetupAnimation(LyricTextView.this);
            }
        }
    }

    /** 
    * @Title: createShader 
    * @Description: 创建歌词显示过程中的遮挡效果，并设定一些值
    * @param 
    * @return void
    * @throws 
    */
    private void createShader() {

        if (wave == null) {
            wave = getResources().getDrawable(R.drawable.wave);
        }

        int waveW = wave.getIntrinsicWidth();
        int waveH = wave.getIntrinsicHeight();

        Bitmap b = Bitmap.createBitmap(waveW, waveH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        c.drawColor(getCurrentTextColor());

        //设置初始像素
        wave.setBounds(0, 0, 1, waveH);
        wave.draw(c);

        shader = new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        getPaint().setShader(shader);

        offsetY = (getHeight() - waveH) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (sinking && shader != null) {

            if (getPaint().getShader() == null) {
                getPaint().setShader(shader);
            }
             shaderMatrix.setTranslate(maskX, maskY + offsetY);
            shader.setLocalMatrix(shaderMatrix);
        } else {
            getPaint().setShader(null);
        }

        super.onDraw(canvas);
    }
}