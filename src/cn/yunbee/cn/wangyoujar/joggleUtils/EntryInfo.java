package cn.yunbee.cn.wangyoujar.joggleUtils;

import cn.yunbee.cn.wangyoujar.joggle.IInitListener;
import cn.yunbee.cn.wangyoujar.joggle.ILoginListener;
import cn.yunbee.cn.wangyoujar.joggle.ILogoutListener;

public class EntryInfo {
	public boolean isDebug;
	public String gameName;
	public ILoginListener loginListener;
	public IInitListener initListener;
	public ILogoutListener logoutListener;
	public boolean isAutoLogin;
	
}
