package cg.yunbee.cn.wangyoujar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;

public class SharedPreferencesManager {
	private SharedPreferences mySharedPreferences = null;
	private String packageName = null;
	public SharedPreferencesManager(Context context , String preferenceName){
		if(packageName == null){
			packageName = SecretData.getInstance().getPackageName();
		}
		this.mySharedPreferences = context.getSharedPreferences(packageName+"_"+preferenceName, Activity.MODE_PRIVATE); 
	}
	public boolean save(String key , String value){
		//ʵ����SharedPreferences.Editor���� 
		SharedPreferences.Editor editor = mySharedPreferences.edit(); 
		//��putString�ķ����������� 
		editor.putString(key, value); 
		//�ύ��ǰ���� 
		boolean result = editor.commit();
		return result;
	}
	
	public boolean saveBoolean(String key, boolean value) {
		//ʵ����SharedPreferences.Editor���� 
		SharedPreferences.Editor editor = mySharedPreferences.edit(); 
		//��putString�ķ����������� 
		editor.putBoolean(key, value); 
		//�ύ��ǰ���� 
		boolean result = editor.commit();
		return result;
	}
	
	public boolean getBoolean(String key) {
		return mySharedPreferences.getBoolean(key, false);
	}
	
	public String getValue(String key){
		// ʹ��getString�������value��ע���2��������value��Ĭ��ֵ 
		String value = mySharedPreferences.getString(key, ""); 
		return value;
	}
	
	public void clear(){
		mySharedPreferences.edit().clear().commit();
	}
}
