package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #logoutSuccess(String,String)
 * @see #logoutFail(String,int,String)
 * @author Administrator
 *
 */
public interface ILogoutListener {
	/**
	 * <font color="blue"><strong>登出成功</strong></font>
	 * 
	 * @param dianId - String类型:
	 * 			  <code>典游用户系统的唯一标识</code>
	 * @param loginName - String类型:
	 * 			  <code>登录名</code>
	 */
	void logoutSuccess(String dianId, String loginName);
	
	/**
	 * <font color="blue"><strong>登出失败</strong></font>
	 * @param loginName - String类型 :
	 *            <code>登录名</code>
	 * @param errorCode - int类型 :
	 *            <code>登出失败的状态码</code>
	 * @param errorInfo - String类型:
	 * 			  <code>错误信息</code>
	 */
	void logoutFail(String loginName, int errorCode, String errorInfo);
}
