package de.fhkl.bluetoothdeviceanalyser;

import java.util.LinkedList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
	View mView;
	
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
		if(mView == null)
		{
			mView = mInflater.inflate(R.layout.device_list_fragment, parent, false);
			((TextView)mView.findViewById(R.id.name))
				.setText(mDevices.get(position).getName());
			((TextView)mView.findViewById(R.id.address))
				.setText(mDevices.get(position).getAddress());
			
			mView.setOnClickListener(this);
		}
		
		return mView;
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
	public void onClick(View v)
	{
		((CheckBox)mView.findViewById(R.id.checkbox)).setChecked(true);
	}
	
	public void add(BluetoothDevice device)
	{
		mDevices.add(device);
	}
	
	public void clear()
	{
		mDevices.clear();
	}
}
