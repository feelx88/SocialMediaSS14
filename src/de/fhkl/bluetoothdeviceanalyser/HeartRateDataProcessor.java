package de.fhkl.bluetoothdeviceanalyser;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;

public class HeartRateDataProcessor implements IGattDataProcessor
{
	BluetoothGatt mGatt;
	
	public HeartRateDataProcessor(BluetoothGatt gatt)
	{
		mGatt = gatt;
		mGatt.writeCharacteristic(new BluetoothGattCharacteristic(new UUID(0x2902L << 32, 0x800000805f9b34fbL), 0x1, 0));
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
		
		
	}

}
