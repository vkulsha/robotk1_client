package com.example.robotk1;

import com.example.robotk1.R;

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import android.view.*;
import android.view.View.OnTouchListener;
import android.content.*;
import android.speech.tts.*;

import java.io.*;
import java.net.*;
import java.util.*;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;
import android.app.ListActivity;

public class TtsActivity extends Activity {
	private TtsDictionary ttsDict;
	private static final byte buttonsPerRow = 4;
	private Context context;

	Button bSpeak;
	EditText eSpeak;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tts);
		context = getApplicationContext();
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		
		ListView listView = (ListView)findViewById(R.id.ttstable);

		final String[] dict = context.getResources().getStringArray(R.array.ttsDictionary2);
		List<String> words = new ArrayList<String>();
		for(String s : dict) {
		    words.add(s);
		}
		
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard,"robotk1dict.txt");
			StringBuilder text = new StringBuilder();
			try {
			    BufferedReader br = new BufferedReader(new FileReader(file));
			    String line;
			    String isClear = sp.getString("ttsClearDictIfFileExists", "0").toString();
			    if (isClear != "") {
			    	words.clear();
			    }
			    
			    while ((line = br.readLine()) != null) {
			    	words.add(line);
			    }
			}
			catch (IOException e) {
			}		
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,	
		        android.R.layout.simple_list_item_1, words);

		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
					long id) {
				Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
				        Toast.LENGTH_SHORT).show();

				SendToRobot s2r = new SendToRobot(context);
				String speakText = ((TextView) itemClicked).getText().toString();
				s2r.execute(new String[] { speakText });
				
			}
		});	

	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
