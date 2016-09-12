package com.example.robotk1;

import com.example.robotk1.R;

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.content.*;

import java.io.*;
import java.net.*;
import java.util.*;

import com.example.robotk1.*;

public class Main extends Activity implements OnTouchListener {
	SeekBar seekBarSpeedMotors, seekBarSpeedHands;
	Context context;
	String comm = "";
	private final int controlButtonsCount = 9;
	private TtsDictionary ttsDict;
	private static final byte buttonsPerRow = 5;
	Button bSpeak, bSpeak2;
	EditText eSpeak, eSpeak2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();

		seekBarSpeedMotors = (SeekBar) findViewById(R.id.seekBar1);
		seekBarSpeedHands = (SeekBar) findViewById(R.id.seekBar2);
		seekBarSpeedMotors.setOnTouchListener(this);
		seekBarSpeedHands.setOnTouchListener(this);

		for (int i = 1; i <= controlButtonsCount; i++) {
			int id = getResources().getIdentifier(
					"ImageButton" + String.format("%02d", i), "id",
					context.getPackageName());
			ImageButton but = (ImageButton) findViewById(id);
			if (but != null) {
				but.setOnTouchListener(this);
			}
		}

		eSpeak = (EditText) findViewById(R.id.eSpeak);
		bSpeak = (Button) findViewById(R.id.bSpeak);
		bSpeak.setOnTouchListener(this);
		eSpeak2 = (EditText) findViewById(R.id.eSpeak2);
		bSpeak2 = (Button) findViewById(R.id.bSpeak2);
		bSpeak2.setOnTouchListener(this);
		ttsDict = new TtsDictionary(context);

		TableLayout tl = (TableLayout) findViewById(R.id.ttstable_main);

		OnClickListener onClickListener = new OnClickListener() {
			public void onClick(View v) {
				SendToRobot s2r = new SendToRobot(context);
				String speakText = ttsDict.getSpeakWord(((Button) v).getText()
						.toString());
				s2r.execute(new String[] { speakText });
			}
		};

		TableRow tr = null;
		for (int i = 0; i < ttsDict.lstTtsDictMain.size(); i++) {
			if (i % buttonsPerRow == 0 || i == 0) {
				tr = new TableRow(this);
				tl.addView(tr);
			}
			Button b = new Button(this);
			b.setWidth(80);
			b.setHeight(32);
			b.setTextSize(10);
			b.setText(ttsDict.lstTtsDictMain.get(i));
			b.setOnClickListener(onClickListener);
			tr.addView(b);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, "���������");
		mi.setIntent(new Intent(this, PrefActivity.class));
		MenuItem mi2 = menu.add(0, 2, 1, "�����");
		mi2.setIntent(new Intent(this, TtsActivity.class));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String name = data.getStringExtra("name");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void sendToSpeak(String speakText) {
		SendToRobot s2r = new SendToRobot(context);
		s2r.execute(new String[] { speakText });

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		String comm = "", speed = "";
		byte val1 = 0, val2 = 0;
		boolean front = false, left = false, right = false, back = false, up1 = false, down1 = false, up2 = false, down2 = false, manip1on = false, manip1off = false, manip2on = false, manip2off = false, off = false;
		String speakText = "";

		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			switch (v.getId()) {
			case R.id.ImageButton01:
				front = true;
				comm += "front";
				break;
			case R.id.ImageButton02:
				left = true;
				comm += "left";
				break;
			case R.id.ImageButton03:
				right = true;
				comm += "right";
				break;
			case R.id.ImageButton04:
				back = true;
				comm += "back";
				break;
			case R.id.ImageButton05:
				up1 = true;
				comm += "up1";
				break;
			case R.id.ImageButton06:
				down1 = true;
				comm += "down1";
				break;
			case R.id.ImageButton07:
				up2 = true;
				comm += "up2";
				break;
			case R.id.ImageButton08:
				down2 = true;
				comm += "down2";
				break;
			case R.id.ImageButton09:
				manip2on = true;
				comm += "manip2on";
				break;
			case R.id.bSpeak: {
				speakText = eSpeak.getText().toString();
			}
				off = true;
				comm += "off";
				break;
			case R.id.bSpeak2: {
				speakText = eSpeak2.getText().toString();
			}
				off = true;
				comm += "off";
				break;
			default:
				comm = "off";
				off = true;
				break;
			}
		} else {
			comm = "off";
			off = true;
		}
		;

		if (speakText != "") {
			sendToSpeak(speakText);
			return false;
		}

		if (comm != "") {
			if (!off) {
				SeekBar speed1, speed2;
				speed1 = (SeekBar) findViewById(R.id.seekBar1);
				speed2 = (SeekBar) findViewById(R.id.seekBar2);
				val1 = (byte) (speed1.getProgress());
				val2 = (byte) (speed2.getProgress());

				if (left || right) {
					val1 = 10;
				}
				;

				if (up2) {// left hand
					val2 = (byte) ((210 + ((val2 - 1) * 5)) & 0xFF);
				} else if (up1) {// right hand
					val2 = (byte) ((210 + ((val2 - 1) * 5)) & 0xFF);
				} else if (down2) {// left hand
					val2 = (byte) ((190 + ((val2 - 1) * 5)) & 0xFF);
				} else if (down1) {// right hand
					val2 = (byte) ((190 + ((val2 - 1) * 5)) & 0xFF);
				} else if (manip1on || manip1off || manip2on || manip2off) {// manip
					val2 = (byte) ((100 + ((val2 - 1) * 5)) & 0xFF);
				}
				;
			}
			;

			speed += String.format("%03d", 2);// pwm count
			speed += String.format("%03d", val1);
			speed += String.format("%03d", val2);

			String sendData = speed + comm;
			// eSpeak.setText(sendData);
			// Toast.makeText(context, sendData, Toast.LENGTH_SHORT).show();
			SendToRobot s2r = new SendToRobot(context);
			s2r.execute(new String[] { sendData });

		}
		return false;
	}
}