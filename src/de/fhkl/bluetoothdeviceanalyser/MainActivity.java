package de.fhkl.bluetoothdeviceanalyser;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements
	ActionBar.TabListener, OnPageChangeListener
{
	protected BluetoothDeviceListAdapter mListAdapter;
	protected ViewPager mViewPager;
	private ActionBar mActionBar;
	
	protected class PagerAdapter extends FragmentPagerAdapter
	{
		Fragment mFragments[] = new Fragment[2];

		public PagerAdapter(FragmentManager fm)
		{
			super(fm);
			mFragments[0] = new DeviceListTabFragment();
			mFragments[1] = new AnalyzeTabFragment();
		}

		@Override
		public int getCount()
		{
			return mFragments.length;
		}

		@Override
		public Fragment getItem(int position)
		{
			return mFragments[position];
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		mViewPager.setOnPageChangeListener(this);
		
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.addTab(getActionBar().newTab().setText("Device list").setTabListener(this));
		mActionBar.addTab(getActionBar().newTab().setText("Analyze").setTabListener(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0)
	{
		mActionBar.setSelectedNavigationItem(arg0);
	}

}
