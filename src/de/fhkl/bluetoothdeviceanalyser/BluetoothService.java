package de.fhkl.bluetoothdeviceanalyser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Binder;
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
	public static final String EXTRA_CHARACTERISTIC_UUID = "EXTRA_CHARACTERISTIC_UUID";
	public static final String EXTRA_CHARACTERISTIC_VALUE = "EXTRA_CHARACTERISTIC_VALUE";
	
	public static final int ID_DATATYPE_ADDED_TO_WATCHLIST = 1; 
	public static final int ID_DATATYPE_GATT_CONNECTION_STATE_CHANGED = 10;
	public static final int ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED = 11;
	public static final int ID_DATATYPE_GATT_SERVICE_DISCOVERY_FINISHED = 12;
	
	protected BluetoothAdapter mAdapter;
	
	protected LinkedList<BluetoothGatt> mGatts = new LinkedList<BluetoothGatt>();
	
	public class LocalBinder extends Binder
	{
        BluetoothService getService()
        {
            return BluetoothService.this;
        }
    }
	
	public class GattCallback extends BluetoothGattCallback
	{
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState)
		{			
			Intent i = new Intent(ACTION_DATA_AVAILABLE);
			i.putExtra(EXTRA_DEVICE, gatt.getDevice());
			i.putExtra(EXTRA_DATA_TYPE, ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED);
			i.putExtra(EXTRA_CONNECTION_STATE, newState);
			sendBroadcast(i);
			
			gatt.discoverServices();
			
			super.onConnectionStateChange(gatt, status, newState);
		}
		
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic)
		{
			Intent i = new Intent(ACTION_DATA_AVAILABLE);
			i.putExtra(EXTRA_DEVICE, gatt.getDevice());
			i.putExtra(EXTRA_DATA_TYPE, ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED);
			i.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristic.getUuid().toString());
			i.putExtra(EXTRA_CHARACTERISTIC_VALUE, Arrays.toString(characteristic.getValue()));
			sendBroadcast(i);
			super.onCharacteristicChanged(gatt, characteristic);
		}
		
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status)
		{
			Intent i = new Intent(ACTION_DATA_AVAILABLE);
			i.putExtra(EXTRA_DEVICE, gatt.getDevice());
			i.putExtra(EXTRA_DATA_TYPE, ID_DATATYPE_GATT_SERVICE_DISCOVERY_FINISHED);
			sendBroadcast(i);
			super.onServicesDiscovered(gatt, status);
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
		if(intent == null)
		{
			return 0;
		}
		
		Bundle extras = intent.getExtras();
		
		if(extras != null)
		{		
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
					BluetoothGatt gatt = device.connectGatt(getApplicationContext(), false,
							new GattCallback());
					mGatts.add(gatt);
					
					Intent i = new Intent(ACTION_DATA_AVAILABLE);
					i.putExtra(EXTRA_DATA_TYPE, ID_DATATYPE_ADDED_TO_WATCHLIST);
					i.putExtra(EXTRA_DEVICE, device);
					i.putExtra(EXTRA_ID, mGatts.size() - 1);
					sendBroadcast(i);
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new LocalBinder();
	}
	
	public static void setScanCallback(LeScanCallback cb)
	{
		mScanCallback = cb;
	}
}
