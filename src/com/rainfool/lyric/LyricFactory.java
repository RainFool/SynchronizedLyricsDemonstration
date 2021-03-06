package com.rainfool.lyric;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/** 
* @ClassName: AbstractLyricFactory 
* @Description: 三个工厂类的父类，处理一些公共操作
* @author TianYu 田雨
* @date 2015年9月17日 下午7:35:02 
*  
*/
public abstract class LyricFactory {

	protected static final String TAG = "LyricFactory";
	
	String url;
	HttpURLConnection conn;
	
	public ILyric createLyric(String url) {
		this.url = url;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
