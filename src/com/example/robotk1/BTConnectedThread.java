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
		//byte[] buffer = new byte[11];
		int bytes = -1;
		String str = "";

		try {
		while (true) {
			//if (btInStream.available() > 0) {
				bytes = btInStream.read();
				if (bytes >= 32) {
					str += (char)bytes;
				} else if (str != "") {
					h.obtainMessage(_RECIEVE_MESSAGE, str).sendToTarget();
					str = "";
					bytes = -1;
				}
			//}
			//bytes = btInStream.read(buffer);
			//if (bytes != -1){			
				//sb.append(new String(buffer, 0, 32));
				//int endOfLineIndex = sb.indexOf(";");
				//if (endOfLineIndex > 0) {
					//Time today = new Time(Time.getCurrentTimezone());
					//today.setToNow();
					//buffer[0] = (byte)today.second;
					//buffer[1] = 22;
					
					//String str = today.format("%k:%M:%S");
					//String str = sb.toString();
					//sb.delete(0, sb.length());
					//h.obtainMessage(_RECIEVE_MESSAGE, str).sendToTarget();
					//h.obtainMessage(_RECIEVE_MESSAGE, buffer[1]).sendToTarget();
				//}
			//}
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
