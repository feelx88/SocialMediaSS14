package de.fhkl.bluetoothdeviceanalyser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

public class WithingWS30Runnable implements Runnable
{
	private static final String TAG = "WithingWS30Runnable";
	private static final String WS30_SEVICE_UUID = "00009999-0000-1000-8000-00805f9b34fb";
	private static final byte[] REQUEST_INFO_SEQUENCE = { 0x01, 0x01, 0x00,
			0x05, 0x01, 0x01, 0x01, 0x00, 0x00 };

	private int mState = 0;
	private Context mContext;
	private BluetoothServerSocket mServerSocket;
	private BluetoothSocket mSocket;

	public WithingWS30Runnable(Context context, BluetoothAdapter adapter)
	{
		mContext = context;
		try
		{
			mServerSocket = adapter.listenUsingRfcommWithServiceRecord(
					"WithingsWS30Hack", UUID.fromString(WS30_SEVICE_UUID));
		}
		catch (IOException e1)
		{
			return;
		}
	}

	@Override
	public void run()
	{
		if (mServerSocket == null)
		{
			android.util.Log.e(TAG, "mServerSocket == null");
			return;
		}

		while (true)
		{
			try
			{
				mSocket = mServerSocket.accept();
				android.util.Log.d(TAG, "Withings WS 30 Connected!");
				InputStream s = mSocket.getInputStream();
				OutputStream out = mSocket.getOutputStream();

				Intent i = new Intent(BluetoothService.ACTION_DATA_AVAILABLE);
				i.putExtra(BluetoothService.EXTRA_DATA_TYPE,
						BluetoothService.ID_DATATYPE_ADDED_TO_WATCHLIST);
				i.putExtra(BluetoothService.EXTRA_DEVICE,
						mSocket.getRemoteDevice());
				i.putExtra(BluetoothService.EXTRA_DEVICE_TYPE,
						BluetoothService.DEVICE_TYPE_WITHINGSWS30);
				mContext.sendBroadcast(i);

				while (true)
				{
					byte buffer[] = null;
					try
					{
						byte buf[] = new byte[1024];
						int length = s.read(buf);
						buffer = new byte[length];
						for (int x = 0; x < length; x++)
						{
							buffer[x] = buf[x];
						}
						handleState(out);
					}
					catch (IOException e)
					{
						// TODO: handle exception
					}

					handleReveived(buffer);
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleState(OutputStream out) throws IOException
	{
		switch (mState)
		{
		case 0:
		{
			out.write(REQUEST_INFO_SEQUENCE);
			break;
		}
		default:
		{
			break;
		}

		}
		mState++;
	}

	private void handleReveived(byte[] buffer)
	{
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		for (byte b : buffer)
		{
			sb.append(String.format("%02x", b & 0xff));
		}
		android.util.Log.d(TAG, sb.toString());

		Intent i = new Intent(BluetoothService.ACTION_DATA_AVAILABLE);
		i.putExtra(BluetoothService.EXTRA_DEVICE, mSocket.getRemoteDevice());
		i.putExtra(BluetoothService.EXTRA_DATA_TYPE,
				BluetoothService.ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED);
		i.putExtra(BluetoothService.EXTRA_CHARACTERISTIC_UUID,
				"WithingsWS30Raw");
		i.putExtra(BluetoothService.EXTRA_CHARACTERISTIC_VALUE, sb.toString());
		mContext.sendBroadcast(i);
	}

}
