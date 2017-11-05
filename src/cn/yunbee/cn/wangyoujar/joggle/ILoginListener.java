package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #loginSuccess(int,String, String)
 * @see #loginFail(int,String)
 * @author Administrator
 *
 */
public interface ILoginListener {
	/**
	 * <font color="blue"><strong>��¼�ɹ�</strong></font>
	 * 
	 * @param resultCode - int���� :
	 *            <code>��¼�ɹ���״̬��</code>
	 * @param dianId - String����:
	 * 			  <code>�����û�ϵͳ��Ψһ��ʶ</code>
	 * @param loginName - String����:
	 * 			  <code>��¼��</code>
	 */
	void loginSuccess(int resultCode, String dianId, String loginName);
	
	/**
	 * <font color="blue"><strong>��¼ʧ��</strong></font>
	 * 
	 * @param errorCode - int���� :
	 *            <code>��¼ʧ�ܵ�״̬��</code>
	 * @param errorInfo - String����:
	 * 			  <code>������Ϣ</code>
	 */
	void loginFail(int errorCode, String errorInfo);
}
