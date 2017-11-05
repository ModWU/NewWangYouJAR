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
	 * <font color="blue"><strong>支付取消</strong></font>
	 * 
	 * @param info - String类型 :
	 *            <code>取消的附带信息</code>
	 */
	public abstract void onCanceled(String info);

	/**
	 * <font color="blue"><strong>支付失败</strong></font>
	 * 
	 * @param info - String类型 :
	 *            <code>失败的附带信息</code>
	 */
	public abstract void onFailed(String info);

	/**
	 * <font color="blue"><strong>支付成功</strong></font>
	 * 
	 * @param info - String类型 :
	 *           <code>成功的附带信息</code>
	 */
	public abstract void onSuccess(String info);
}
