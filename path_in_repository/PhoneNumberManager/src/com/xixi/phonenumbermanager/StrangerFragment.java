package com.xixi.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xixi.phonenumbermanager.entity.Contact;

public class StrangerFragment extends Fragment {
	private ListView mlst_v;
	private String[] contactedPhoneNumbers;
	private String[] notContactedPhoneNumbers;
	private List<Contact> contactedContacts;
	private List<Contact> notContactedContacts;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View fuck = View.inflate(getActivity(), R.layout.activity_stranger, null);
		mlst_v = (ListView)fuck.findViewById(R.id.lst_main);
		contactedContacts = new ArrayList<Contact>();
		notContactedContacts = new ArrayList<Contact>();
		return fuck;
	}
}
