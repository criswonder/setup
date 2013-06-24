package com.xixi.phonenumbermanager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ViewPagerActivity extends BaseActivity {

	public ViewPagerActivity() {
		super(R.string.viewpager);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = View.inflate(getApplicationContext(), R.layout.activity_main, null);
		setContentView(v);
		ViewPager vp = (ViewPager)v.findViewById(R.id.viewPager);
		vp.setId("VP".hashCode());
		vp.setAdapter(new ColorPagerAdapter(getSupportFragmentManager()));

		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
					findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
					findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
					break;
				default:
					findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
					findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					break;
				}
			}

		});
		
		vp.setCurrentItem(0);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	public class ColorPagerAdapter extends FragmentPagerAdapter {
		
		private ArrayList<Fragment> mFragments;
 
		
		public ColorPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<Fragment>();
			mFragments.add(new ContactedFragment());
			mFragments.add(new StrangerFragment());
			
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMenu1 = menu.addSubMenu("Action Item");
        subMenu1.add("拨打电话");
        subMenu1.add("发送短信");
        subMenu1.add("删除短信");

        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.ic_title_share_default);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		super.onCreateOptionsMenu(menu);
		return true;
	}

}
