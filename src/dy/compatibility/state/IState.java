package dy.compatibility.state;

import android.content.Context;
/**
 * @author Administrator
 *
 */
public interface IState {
	
	boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir, boolean isReplace);
	boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir);
	String getJarPathsFlag();
}
