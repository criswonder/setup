package com.xixi.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xixi.phonenumbermanager.entity.Contact;
import com.xixi.phonenumbermanager.utils.DataHelper;

public class StrangerFragment extends Fragment {
	private ListView mlst_v;
	private String[] contactedPhoneNumbers;
	private String[] notContactedPhoneNumbers;
	private List<Contact> contactedContacts;
	private List<Contact> notContactedContacts;
	public Context mContext ;
	public static List<Contact> mContacts;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View fuck = View.inflate(getActivity(), R.layout.activity_stranger,
				null);
		mContext = getActivity();
		mlst_v = (ListView) fuck.findViewById(R.id.lst_main);
		mContacts = DataHelper.notContactedContacts;
		mlst_v.setAdapter(new ContactLstAdapter());
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

}
