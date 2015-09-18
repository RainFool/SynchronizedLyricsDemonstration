package com.rainfool.lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.string;
import android.util.Log;

public class LyricLYCFactory extends LyricFactory{
	
	
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
			/*
			 * 默认读取方式为GBK编码，先读取前三个字节，看是否为十六进制的“EFBBBF”
			 * 如果是，则使用UTF-8方式读取
			 * 否则则使用默认GBK方式读取
			 */
			BufferedInputStream bis = new BufferedInputStream(is);
			String encoding = "gbk";
			bis.mark(3);
			byte[] bom = new byte[3];
			bis.read(bom);
			bis.reset();
			if(bom[0] == Integer.valueOf("EF",16).byteValue()
					&&bom[1] == Integer.valueOf("BB",16).byteValue()
					&&bom[2] == Integer.valueOf("BF",16).byteValue()){
				encoding = "utf-8";
			} else if(bom[0] == Integer.valueOf("FF",16).byteValue()
					&&bom[1] == Integer.valueOf("FE",16).byteValue()) {
				encoding = "Unicode";
			}
			System.out.println(encoding);
			//正式读取文件开始
			bfr = new BufferedReader(new InputStreamReader(bis,encoding));
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
			/*
			 * LYC歌词的正文有两种方式，
			 * 一种是时间和歌词之间可以通过"|"分开,含有“|”则判断为这种方式
			 * 一种是没有“|”，只包含了时间和歌词，
			 */
			if (rawLine.contains("|")) {
				String[] rawData = rawLine.split("\\|");
				for (int i = 0; i < rawData.length; i++) {
					//偶数为时间戳，奇数为文字
					if (i % 2 == 0) {

						rowTimestamps.add(parseTimestamp(rawData[i]));
					} else {
						rowWords.add(rawData[i]);
					}
				} //End of for
			}else {
				Pattern pattern = Pattern.compile("\\[(\\d{2}:){2}\\d{3}\\]");
				Matcher matcher = pattern.matcher(rawLine);
				while(matcher.find()) {
					rowTimestamps.add(parseTimestamp(matcher.group(0)));
				}
				String[] sTemp = rawLine.split("\\[(\\d{2}:){2}\\d{3}\\]");
				for(String s : sTemp) {
					if(!s.isEmpty()) {
						rowWords.add(s);
					}
				}
				
			}
		}else if(rawLine.contains(":")) {
			System.out.println(rawLine);
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
	/*
	 * 解析时间，xx:xx:xxx将会按照“分：秒：毫秒”的关系来转换
	 */
	private long parseTimestamp(String sTimestamp) {
		sTimestamp = sTimestamp.replace("[", "").replace("]", "");
		String[] ss = sTimestamp.split(":");
		if (ss.length != 3) {
			Log.e(TAG, "解析文件时发现时间格式不对！");
			return -1;
		}
		long result = Long.parseLong(ss[0]) * 60 * 1000 + Long.parseLong(ss[1]) * 1000 + Long.parseLong(ss[2]);
		return result;
	}
}
