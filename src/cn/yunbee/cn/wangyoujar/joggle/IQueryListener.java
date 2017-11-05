package cn.yunbee.cn.wangyoujar.joggle;
/**
 * 单机类型查询接口
 * @see #onQuerySuccess()
 * @see #onQueryFail()
 * @author Administrator-2017/2/14
 */
public interface IQueryListener {
	/**
	 * <font color="blue"><strong>查询成功，此时可以下发道具了</strong></font>
	 */
	void onQuerySuccess();
	/**
	 * <font color="blue"><strong>查询失败，此时应该不下发道具</strong></font>
	 */
	void onQueryFail();
	
	/**
	 * <font color="blue"><strong>查询取消</strong></font>
	 */
	void onQueryCancel();
}
