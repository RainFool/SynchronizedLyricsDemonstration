package com.example.lyricanimator;

import java.util.ArrayList;
import java.util.List;

import com.example.lyricanimator.LyricTextView.AnimationSetupCallback;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
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

	public void start(final LyricTextView textView) {

		final Runnable animate = new Runnable() {
			// ÿһ�����ֵ�����ֵ
			float hanziPix = textView.getTextSize();
			// ÿһ��Ӣ����ĸ������ֵ TODO �ݶ�Ϊ���ֵ�һ��
			float letterPix = hanziPix / 2;
			// ÿһ���ո�Ϊ���ֵ�һ��
			float spacePix = hanziPix / 4;

			@Override
			public void run() {

				textView.setSinking(true);
				int w = textView.getWidth();
				List<Long> durations = textView.getDurations();

				// // ���durations������ֵ�Ƿ�ͺ��ֻ򵥴ʸ���ƥ��
				// if (textView.getText().length() != durations.size()) {
				// Log.e(TAG, "���ʱ��������ַ�����ƥ��:text length=" +
				// textView.getText().length() + " durations size=" +
				// durations.size());
				// return;
				// }
				// ��ȡtextView��textֵ,ת����Ϊ����
				String text = textView.getText().toString();
				char[] textChars = text.toCharArray();

				// ����һ���������ϣ����еĶ�������maskX����һ����λ,�����ո�����ɲ�ʹ��ʱ�䣬����Ӣ����ĸ����һ��һ��ʱ��Ķ���
				// TODO Ӣ��
				List<ObjectAnimator> maskXAnimators = new ArrayList<>();
				// ��ǰ����
				float currentPosition = 0;
				for (int i = 0, j = 0; i < durations.size() && j < textChars.length; j++) {

					ObjectAnimator maskXAnimator;
					if (textChars[j] == 32) {
						maskXAnimator = ObjectAnimator.ofFloat(textView, "maskX", currentPosition,
								currentPosition += spacePix);
						maskXAnimator.setDuration(0);
					} else {
						maskXAnimator = ObjectAnimator.ofFloat(textView, "maskX", currentPosition,
								currentPosition += hanziPix);
						maskXAnimator.setDuration((long) durations.get(i));
						i++;
					}
					maskXAnimator.setInterpolator(new LinearInterpolator());
					maskXAnimators.add(maskXAnimator);
				}

				animatorSet = new AnimatorSet();
				animatorSet.play(maskXAnimators.get(0));
				// ��˳��ִ�ж�����������animatorSet����ʱ��
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

				animatorSet.start();
			}
		};

		if (!textView.isSetUp()) {
			textView.setAnimationSetupCallback(new AnimationSetupCallback() {

				@Override
				public void onSetupAnimation(LyricTextView titanicTextView) {
					animate.run();
				}
			});
		} else {
			animate.run();
		}
	}

	public void cancel() {
		if (animatorSet != null) {
			animatorSet.cancel();
		}
	}
}