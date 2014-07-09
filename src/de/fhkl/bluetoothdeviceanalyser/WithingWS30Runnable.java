package de.fhkl.bluetoothdeviceanalyser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
	private static final String REQUEST_JSON_STRING = "\"status\":0,\"body\":{"
			+ "  \"sessionid\":\"7314-23b5014f-52336b6c\","
			+ "  \"sp\": { "
			+ "    \"cusers\":["
			+ "		 {"
			+ "			\"id\":2590409,\"sn\":\"KAT\",\"wt\":56.283,\"ht\":1.65,\"agt\":33.5,\"sx\":1,\"fm\":3,\"cr\":1387624198,\"att\":0"
			+ "		 },"
			+ "		 {"
			+ "		   \"id\":2590530,\"sn\":\"JOH\",\"wt\":88.928,\"ht\":1.92,\"agt\":34.5,\"sx\":0,\"fm\":3,\"cr\":1387626873,\"att\":0"
			+ "		 },"
			+ "		 {"
			+ "		   \"id\":3528085,\"sn\":\"ACD\",\"wt\":90,\"ht\":1.98,\"agt\":28.9\"sx\":0,\"fm\":131,\"cr\":1403008761,\"att\":2"
			+ "		 }" + "	 ]" + "  }," + "  \"ind\":" + "  {"
			+ "    \"lg\":\"de_DE\",\"imt\":1,\"stp\":1,\"f\":0,\"g\":98103"
			+ "  }," + "  \"syp\":" + "  {" + "    \"utc\":1403627066" + "  },"
			+ "  \"ctp\":" + "  {"
			+ "    9y83/.\"goff\":7200,\"dst\":1414285200,\"ngoff\":3600"
			+ "  }" + "}";

	private int mState = 0;
	private Context mContext;
	private BluetoothServerSocket mServerSocket;
	private BluetoothSocket mSocket;
	private BluetoothDevice mDevice;

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
				mDevice = mSocket.getRemoteDevice();
				android.util.Log.d(TAG, "Withings WS 30 Connected!");
				InputStream s = mSocket.getInputStream();
				OutputStream out = mSocket.getOutputStream();

				Intent i = new Intent(BluetoothService.ACTION_DATA_AVAILABLE);
				i.putExtra(BluetoothService.EXTRA_DATA_TYPE,
						BluetoothService.ID_DATATYPE_ADDED_TO_WATCHLIST);
				i.putExtra(BluetoothService.EXTRA_DEVICE, mDevice);
				i.putExtra(BluetoothService.EXTRA_DEVICE_TYPE,
						BluetoothService.DEVICE_TYPE_WITHINGSWS30);
				mContext.sendBroadcast(i);

				while (true)
				{
					if (!mSocket.isConnected())
					{
						android.util.Log.d(TAG, "Socket connection lost");
						Intent i2 = new Intent(
								BluetoothService.ACTION_DATA_AVAILABLE);
						i2.putExtra(BluetoothService.EXTRA_DEVICE, mDevice);
						i2.putExtra(
								BluetoothService.EXTRA_DATA_TYPE,
								BluetoothService.ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED);
						i2.putExtra(BluetoothService.EXTRA_CHARACTERISTIC_UUID,
								"WithingsWS30Raw");
						i2.putExtra(
								BluetoothService.EXTRA_CHARACTERISTIC_VALUE,
								"Connection closed");
						mContext.sendBroadcast(i2);
						break;
					}

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

						handleReveived(buffer);
						handleState(out);
					}
					catch (IOException e)
					{
					}
				}
			}
			catch (IOException e)
			{
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
		case 1:
		{
			byte buffer[] = new byte[REQUEST_JSON_STRING.length()];
			for(int x = 0; x < buffer.length; x++)
			{
				buffer[x] = (byte) REQUEST_JSON_STRING.charAt(x);
			}
			out.write(buffer);
		}
		default:
		{
			mSocket.close();
			mState = 0;

			return;
		}
		}
		mState++;
	}

	private void handleReveived(byte[] buffer)
	{
		if (buffer == null)
		{
			return;
		}

		StringBuilder sb = new StringBuilder(buffer.length * 2);
		for (byte b : buffer)
		{
			sb.append(String.format("%02x", b & 0xff));
		}
		android.util.Log.d(TAG, sb.toString());

		Intent i = new Intent(BluetoothService.ACTION_DATA_AVAILABLE);
		i.putExtra(BluetoothService.EXTRA_DEVICE, mDevice);
		i.putExtra(BluetoothService.EXTRA_DATA_TYPE,
				BluetoothService.ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED);
		i.putExtra(BluetoothService.EXTRA_CHARACTERISTIC_UUID,
				"WithingsWS30Raw");
		i.putExtra(BluetoothService.EXTRA_CHARACTERISTIC_VALUE, sb.toString());
		mContext.sendBroadcast(i);
	}

}
