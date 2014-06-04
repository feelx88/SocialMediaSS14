package de.fhkl.bluetoothdeviceanalyser;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;

public class HeartRateDataProcessor implements IGattDataProcessor
{

	public HeartRateDataProcessor(BluetoothGatt gatt)
	{
	}

	@Override
	public void processIncomingData(Intent data)
	{
	}

}
