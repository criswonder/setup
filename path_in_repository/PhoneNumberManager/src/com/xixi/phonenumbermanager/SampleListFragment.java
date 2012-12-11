package com.xixi.phonenumbermanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mucang.android.wzlite.more.Car;
import cn.mucang.android.wzlite.more.FeedBackListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SampleListFragment extends ListFragment implements OnItemClickListener {
	View lst_main;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_more_lst, null);
		lst_main = v.findViewById(android.R.id.list);
		ListView lst_v = (ListView)lst_main;
		lst_v.setAdapter(new SampleAdapter(this.getActivity()));
		lst_v.setOnItemClickListener(this);
		return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < 4; i++) {
			adapter.add(new SampleItem("Sample List", android.R.drawable.ic_menu_search));
		}
		setListAdapter(adapter);
	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_menu_list_item, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.menuText);
			tv.setText("用户反馈");
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UMFeedbackService.openUmengFeedbackSDK(this);
		
		
		FeedBackListener listener = new FeedBackListener() {
		    @Override
		    public void onSubmitFB(Activity activity) {
		    	Map<String, String> contactMap = new HashMap<String, String>();
		        int i=1;
		        UMFeedbackService.setContactMap(contactMap);
		       /* Map<String, String> remarkMap = new HashMap<String, String>();
		        remarkMap.put("name", "mao");
		        UMFeedbackService.setRemarkMap(remarkMap);*/
		    }
		    @Override
		    public void onResetFB(Activity activity,
		            Map<String, String> contactMap,
		            Map<String, String> remarkMap) {
		        // TODO Auto-generated method stub`
		        // FB initialize itself,load other attribute
		        // from local storage and set them
		        /*EditText phoneText = (EditText) activity
		                .findViewById(R.id.feedback_phone);
		        EditText qqText = (EditText) activity
		                .findViewById(R.id.feedback_qq);
		        EditText nameText = (EditText) activity
		                .findViewById(R.id.feedback_name);
		        EditText emailText = (EditText) activity
		                .findViewById(R.id.feedback_email);
		        if (remarkMap != null) {
		            nameText.setText(remarkMap.get("name"));
		            emailText.setText(remarkMap
		                    .get("email"));
		        }
		        if (contactMap != null) {
		            phoneText.setText(contactMap
		                    .get("phone"));
		            qqText.setText(contactMap.get("qq"));
		        }*/
		    }
		};
		UMFeedbackService.setFeedBackListener(listener);   
		break;
	}
}
