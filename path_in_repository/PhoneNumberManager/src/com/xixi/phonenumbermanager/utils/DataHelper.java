package com.xixi.phonenumbermanager.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Log;

import com.xixi.phonenumbermanager.R;
import com.xixi.phonenumbermanager.XxApplication;
import com.xixi.phonenumbermanager.entity.Contact;

public class DataHelper {
	public static List<Contact> mContacts;
	private static Context mContext = XxApplication.getContext();
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** 联系人显示名称 **/

	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;
	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;
	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	
	private static String[] contactedPhoneNumbers;
	private String[] notContactedPhoneNumbers;
	public static List<Contact> contactedContacts = new ArrayList<Contact>();
	public static List<Contact> notContactedContacts = new ArrayList<Contact>();

	public static void getPhoneContacts() {
		mContacts = new ArrayList<Contact>();
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			Contact contact;
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				contact = new Contact();
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);

				} else {
					contactPhoto = BitmapFactory.decodeResource(mContext.getResources(),
							R.drawable.ic_launcher);
				}
				contact.setName(contactName);
				contact.setPhoneNum(phoneNumber);
				mContacts.add(contact);

			}
			phoneCursor.close();

		}

	}
	
	public static void getStrangers() {
		for(Contact cont:mContacts){
			String contNum = cont.getPhoneNum();
			Boolean find= false;
			String str[];
			for(int i=0;i<contactedPhoneNumbers.length;i++){
				str = contactedPhoneNumbers[i].split(",");
				if(contNum.equals(str[0])){
					cont.setCallLogCounter(Integer.parseInt(str[1]));
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
	public static void getSms(){

		final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";
		final String SMS_URI_OUTBOX = "content://sms/outbox";
		final String SMS_URI_FAILED = "content://sms/failed";
		final String SMS_URI_QUEUED = "content://sms/queued";
		
		Uri sms = Uri.parse("content://sms/");
        ContentResolver cr = mContext.getContentResolver();
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
			String[] projection = new String[] { "replace(replace(address,'+86',''),' ','') as phoneNum","count(*) as fuck" };
			Cursor cur = mContext.getContentResolver().query(uri, projection, "1=1  )group by phoneNum--", null, "date desc");		// 获取手机内部短信

			if (cur.moveToFirst()) {
				do{
				String address = cur.getString(0);
				String addressCount= cur.getString(1);
				Contact c1= contactedPhoneNumbersContains(address);
				if(c1!=null){
					c1.setSmsCounter(Integer.parseInt(addressCount));
				}else{
					c1 = getContactFromContacts(address);
					if(c1!=null){
						c1.setSmsCounter(Integer.parseInt(addressCount));
						contactedContacts.add(c1);
					}
				}
				Log.d("readSms", address+",count="+addressCount);
				}while(cur.moveToNext());
				for(Object obj:contactedContacts){
					Log.i("readSms", obj.toString());
				}
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
	private static Contact getContactFromContacts(String address) {
		for(Contact con:mContacts){
			if(address.equalsIgnoreCase(con.getPhoneNum())){
				return con;
			}
		}
		return null;
	}
	private static Contact contactedPhoneNumbersContains(String address) {
		for(Contact con:contactedContacts){
			if(address.equalsIgnoreCase(con.getPhoneNum())){
				return con;
			}
		}
		return null;
	}
	@SuppressWarnings("unused")
	public static void readCallLogs(){
//		Cursor cursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI,new String["name","number","duration"], CallLog.Calls.DATE + " > " + 0);
		Uri uri = CallLog.Calls.CONTENT_URI;
		
		String[] projection = new String[]{CallLog.Calls.CACHED_NAME,CallLog.Calls.CACHED_NUMBER_LABEL,
				CallLog.Calls.DURATION,CallLog.Calls.NUMBER};
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = mContext.getContentResolver().query(uri, projection , 
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
				contactedPhoneNumbers[i] = number+","+dur;
				i++;
				
			}
			
		}
	}
	/**
	 * Delete callLogs maybe need backup calllogs
	 * @param phoneNum
	 * @return
	 */
	public static boolean deleteCallLog(String num,boolean needBackUp){
		//TODO
		String nums[] = new String[]{num};
		ContentResolver resv = mContext.getContentResolver();
		Uri uri = CallLog.Calls.CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put("mark_deleted", 1);
//		int uRows = resv.update(uri, values, CallLog.Calls.NUMBER+"=?", nums);
		if(needBackUp){
			Cursor original  = resv.query(Uri.withAppendedPath(CallLog.Calls.CONTENT_FILTER_URI, num), 
					new String[]{CallLog.Calls.DATE,
									CallLog.Calls.DURATION,CallLog.Calls.TYPE,CallLog.Calls._ID
					}, null	, null, null);
			if(original!=null){
				int i=1;
				while(original.moveToNext()){
					original.getCount();
					ContentValues cvalues = new ContentValues();
					cvalues.put(CallLog.Calls.NUMBER, num);
					cvalues.put(CallLog.Calls.DATE, original.getLong(0));
					cvalues.put(CallLog.Calls.DURATION, original.getInt(1));
					cvalues.put(CallLog.Calls.TYPE, original.getInt(2));
					cvalues.put(CallLog.Calls._ID, original.getInt(3));
					Calendar cal = Calendar.getInstance(Locale.CHINA);
					cal.setTimeInMillis(cvalues.getAsLong(CallLog.Calls.DATE));
//					Date date = cal.getTime();
//					String str = cal.get(Calendar.YEAR)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.DAY_OF_MONTH)+" " +cal.get(Calendar.MINUTE) +":"+cal.get(Calendar.SECOND);
					resv.insert(Uri.parse(LocalContentProvider.bak_uri.toString()+"/callLogs"), cvalues);
					i++;
					
				}
//				if(original.moveToFirst()){
//					do{
//						original.getCount();
//						ContentValues cvalues = new ContentValues();
//						cvalues.put(CallLog.Calls.NUMBER, num);
//						cvalues.put(CallLog.Calls.DURATION, original.getInt(0));
//						cvalues.put(CallLog.Calls.DATE, original.getInt(1));
//						
//						resv.insert(Uri.parse(LocalContentProvider.bak_uri.toString()+"/callLogs"), cvalues);
//					}while(original.moveToNext());
//					
//				}
				
			}
		}else{
			
		}
		int rows = mContext.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.NUMBER+"=?", nums);
		return false;
	}
	public static boolean deleteSms(String num,boolean needBackUp){
		return false;
	}

	public static void recover(String mPhoneNum) {
		ContentResolver resv = mContext.getContentResolver();
		Cursor c1 = resv.query(Uri.parse(LocalContentProvider.bak_uri.toString()+"/callLogs"), 
						new String[]{CallLog.Calls.DURATION,CallLog.Calls.DATE,CallLog.Calls._ID,CallLog.Calls.TYPE},
						"number=?", new String[]{mPhoneNum	}, null);
		if(c1!=null){
			int i=1;
			while(c1.moveToNext()){
				ContentValues values = new ContentValues();
				values.put(CallLog.Calls.NUMBER, mPhoneNum);
				values.put(CallLog.Calls.DURATION, c1.getInt(0));
				values.put(CallLog.Calls.DATE, c1.getLong(1));
				values.put(CallLog.Calls._ID, c1.getLong(2));
				values.put(CallLog.Calls.TYPE, c1.getInt(3));
				resv = mContext.getContentResolver();
				c1.getCount();
				resv.insert(CallLog.Calls.CONTENT_URI, values);
				Log.i("recoverSuccessful", ""+i);
				i++;
			}
			
		}
	}
}
