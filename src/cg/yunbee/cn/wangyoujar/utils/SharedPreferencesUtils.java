package cg.yunbee.cn.wangyoujar.utils;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * �������SharedPreferences���������ڱ���Ŀ¼��<font style="color:red"><strong>/data/data/xyz.monkeytong.hongbao/shared_prefs</strong></font> �С�
 * @author Administrator-2016/9/23
 *
 */
public class SharedPreferencesUtils {
	
	/**
	 * value:"sharepreference"
	 */
	public static final String DEFAULT_FILENAME = "sharepreference";
	
	/**
	 * value:Context.MODE_PRIVATE
	 */
	public static final int DEFAULT_MODE = Context.MODE_APPEND;
	
	private static final int MODE_MASK = Context.MODE_APPEND | Context.MODE_PRIVATE | 1 | 2;
	
	private SharedPreferences sp;
	
	private Editor editor = null;
	
	private String filename = DEFAULT_FILENAME;
	
	private int mode = DEFAULT_MODE;
	
	private Context context;
	
	private SharedPreferencesUtils(Context context, String filename) {
		this.context = context;
		if(filename != null) 
			this.filename = filename;
		sp =  context.getSharedPreferences(this.filename, mode);
	}
	
	/**
	 * �÷����ᴴ��һ��<code>SharedPreferences</code>���������ļ���Ϊ{@link #DEFAULT_FILENAME DEFAULT_FILENAME}��<p>
	 * ģʽΪ{@link #DEFAULT_MODE DEFAULT_MODE}
	 * @param context - ������
	 * @return SharedPreferencesUtils - ����
	 */
	public static SharedPreferencesUtils newInstance(Context context) {
		return new SharedPreferencesUtils(context, DEFAULT_FILENAME);
	}
	
	
	public static SharedPreferencesUtils newInstance(Context context, String filename) {
		return new SharedPreferencesUtils(context, filename);
	}
	
	/**
	 * ����ģʽ
	 * @param mode - ģʽ
	 * @see #setModeAndFilename(String, int)
	 * setModeAndFilename
	 * @return SharedPreferencesUtils
	 */
	public synchronized SharedPreferencesUtils setMode(int mode) {
		
		if((~MODE_MASK & mode) != 0) 
			return this;
		
		sp = context.getSharedPreferences(filename, mode);
		this.mode = mode;
		return this;
	}
	
	/**
	 * �����ļ���
	 * @param filename - �ļ���
	 * @see #setModeAndFilename(String, int)
	 * setModeAndFilename
	 * @return SharedPreferencesUtils
	 */
	public synchronized SharedPreferencesUtils setFilename(String filename) {
		if(filename == null || filename.trim().equals("")) return this;
		sp = context.getSharedPreferences(filename, mode);
		this.filename = filename;
		return this;
	}
	
	/**
	 * ����ģʽ���ļ���
	 * @param filename - �ļ���
	 * @param mode - ģʽ
	 * @see #setFilename(String)
	 * ����setFilename
	 * @see #setMode(int)
	 * ����setMode
	 * @return SharedPreferencesUtils
	 */
	public synchronized SharedPreferencesUtils setModeAndFilename(String filename, int mode) {
		if(filename == null || filename.trim().equals("") || (~MODE_MASK & mode) != 0) return this;
		sp = context.getSharedPreferences(filename, mode);
		return this;
	}
	
	public synchronized SharedPreferencesUtils saveString(String key, String value) {
		if(editor == null)
			editor = sp.edit();
		editor.putString(key, value);
		return this;
	}
	
	public synchronized SharedPreferencesUtils saveInt(String key, int value) {
		if(editor == null)
			editor = sp.edit();
		editor.putInt(key, value);
		return this;
	}
	
	public synchronized SharedPreferencesUtils saveBoolean(String key, boolean value) {
		if(editor == null)
			editor = sp.edit();
		editor.putBoolean(key, value);
		return this;
	}
	
	public synchronized SharedPreferencesUtils saveFloat(String key, float value) {
		if(editor == null)
			editor = sp.edit();
		editor.putFloat(key, value);
		return this;
	}
	
	public synchronized SharedPreferencesUtils saveLong(String key, long value) {
		if(editor == null)
			editor = sp.edit();
		editor.putLong(key, value);
		return this;
	}
	
	
	public synchronized SharedPreferencesUtils clear() {
		if(editor != null)
			editor.clear();
		return this;
	}
	
	public synchronized void commit() {
		if(editor != null) {
			editor.commit();
			editor = null;
		}
	}
	
	public synchronized Map<String, ?> getAll() {
		return sp.getAll();
	}
	
	public synchronized float getFloat(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}
	
	public synchronized int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}
	
	public synchronized long getLong(String key, long defValue) {
		return sp.getLong(key, defValue);
	}
	
	public synchronized String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}
	
	public synchronized boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	public synchronized boolean contains(String key) {
		return sp.contains(key);
	}
	
	
	
}
