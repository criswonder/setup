package com.xixi.phonenumbermanager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xixi.phonenumbermanager.entity.Contact;

public class ContactedFragment extends Fragment {

	private int mColorRes = -1;
	private Context mContext;
	public static List<Contact> mContacts;

	
	private ListView mlst_v;
	
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
		
		mContacts = DataHelper.contactedContacts;
		View fuck = View.inflate(getActivity(), R.layout.activity_contacted, null);
		mlst_v = (ListView)fuck.findViewById(R.id.lst_main);
		mlst_v.setAdapter(new ContactLstAdapter());
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

}
