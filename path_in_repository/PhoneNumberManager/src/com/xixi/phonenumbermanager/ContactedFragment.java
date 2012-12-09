package com.xixi.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xixi.phonenumbermanager.entity.Contact;
import com.xixi.phonenumbermanager.utils.DataHelper;
import com.xixi.phonenumbermanager.utils.ViewHelper;

public class ContactedFragment extends Fragment implements OnClickListener, IMyClicker {

	private int mColorRes = -1;
	private Context mContext;
	public static List<Contact> mContacts;

	
	private ListView mlst_v;
	private String mPhoneNum;
	
	public ContactedFragment() {
		this(R.color.white);
	}

	public ContactedFragment(int colorRes) {
		mColorRes = colorRes;
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mColorRes = savedInstanceState.getInt("mColorRes");
		mContext = getActivity();
		DataHelper.getPhoneContacts();
		DataHelper.readCallLogs();
		DataHelper.getStrangers();
		DataHelper.getSms();
		
		mContacts  = new ArrayList<Contact>();
		mContacts.addAll(DataHelper.mContactedContacts.values());
		View fuck = View.inflate(getActivity(), R.layout.activity_contacted, null);
		mlst_v = (ListView)fuck.findViewById(R.id.lst_main);
		mlst_v.setAdapter(new ContactLstAdapter());
		mlst_v.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Contact cont = (Contact)arg0.getAdapter().getItem(arg2);
				mPhoneNum = cont.getPhoneNum();
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				AlertDialog dialog = builder.create();
				dialog.show();
				dialog.setContentView(R.layout.operation_menu_contacted);
				
				dialog.findViewById(R.id.btn_delete_calllog).setOnClickListener(ContactedFragment.this);
				dialog.findViewById(R.id.btn_delete_sms).setOnClickListener(ContactedFragment.this);
				dialog.findViewById(R.id.btn_call).setOnClickListener(ContactedFragment.this);
				dialog.findViewById(R.id.btn_sms).setOnClickListener(ContactedFragment.this);
				dialog.findViewById(R.id.btn_recover).setOnClickListener(ContactedFragment.this);
				return false;
			}
		});
		return fuck;
	}

	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mColorRes);
	}
	
	 class ContactLstAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContacts.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mContacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = View.inflate(mContext, R.layout.lst_item_contacted, null);
			Contact con=(Contact)getItem(position);
			TextView tv_name = (TextView)v.findViewById(R.id.tv_name);
			tv_name.setText(con.getName());
			TextView tv_phoneNum = (TextView)v.findViewById(R.id.tv_phoneNum);
			TextView tv_phoneTime = (TextView)v.findViewById(R.id.tv_phoneTotalTime);
			TextView tv_smsTotalNumber= (TextView)v.findViewById(R.id.tv_phoneSmsNumbers);
			tv_phoneTime.setText(con.getCallLogCounter()+"");
			tv_smsTotalNumber.setText(con.getSmsCounter()+"");
			tv_phoneNum.setText(con.getPhoneNum());
			
			return v;
		}
		
		
	}

	@Override
	public void onClick(View v) {
		if(TextUtils.isEmpty(mPhoneNum))
			return;
		if(v.getId()==R.id.btn_delete_calllog){
			ViewHelper.performClick(this, v.getId());
		}else if(v.getId()==R.id.btn_delete_sms){
			ViewHelper.performClick(this, v.getId());
		}else if(v.getId()==R.id.btn_sms){
			Intent it = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+mPhoneNum));
	        it.putExtra("sms_body", "");
//	        it.setType("vnd.android-dir/mms-sms");
	        startActivity(it);
		}else if(v.getId()==R.id.btn_call){
			Intent intent =  new Intent("android.intent.action.DIAL", Uri.parse("tel:"+mPhoneNum));
			mContext.startActivity(intent);
			
		}else if(v.getId()==R.id.btn_recover){
//			DataHelper.recoverCallLogs(mPhoneNum);
			DataHelper.recoverSmss(mPhoneNum);
			
		}
	}

	@Override
	public void doClick(int i) {
		if(i==R.id.btn_delete_calllog){
			DataHelper.deleteCallLog(mPhoneNum, true);
		}else if(i==R.id.btn_delete_sms){
			DataHelper.deleteSms(mPhoneNum, true);
		}
	}

}
