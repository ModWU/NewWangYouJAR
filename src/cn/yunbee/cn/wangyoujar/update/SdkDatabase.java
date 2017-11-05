package cn.yunbee.cn.wangyoujar.update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class SdkDatabase extends SQLiteOpenHelper {
	
	static final String DB_NAME = "db_dysdk.db";
	
	static final String DBNAME_DYSDK_tb = "dysdk_tb";
	//static final String DBNAME_DYSDK_INFO_tb = "dysdk_info_tb";
	
	private static final int VERSION = 1;
	
	
	
	private static final String CREATE_DYSDK_SQ = "CREATE TABLE IF NOT EXISTS "
	
			+ DBNAME_DYSDK_tb + " (sdkno integer primary key autoincrement, "
					+ "type_code integer not null, "
					+ "type_str varchar[20] not null, "
					+ "version integer not null, "
					+ "size integer, "
					+ "downloadUri text, "
					+"last_update_time datetime default current_timestamp not null, "//取值时使用: "datetime(last_update_time,'localtime')"
					+ "state integer not null"
					+ ")";
	
	/*private static final String CREATE_DYSDK_INFO_SQ =  "CREATE TABLE IF NOT EXISTS "
			
			+ DBNAME_DYSDK_INFO_tb + " (infono integer primary key autoincrement,"
					+ "sdkno integer,"
					+ "sdkTypeCode integer not null,"
					+ "libtype varchar[20] not null,"
					+ "libname text not null,"
					+ "size integer not null,"//单位:字节
					+ "file_path text not null,"
					+ "file_out_path text not null,"
					+ "parent_path text not null,"
					+ "version integer not null,"
					+ "update_time datetime default current_timestamp not null,"
					+ "foreign key (sdkno) references " + DBNAME_DYSDK_tb + " (sdkno)"
					+ ")";*/

	public SdkDatabase(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		TAGS.log("---------SdkDatabase-onCreate---------");
		db.execSQL(CREATE_DYSDK_SQ);
		//db.execSQL(CREATE_DYSDK_INFO_SQ);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if(!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//暂时不更新表结构
		TAGS.log("---------SdkDatabase-onUpgrade---------");
		TAGS.log("oldVersion: " + oldVersion);
		TAGS.log("newVersion: " + newVersion);
	   // db.execSQL("DROP TABLE IF EXISTS " + DBNAME_DYSDK_INFO_tb);  
	    db.execSQL("DROP TABLE IF EXISTS " + DBNAME_DYSDK_tb);  
	    onCreate(db); 
	}

}
