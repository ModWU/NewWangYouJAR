package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #loginSuccess(int,String, String)
 * @see #loginFail(int,String)
 * @author Administrator
 *
 */
public interface ILoginListener {
	/**
	 * <font color="blue"><strong>登录成功</strong></font>
	 * 
	 * @param resultCode - int类型 :
	 *            <code>登录成功的状态码</code>
	 * @param dianId - String类型:
	 * 			  <code>典游用户系统的唯一标识</code>
	 * @param loginName - String类型:
	 * 			  <code>登录名</code>
	 */
	void loginSuccess(int resultCode, String dianId, String loginName);
	
	/**
	 * <font color="blue"><strong>登录失败</strong></font>
	 * 
	 * @param errorCode - int类型 :
	 *            <code>登录失败的状态码</code>
	 * @param errorInfo - String类型:
	 * 			  <code>错误信息</code>
	 */
	void loginFail(int errorCode, String errorInfo);
}
