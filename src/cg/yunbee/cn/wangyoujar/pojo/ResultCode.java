package cg.yunbee.cn.wangyoujar.pojo;

public class ResultCode {
	public static final String SUCCESS = "0";
	public static final String SMS_SEND_TIMEOUT = "1";
	public static final String SMS_SEND_FAILED = "2";
	public static final String SMS_NOT_RECEIVED = "3";// 未收到下行短信
	public static final String SMS_VERIFYCODE_ERROR = "4";// 未匹配到验证码
	public static final String NO_LOCAL_PHONENUMBER = "5";// 没有本机号码
	public static final String HTTP_REQUEST_FAILED = "10";

	public static final String CONFIG_FILE_ERROR = "99";// 配置文件错误
	public static final String UNKNOWN_ERROR = "100";// 配置文件错误
}
