package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #initSuccess()
 * @see #initFail()
 * @author Administrator
 *
 */
public interface IInitListener {
	/**
	 * <font color="blue"><strong>��ʼ���ɹ�</strong></font>
	 */
	void initSuccess();
	
	/**
	 * <font color="blue"><strong>��ʼ��ʧ��</strong></font>
	 */
	void initFail();
}