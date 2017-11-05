package dy.compatibility.state;

import android.content.Context;
import android.util.Log;
import dy.compatibility.work.Utils;

public class SDKDYWangyouState implements IState {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		return false;
	}


	@Override
	public String getJarPathsFlag() {
		return "flag_dywangyou_jarPaths";
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		Log.i("INFO", "-------------����->checkCopyFile---------------");
		boolean isReCopy = Utils.checkCopyFile(context, getJarPathsFlag(), fromAssetsDir, toJarDir, toSoDir);
		if(isReCopy) {
			Log.i("INFO", "����copy�����ļ�(���滻�Ѵ��ڵ��ļ�)");
			copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, false);
		}
		return isReCopy;
	}

}
