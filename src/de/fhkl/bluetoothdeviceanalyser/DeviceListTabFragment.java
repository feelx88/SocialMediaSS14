package de.fhkl.bluetoothdeviceanalyser;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class DeviceListTabFragment extends Fragment
{
	private BluetoothDeviceListAdapter mListAdapter;
	private boolean mScanning = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.device_list_tab_fragment, container, false);
		
		mListAdapter = new BluetoothDeviceListAdapter(getActivity());
		final ListView list = ((ListView)view.findViewById(R.id.deviceList));
		list.setAdapter(mListAdapter);
		
		final ProgressBar progressScan = (ProgressBar) view.findViewById(R.id.progressScan);
		
		BluetoothService.setScanCallback(new LeScanCallback() {
			
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
			{
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run()
					{
						mListAdapter.add(device);
						list.requestLayout();
					}
				});
			}
		});
		
		final Intent serviceIntent = new Intent(getActivity(), BluetoothService.class);
		
		view.findViewById(R.id.buttonScan).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(mScanning)
				{
					serviceIntent.putExtra(BluetoothService.EXTRA_ACTION,
							BluetoothService.ID_STOP_SCAN);
					progressScan.setVisibility(View.GONE);
				}
				else
				{
					serviceIntent.putExtra(BluetoothService.EXTRA_ACTION,
							BluetoothService.ID_START_SCAN);
					progressScan.setVisibility(View.VISIBLE);
					mListAdapter.clear();
					list.requestLayout();
					
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run()
						{
							serviceIntent.putExtra(BluetoothService.EXTRA_ACTION,
									BluetoothService.ID_STOP_SCAN);
							progressScan.setVisibility(View.GONE);
						}
					}, 30000);
				}
				mScanning = !mScanning;
				getActivity().startService(serviceIntent);
			}
		});
		
		view.findViewById(R.id.buttonScan).performClick();
		
		return view;
	}
}
