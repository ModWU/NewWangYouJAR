package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #logoutSuccess(String,String)
 * @see #logoutFail(String,int,String)
 * @author Administrator
 *
 */
public interface ILogoutListener {
	/**
	 * <font color="blue"><strong>�ǳ��ɹ�</strong></font>
	 * 
	 * @param dianId - String����:
	 * 			  <code>�����û�ϵͳ��Ψһ��ʶ</code>
	 * @param loginName - String����:
	 * 			  <code>��¼��</code>
	 */
	void logoutSuccess(String dianId, String loginName);
	
	/**
	 * <font color="blue"><strong>�ǳ�ʧ��</strong></font>
	 * @param loginName - String���� :
	 *            <code>��¼��</code>
	 * @param errorCode - int���� :
	 *            <code>�ǳ�ʧ�ܵ�״̬��</code>
	 * @param errorInfo - String����:
	 * 			  <code>������Ϣ</code>
	 */
	void logoutFail(String loginName, int errorCode, String errorInfo);
}
