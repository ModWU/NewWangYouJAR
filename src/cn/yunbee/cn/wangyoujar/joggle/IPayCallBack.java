package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #onCanceled(String)
 * @see #onFailed(String)
 * @see #onSuccess(String)
 * @author Administrator
 *
 */
public interface IPayCallBack {
	/**
	 * <font color="blue"><strong>֧��ȡ��</strong></font>
	 * 
	 * @param info - String���� :
	 *            <code>ȡ���ĸ�����Ϣ</code>
	 */
	public abstract void onCanceled(String info);

	/**
	 * <font color="blue"><strong>֧��ʧ��</strong></font>
	 * 
	 * @param info - String���� :
	 *            <code>ʧ�ܵĸ�����Ϣ</code>
	 */
	public abstract void onFailed(String info);

	/**
	 * <font color="blue"><strong>֧���ɹ�</strong></font>
	 * 
	 * @param info - String���� :
	 *           <code>�ɹ��ĸ�����Ϣ</code>
	 */
	public abstract void onSuccess(String info);
}
