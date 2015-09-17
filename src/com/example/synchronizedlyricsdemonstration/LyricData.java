package com.example.synchronizedlyricsdemonstration;

import java.util.ArrayList;

public class LyricData {
	/**
	 * @Fields resultDurations :�������е��е�ʱ����Ϣ,Ƕ�׵ļ��ϴ���˶�Ӧ�������ÿ���ֵĳ���ʱ��
	 */
	public ArrayList<ArrayList<Long>> resultDurations;
	/**
	 * @Fields resultWords :�������еĸ��������Ϣ�����û�����������
	 */
	public ArrayList<ArrayList<String>> resultWords;
	/** 
	* @Fields lines :�������и��������Ϣ����resultWords��ͬ���ǣ���������洢��ÿһ�и����Ϊһ��String
	*/
	public ArrayList<String> lines;
	/**
	 * @Fields lineStart:ÿһ�еĿ�ʼʱ�䣬��ÿһ�г��ֵĵ�һ��ʱ����ַ��������ʽ
	 */
	public ArrayList<String> lineStart;
	/** 
	* @Fields lineStartTime :ÿһ�и�ʿ�ʼʱ��
	*/
	public ArrayList<Long> lineStartTime;

	public LyricData() {
		resultDurations = new ArrayList<>();
		resultWords = new ArrayList<>();
		lines = new ArrayList<>();
		lineStart = new ArrayList<>();
		lineStartTime = new ArrayList<>();
	}
}