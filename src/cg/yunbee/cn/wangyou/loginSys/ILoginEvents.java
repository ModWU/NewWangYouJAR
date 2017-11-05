package cg.yunbee.cn.wangyou.loginSys;

public interface ILoginEvents {
	void login();
	void register();
	void quickLogin();
	void pwdVisible();
	void showMore();
	void alterPwd();
	void forgetPwd();
	void deleteRecord(UserInfo userInfo, int deleteType);
	void autoLogin();
	void rememberPwd();
	void selectUser(UserInfo userInfo);
}
