package cn.yunbee.cn.wangyoujar.update;

import java.io.File;

import android.content.Context;

public abstract class SdkAllBuilder extends SdkBuilder {
	
	private static final String FILE_ALL = "zz_all";

	protected SdkAllBuilder(Context context) {
		super(context);
	}
	
	protected String getDownload_jardir() {
		return super.getDownload_jardir() + File.separator + FILE_ALL;
	}
	
	protected String getDownload_sodir() {
		return super.getDownload_sodir() + File.separator + FILE_ALL;
	}
	
	protected String getDownload_zipDir() {
		return super.getDownload_zipDir() + File.separator + FILE_ALL;
	}
			
	protected String getReal_jardir() {
		return super.getReal_jardir() + File.separator + FILE_ALL;
	}
			
	protected String getReal_outdexdir() {
		return super.getReal_outdexdir() + File.separator + FILE_ALL;
	}
			
	protected String getReal_sodir() {
		return super.getReal_sodir() + File.separator + FILE_ALL;
	}


}
