package com.xixi.phonenumbermanager.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
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
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;

import com.xixi.phonenumbermanager.R;
import com.xixi.phonenumbermanager.XxApplication;
import com.xixi.phonenumbermanager.entity.Contact;

public class DataHelper {
	private static final String[] SMS_LOCAL_TABLE_COLUMNS = new String[]{"_id","address","date","type","body"};
	private static final Uri SMS_LOCAL_URI = Uri.parse("content://com.xixi.pnm/smss");
	private static final Uri CONTACT_LOCAL_URI = Uri.parse("content://com.xixi.pnm/contacts");
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
//	public static List<Contact> contactedContacts = new ArrayList<Contact>();
//	public static List<Contact> notContactedContacts = new ArrayList<Contact>();

	
	public static Map<String, Integer> mMap = new HashMap<String, Integer>();
	public static Map<String, Contact> mContactedContacts  = new HashMap<String, Contact>();
	public static Map<String, Contact> mNotContactedContacts  = new HashMap<String, Contact>();
	
	public static void getPhoneContacts() {
		mContacts = new ArrayList<Contact>();
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			Contact contact;
			Log.i("getPhoneContacts", "totoal items="+phoneCursor.getCount());
			int i=1;
			while (phoneCursor.moveToNext()) {
				Log.i("getPhoneContacts", "item "+i);
				i++;
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
//		Log.i("getStrangers", "mContacts.size;contactedPhoneNumbers.length"+mContacts.size()+","+contactedPhoneNumbers.length);
		long startTime = System.currentTimeMillis();
		for(Contact cont:mContacts){
			String contNum = cont.getPhoneNum();
			Boolean find= false;
			if(mMap.containsKey(contNum)){
				cont.setCallLogCounter(mMap.get(contNum));
//				contactedContacts.add(cont);
				mContactedContacts.put(contNum, cont);
				find = true;
			}
//			String str[];
//			for(int i=0;i<contactedPhoneNumbers.length;i++){
//				str = contactedPhoneNumbers[i].split(",");
//				if(contNum.equals(str[0])){
//					cont.setCallLogCounter(Integer.parseInt(str[1]));
//					contactedContacts.add(cont);
//					find = true;
//					break;
//				}else{
//					continue;
//				}
//			}
			if(!find){
//				notContactedContacts.add(cont);
				mNotContactedContacts.put(contNum, cont);
			}
		}
		long finishTime = System.currentTimeMillis();
		tellMeHowLong(startTime, finishTime);
		
	}
	 protected static void tellMeHowLong(long startTime, long finishTime) {
	        long total = finishTime - startTime;
	        float hours = (float) Math.floor(total / 1000 / 60 / 60);
	        float minutes = (float) Math.floor(total / 1000 / 60) - hours * 60;
	        float seconds = (float)total / 1000 - hours * 60 * 60 - minutes * 60;
	        System.out.println(total);
	        System.out.println("expends:   H" + hours + ":M" + minutes + ":S" + seconds);
	    }
	static final String SMS_URI_ALL = "content://sms/";
	@SuppressWarnings("unused")
	public static void getSms(){
		long startTime = System.currentTimeMillis();
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";
		final String SMS_URI_OUTBOX = "content://sms/outbox";
		final String SMS_URI_FAILED = "content://sms/failed";
		final String SMS_URI_QUEUED = "content://sms/queued";
		
		Uri sms = Uri.parse("content://sms/");
        ContentResolver cr = mContext.getContentResolver();
        //TODO this is to check columns contained in a table.
//        Cursor c = cr.query(sms, null, null, null, null);
//        for (int i = 0; i < c.getColumnCount(); i++)
//        {
//            Log.v("tables", c.getColumnName(i).toString());
//        }
//        c.close();
//		if(1!=1)return;
		StringBuilder smsBuilder = new StringBuilder();

		try {
			Uri uri = Uri.parse(SMS_URI_ALL);
			String[] projection = new String[] { "replace(replace(address,'+86',''),' ','') as phoneNum","count(*) as fuck" };
			Cursor cur = cr.query(uri, projection, "1=1  )group by phoneNum--", null, "date desc");		// 获取手机内部短信
			int i =1;
			if (cur.moveToFirst()) {
				Log.i("getSms", "sms total items = "+cur.getCount());
				do {
					Log.i("getSms", "sms item = "+i);
					i++;
					String address = cur.getString(0);
					String addressCount = cur.getString(1);
					Contact c1 = contactedPhoneNumbersContains(address);
					if (c1 != null) {
						c1.setSmsCounter(Integer.parseInt(addressCount));
					} else {
						c1 = notContactedPhoneNumbersContains(addressCount);
						if (c1 != null) {
							c1.setSmsCounter(Integer.parseInt(addressCount));
							mNotContactedContacts.remove(address);
							mContactedContacts.put(address, c1);
						}
					}
//					Log.d("readSms", address + ",count=" + addressCount);
				} while (cur.moveToNext());
//				for(Object obj:contactedContacts){
//					Log.i("readSms", obj.toString());
//				}
				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				smsBuilder.append("no result!");
			} // end if

//			smsBuilder.append("getSmsInPhone has executed!");
			long end = System.currentTimeMillis();
			tellMeHowLong(startTime, end);
		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}

		 smsBuilder.toString();
	}
	private static Contact getContactFromContacts(String address) {
		if(address!=null){
			for(Contact con:mContacts){
				if(address.equalsIgnoreCase(con.getPhoneNum())){
					return con;
				}
			}
		}
		return null;
	}
	private static Contact contactedPhoneNumbersContains(String address) {
		if(address!=null){
//			for(Contact con:contactedContacts){
//				if(address.equalsIgnoreCase(con.getPhoneNum())){
//					return con;
//				}
//			}
			if(mContactedContacts.containsKey(address)){
				return mContactedContacts.get(address);
			}
		}
		return null;
	}
	private static Contact notContactedPhoneNumbersContains(String address) {
		if(address!=null){
			if(mNotContactedContacts.containsKey(address)){
				return mNotContactedContacts.get(address);
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
			Log.i("readCallLogs", "totoal items="+cursor.getCount());
//			contactedPhoneNumbers = new String[cursor.getCount()];
			int i=0;
			while(cursor.moveToNext()){
				Log.i("readCallLogs", "item="+i);
				String name = cursor.getString(0);
				cursor.getString(1);
				cursor.getString(1);
				int dur = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
//				String name1 = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
//				String num = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
				String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
//				Log.i("", "name="+name1+",num="+num+",dur="+dur+",number="+number);
//				contactedPhoneNumbers[i] = number+","+dur;
				mMap.put(number, dur);
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
				String nums[] = new String[]{num};
				ContentResolver resv = mContext.getContentResolver();
				Uri smsUri = Uri.parse(SMS_URI_ALL);
				if(needBackUp){
					Cursor original  = resv.query(smsUri, 
							SMS_LOCAL_TABLE_COLUMNS
							, "address=?", new String[]{num}, null);
					if(original!=null){
						int i=1;
						while(original.moveToNext()){
							original.getCount();
							ContentValues values = new ContentValues();
							values.put("_id", original.getLong(0));
							values.put("address", num);
							values.put("date", original.getLong(2));
							values.put("type", original.getInt(3));
							values.put("body", original.getString(4));
							Calendar cal = Calendar.getInstance(Locale.CHINA);
							cal.setTimeInMillis(values.getAsLong(CallLog.Calls.DATE));
//							Date date = cal.getTime();
//							String str = cal.get(Calendar.YEAR)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.DAY_OF_MONTH)+" " +cal.get(Calendar.MINUTE) +":"+cal.get(Calendar.SECOND);
							resv.insert(SMS_LOCAL_URI, values);
							i++;
							
						}
//						if(original.moveToFirst()){
//							do{
//								original.getCount();
//								ContentValues cvalues = new ContentValues();
//								cvalues.put(CallLog.Calls.NUMBER, num);
//								cvalues.put(CallLog.Calls.DURATION, original.getInt(0));
//								cvalues.put(CallLog.Calls.DATE, original.getInt(1));
//								
//								resv.insert(Uri.parse(LocalContentProvider.bak_uri.toString()+"/callLogs"), cvalues);
//							}while(original.moveToNext());
//							
//						}
						
					}
				}else{
					
				}
				int rows = mContext.getContentResolver().delete(smsUri, "address=?", nums);
				return false;
	}

	public static void recoverCallLogs(String mPhoneNum) {
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
	public static void recoverContact(String mPhoneNum) {
		if(1==1)
		{
//			insertContact();
//			insert("mao","hongyun","150888888888",null);
			insertContact(mPhoneNum, mPhoneNum);
			return;
		}
		ContentResolver resv = mContext.getContentResolver();
		Cursor c1 = resv.query(CONTACT_LOCAL_URI, 
				new String[]{"name","number"},
				"number=?", new String[]{mPhoneNum	}, null);
		if(c1!=null){
			int i=1;
			while(c1.moveToNext()){
				String name = c1.getString(0);
				String number = c1.getString(1);
				
				 ContentValues values = new ContentValues();
				 values.put(Data.RAW_CONTACT_ID, 12);
				 values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				 values.put(Phone.NUMBER, number);
				 values.put(Phone.TYPE, Phone.TYPE_CUSTOM);
				 values.put(Phone.LABEL, name);
				 Uri dataUri = resv.insert(Data.CONTENT_URI, values);
				Log.i("recoverSuccessful", ""+i);
				i++;
			}
			
		}
	}
	/** ---------------------------------------------------------------------------------------------------------
	 * NOTICE: If you set ContactsContract.CommonDataKinds.Email.TYPE with "qq" or other non specify value, you will get
	 * nonpointer exception on 1s phone. so do   ContactsContract.CommonDataKinds.Phone.TYPE
	 ---------------------------------------------------------------------------------------------------------*/
	private static void insertContact(){
		
		 ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>(); 
         op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
             .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.android.contacts.sim") 
             .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "SIM") 
             .withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT) 
             .build()); 

      // first and last names 
           op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
       .withValueBackReference(Data.RAW_CONTACT_ID, 0) 
             .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE) 
             .withValue(StructuredName.GIVEN_NAME, "毛红云") 
             .build()); 

           op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
                   .withValueBackReference(Data.RAW_CONTACT_ID, 0) 
                   .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                   .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "1231231232312")
                   .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                   .build());
           op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
                   .withValueBackReference(Data.RAW_CONTACT_ID, 0)

           .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
           .withValue(ContactsContract.CommonDataKinds.Email.DATA, "andymao@qq.com")
           .withValue(ContactsContract.CommonDataKinds.Email.TYPE, "qq")// this will cause the above notice error
           .build());

      try{ 
       ContentProviderResult[] results = mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, op_list); 
      }catch(Exception e){ 
       e.printStackTrace(); 
      } 
	}
	public static void recoverSmss(String mPhoneNum) {
		ContentResolver resv = mContext.getContentResolver();
		Cursor c1 = resv.query(SMS_LOCAL_URI, 
				SMS_LOCAL_TABLE_COLUMNS,
				"address=?", new String[]{mPhoneNum	}, null);
		if(c1!=null){
			int i=1;
			c1.moveToFirst();
			while(c1.moveToNext()){
				ContentValues values = new ContentValues();
				values.put("_id", c1.getLong(0));
				values.put("address", mPhoneNum);
				values.put("date", c1.getLong(2));
				values.put("type", c1.getInt(3));
				values.put("body", c1.getString(4));
				resv = mContext.getContentResolver();
				c1.getCount();
				resv.insert(CallLog.Calls.CONTENT_URI, values);
				Log.i("recoverSuccessful", ""+i);
				i++;
			}
			
		}
	}

	public static boolean deleteContact(String name,String num, boolean backup){
		ContentResolver resv = mContext.getContentResolver();
		if (backup) {
			ContentValues values = new ContentValues(2);
			values.put("number", num);
			values.put("name", name);
			
			resv.insert(CONTACT_LOCAL_URI, values);
		} else {
			
		}
		Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(num));
		Cursor cur = mContext.getContentResolver().query(contactUri, null,
				null, null, null);
		try {
			if (cur.moveToFirst()) {
				do {
					if (cur.getString(
							cur.getColumnIndex(PhoneLookup.DISPLAY_NAME))
							.equalsIgnoreCase(name)) {
						String lookupKey = cur
								.getString(cur
										.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
						Uri uri = Uri.withAppendedPath(
								ContactsContract.Contacts.CONTENT_LOOKUP_URI,
								lookupKey);
						mContext.getContentResolver().delete(uri, null, null);
						return true;
					}

				} while (cur.moveToNext());
			}

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return false;
	}
	
	public static int insertContact(String firstName, String phoneNumber)
	{
	    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

	    Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
	    builder.withValue(RawContacts.ACCOUNT_TYPE, null);
	    builder.withValue(RawContacts.ACCOUNT_NAME, null);
	    ops.add(builder.build());

	    // Name
	    builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
	    builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
	    builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
	    builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName);
	    ops.add(builder.build());

	    // Number
	    builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
	    builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
	    builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
	    builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
	    builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
	    ops.add(builder.build());

	    // Picture
//	    try
//	    {
//	        Bitmap mBitmap = Media.getBitmap(context.getContentResolver(), Uri.parse(photo_uri));
//
//	        ByteArrayOutputStream image = new ByteArrayOutputStream();
//	        mBitmap.compress(Bitmap.CompressFormat.JPEG , 100, image);
//
//	        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
//	        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
//	        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
//	        builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray());
//	        ops.add(builder.build());
//	    }
//	    catch (Exception e)
//	    {
//	        e.printStackTrace();
//	    }

	    // Add the new contact
	    ContentProviderResult[] res;
	    try
	    {
	        res =mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	        if (res != null && res[0] != null)
	        {
	            String uri = res[0].uri.getPath().substring(14);
	            return new Integer(uri).intValue(); // Book ID
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    return -1;
	}
}
