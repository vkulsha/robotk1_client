package com.example.robotk1;

import android.os.*;
import android.widget.Toast;

import java.io.IOException;

import android.bluetooth.*;

import java.lang.reflect.*;
import java.util.UUID;

class BTConnectThread extends Thread {
	public BluetoothSocket btSocket;
	private BluetoothAdapter btAdapter;
	private String btAddress;
	private Handler h;
	private int _RECIEVE_MESSAGE;

	public BTConnectThread(BluetoothAdapter adapter, String address, Handler handler, int RECIEVE_MESSAGE) {
		btAddress = address;
		btAdapter = adapter;
		btAdapter.cancelDiscovery();
		h = handler;
		_RECIEVE_MESSAGE = RECIEVE_MESSAGE;
	}

	public void run() {
		BluetoothDevice btDevice = btAdapter.getRemoteDevice(btAddress);
		try {
			//Method m = btDevice.getClass().getMethod("createRfcommSocketToServiceRecord", new Class[] { int.class });
			//btSocket = (BluetoothSocket)m.invoke(btDevice, Integer.valueOf(1));
			btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			btSocket.connect();
		//} catch (NoSuchMethodException nsme) { 			
		//} catch (InvocationTargetException ite) { 			
		//} catch (IllegalAccessException iae) { 			
		} catch (IOException connectException) {
		}
		h.obtainMessage(_RECIEVE_MESSAGE, btSocket).sendToTarget();
	}

	public void cancel() {
		try {
			btSocket.close();
		} catch (IOException e) {
		}
	}
}
