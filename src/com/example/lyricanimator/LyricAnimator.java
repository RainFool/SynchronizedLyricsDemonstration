package com.example.lyricanimator;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

public class LyricAnimator {

	private static final String TAG = "LyricAnimator";

	private AnimatorSet animatorSet;
	private Animator.AnimatorListener animatorListener;

	public Animator.AnimatorListener getAnimatorListener() {
		return animatorListener;
	}

	public void setAnimatorListener(Animator.AnimatorListener animatorListener) {
		this.animatorListener = animatorListener;
	}

	public void start(final LyricTextView_back textView) {

		List<Long> durations = textView.getDurations();
		// 获取textView的text值,转化成为数组
		String text = textView.getText().toString();
		AnimatorSet set1 = createAnimatorSet(textView, durations, text);
		set1.start();
	}

	
	public void start(final LyricTextView_back textView, final List<String> lines, List<ArrayList<Long>> resultDurations) {
		textView.setText(lines.get(11));
		textView.setDurations(resultDurations.get(11));
		AnimatorSet set1 = createAnimatorSet(textView, resultDurations.get(11), lines.get(11));
		final AnimatorSet set2 = createAnimatorSet(textView, resultDurations.get(12), lines.get(12));
		set1.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				
			}
		});
		set1.start();
		start(textView);
	}

	public void cancel() {
		if (animatorSet != null) {
			animatorSet.cancel();
		}
	}
	
	private AnimatorSet createAnimatorSet(final LyricTextView_back textView, List<Long> durations, String text) {
		// 每一个汉字的像素值
		float hanziPix = textView.getTextSize();
		// 每一个英文字母的像素值 TODO 暂定为汉字的一半
		float letterPix = hanziPix / 2;
		// 每一个空格为汉字的一半
		float spacePix = hanziPix / 4;

		textView.setSinking(true);
		int w = textView.getWidth();

		char[] textChars = text.toCharArray();

		// 创建一个动画集合，其中的动画均将maskX扩大一个单位,遇到空格就生成不使用时间，遇到英文字母生成一个一半时间的动画
		// TODO 英文
		List<ObjectAnimator> maskXAnimators = new ArrayList<>();
		// 当前像素
		float currentPosition = 0;
		for (int i = 0, j = 0; i < durations.size() && j < textChars.length; j++) {

			ObjectAnimator maskXAnimator;
			if (textChars[j] == 32) {
				maskXAnimator = ObjectAnimator.ofFloat(textView, "maskX", currentPosition, currentPosition += spacePix);
				maskXAnimator.setDuration(0);
			} else {
				maskXAnimator = ObjectAnimator.ofFloat(textView, "maskX", currentPosition, currentPosition += hanziPix);
				maskXAnimator.setDuration((long) durations.get(i));
				i++;
			}
			maskXAnimator.setInterpolator(new LinearInterpolator());
			maskXAnimators.add(maskXAnimator);
		}

		animatorSet = new AnimatorSet();
		animatorSet.play(maskXAnimators.get(0));
		// 按顺序执行动画，并计算animatorSet持续时间
		long animatorSetDuration = maskXAnimators.get(0).getDuration();
		for (int i = 1; i < maskXAnimators.size(); i++) {
			animatorSet.play(maskXAnimators.get(i)).after(maskXAnimators.get(i - 1));
			animatorSetDuration += maskXAnimators.get(i).getDuration();
		}

		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animatorSet = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});

		if (animatorListener != null) {
			animatorSet.addListener(animatorListener);
		}

		return animatorSet;
	}

}