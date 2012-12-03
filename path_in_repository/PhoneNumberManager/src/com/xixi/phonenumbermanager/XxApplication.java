package com.xixi.phonenumbermanager;

import android.app.Application;
import android.content.Context;

public class XxApplication extends Application {
	
	public static Application getContext(){
		return application;
	}
	private static XxApplication application;
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}
	

}
