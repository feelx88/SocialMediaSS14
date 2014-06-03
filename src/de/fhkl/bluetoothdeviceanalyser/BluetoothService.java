package de.fhkl.bluetoothdeviceanalyser;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothService extends Service
{
	protected BluetoothAdapter mAdapter;
	
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
		if(mScanCallback != null)
		{
			mAdapter.startLeScan(mScanCallback);
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
