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
* @Description: �Զ���Ĺ��������ʾ�ؼ�
* @author TianYu ����
* @date 2015��8��31�� ����3:08:24 
*  ��xml��ֱ������
*  ��Ҫʹ��һ��drawable��Դ�ļ��������涨����������ڵ���ʵ���ɫ
*  ��Ҫʹ��setDurations()���趨ÿ���ֵĳ���ʱ��
*  TODO Ӣ�ĸ���ƶ�������ֵ�ݶ�Ϊ���ֵ�1/2
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
    * @Fields durations : ��ʲ�����Ϊÿһ���ֵĳ���ʱ��,��λ����
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
    * @Description: ���������ʾ�����е��ڵ�Ч�������趨һЩֵ
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

        //���ó�ʼ����
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