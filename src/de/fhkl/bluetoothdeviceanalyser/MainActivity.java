package de.fhkl.bluetoothdeviceanalyser;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class MainActivity extends Activity
{
	protected BluetoothDeviceListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListAdapter = new BluetoothDeviceListAdapter(this);
		final ListView list = ((ListView)findViewById(R.id.deviceList));
		list.setAdapter(mListAdapter);
		
		BluetoothService.setScanCallback(new LeScanCallback() {
		
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
			{
				runOnUiThread(new Runnable() {
					
					@Override
					public void run()
					{
						mListAdapter.add(device);
						list.requestLayout();
					}
				});
			}
		});
		
		final Intent serviceIntent = new Intent(this, BluetoothService.class);
		startService(serviceIntent);
		
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				startService(serviceIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
