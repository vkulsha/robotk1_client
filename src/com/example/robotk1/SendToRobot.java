package com.example.robotk1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import android.content.*;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class SendToRobot extends AsyncTask<String, Void, Void> {
	private SharedPreferences sp;

	SendToRobot(Context context) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected Void doInBackground(String... params) {
		String address = sp.getString("robotIpAddress", "192.168.43.152");
		String ip = sp.getString("robotIpPort", "4567");
		try {
			Socket socket = new Socket(address, Integer.parseInt(ip));
			new PrintWriter(socket.getOutputStream(), true).println(params[0]);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}