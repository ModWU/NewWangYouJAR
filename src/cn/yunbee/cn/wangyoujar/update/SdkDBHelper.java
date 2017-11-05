package cn.yunbee.cn.wangyoujar.update;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class SdkDBHelper {
	
	private static SdkDBHelper INSTANCE;
	private SdkDatabase sdkDatabase;
	private SQLiteDatabase db;
	
	private SdkDBHelper(Context context) {
		sdkDatabase = new SdkDatabase(context);
	}
	public static SdkDBHelper getInstance(Context context) {
		if(INSTANCE == null)
			synchronized (SdkDBHelper.class) {
				if(INSTANCE == null)
					INSTANCE = new SdkDBHelper(context);
			}
		return INSTANCE;
	}
	
	
	public synchronized ArrayList<SdkInfo> queryAllSdk() {
		ArrayList<SdkInfo> list = new ArrayList<SdkInfo>();
		db = sdkDatabase.getReadableDatabase();
		String querySql = "SELECT * FROM " + SdkDatabase.DBNAME_DYSDK_tb;
		Cursor cursor = db.rawQuery(querySql, null);
		while(cursor.moveToNext()) {
			int sdkno = cursor.getInt(cursor.getColumnIndex("sdkno"));
			int type_code = cursor.getInt(cursor.getColumnIndex("type_code"));
			String type_str = cursor.getString(cursor.getColumnIndex("type_str"));
			int version = cursor.getInt(cursor.getColumnIndex("version"));
			long size = cursor.getLong(cursor.getColumnIndex("size"));
			String downloadUri = cursor.getString(cursor.getColumnIndex("downloadUri"));
			String last_update_time = cursor.getString(cursor.getColumnIndex("last_update_time"));
			int state = cursor.getInt(cursor.getColumnIndex("state"));
			list.add(new SdkInfo(sdkno, type_code, type_str, version, size, downloadUri, last_update_time, state));
		}
		
		cursor.close();
		db.close();
		
		return list;
		
	}
	
	
	public synchronized SdkInfo querySdk(int type_code) {
		SdkInfo sdkInfo = null;
		db = sdkDatabase.getReadableDatabase();
		String querySql = "SELECT * FROM " + SdkDatabase.DBNAME_DYSDK_tb + " where type_code = ?";
		Cursor cursor = db.rawQuery(querySql, new String[]{type_code + ""});
		if(cursor.moveToFirst()) {
			int sdkno = cursor.getInt(cursor.getColumnIndex("sdkno"));
			String type_str = cursor.getString(cursor.getColumnIndex("type_str"));
			int version = cursor.getInt(cursor.getColumnIndex("version"));
			long size = cursor.getLong(cursor.getColumnIndex("size"));
			String downloadUri = cursor.getString(cursor.getColumnIndex("downloadUri"));
			String last_update_time = cursor.getString(cursor.getColumnIndex("last_update_time"));
			int state = cursor.getInt(cursor.getColumnIndex("state"));
			sdkInfo = new SdkInfo(sdkno, type_code, type_str, version, size, downloadUri, last_update_time, state);
		}
		cursor.close();
		db.close();
		return sdkInfo;
	}
	
	public synchronized void deleteAllSdk() {
		TAGS.log("----------------deleteAllSdk------------------");
		
		db = sdkDatabase.getWritableDatabase();
		
		db.delete(SdkDatabase.DBNAME_DYSDK_tb, null, null);
		
		db.close();
	}
	
	public synchronized boolean isHasSdkInfo() {
		TAGS.log("----------------isHasSdkInfo------------------");
		String querySql = "SELECT * FROM " + SdkDatabase.DBNAME_DYSDK_tb;
		db = sdkDatabase.getReadableDatabase();
		Cursor cursor = db.rawQuery(querySql, null);
		int count = (cursor == null ? 0 : cursor.getCount());
		if(cursor != null)
			cursor.close();
		db.close();
		return count > 0;
	}
	
	public  static String getDateStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));// 中国北京时间，东八区 
		return format.format(new Date(System.currentTimeMillis()));
	}
	
	public synchronized boolean insert(SdkInfo sdkInfo) {
		if(sdkInfo == null) return false;
		db = sdkDatabase.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type_code", sdkInfo.getType_code());
		cv.put("type_str", sdkInfo.getType_str());
		cv.put("version", sdkInfo.getVersion());
		cv.put("size", sdkInfo.getSize());
		if(sdkInfo.getDownloadUri() != null)
			cv.put("downloadUri", sdkInfo.getDownloadUri());
		cv.put("last_update_time", getDateStr());//能自动生成
		cv.put("state", 1);
		long sdkno = db.insert(SdkDatabase.DBNAME_DYSDK_tb, null, cv);
		db.close();
		return sdkno > 0;
		
	}
	
	public synchronized void delete(int sdkno) {
		db = sdkDatabase.getWritableDatabase();
		db.delete(SdkDatabase.DBNAME_DYSDK_tb, "sdkno=?", new String[]{sdkno + ""});
		db.close();
	}
	
	
	public synchronized void update(SdkInfo sdkInfo) {
		if(sdkInfo == null) return;
		db = sdkDatabase.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type_code", sdkInfo.getType_code());
		cv.put("type_str", sdkInfo.getType_str());
		cv.put("version", sdkInfo.getVersion());
		cv.put("size", sdkInfo.getSize());
		if(sdkInfo.getDownloadUri() != null)
			cv.put("downloadUri", sdkInfo.getDownloadUri());
		cv.put("last_update_time", getDateStr());//能自动生成
		cv.put("state", sdkInfo.getState());
		
		db.update(SdkDatabase.DBNAME_DYSDK_tb, cv, "type_code=?", new String[]{sdkInfo.getType_code() + ""});
		
		db.close();
	}
	
}
