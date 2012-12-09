package com.xixi.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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

public class StrangerFragment extends Fragment implements OnClickListener, IMyClicker {
	private ListView mlst_v;
	private String[] contactedPhoneNumbers;
	private String[] notContactedPhoneNumbers;
	private List<Contact> contactedContacts;
	private List<Contact> notContactedContacts;
	public Context mContext ;
	public static List<Contact> mContacts;
	private String mPhoneNum;
	private String mName;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View fuck = View.inflate(getActivity(), R.layout.activity_stranger,
				null);
		mContext = getActivity();
		mlst_v = (ListView) fuck.findViewById(R.id.lst_main);
		mContacts  = new ArrayList<Contact>();
		mContacts.addAll(DataHelper.mNotContactedContacts.values());
		mlst_v.setAdapter(new ContactLstAdapter());
		
		mlst_v.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Contact cont = (Contact)arg0.getAdapter().getItem(arg2);
				mPhoneNum = cont.getPhoneNum();
				mName = cont.getName();
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				AlertDialog dialog = builder.create();
				dialog.show();
				dialog.setContentView(R.layout.operation_menu_stranger);
				
				dialog.findViewById(R.id.btn_delete_contact).setOnClickListener(StrangerFragment.this);
				dialog.findViewById(R.id.btn_call).setOnClickListener(StrangerFragment.this);
				dialog.findViewById(R.id.btn_sms).setOnClickListener(StrangerFragment.this);
				dialog.findViewById(R.id.btn_recover).setOnClickListener(StrangerFragment.this);
				return false;
			}
		});
		return fuck;
	}

	class ContactLstAdapter extends BaseAdapter {

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
			View v = View.inflate(mContext , R.layout.lst_item_stranger, null);
			Contact con = (Contact) getItem(position);
			TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_name.setText(con.getName());
			TextView tv_phoneNum = (TextView) v.findViewById(R.id.tv_phoneNum);
			tv_phoneNum.setText(con.getPhoneNum());

			return v;
		}

	}
	

	@Override
	public void onClick(View v) {
		if(TextUtils.isEmpty(mPhoneNum))
			return;
		if(v.getId()==R.id.btn_delete_contact){
//			DataHelper.deleteContact(mName,mPhoneNum, true);
			ViewHelper.performClick(this,v.getId());
		}else if(v.getId()==R.id.btn_delete_sms){
			
		}else if(v.getId()==R.id.btn_sms){
			Intent it = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+mPhoneNum));
	        it.putExtra("sms_body", "");
	        startActivity(it);
		}else if(v.getId()==R.id.btn_call){
			Intent intent =  new Intent("android.intent.action.DIAL", Uri.parse("tel:"+mPhoneNum));
			mContext.startActivity(intent);
		}else if(v.getId()==R.id.btn_recover){
//			DataHelper.recoverCallLogs(mPhoneNum);
			DataHelper.recoverContact(mPhoneNum);
			
		}
	}
	@Override
	public void doClick(int i){
		if(1==1)
			return;
		DataHelper.deleteContact(mName,mPhoneNum, true);
	}
}
