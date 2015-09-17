package com.rainfool.lyric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class LyricLYCFactory extends AbstractLyricFactory{

	HttpURLConnection conn;
	
	private LyricLYCFactory() {
	}
	
	@Override
	public ILyric createLyric(String url) {
		super.createLyric(url);
		try {
			if(conn != null && conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				
				return createLyricByInputStream(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	private ILyric createLyricByInputStream(InputStream is) {
		
		ArrayList<ArrayList<Long>> timestamps = new ArrayList<>();
		ArrayList<ArrayList<String>> words = new ArrayList<>();
		
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new InputStreamReader(is,"utf-8"));
			String rawLine = null;
			while ((rawLine = bfr.readLine()) != null) {
				ArrayList<Long> rowTimestamps = null;
				ArrayList<String> rowWords = null;
				handleRawline(rawLine,rowTimestamps,rowWords);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bfr != null) {
				try {
					bfr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private void handleRawLine(String rawLine,ArrayList<Long> rowTimestamps,ArrayList<String> rowWords) {
		/*
		 * 歌词文件的处理分两种情况
		 * 1.歌词特殊行，一般出现在歌词的前几句，会标注作者或者其他信息，并不作为正文处理
		 */
	}
}
