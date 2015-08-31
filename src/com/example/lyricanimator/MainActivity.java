package com.example.lyricanimator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        LyricTextView tv = (LyricTextView) findViewById(R.id.my_text_view);
        

        InputStream is;
        try {
			is = getAssets().open("1.lyc");
			
			LyricsFileAnalyse lf = new LyricsFileAnalyse(is);
			
			List<ArrayList<String>>  t1 = lf.getResultWords();
			List<String> text = lf.getLines();
			tv.setText(text.get(12).toString()); 
			tv.setDurations(lf.getResultDurations().get(12));
			Log.e("MainActivity", "Ê±¼ä£º" +lf.getResultDurations().get(22).toString() + "\nÎÄ×Ö£º" + text.get(22).toString());
			new LyricAnimator().start(tv);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		

        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
