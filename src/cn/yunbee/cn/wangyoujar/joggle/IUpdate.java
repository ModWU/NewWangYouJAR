package cn.yunbee.cn.wangyoujar.joggle;

public interface IUpdate {
	void checkUpdate(ICheckUpdate listener);
	void tryAddSdkLibs();
}
