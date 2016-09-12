package com.example.robotk1;

import android.os.*;
import java.io.*;
import android.bluetooth.*;

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
		byte[] buffer = new byte[32];
		int bytes;
		StringBuilder sb = new StringBuilder();

		while (true) {
			try {
				bytes = btInStream.read(buffer);
			} catch (IOException e) {
				break;
			}
			sb.append(new String(buffer, 0, 32));
			int endOfLineIndex = sb.indexOf("\r\n");
			if (endOfLineIndex > 0) {
				h.obtainMessage(_RECIEVE_MESSAGE, sb.substring(0, endOfLineIndex)).sendToTarget();
				sb.delete(0, sb.length());
			}
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
