package cg.yunbee.cn.wangyou.loginSys;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DySpinnerItemAdpater extends BaseAdapter implements Filterable, OnClickListener  {
	
	private int themeId;
	private Context mContext;
	private ArrayFilter mFilter; 
	private List<UserName> mData;
	private List<UserName> mUnfilteredData;
	private ILoginEvents mLoginEvents;
	private AutoCompleteTextView mAutoTv;
	
	public DySpinnerItemAdpater(Context context, AutoCompleteTextView autoTv, ILoginEvents loginEvents, List<UserName> data) {
		mContext = context;
		mAutoTv = autoTv;
		mLoginEvents = loginEvents;
		mData = data;
		themeId = ResourceUtils.getStyleId(context, "dyLoginDialogTheme");
		mAutoTv.setOnItemClickListener(onItemClickListener);
		
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public UserName getItem(int position) {
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
		
		handleTxtView(viewHolder, position);
		
		viewHolder.itemBtn.setTag(position);
		
		return convertView;
	}
	
	private void handleTxtView(ViewHolder viewHolder, int position) {
		TextView tv = viewHolder.itemTxt;
		UserName username = getItem(position);
		String content = username.getContent();
		String prefixString = username.getPrefixString();
		if(Utils.isEmpty(content)) return;
		if(Utils.isEmpty(prefixString)) { tv.setText(content); return;}
		
		int totalLen = content.length();
		int prefixLen = prefixString.length();
		int prefixStartIndex = content.toLowerCase().indexOf(prefixString.toLowerCase());
		int prefixEndIndex = prefixStartIndex + prefixLen;
		
		SpannableStringBuilder builder = new SpannableStringBuilder(username.getContent());
		ForegroundColorSpan backColorSpan = new ForegroundColorSpan(Utils.getResourceColor(mContext, "dy_default_et_text"));
		ForegroundColorSpan foreColorSpan = new ForegroundColorSpan(Utils.getResourceColor(mContext, "dy_default_et_light_text"));
		builder.setSpan(backColorSpan, 0, totalLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(foreColorSpan, prefixStartIndex, prefixEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(builder);
	}
	
	@Override
	public Filter getFilter() {
		if(mFilter == null) mFilter = new ArrayFilter();
		return mFilter;
	}
	
	private class ViewHolder {
		TextView itemTxt;
		TextView itemBtn;
	}
	
	private class ArrayFilter extends Filter {
		
		@Override
		public CharSequence convertResultToString(Object resultValue) {
			UserName username = (UserName) resultValue;
			return super.convertResultToString(username.getContent());
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			
			
			FilterResults results = new FilterResults();  
			  
            if (mUnfilteredData == null) {  
                mUnfilteredData = new ArrayList<UserName>(mData);  
            } 
            
            if (prefix == null || prefix.length() == 0) {  
                List<UserName> list = mUnfilteredData;  
                results.values = list;  
                results.count = list.size();  
            } else {  
            	String owerStr = prefix.toString();
                String prefixString = owerStr.toLowerCase();  
  
                List<UserName> unfilteredValues = mUnfilteredData;  
                int count = unfilteredValues.size();  
  
                ArrayList<UserName> newValues = new ArrayList<UserName>(count);  
                
                TAGS.log("---------------performFiltering------------------");
                TAGS.log("prefixString: " + prefixString);
                
  
                for (int i = 0; i < count; i++) {  
                	UserName pc = unfilteredValues.get(i); 
                	TAGS.log("Content: " + pc.getContent());
                    if (pc != null) {
                    	String content = pc.getContent() == null ? "" : pc.getContent().toLowerCase();
                    	if(content.contains(prefixString)) {
                    		int baseIndex = content.indexOf(prefixString);
                    		String left = pc.getContent().substring(0, baseIndex);
                    		String center = owerStr;
                    		String right = pc.getContent().substring(baseIndex + prefixString.length());
                    		pc.setLeft(left);
                    		pc.setCenter(center);
                    		pc.setRight(right);
                    		pc.setPrefixString(owerStr);
                    		pc.setEmpty();
                    		newValues.add(pc);
                    	}
                    }
                    	
                }
  
                results.values = newValues;  
                results.count = newValues.size(); 
                
                TAGS.log("results.values: " +  results.values);
                TAGS.log("results.count: " + results.count);
            }
            
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mData = (List<UserName>) results.values;  
            if (results.count > 0) {  
                notifyDataSetChanged();  
            } else {  
                notifyDataSetInvalidated();  
            }  
		}
		
	}

	@Override
	public void onClick(View v) {
		final int position = (Integer) v.getTag();
		/*View view = View.inflate(mContext, ResourceUtils.getLayoutId(mContext, "dy_delete_record"), null);
		final Dialog dialog = new Dialog(mContext, themeId);
		dialog.setContentView(view);
		dialog.show();
		
		TextView cancelBtn = (TextView) view.findViewById(ResourceUtils.getId(mContext, "cancelBtn"));
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(mLoginEvents != null)
					mLoginEvents.deleteRecord(false);
			}
		});
		
		TextView deleteBtn = (TextView) view.findViewById(ResourceUtils.getId(mContext, "deleteBtn"));
		deleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(mLoginEvents != null)
					mLoginEvents.deleteRecord(true);
			}
		});*/
		
		
		UserName userName = mData.remove(position);
		if(mUnfilteredData != null)
			mUnfilteredData.remove(position);
		UserInfo userInfo = LoginRecords.getInstance(mContext).deleteUserInfo(userName.getContent());
		if(mLoginEvents != null) {
			mLoginEvents.deleteRecord(userInfo, DyLoginActivity.DELETE_USER_ACTV);
		}
		
		notifyDataSetChanged();
		
	}
	
	public void deleteUserInfo(UserInfo userInfo) {
		if(userInfo != null) {
			mData.remove(new UserName(userInfo.getLoginName()));
			if(mUnfilteredData != null)
				mUnfilteredData.remove(new UserName(userInfo.getLoginName()));
		}
	}
	
	private final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(mLoginEvents != null) {
				UserInfo userInfo = LoginRecords.getInstance(mContext).getAllMapUsers().get(mData.get(position).getContent());
				mLoginEvents.selectUser(userInfo);
			}
		}
	};

}
