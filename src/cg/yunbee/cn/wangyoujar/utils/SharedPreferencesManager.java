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
		//实例化SharedPreferences.Editor对象 
		SharedPreferences.Editor editor = mySharedPreferences.edit(); 
		//用putString的方法保存数据 
		editor.putString(key, value); 
		//提交当前数据 
		boolean result = editor.commit();
		return result;
	}
	
	public boolean saveBoolean(String key, boolean value) {
		//实例化SharedPreferences.Editor对象 
		SharedPreferences.Editor editor = mySharedPreferences.edit(); 
		//用putString的方法保存数据 
		editor.putBoolean(key, value); 
		//提交当前数据 
		boolean result = editor.commit();
		return result;
	}
	
	public boolean getBoolean(String key) {
		return mySharedPreferences.getBoolean(key, false);
	}
	
	public String getValue(String key){
		// 使用getString方法获得value，注意第2个参数是value的默认值 
		String value = mySharedPreferences.getString(key, ""); 
		return value;
	}
	
	public void clear(){
		mySharedPreferences.edit().clear().commit();
	}
}
