package de.fhkl.bluetoothdeviceanalyser;

import java.util.HashMap;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BluetoothDeviceListAdapter implements ListAdapter, OnClickListener
{
	LinkedList<BluetoothDevice> mDevices = new LinkedList<BluetoothDevice>();
	LayoutInflater mInflater;
	HashMap<View, Integer> mDeviceIndizes = new HashMap<View, Integer>();

	public BluetoothDeviceListAdapter(Context context)
	{
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return mDevices.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return mDevices.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = mInflater.inflate(R.layout.device_list_fragment, parent,
				false);
		((TextView) view.findViewById(R.id.name)).setText(mDevices
				.get(position).getName());
		((TextView) view.findViewById(R.id.address)).setText(mDevices.get(
				position).getAddress());
		((CheckBox) view.findViewById(R.id.checkbox)).setOnClickListener(this);

		mDeviceIndizes.put(view.findViewById(R.id.checkbox), position);

		return view;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		return mDevices.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	@Override
	public boolean isEnabled(int arg0)
	{
		return true;
	}

	@Override
	public void onClick(final View v)
	{
		CharSequence deviceTypes[] = new CharSequence[] { "Heart Rate Monitor",
				"Withings WS 30 Scale",
				"Generic" };

		AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
		builder.setTitle("Select device type");
		builder.setItems(deviceTypes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Context context = mInflater.getContext();
				Intent i = new Intent(context, BluetoothService.class);
				i.putExtra(BluetoothService.EXTRA_ACTION,
						BluetoothService.ID_ADD_DEVICE);
				i.putExtra(BluetoothService.EXTRA_DEVICE,
						mDevices.get(mDeviceIndizes.get(v)));
				
				switch (which)
				{
				case 0:
				{
					i.putExtra(BluetoothService.EXTRA_DEVICE_TYPE,
							BluetoothService.DEVICE_TYPE_HRM);
					break;
				}
				case 1:
				{
					i.putExtra(BluetoothService.EXTRA_DEVICE_TYPE,
							BluetoothService.DEVICE_TYPE_WITHINGSWS30);
					break;
				}
				case 2:
				{
					i.putExtra(BluetoothService.EXTRA_DEVICE_TYPE,
							BluetoothService.DEVICE_TYPE_GENERIC);
					break;
				}
				}
				
				context.startService(i);
			}
		});
		builder.show();
	}

	public void add(BluetoothDevice device)
	{
		mDevices.add(device);
	}

	public void clear()
	{
		mDevices.clear();
		mDeviceIndizes.clear();
	}
}
