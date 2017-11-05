package cn.yunbee.cn.wangyoujar.update;

import java.io.File;

import android.content.Context;
import cn.yunbee.cn.wangyoujar.update.SdkContant.SDK_TYPE;

public class SdkAibeiBuilder extends SdkBuilder {
	
	private static final String FILE_AIBEI = "aibei";
	
	protected SdkAibeiBuilder(Context context) {
		super(context);
	}
	
	protected String getDownload_jardir() {
		return super.getDownload_jardir() + File.separator + FILE_AIBEI;
	}
	
	protected String getDownload_sodir() {
		return super.getDownload_sodir() + File.separator + FILE_AIBEI;
	}
	
	protected String getDownload_zipDir() {
		return super.getDownload_zipDir() + File.separator + FILE_AIBEI;
	}
			
	protected String getReal_jardir() {
		return super.getReal_jardir() + File.separator + FILE_AIBEI;
	}
			
	protected String getReal_outdexdir() {
		return super.getReal_outdexdir() + File.separator + FILE_AIBEI;
	}
			
	protected String getReal_sodir() {
		return super.getReal_sodir() + File.separator + FILE_AIBEI;
	}

	@Override
	protected SDK_TYPE getSdkType() {
		return SDK_TYPE.AIBEI;
	}


}
