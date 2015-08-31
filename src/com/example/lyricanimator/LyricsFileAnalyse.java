package com.example.lyricanimator;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/** 
* @ClassName: LyricsFileAnalyse 
* @Description: ������ļ�ת��Ϊһ���洢��ʵļ��ϣ���һ���洢��Ӧ�����ÿ���ֳ���ʱ��ļ���
* @author TianYu ����
* @date 2015��8��31�� ����6:08:47 
*  �ڹ��췽���д���File�࣬Ȼ���������getResult...��������
*/
public class LyricsFileAnalyse {

	/**
	 * @Fields lyricFile : ���÷���ʱ������ļ�
	 */
	File lyricFile;

	/**
	 * @Fields resultDurations :�������е��е�ʱ����Ϣ,Ƕ�׵ļ��ϴ���˶�Ӧ�������ÿ���ֵĳ���ʱ��
	 */
	ArrayList<ArrayList<Long>> resultDurations;

	/**
	 * @Fields resultWords :�������еĸ��������Ϣ�����û�����������
	 */
	ArrayList<ArrayList<String>> resultWords;
	
	/** 
	* @Fields lines :�������и��������Ϣ����resultWords��ͬ���ǣ���������洢��ÿһ�и����Ϊһ��String
	*/ 
	ArrayList<String> lines;

	/**
	 * @Fields lineStartTime :ÿһ�еĿ�ʼʱ�䣬��ÿһ�г��ֵĵ�һ��ʱ��
	 */
	ArrayList<String> lineStartTime = new ArrayList<>();

	public LyricsFileAnalyse(File lyricFile) {
		this.lyricFile = lyricFile;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(lyricFile),"UTF-8"));
			lyricFileAnalyse(br);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		setLines();
	}
	
	public LyricsFileAnalyse(InputStream is) {
		this.lyricFile = lyricFile;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			lyricFileAnalyse(br);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		setLines();
	}

	public ArrayList<ArrayList<Long>> getResultDurations() {
		return resultDurations;
	}

	public ArrayList<ArrayList<String>> getResultWords() {
		return resultWords;
	}

	public ArrayList<String> getLineStartTime() {
		return lineStartTime;
	}

	public ArrayList<String> getLines() {
		return lines;
	}



	/** 
	* @Title: lyricFileAnalyse 
	* @Description: ���ļ��еĸ�ʺ�ʱ����ȡ�����������У�ͬʱ��ÿһ�еĿ�ʼʱ��Ҳ��ȡ��
	* @param @param lyricFile
	* @return void
	* @throws 
	*/
	public void lyricFileAnalyse(BufferedReader br) {
		try {
			resultDurations = new ArrayList<>();
			resultWords = new ArrayList<>();
			
			// ���ļ���ȡ����ԭʼ��һ������
			String rawLine = null;

			while ((rawLine = br.readLine()) != null) {

				// ÿһ�д������ʱ��ļ���
				ArrayList<Long> lineDurations = new ArrayList<>();
				// ÿһ�д���������ĵļ���
				ArrayList<String> lineWords = new ArrayList<>();
				lineHandle(rawLine, lineDurations, lineWords);
				
				resultDurations.add(lineDurations);
				resultWords.add(lineWords);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void lineHandle(String rawLine, ArrayList<Long> lineDurations, ArrayList<String> lineWords) {

		String[] data = rawLine.split("\\|");

		// ���û�С�|������,�������ݣ��ͽ�ʱ����Ϊ��ʼʱ�䣬�����ַ����ڵ�"["ȥ������
		if (data.length == 1) {
			lineStartTime.add("[00:00:00]");
			lineWords.add(rawLine.replace("[", "").replace("]", ""));
			return;
		}

		// �����������еĴ����ȼ���ÿһ�е���ʼʱ��,����ʱ��ġ�[���͡�]��ȥ��
		lineStartTime.add(data[0].replace("[", "").replace("]", ""));

		// ѭ�������������ݣ�һ�б��ָ��������ż��Ϊʱ�䣬�ȴ�ŵ���ʱ������,����Ϊ���Ĵ����lineWords��
		ArrayList<String> times = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {

			if (i % 2 == 0) {
				times.add(data[i]);
			} else if (i % 2 == 1) {
				lineWords.add(data[i]);
			}
		}

		// ���Ĵ�����ϣ����ڽ���ʱ�����е�����ת��Ϊÿ���ֵĳ���ʱ��
		long[] nums = new long[times.size()];
		for (int i = 0; i < times.size(); i++) {
			nums[i] = timeParse(times.get(i));
		}
		for(int i = 1;i <nums.length; i ++) {
			lineDurations.add(nums[i] - nums[i-1]);
		}
	}

	// ��ʱ���ַ���ת��Ϊlong����ֵ
	private long timeParse(String time) {

		time = time.replace("[", "").replace("]", "");
		String[] ss = time.split(":");
		if (ss.length != 3) {
			// TODO
		}
		long result = Long.parseLong(ss[0]) * 60 * 1000 + Long.parseLong(ss[1]) * 1000 + Long.parseLong(ss[2]);
		return result;
	}
	
	//��ʼ��lines
	private void setLines() {
		lines = new ArrayList<>();
		for(int i = 0;i < resultWords.size();i ++) {
			StringBuffer sb = new StringBuffer();
			for(int j = 0; j < resultWords.get(i).size(); j ++) {
				sb.append(resultWords.get(i).get(j));
			}
			lines.add(sb.toString());
		}
	}
}
