package de.fhkl.bluetoothdeviceanalyser;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GenericDataProcessor implements IGattDataProcessor
{
	BluetoothGatt mGatt;
	
	public GenericDataProcessor(BluetoothGatt gatt)
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
			/*BluetoothGattCharacteristic characteristic =
					mGatt.getService(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"))
					.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
			mGatt.setCharacteristicNotification(characteristic, true);
			BluetoothGattDescriptor desc =
					characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mGatt.writeDescriptor(desc);*/
			List<BluetoothGattService> services = mGatt.getServices();
			for(int i = 0; i < services.size(); i++){//for(BluetoothGattService service : services){
				BluetoothGattService service = services.get(i);	
				Log.v("UUID", "ServiceUUID: " + service.getUuid().toString());
				List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
				for(int j = 0; j < characteristics.size(); j++){//for(BluetoothGattCharacteristic characteristic : characteristics){
					BluetoothGattCharacteristic characteristic = characteristics.get(j);
					Log.v("UUID", "  CharacteristicUUID: " + characteristic.getUuid().toString());
					List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
					for(int k = 0; k < descriptors.size(); k++){//for(BluetoothGattDescriptor descriptor : descriptors){
						BluetoothGattDescriptor descriptor = descriptors.get(k);
						Log.v("UUID", "    DescriptorUUID: " +descriptor.getUuid().toString());
						mGatt.setCharacteristicNotification(characteristic, true);
						descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
						mGatt.writeDescriptor(descriptor);
					}
				}
			}
		}
	}

}
