package de.fhkl.bluetoothdeviceanalyser;

import java.util.HashMap;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalyzeTabFragment extends Fragment
{
	private LinearLayout mLayout;

	private LayoutInflater mInflater;

	private HashMap<BluetoothDevice, View> mDeviceViews = new HashMap<BluetoothDevice, View>();
	private HashMap<BluetoothDevice, Integer> mDeviceTypes = new HashMap<BluetoothDevice, Integer>();

	private BroadcastReceiver mDataReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			Bundle extras = intent.getExtras();
			String action = intent.getAction();
			if (action == BluetoothService.ACTION_DATA_AVAILABLE)
			{
				BluetoothDevice device = (BluetoothDevice) extras
						.get(BluetoothService.EXTRA_DEVICE);

				int datatype = extras.getInt(BluetoothService.EXTRA_DATA_TYPE);
				if (datatype == BluetoothService.ID_DATATYPE_ADDED_TO_WATCHLIST)
				{
					int devicetype = extras
							.getInt(BluetoothService.EXTRA_DEVICE_TYPE);
					mDeviceTypes.put(device, devicetype);

					switch (devicetype)
					{
					case BluetoothService.DEVICE_TYPE_HRM:
					{
						final View layout = mInflater.inflate(
								R.layout.hrm_analyze_fragment, mLayout);

						layout.findViewById(R.id.buttonShiftLeft)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v)
									{
										Integer shift = (Integer) layout
												.getTag();
										if (shift > 0)
										{
											shift--;
										}
										layout.setTag(shift);
									}
								});
						layout.findViewById(R.id.buttonShiftRight)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v)
									{
										Integer shift = (Integer) layout
												.getTag();
										if (shift < 32)
										{
											shift++;
										}
										layout.setTag(shift);
									}
								});

						mDeviceViews.put(device, layout);
						layout.setTag(Integer.valueOf(0)); // shift amount

						break;
					}
					case BluetoothService.DEVICE_TYPE_WITHINGSWS30:
					{
						TextView textView = new TextView(context);
						textView.setLines(10);
						textView.setMovementMethod(new ScrollingMovementMethod());
						mLayout.addView(textView);
						mDeviceViews.put(device, textView);

						textView.append("Added device to watchlist: ");
						textView.append(device.getName() + " ("
								+ device.getAddress() + ")");
						textView.scrollTo(0, Integer.MAX_VALUE);

						break;
					}
					}
				}
				else if (datatype == BluetoothService.ID_DATATYPE_GATT_CHARACTERISTIC_CHANGED)
				{
					int devicetype = mDeviceTypes.get(device);

					switch (devicetype)
					{
					case BluetoothService.DEVICE_TYPE_HRM:
					{
						byte data[] = extras
								.getByteArray(BluetoothService.EXTRA_CHARACTERISTIC_VALUE);
						if (data == null)
						{
							break;
						}

						LinearLayout l = (LinearLayout) mDeviceViews
								.get(device);

						int value = data[1] << (Integer) l.getTag();

						String text = Integer.toString(value);
						((TextView) l.findViewById(R.id.heartRate))
								.setText(text);
						break;
					}
					case BluetoothService.DEVICE_TYPE_WITHINGSWS30:
					{
						TextView textView = (TextView) mDeviceViews.get(device);

						textView.append("\n");
						textView.append("Data block received from Withings WS 30:");
						textView.append("Value (Hex): "
								+ extras.getString(BluetoothService.EXTRA_CHARACTERISTIC_VALUE)
								+ "\n");
						textView.scrollTo(0, Integer.MAX_VALUE);

						break;
					}
					}
				}
				else if (datatype == BluetoothService.ID_DATATYPE_GATT_SERVICE_DISCOVERY_FINISHED)
				{
					int devicetype = mDeviceTypes.get(device);

					switch (devicetype)
					{
					case BluetoothService.DEVICE_TYPE_HRM:
					{
						LinearLayout l = (LinearLayout) mDeviceViews
								.get(device);
						l.findViewById(R.id.serviceDiscoveryRunning)
								.setVisibility(View.GONE);
						break;
					}
					case BluetoothService.DEVICE_TYPE_WITHINGSWS30:
					{
						// Does not happen
						break;
					}
					}
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mInflater = inflater;
		getActivity().registerReceiver(mDataReceiver,
				new IntentFilter(BluetoothService.ACTION_DATA_AVAILABLE));
		View view = inflater.inflate(R.layout.analyze_tab_fragment, container,
				false);
		mLayout = (LinearLayout) view.findViewById(R.id.analyzeLayout);
		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		getActivity().unregisterReceiver(mDataReceiver);
	}
}
