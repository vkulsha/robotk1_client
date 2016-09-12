package com.example.robotk1;

import com.example.robotk1.R;

import android.app.Activity;
import android.os.*;
import android.view.MenuItem;
import android.widget.*;
import android.view.*;
import android.view.View.OnTouchListener;

import java.io.*;
import java.net.*;
import java.util.*;

import android.content.*;
import android.os.Environment;
import android.preference.PreferenceManager;

public class TtsDictionary {
	private SharedPreferences sp;
	private int wpp;
	private ArrayList<String> lstTtsDict = new ArrayList<String>();
	public ArrayList<String> lstTtsDictMain = new ArrayList<String>();
	public ArrayList<Integer> lstTtsDictMainCounter = new ArrayList<Integer>();
	private String[] words;

	TtsDictionary(Context context) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		wpp = Integer.valueOf(sp.getString("ttsWordsPerPattern", "5"));
		words = context.getResources().getStringArray(R.array.ttsDictionary);
		initTtsDict(wpp);
	}

	private void initTtsDict(int wpp) {
		String line;
		lstTtsDict.clear();
		lstTtsDictMain.clear();

		for (int i = 0; i < words.length; i++) {
			line = words[i];
			lstTtsDict.add(line);
			if (i == 0 || i % wpp == 0) {
				lstTtsDictMain.add(line);
				lstTtsDictMainCounter.add(1);
			}
		}
	}

	public String getSpeakWord(String pattern) {
		String result = pattern;
		int ind = lstTtsDict.indexOf(pattern);
		int indMain = lstTtsDictMain.indexOf(pattern);
		int num = lstTtsDictMainCounter.get(indMain);
		lstTtsDictMainCounter.set(indMain, num == wpp ? 1 : num + 1);
		result = lstTtsDict.get(ind + (num - 1));
		return result;
	}

}
