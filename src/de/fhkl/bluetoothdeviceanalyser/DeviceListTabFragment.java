package de.fhkl.bluetoothdeviceanalyser;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class DeviceListTabFragment extends Fragment
{
	private BluetoothDeviceListAdapter mListAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.device_list_tab_fragment, container, false);
		
		mListAdapter = new BluetoothDeviceListAdapter(getActivity());
		final ListView list = ((ListView)view.findViewById(R.id.deviceList));
		list.setAdapter(mListAdapter);
		
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
		getActivity().startService(serviceIntent);
		
		view.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				getActivity().startService(serviceIntent);
			}
		});
		
		return view;
	}
}
