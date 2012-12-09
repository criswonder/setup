package com.xixi.phonenumbermanager.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;

import com.xixi.phonenumbermanager.IMyClicker;
import com.xixi.phonenumbermanager.XxApplication;

public class ViewHelper {
	public static void performClick(final IMyClicker click,final int i){
		AlertDialog.Builder builder = new Builder(((Fragment)click).getActivity() ) ;
		builder.setMessage("确认删除吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				click.doClick(i);
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}
}
