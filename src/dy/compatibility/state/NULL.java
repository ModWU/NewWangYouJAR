package dy.compatibility.state;

import android.content.Context;

public class NULL implements IState {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		return false;
	}

	@Override
	public String getJarPathsFlag() {
		return "";
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		return false;
	}

}
