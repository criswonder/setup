package com.xixi.phonenumbermanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.util.Log;
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
		readCallLogs();
		contactedContacts = new ArrayList<Contact>();
		notContactedContacts = new ArrayList<Contact>();
		getStrangers();
		getSms();
		return fuck;
	}
	private void getStrangers() {
		for(Contact cont:ContactedFragment.mContacts){
			String contNum = cont.getPhoneNum();
			Boolean find= false;
			for(int i=0;i<contactedPhoneNumbers.length;i++){
				if(contNum.equals(contactedPhoneNumbers[i])){
					contactedContacts.add(cont);
					find = true;
					break;
				}else{
					continue;
				}
			}
			if(!find){
				notContactedContacts.add(cont);
			}
		}
	}
	@SuppressWarnings("unused")
	public void readCallLogs(){
//		Cursor cursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI,new String["name","number","duration"], CallLog.Calls.DATE + " > " + 0);
		Uri uri = CallLog.Calls.CONTENT_URI;
		
		String[] projection = new String[]{CallLog.Calls.CACHED_NAME,CallLog.Calls.CACHED_NUMBER_LABEL,
				CallLog.Calls.DURATION,CallLog.Calls.NUMBER};
		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = getActivity().getContentResolver().query(uri, projection , 
		CallLog.Calls.DURATION+">0",
				null, null);
		
		if(cursor!=null){
			contactedPhoneNumbers = new String[cursor.getCount()];
			int i=0;
			while(cursor.moveToNext()){
				
				String name = cursor.getString(0);
				cursor.getString(1);
				cursor.getString(1);
				int dur = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
				String name1 = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
				String num = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
				String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
				Log.i("", "name="+name1+",num="+num+",dur="+dur+",number="+number);
				contactedPhoneNumbers[i] = number;
				i++;
				
			}
			
		}
	}
	
	@SuppressWarnings("unused")
	public void getSms(){

		final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";
		final String SMS_URI_OUTBOX = "content://sms/outbox";
		final String SMS_URI_FAILED = "content://sms/failed";
		final String SMS_URI_QUEUED = "content://sms/queued";
		
		Uri sms = Uri.parse("content://sms/");
        ContentResolver cr = getActivity().getContentResolver();
        Cursor c = cr.query(sms, null, null, null, null);
        for (int i = 0; i < c.getColumnCount(); i++)
        {
            Log.v("tables", c.getColumnName(i).toString());
        }
        c.close();
		if(1!=1)return;
		StringBuilder smsBuilder = new StringBuilder();

		try {
			Uri uri = Uri.parse(SMS_URI_ALL);
			String[] projection = new String[] { "address", "count(address) as fuck" };
			Cursor cur = getActivity().getContentResolver().query(uri, projection, "1=1  )group by address--", null, "date desc");		// 获取手机内部短信

			if (cur.moveToFirst()) {
				cur.getString(0);
				cur.getString(1);
				int index_Address = cur.getColumnIndex("address");
				int index_Person = cur.getColumnIndex("person");
				int index_Body = cur.getColumnIndex("body");
				int index_Date = cur.getColumnIndex("date");
				int index_Type = cur.getColumnIndex("type");

				do {
					String strAddress = cur.getString(index_Address);
					int intPerson = cur.getInt(index_Person);
					String strbody = cur.getString(index_Body);
					long longDate = cur.getLong(index_Date);
					int intType = cur.getInt(index_Type);

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date d = new Date(longDate);
					String strDate = dateFormat.format(d);

					String strType = "";
					if (intType == 1) {
						strType = "接收";
					} else if (intType == 2) {
						strType = "发送";
					} else {
						strType = "null";
					}

					smsBuilder.append("[ ");
					smsBuilder.append(strAddress + ", ");
					smsBuilder.append(intPerson + ", ");
					smsBuilder.append(strbody + ", ");
					smsBuilder.append(strDate + ", ");
					smsBuilder.append(strType);
					smsBuilder.append(" ]\n\n");
					Log.i("sms", smsBuilder.toString());
				} while (cur.moveToNext());

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				smsBuilder.append("no result!");
			} // end if

			smsBuilder.append("getSmsInPhone has executed!");

		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}

		 smsBuilder.toString();
	
		
	}
}
