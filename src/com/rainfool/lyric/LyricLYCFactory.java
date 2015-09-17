package com.rainfool.lyric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricLYCFactory extends AbstractLyricFactory{

	private LyricLYCFactory() {
	}
	
	@Override
	public ILyric createLyric(String url) {
		super.createLyric(url);
		HttpURLConnection conn = this.conn;
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
		}//End of try-catch
		return null;
	}

	private ILyric createLyricByInputStream(InputStream is) {
		Lyric lyric = new Lyric();
		
		ArrayList<ArrayList<Long>> timestamps = new ArrayList<>();
		ArrayList<ArrayList<String>> words = new ArrayList<>();
		
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new InputStreamReader(is,"utf-8"));
			String rawLine = null;
			while ((rawLine = bfr.readLine()) != null) {
				ArrayList<Long> rowTimestamps = new ArrayList<>();
				ArrayList<String> rowWords = new ArrayList<>();
				handleRawLine(lyric,rawLine,rowTimestamps,rowWords);
				
				if(!rowTimestamps.isEmpty())
					timestamps.add(rowTimestamps);
				if(!rowWords.isEmpty()) 
					words.add(rowWords);
			}//End of while
			lyric.timestamps = timestamps;
			lyric.words = words;
			lyric.url = this.url;
			return lyric;
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
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}//End of try-catch
		return null;
	}
	
	private void handleRawLine(Lyric lyric,String rawLine,ArrayList<Long> rowTimestamps,ArrayList<String> rowWords) {
		/*
		 * 歌词文件的处理分两种情况
		 * 1.歌词特殊行，一般出现在歌词的前几句，会标注作者或者其他信息，并不作为正文处理
		 * 2.歌词正文行，需要将当前处理的行，转化为时间戳集合，和正文集合
		 * 3.其他情况，比如有特殊符号影响到了文件读取，或者文件读取过程中出现异常，则将这一行文字照搬，时间设为0，但须尽量避免这种情况
		 */

		if(isMainBody(rawLine)) {
			String[] rawData = rawLine.split("|");
			for(int i = 0;i < rawData.length; i ++) {
				//偶数为时间戳，奇数为文字
				if(i % 2 == 0) {
					String sTimestamp = rawData[i].replace("[", "").replace("]", "");
					rowTimestamps.add(Long.parseLong(sTimestamp));
				}else {
					rowWords.add(rawData[i]);
				}
			}//End of for
		}else if(rawLine.contains(":")) {
			int start = rawLine.indexOf("[");
			int end = rawLine.indexOf("]");
			String sData = rawLine.substring(start,end).replace("[", "").replace("]", "");
			String[] data = sData.split(":");
			if		("al".equals(data[0]))
				lyric.al = data[0];
			else if	("ar".equals(data[0]))
				lyric.ar = data[0];
			else if	("by".equals(data[0]))
				lyric.by = data[0];
			else if	("ti".equals(data[0]))
				lyric.ti = data[0];
				
		}else {
			rowWords.add(rawLine);
			rowTimestamps.add(0L);
		}
	}//End of method
	/*
	 * 判断是否为一个正文
	 * 判断的标准为，如果含有[00:00:00]这样的形式，就认为这是一个正文行
	 */
	private boolean isMainBody(String rawLine) {
		Pattern pattern = Pattern.compile("\\[\\d\\d:\\d\\d:\\d*\\]");
		Matcher matcher = pattern.matcher(rawLine);
		return matcher.lookingAt();
	}
}
