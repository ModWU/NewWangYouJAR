package cn.yunbee.cn.wangyoujar.update;

import android.content.Context;
import cn.yunbee.cn.wangyoujar.update.SdkContant.SDK_TYPE;

public class SdkNULLBuilder extends SdkBuilder {

	protected SdkNULLBuilder(Context context) {
		super(context);
	}

	protected String getDownload_jardir() {
		return "";
	}
	
	protected String getDownload_zipDir() {
		return "";
	}
	
	protected String getDownload_sodir() {
		return "";
	}
			
	protected String getReal_jardir() {
		return "";
	}
			
	protected String getReal_outdexdir() {
		return "";
	}
			
	protected String getReal_sodir() {
		return "";
	}

	@Override
	protected SDK_TYPE getSdkType() {
		return SDK_TYPE.NULL;
	}

}
