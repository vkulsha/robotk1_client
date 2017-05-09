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
import android.bluetooth.*;
import android.preference.PreferenceManager;

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.IOException;

import com.example.robotk1.*;

public class Main extends Activity implements OnTouchListener {
	SeekBar seekBarSpeedMotors, seekBarSpeedHands;
	Context context;
	String comm = "";
	private final int controlButtonsCount = 9;
	private TtsDictionary ttsDict;
	private static final byte buttonsPerRow = 5;
	Button bSpeak, bSpeak2, bBTConnect, bBTDisconnect;
	EditText eSpeak, eSpeak2;
	private SharedPreferences sp;
	Handler h;
	final int RECIEVE_MESSAGE_BT_CONNECT = 1, RECIEVE_MESSAGE_BT_CONNECTED = 2;
	private BTConnectThread mBTConnectThread = null;
	private BTConnectedThread mBTConnectedThread = null;
	private BluetoothSocket btSocket = null;
	private BluetoothAdapter btAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();
		sp = PreferenceManager.getDefaultSharedPreferences(context);

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
		bBTConnect = (Button) findViewById(R.id.bBTConnect);
		bBTConnect.setOnTouchListener(this);
		bBTDisconnect = (Button) findViewById(R.id.bBTDisconnect);
		bBTDisconnect.setOnTouchListener(this);
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

		h = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
					case RECIEVE_MESSAGE_BT_CONNECT:
						btSocket = (BluetoothSocket) msg.obj;
						if (btSocket != null && btSocket.getRemoteDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
							Toast.makeText(context, "Bluetooth connection is successful!",	Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(context, "Bluetooth connection failed!",	Toast.LENGTH_SHORT).show();
						}

						mBTConnectedThread = new BTConnectedThread(btSocket, h, RECIEVE_MESSAGE_BT_CONNECTED);
						mBTConnectedThread.start();
					break;
					case RECIEVE_MESSAGE_BT_CONNECTED:
						String str = (String) msg.obj;
						eSpeak2 = (EditText) findViewById(R.id.eSpeak2);
						eSpeak2.setText(str);
						//try {
							//byte[] bb = toByteArray(msg.obj);
							//eSpeak2.setText(":"+(Byte)bb[0]+","+(Byte)bb[1]);
						//	Byte bb = (Byte)msg.obj;
						//	eSpeak2.setText(":"+bb);
						//} catch (IOException e) {
						//}
					break;
				}
			};		
		};
		
	}
	
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, "PREFS");
		mi.setIntent(new Intent(this, PrefActivity.class));
		MenuItem mi2 = menu.add(0, 2, 1, "TTS");
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
			case R.id.bSpeak: 
				speakText = eSpeak.getText().toString();
				off = true;
				comm += "off";
				break;
			case R.id.bSpeak2: 
				speakText = eSpeak2.getText().toString();
				off = true;
				comm += "off";
				//Toast.makeText(context, "111", Toast.LENGTH_SHORT).show();
				break;
			case R.id.bBTConnect: 
				btAdapter = BluetoothAdapter.getDefaultAdapter();
				String btAddress = sp.getString("btAddress", "98:D3:31:80:5B:FF");
				if (btAdapter != null && btAdapter.isEnabled()) {
					SendToRobot s2r = new SendToRobot(context);
					s2r.execute(new String[] { "BT_OFF" });
					mBTConnectThread = new BTConnectThread(btAdapter, btAddress, h, RECIEVE_MESSAGE_BT_CONNECT);
					mBTConnectThread.start();
				} else {
					Toast.makeText(context, "BluetoothAdapter is disabled!", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.bBTDisconnect: 
				if (mBTConnectThread != null) {
					try {
						btAdapter = null;
						mBTConnectThread.cancel();
						SendToRobot s2r = new SendToRobot(context);
						s2r.execute(new String[] { "BT_ON" });
						Toast.makeText(context, "BluetoothAdapter is disabled successful!", Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(context, "BluetoothAdapter disabled is failed!", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context, "BluetoothAdapter is not been enabled!", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				//Toast.makeText(context, "444", Toast.LENGTH_SHORT).show();
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

				//if (left || right) {
				//	val1 = 10;
				//};

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
			};

			speed += String.format("%03d", 2);// pwm count
			speed += String.format("%03d", val1);
			speed += String.format("%03d", val2);

			String sendData = speed + comm;
			// eSpeak.setText(sendData);
			// Toast.makeText(context, sendData, Toast.LENGTH_SHORT).show();
			
			if (btAdapter != null && btAdapter.isEnabled() && btSocket != null && btSocket.getRemoteDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
				String str = sendData;
				if (str.charAt(0) == '0') {
					String val = str.substring(3);
					byte pwmCount = Byte.valueOf(str.substring(0, 3));
					sss(val.substring(pwmCount * 3), val.substring(0, (pwmCount * 3)));
				}
			} else {
				SendToRobot s2r = new SendToRobot(context);
				s2r.execute(new String[] { sendData });
			}

		}
		return false;
	}
	
	public void sss(String comm, String speed) {
		byte s1 = 0, s2 = 0;
		boolean front = false, left = false, right = false, back = false, up1 = false, down1 = false, up2 = false, down2 = false, manip1on = false, manip1off = false, manip2on = false, manip2off = false, off = false;

		if (comm != "off") {
			front = comm.contains("front") && comm != "off";
			left = comm.contains("left") && comm != "off";
			right = comm.contains("right") && comm != "off";
			back = comm.contains("back") && comm != "off";
			up1 = comm.contains("up1") && comm != "off";
			down1 = comm.contains("down1") && comm != "off";
			up2 = comm.contains("up2") && comm != "off";
			down2 = comm.contains("down2") && comm != "off";
			manip1on = comm.contains("manip1on") && comm != "off";
			manip1off = comm.contains("manip1off") && comm != "off";
			manip2on = comm.contains("manip2on") && comm != "off";
			manip2off = comm.contains("manip2off") && comm != "off";

			s1 = speed.length() >= 3 ? Byte.valueOf(speed.substring(0, 3)) : 0;
			s2 = speed.length() >= 6 ? Byte.valueOf(speed.substring(3, 6)) : 0;
		};

		sendData(front, left, right, back, up1, down1, up2, down2, manip1on,
				manip1off, manip2on, manip2off, s1, s2);

	}

	public static byte b2i(boolean value) {
		byte one = 1;
		byte nul = 0;
		return value ? one : nul;
	}

	public void sendData(boolean front, boolean left, boolean right,
			boolean back, boolean up1, boolean down1, boolean up2,
			boolean down2, boolean manip1on, boolean manip1off,
			boolean manip2on, boolean manip2off, byte val1, byte val2) {
		byte[] data = new byte[54];
		for (int i = 0; i <= 53; i++) {
			data[i] = 0;
		}
		data[12] = (byte) (val1 * b2i(front || left || right || back));// pwm for left front motor
		data[11] = (byte) (val1 * b2i(front || left || right || back));// pwm for left back motor
		data[10] = (byte) (val1 * b2i(front || left || right || back));// pwm for right front motor
		data[9]  = (byte) (val1 * b2i(front || left || right || back));// pwm for right back motor
		data[13] = (byte) (val2 * b2i(up1 || down1 || up2 || down2 || manip1on
				|| manip1off || manip2on || manip2off));// pwm for left and right hands motors

		data[22] = b2i(front || right);// rel for left motors -> front
		data[23] = b2i(left || back);// rel for left motors -> back
		data[24] = b2i(front || left);// rel for right motors -> front
		data[25] = b2i(right || back);// rel for right motors -> back

		data[26] = b2i(up1);// rel for left hand -> up
		data[27] = b2i(down1);// rel for left hand -> down
		data[28] = b2i(up2);// rel for right hand -> up
		data[29] = b2i(down2);// rel right hand -> dowm

		data[30] = b2i(manip1on);// rel for left hand manipulator -> open
		data[31] = b2i(manip1off);// rel for left hand manipulator -> close
		data[32] = b2i(manip2on);// rel for right hand manipulator -> open
		data[33] = b2i(manip2off);// rel for right hand manipulator -> close

		sendControlData(data);
	}

	public void sendControlData(byte[] data) {
		try {
			//mBTConnectedThread.write(data);
			OutputStream btOutStream = btSocket.getOutputStream();
			btOutStream.write(data);
		} catch (IOException e) {
			Toast.makeText(this.getApplicationContext(), "bt error send", Toast.LENGTH_SHORT).show();
		}
	}
	
}