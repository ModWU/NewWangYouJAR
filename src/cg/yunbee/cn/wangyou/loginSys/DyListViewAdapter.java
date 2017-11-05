package cg.yunbee.cn.wangyou.loginSys;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DyListViewAdapter extends BaseAdapter implements OnClickListener {
	private Context mContext;
	private List<UserInfo> mData;
	private ILoginEvents mLoginEvents;
	private ListView mListView;
	
	public DyListViewAdapter(Context context, List<UserInfo> listUserInfo, ListView listView, ILoginEvents loginEvents) {
		mContext = context;
		mData = listUserInfo;
		mLoginEvents = loginEvents;
		mListView = listView;
		mListView.setOnItemClickListener(onItemClickListener);
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public UserInfo getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = View.inflate(mContext, ResourceUtils.getLayoutId(mContext, "dy_spiner_item"), null); 
			viewHolder = new ViewHolder();
			viewHolder.itemTxt = (TextView) convertView.findViewById(ResourceUtils.getId(mContext, "itemTxt"));
			viewHolder.itemBtn = (TextView) convertView.findViewById(ResourceUtils.getId(mContext, "itemBtn"));
			viewHolder.itemBtn.setOnClickListener(this);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.itemBtn.setTag(position);
		viewHolder.itemTxt.setText(getItem(position).getLoginName());
		return convertView;
	}
	
	private class ViewHolder {
		TextView itemTxt;
		TextView itemBtn;
	}
	
	private final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(mLoginEvents != null) {
				UserInfo userInfo = getItem(position);
				mLoginEvents.selectUser(userInfo);
			}
		}
	};
	

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		UserInfo userInfo = mData.remove(position);
		LoginRecords.getInstance(mContext).deleteUserInfo(userInfo.getLoginName());
		if(mLoginEvents != null) {
			mLoginEvents.deleteRecord(userInfo, DyLoginActivity.DELETE_USER_POPW);
		}
		notifyDataSetChanged();
	}
	
	public void deleteUserInfo(UserInfo userInfo) {
		if(userInfo != null) {
			mData.remove(userInfo);
		}
	}

}
