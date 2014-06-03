package de.fhkl.bluetoothdeviceanalyser;

import java.util.LinkedList;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity
{
	
	protected LinkedList<String> mList = new LinkedList<String>();
	protected ArrayAdapter<String> mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListAdapter = new ArrayAdapter<String>(
						this, R.layout.device_list_fragment, R.id.name, mList);
		((ListView)findViewById(R.id.deviceList)).setAdapter(mListAdapter);
		
		BluetoothService.setScanCallback(new LeScanCallback() {
		
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
			{
				mList.add(device.getName());
				mListAdapter.notifyDataSetChanged();
			}
		});
		
		Intent i = new Intent(this, BluetoothService.class);
		startService(i);
		
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				Intent i = new Intent(getApplicationContext(), BluetoothService.class);
				startService(i);
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
