package cn.yunbee.cn.wangyoujar.joggle;

/**
 * @see #initSuccess()
 * @see #initFail()
 * @author Administrator
 *
 */
public interface IInitListener {
	/**
	 * <font color="blue"><strong>初始化成功</strong></font>
	 */
	void initSuccess();
	
	/**
	 * <font color="blue"><strong>初始化失败</strong></font>
	 */
	void initFail();
}