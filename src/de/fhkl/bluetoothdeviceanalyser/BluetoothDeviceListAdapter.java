package de.fhkl.bluetoothdeviceanalyser;

import java.util.LinkedList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BluetoothDeviceListAdapter implements ListAdapter
{
	LinkedList<BluetoothDevice> mDevices = new LinkedList<BluetoothDevice>();
	LayoutInflater mInflater;
	
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
		View view = mInflater.inflate(R.layout.device_list_fragment, parent, false);
		((TextView)view.findViewById(R.id.name))
			.setText(mDevices.get(position).getName());
		((TextView)view.findViewById(R.id.address))
			.setText(mDevices.get(position).getAddress());
		
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
	
	public void add(BluetoothDevice device)
	{
		mDevices.add(device);
	}

}
