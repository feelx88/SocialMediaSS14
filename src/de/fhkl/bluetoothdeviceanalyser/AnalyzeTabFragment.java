package de.fhkl.bluetoothdeviceanalyser;

import java.util.HashMap;
import java.util.LinkedList;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AnalyzeTabFragment extends Fragment
{
	private TextView mTextView;
	
	private LinkedList<BluetoothDevice> mRegisteredDevices =
			new LinkedList<BluetoothDevice>();
	
	private BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Bundle extras = intent.getExtras();
			String action = intent.getAction();
			if(action == BluetoothService.ACTION_DATA_AVAILABLE)
			{
				BluetoothDevice device =
						(BluetoothDevice) extras.get(BluetoothService.EXTRA_DEVICE);
				
				int datatype = extras.getInt(BluetoothService.EXTRA_DATA_TYPE);
				if(datatype == BluetoothService.ID_DATATYPE_ADDED_TO_WATCHLIST)
				{
					mRegisteredDevices.add(device);
					mTextView.append("\n");
					mTextView.append("Added device to watchlist: ");
					mTextView.append(device.getName() + " (" + device.getAddress() + ")");
				}
				else if(datatype == BluetoothService.ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED)
				{
					mTextView.append("\n");
					mTextView.append("Characteristic changed:\n");
					mTextView.append("UUID: " + extras.getString(BluetoothService.EXTRA_CHARACTERISTIC_UUID) + "\n");
					mTextView.append("Value: " + extras.getString(BluetoothService.EXTRA_CHARACTERISTIC_VALUE) + "\n");
				}
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		getActivity().registerReceiver(mDataReceiver,
				new IntentFilter(BluetoothService.ACTION_DATA_AVAILABLE));
		View view = inflater.inflate(R.layout.analyze_tab_fragment, container, false);
		mTextView = (TextView) view.findViewById(R.id.textView1);
		return view;
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		getActivity().unregisterReceiver(mDataReceiver);
	}
}
