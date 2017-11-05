package cg.yunbee.cn.wangyoujar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class ChannelActivity extends Activity {
	static final int GAME_FINISH_REQUEST = 1864;
	Resources resources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		resources = ChannelActivity.this.getResources();
		int layoutId = getLayoutId(ChannelActivity.this,
				"yunbee_channel_activity");
		TAGS.log("layoutId:" + layoutId);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(layoutId);
		ViewGroup rl = (ViewGroup) findViewById(getId(ChannelActivity.this,
				"layout"));
		ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		int imageId = getDrawableId(ChannelActivity.this, "channel_picture");
		TAGS.log("imageId:" + imageId);
		if (imageId != 0) {
			TAGS.log("спиафа");
			imageView.setImageResource(imageId);
			rl.addView(imageView);
			

			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						Intent intent;
						String name = "yunbee_class_name";
						int id = resources.getIdentifier(name, "string",
								ChannelActivity.this.getPackageName());
						if (id == 0) {
							Log.i(ChannelActivity.class.getName(),
									"Lookup id for resource '" + name
											+ "failed");
							// graceful error handling code here
						} else {
							Log.i(ChannelActivity.class.getName(),
									"Lookup id for resource '" + name
											+ "success");
							intent = new Intent(ChannelActivity.this, Class
									.forName(resources.getString(id)));
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							ChannelActivity.this.finish();
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}, 1000);
		} else {
			TAGS.log("ц╩спиафа");
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					try {
						Intent intent;
						String name = "yunbee_class_name";
						int id = resources.getIdentifier(name, "string",
								ChannelActivity.this.getPackageName());
						if (id == 0) {
							Log.i(ChannelActivity.class.getName(),
									"Lookup id for resource '" + name
											+ "failed");
							// graceful error handling code here
						} else {
							Log.i(ChannelActivity.class.getName(),
									"Lookup id for resource '" + name
											+ "success");
							intent = new Intent(ChannelActivity.this, Class
									.forName(resources.getString(id)));
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							ChannelActivity.this.finish();
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			});
		}

		// modify by liya 2016.1.29

		super.onCreate(savedInstanceState);
	}
	
	

	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// Log.i(YunbeeActivity.class.getName(), "onActivityResult");
	// if (requestCode == GAME_FINISH_REQUEST) {
	// ChannelActivity.this.finish();
	// }
	// }

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", paramContext.getPackageName());
	}

	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}
	
	@Override
	public void onBackPressed() {
	}
}
