package de.fhkl.bluetoothdeviceanalyser;

import java.util.HashMap;

import de.fhkl.bluetoothdeviceanalyser.BluetoothService.LocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
	private HashMap<BluetoothDevice, IGattDataProcessor> mDataProcessors = new HashMap<BluetoothDevice, IGattDataProcessor>();
	private BluetoothService mBluetoothService;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mBluetoothService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			LocalBinder binder = (LocalBinder) service;
			mBluetoothService = (BluetoothService) binder.getService();
		}
	};

	private BroadcastReceiver mDataReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action == BluetoothService.ACTION_DATA_AVAILABLE)
			{
				BluetoothDevice device = (BluetoothDevice) intent.getExtras()
						.get(BluetoothService.EXTRA_DEVICE);

				int datatype = intent.getExtras().getInt(
						BluetoothService.EXTRA_DATA_TYPE);
				if (datatype == BluetoothService.ID_DATATYPE_ADDED_TO_WATCHLIST)
				{
					if (mBluetoothService != null)
					{
						int devicetype = intent.getExtras().getInt(
								BluetoothService.EXTRA_DEVICE_TYPE);
						
						switch(devicetype)
						{
						case BluetoothService.DEVICE_TYPE_HRM:
						{
							BluetoothGatt gatt = mBluetoothService.mGatts.get(0);
							mDataProcessors.put(device, new HeartRateDataProcessor(
									gatt));
							break;
						}
						case BluetoothService.DEVICE_TYPE_WITHINGSWS30:
						{
							mDataProcessors.put(device, new WithingsWS30GattDummy());
							break;
						}
						}
					}
				}
				else
				{
					IGattDataProcessor processor = mDataProcessors.get(device);
					if (processor != null)
					{
						processor.processIncomingData(intent);
					}
				}
			}
		}
	};

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
		mActionBar.addTab(getActionBar().newTab().setText("Device list")
				.setTabListener(this));
		mActionBar.addTab(getActionBar().newTab().setText("Analyze")
				.setTabListener(this));

		registerReceiver(mDataReceiver, new IntentFilter(
				BluetoothService.ACTION_DATA_AVAILABLE));
		Intent i = new Intent(this, BluetoothService.class);
		bindService(i, mServiceConnection, 0);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mDataReceiver);
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
