package de.fhkl.bluetoothdeviceanalyser;

import android.content.Intent;

public interface IGattDataProcessor
{
	public void processIncomingData(Intent data);
}
