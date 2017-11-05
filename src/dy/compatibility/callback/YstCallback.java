package dy.compatibility.callback;

public interface YstCallback {
	void onUserCancel();
	void onPaySuccess();
	void onPayFail(int errorCode, String errorMsg);
}
