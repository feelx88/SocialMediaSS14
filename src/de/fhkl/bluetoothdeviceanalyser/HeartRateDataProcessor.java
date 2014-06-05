package de.fhkl.bluetoothdeviceanalyser;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.os.Bundle;

public class HeartRateDataProcessor implements IGattDataProcessor
{
	BluetoothGatt mGatt;
	
	public HeartRateDataProcessor(BluetoothGatt gatt)
	{
		mGatt = gatt;
	}

	@Override
	public void processIncomingData(Intent data)
	{
		if(data == null)
		{
			return;
		}
		
		Bundle extras = data.getExtras();
		if(extras == null)
		{
			return;
		}
		
		int datatype = extras.getInt(BluetoothService.EXTRA_DATA_TYPE);
		
		if(datatype == BluetoothService.ID_DATATYPE_GATT_SERVICE_DISCOVERY_FINISHED)
		{
			BluetoothGattCharacteristic characteristic = mGatt.getService(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")).getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
			mGatt.setCharacteristicNotification(characteristic, true);
			BluetoothGattDescriptor desc = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mGatt.writeDescriptor(desc);
		}
	}

}
