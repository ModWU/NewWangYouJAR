package cg.yunbee.cn.wangyoujar.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import android.content.Context;
import android.util.Log;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.work.PayManager;

public class LoadFileToString {
	/*public synchronized static String loadFileFromAssets(Context context,
			String fileName) {
		StringBuffer sbContent = new StringBuffer();
		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			String myCharset = System.getProperty("file.encoding");
			inputReader = new InputStreamReader(context.getResources()
					.getAssets().open(fileName), "GBK");
			bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				sbContent.append(line);
		} catch (Exception e) {
			// e.printStackTrace();
			TAGS.log(e.getMessage());
		} finally {
			try {
				if (bufReader != null)
					bufReader.close();
				if (inputReader != null)
					inputReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				TAGS.log(e.getMessage());
			}
		}
		// 去掉不可见字符
		String content = sbContent.toString();
		content = content.replaceAll("\\s", "");
		TAGS.log("--------从" + fileName + "中读取的内容为：" + content);
		return content;
	}*/
	
	
	public synchronized static String loadFileFromAssets(Context context,
			String fileName) {
		StringBuffer sbContent = new StringBuffer();
		try {
			sbContent.append(readMdFile(context.getResources().getAssets().open(fileName)));
		} catch (IOException e) {
			Log.i("chaochao", "loadFileFromAssets->" + e.toString());
		}
			
		// 去掉不可见字符
		String content = sbContent.toString();
		content = content.replaceAll("\\s", "");
		TAGS.log("--------从" + fileName + "中读取的内容为：" + content);
		return content;
	}
	
	public static String readMdFile(InputStream is) {
		StringWriter sw = new StringWriter();
		DataInputStream dos = null;
		try {
			dos = new DataInputStream(is);
			String len = null;
			
			while(dos.available() != 0 && (len = dos.readUTF()) != null) {
				sw.write(len);
				sw.flush();
			}
			
			dos.close();
			
		} catch (Exception e) {
			Log.i("chaochao", "readMdFile->" + e.toString());
		} finally {
			try {
				sw.close();
				if(dos != null)
					dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return PayManager.decodeInfo(sw.toString());
	}
}
