package de.fhkl.bluetoothdeviceanalyser;

import java.util.LinkedList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class BluetoothService extends Service
{
	public static final String EXTRA_ACTION = "EXTRA_ACTION";
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	
	public static final int ID_START_SCAN = 1;
	public static final int ID_STOP_SCAN = 2;
	public static final int ID_ADD_DEVICE = 3;
	
	protected BluetoothAdapter mAdapter;
	
	protected LinkedList<BluetoothDevice> mRegisteredDevices =
			new LinkedList<BluetoothDevice>();
	
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
				mRegisteredDevices.add((BluetoothDevice) extras.get(EXTRA_DEVICE));
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
