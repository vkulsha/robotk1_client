package com.example.robotk1;

import android.os.*;
import java.io.*;
import android.bluetooth.*;
import android.text.format.Time;

class BTConnectedThread extends Thread {
	private InputStream btInStream;
	private OutputStream btOutStream;
	private Handler h;
	private int _RECIEVE_MESSAGE;

	public BTConnectedThread(BluetoothSocket socket, Handler handler, int RECIEVE_MESSAGE) {
		try {
			btInStream = socket.getInputStream();
			btOutStream = socket.getOutputStream();
			h = handler;
			_RECIEVE_MESSAGE = RECIEVE_MESSAGE;
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		int bytes = -1;
		String str = "";

		try {
		while (true) {
			bytes = btInStream.read();
			if (bytes >= 32) {
				str += (char)bytes;
			} else if (str != "") {
				h.obtainMessage(_RECIEVE_MESSAGE, str).sendToTarget();
				str = "";
				bytes = -1;
			}
		}
		} catch (IOException e) {
		}
	}

	public void write(String message) {
		byte[] msgBuffer = message.getBytes();
		try {
			btOutStream.write(msgBuffer);
		} catch (IOException e) {
		}
	}

	public void write(byte[] message) {
		try {
			btOutStream.write(message);
		} catch (IOException e) {
		}
	}

	public void cancel() {
		try {
			btInStream.close();
			btOutStream.close();
		} catch (IOException e) {
		}
	}
}
