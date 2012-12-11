package com.xixi.phonenumbermanager;

import android.os.Bundle;

import com.slidingmenu.lib.app.SlidingActivity;

public class SlideTest extends SlidingActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.activity_more_lst);
		setContentView(R.layout.activity_contacted);
	}
}
