package de.fhkl.bluetoothdeviceanalyser;

import java.util.LinkedList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class BluetoothService extends Service
{
	//Intent receive constants
	public static final String EXTRA_ACTION = "EXTRA_ACTION";
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	public static final String EXTRA_CALLBACK_CLASS = "EXTRA_CALLBACK_CLASS";
	
	public static final int ID_START_SCAN = 1;
	public static final int ID_STOP_SCAN = 2;
	public static final int ID_ADD_DEVICE = 3;
	
	//intent send constants
	public static final String ACTION_DATA_AVAILABLE =
			"de.fhkl.bluetoothdeviceanalyser.BluetoothService.ACTION_DATA_AVAILABLE";
	
	public static final String EXTRA_ID = "EXTRA_ID"; 
	public static final String EXTRA_DATA_TYPE = "EXTRA_DATA_TYPE";
	public static final String EXTRA_CONNECTION_STATE = "EXTRA_CONNECTION_STATE";
	
	public static final int ID_DATATYPE_ADDED_TO_WATCHLIST = 1; 
	public static final int ID_DATATYPE_GATT_CONNECTION_STATE_CHANGED = 10;
	
	protected BluetoothAdapter mAdapter;
	
	protected LinkedList<BluetoothGatt> mGatts = new LinkedList<BluetoothGatt>();
	
	public class GattCallback extends BluetoothGattCallback
	{		
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState)
		{
			Intent i = new Intent(ACTION_DATA_AVAILABLE);
			i.putExtra(EXTRA_DEVICE, gatt.getDevice());
			i.putExtra(EXTRA_DATA_TYPE, ID_DATATYPE_GATT_CONNECTION_STATE_CHANGED);
			i.putExtra(EXTRA_CONNECTION_STATE, newState);
			sendBroadcast(i);
			super.onConnectionStateChange(gatt, status, newState);
		}
	}
	
	protected static LeScanCallback mScanCallback = null;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Bundle extras = intent.getExtras();
		
		if(extras.containsKey(EXTRA_ACTION))
		{
			if(extras.getInt(EXTRA_ACTION) == ID_START_SCAN)
			{
				if(mScanCallback != null)
				{
					if(mAdapter.startLeScan(mScanCallback) == false)
					{
						mAdapter.stopLeScan(mScanCallback);
					}
				}
			}
			else if(extras.getInt(EXTRA_ACTION) == ID_ADD_DEVICE)
			{
				BluetoothDevice device = (BluetoothDevice) extras.get(EXTRA_DEVICE);
				mGatts.add(device.connectGatt(getApplicationContext(), false,
						new GattCallback()));
				
				Intent i = new Intent(ACTION_DATA_AVAILABLE);
				i.putExtra(EXTRA_DATA_TYPE, ID_DATATYPE_ADDED_TO_WATCHLIST);
				i.putExtra(EXTRA_DEVICE, device);
				i.putExtra(EXTRA_ID, mGatts.size() - 1);
				sendBroadcast(i);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public static void setScanCallback(LeScanCallback cb)
	{
		mScanCallback = cb;
	}
}
