package cn.yunbee.cn.wangyoujar.joggle;
/**
 * �������Ͳ�ѯ�ӿ�
 * @see #onQuerySuccess()
 * @see #onQueryFail()
 * @author Administrator-2017/2/14
 */
public interface IQueryListener {
	/**
	 * <font color="blue"><strong>��ѯ�ɹ�����ʱ�����·�������</strong></font>
	 */
	void onQuerySuccess();
	/**
	 * <font color="blue"><strong>��ѯʧ�ܣ���ʱӦ�ò��·�����</strong></font>
	 */
	void onQueryFail();
	
	/**
	 * <font color="blue"><strong>��ѯȡ��</strong></font>
	 */
	void onQueryCancel();
}
