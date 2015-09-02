package com.example.lyricanimator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private List<ArrayList<String>> resultWords;
	private ArrayList<ArrayList<Long>> resultDurations;
	private List<String> lines;
	private List<String> lineStartTime;
	
	private LyricTextView mLyricsTextView;
	private Button mButtonPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        mLyricsTextView = (LyricTextView) findViewById(R.id.my_text_view);
        mButtonPause = (Button) findViewById(R.id.button_pause);
        mButtonPause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLyricsTextView.pauseSwitch();
			}
		});
        
        initData();
        int number = 15;
//        Log.e("MainActivity", "Words" + resultWords.get(number).toString() + "\nDuration" + resultDurations.get(number).toString());
        mLyricsTextView.setLineAndDurations(lines.get(number), resultWords.get(number), resultDurations.get(number));
        
	}
	
	private long getLineTime(ArrayList<Long> durations) {
	    long result = 0;
		for(long i : durations) {
			result += i;
		}
		return result;
	}

	/** 
	* @Title: initData 
	* @Description:取出歌词文件并装载到对应的集合内
	* @param 
	* @return void
	* @throws 
	*/
	private void initData() {
		//取出歌词文件
        InputStream is = null;
        try {
			is = getAssets().open("1.lyc");
		} catch (IOException e) {
			e.printStackTrace();  
		}
        LyricsFileAnalyse lf = new LyricsFileAnalyse(is);

        resultWords = lf.getResultWords();
        resultDurations = lf.getResultDurations();
        lines = lf.getLines();
        lineStartTime = lf.getLineStartTime();
	}
}
