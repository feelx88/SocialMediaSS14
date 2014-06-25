package de.fhkl.bluetoothdeviceanalyser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class WithingWS30Runnable implements Runnable
{
	private static final String WS30_UUID = "00009999-0000-1000-8000-00805f9b34fb";
	private static final byte[] REQUEST_INFO_SEQUENCE =
		{ 0x01, 0x01, 0x00, 0x05, 0x01, 0x01, 0x01, 0x00, 0x00 };
	private BluetoothServerSocket mServerSocket;

	public WithingWS30Runnable(BluetoothAdapter adapter)
	{
		try
		{
			mServerSocket = adapter.listenUsingRfcommWithServiceRecord(
					"WithingsWS30Hack",
					UUID.fromString(WS30_UUID));
		}
		catch (IOException e1)
		{
			return;
		}
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				BluetoothSocket sock = mServerSocket.accept();
				android.util.Log
						.d("################", "Connected!");
				InputStream s = sock.getInputStream();
				OutputStream out = sock.getOutputStream();

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
						out.write(REQUEST_INFO_SEQUENCE);
					}
					catch (IOException e)
					{
						// TODO: handle exception
					}

					StringBuilder sb = new StringBuilder(
							buffer.length * 2);
					for (byte b : buffer)
					{
						sb.append(String.format("%02x", b & 0xff));
					}
					android.util.Log.d("################",
							sb.toString());
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
