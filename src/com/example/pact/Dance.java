package com.example.pact;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Dance extends ActionBarActivity {

	/* ATTRIBUTS LIES AU MEDIAPLAYER */
	private Button clear = null;
	private Button play = null;
	private Button pause = null;
	private Button loop = null;
	private Button save = null;
	private Button metronom;
	private Button startRecording = null;
	private MediaPlayer mPlayer1;
	private MediaPlayer mPlayer2;
	private MediaPlayer mPlayer3;
	private MediaPlayer mPlayer4;
	private MediaPlayer mPlayer5;
	private boolean isLooping = false;

	// Pour Save
	// int[][] tableau = new int[2][1000];
	int i = 0;
	long ref;
	private ArrayList<Pas> piste = new ArrayList<Pas>();

	// Métronome
	Timer timer1 = new Timer("timer1");

	/* ATTRIBUTS LIES AU CHRONO ET A LA BARRE */
	private Timer timer = new Timer();
	private ProgressBar pb;
	private int MAX_DURATION = 300000;
	private boolean stopped;
	private boolean paused;
	private boolean started;
	private Chronometer chronometer;
	private long timeWhenStopped = 0;
	/* garde en memoire le temps ecoule avant une pause */

	// Attributs Bluetooth
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final int MESSAGE_READ = 1;
	protected static final int SUCCESS_CONNECT = 0;
	ArrayAdapter<String> listAdapter;
	Button bSend;
	ListView listView;
	TextView textViewReceive;
	EditText editTextSend;
	private boolean isBTEnabled = false;

	BluetoothAdapter btAdapter;
	Set<BluetoothDevice> devicesArray;
	ArrayList<String> pairedDevices;
	ArrayList<BluetoothDevice> devices;
	IntentFilter filter;
	BroadcastReceiver receiver;
	Handler mHandler;
	BluetoothDevice myDevice;

	/* ATTRIBUTS DU SPINNER */
	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	public ArrayList<SpinnerModel> CustomListViewValuesThemes = new ArrayList<SpinnerModel>();
	CustomAdapter themeAdapter;

	CustomAdapter adapter;
	Dance activity = null;
	private Integer[] theme2soundIds = { R.raw.accordeon1, R.raw.accordeon2,
			R.raw.accordeon3 };
	private Integer[] theme1soundIds = { R.raw.percu1, R.raw.percu2,
			R.raw.percu3, R.raw.percu5, R.raw.percu4 };
	private Integer[] theme3soundIds = { R.raw.cloccloc, R.raw.applaudissement,
			R.raw.mm, R.raw.empty_sound, R.raw.enchainement };
	private Integer[] theme4soundIds = { R.raw.cloccloc, R.raw.applaudissement,
			R.raw.mm, R.raw.empty_sound, R.raw.enchainement };
	private Integer[][] soundThemes = { theme1soundIds, theme2soundIds,
			theme3soundIds, theme4soundIds };
	private Integer[] sensorTheme;
	private Button buttonPreview1;
	private Button buttonPreview2;
	private Button buttonPreview3;
	private Button buttonPreview4;
	private Spinner SpinnerSensorTheme;

	private Spinner SpinnerSensor1;
	private Spinner SpinnerSensor2;
	private Spinner SpinnerSensor3;
	private Spinner SpinnerSensor4;
	int sensor1MusicId;
	int sensor2MusicId;
	int sensor3MusicId;
	int sensor4MusicId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dance);

		/* PARTIE SPINNERS */

		activity = this;

		SpinnerSensorTheme = (Spinner) findViewById(R.id.spinnerSensorTheme);
		SpinnerSensor1 = (Spinner) findViewById(R.id.spinnerSensor1);
		SpinnerSensor2 = (Spinner) findViewById(R.id.spinnerSensor2);
		SpinnerSensor3 = (Spinner) findViewById(R.id.spinnerSensor3);
		SpinnerSensor4 = (Spinner) findViewById(R.id.spinnerSensor4);

		setListData("Sound ");
		setListTheme("Theme ");

		Resources res = getResources();
		adapter = new CustomAdapter(activity, R.layout.spinner_rows,
				CustomListViewValuesArr, res);
		themeAdapter = new CustomAdapter(activity, R.layout.spinner_rows,
				CustomListViewValuesThemes, res);

		SpinnerSensorTheme.setAdapter(themeAdapter);
		SpinnerSensor1.setAdapter(adapter);
		SpinnerSensor2.setAdapter(adapter);
		SpinnerSensor3.setAdapter(adapter);
		SpinnerSensor4.setAdapter(adapter);

		SpinnerSensorTheme
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View v, int position, long id) {
						sensorTheme = soundThemes[position];
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
					}
				});

		SpinnerSensor1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long id) {
				sensor1MusicId = sensorTheme[position].intValue();
				buttonPreview1 = (Button) findViewById(R.id.buttonPreview1);
				buttonPreview1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						playSound(sensor1MusicId, mPlayer1);
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		SpinnerSensor2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long id) {
				sensor2MusicId = sensorTheme[position].intValue();
				buttonPreview2 = (Button) findViewById(R.id.buttonPreview2);
				buttonPreview2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						playSound(sensor2MusicId, mPlayer2);
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		SpinnerSensor3.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long id) {
				sensor3MusicId = sensorTheme[position].intValue();
				buttonPreview3 = (Button) findViewById(R.id.buttonPreview3);
				buttonPreview3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						playSound(sensor3MusicId, mPlayer3);
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		SpinnerSensor4.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long id) {
				sensor4MusicId = sensorTheme[position].intValue();
				buttonPreview4 = (Button) findViewById(R.id.buttonPreview4);
				buttonPreview4.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						playSound(sensor4MusicId, mPlayer4);
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		/* PARTIE BLUETOOTH */

		init();

		if (btAdapter == null) {
			Toast.makeText(getApplicationContext(), "No bluetooth detected", 0)
					.show();
			finish();
		} else {
			if (!btAdapter.isEnabled()) {
				turnOnBT();
			}
			while (!btAdapter.isEnabled())
				;
			getPaireDevices();
			startDiscovery();
		}

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SUCCESS_CONNECT:
					ConnectedThread connectedThread = new ConnectedThread(
							(BluetoothSocket) msg.obj);
					Toast.makeText(getApplicationContext(), "CONNECT", 0)
							.show();
					connectedThread.start();
					break;
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					String receiveMsg = new String(readBuf);

					if (isBTEnabled == true) {

						if (receiveMsg.contains("1")) {
							playSound(sensor1MusicId, mPlayer1);
							if (isLooping == true) {
								/*
								 * tableau[0][i] = (int) ((int)
								 * SystemClock.uptimeMillis()-ref);
								 * tableau[1][i] = sensor1MusicId; i++;
								 */
								Pas pas = new Pas(
										(int) ((int) SystemClock.uptimeMillis() - ref),
										sensor1MusicId);
								piste.add(pas);
							}
						} else if (receiveMsg.contains("2")) {
							playSound(sensor2MusicId, mPlayer2);
							if (isLooping == true) {
								/*
								 * tableau[0][i] = (int) ((int)
								 * SystemClock.uptimeMillis()-ref) ;
								 * tableau[1][i] = sensor2MusicId; i++;
								 */
								Pas pas = new Pas(
										(int) ((int) SystemClock.uptimeMillis() - ref),
										sensor2MusicId);
								piste.add(pas);
							}

						} else if (receiveMsg.contains("3")) {
							playSound(sensor3MusicId, mPlayer3);
							if (isLooping == true) {
								/*
								 * tableau[0][i] = (int) ((int)
								 * SystemClock.uptimeMillis()-ref);
								 * tableau[1][i] = sensor3MusicId; i++;
								 */
								Pas pas = new Pas(
										(int) ((int) SystemClock.uptimeMillis() - ref),
										sensor3MusicId);
								piste.add(pas);
							}
						} else if (receiveMsg.contains("4")) {
							playSound(sensor4MusicId, mPlayer4);
							if (isLooping == true) {
								/*
								 * tableau[0][i] = (int) ((int)
								 * SystemClock.uptimeMillis()-ref);
								 * tableau[1][i] = sensor4MusicId; i++;
								 */
								Pas pas = new Pas(
										(int) ((int) SystemClock.uptimeMillis() - ref),
										sensor4MusicId);
								piste.add(pas);
							}
						}

						break;
					}
				}
			}
		};

		/* PARTIE MEDIAPLAYER */

		clear = (Button) findViewById(R.id.clear);
		play = (Button) findViewById(R.id.play);
		save = (Button) findViewById(R.id.save);
		pb = (ProgressBar) findViewById(R.id.progressBar);
		pause = (Button) findViewById(R.id.pause);
		loop = (Button) findViewById(R.id.loop);
		metronom = (Button) findViewById(R.id.metronom);
		startRecording = (Button) findViewById(R.id.startRecording);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		stopped = true;
		paused = false;
		started = false;
		pb.setMax(MAX_DURATION); // on suppose que la duree maxi est 5 minutes
		pb.setProgress(0);
		startProgress();

		/*
		 * metronom.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub timer1.schedule(new TimerTask() { int bpm;
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * playSound(R.raw.applaudissement, mPlayer5); }
		 * 
		 * }, 1, bpm); // A faire avec un BPM que l'utilisateur peut rentrer }
		 * });
		 */

		startRecording.setOnClickListener(new View.OnClickListener() {

			/*
			 * ce bouton demarre l'enregistrement des pas, lance le chrono,
			 * lance la progressbar Tant que clear n'est pas selectionne, il ne
			 * fait plus rien
			 */

			@Override
			public void onClick(View arg0) {
				isBTEnabled = true;

				if (stopped == true) {
					chronometer.setBase(SystemClock.elapsedRealtime()
							+ timeWhenStopped);
					chronometer.start();

					/*
					 * try { mmOutputStream.write("1+ \n".getBytes());
					 * 
					 * } catch (IOException e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); }
					 */

					Toast.makeText(getBaseContext(), "Recording started",
							Toast.LENGTH_SHORT).show();
					stopped = false;
					started = true;
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Still recording; press Pause to interrupt or Clear to stop and restart",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(getBaseContext(), "Sound saved",
						Toast.LENGTH_SHORT).show();
				
				//popup pour donner le nom du fichier à sauvegarder
				
				String result = "result";
				
				compileMusic(piste,result);

			}
		});

		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (started == true) {
					stopped = true;
					started = false;
					isBTEnabled = false;
					paused = false;
					chronometer.setBase(SystemClock.elapsedRealtime()); /*
																		 * reinitialisation
																		 * du
																		 * chronometre
																		 */
					timeWhenStopped = 0; /* reinitialisation du temps ecoule */
					pb.setProgress(0); /* remet la barre a zero */
					chronometer.stop(); /* empeche le chronometre de redemarrer */
					onStop();
					Toast.makeText(getBaseContext(), "Sound cleared",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		pause.setOnClickListener(new View.OnClickListener() {

			/*
			 * ce bouton permet d'interrompre momentanement l'enregistrement: le
			 * chrono s'arr�te, la progressbar aussi. Si on le selectionne une
			 * seconde fois, l'enregistrement reprend.
			 */

			public void onClick(View v) {

				if (started == true) {
					if (paused == false) {
						paused = true;
						isBTEnabled = false;

						timeWhenStopped = chronometer.getBase()
								- SystemClock.elapsedRealtime(); /*
																 * sauvegarde du
																 * temps deja
																 * ecoule
																 */
						chronometer.stop();
						onPause(0, mPlayer1);
						onPause(0, mPlayer2);
						onPause(0, mPlayer3);
						onPause(0, mPlayer4);
						Toast.makeText(getBaseContext(),
								"Recording interrupted", Toast.LENGTH_SHORT)
								.show();
					} else {
						paused = false;
						isBTEnabled = true;

						chronometer.setBase(SystemClock.elapsedRealtime()
								+ timeWhenStopped);
						chronometer.start();
						Toast.makeText(getBaseContext(), "Recording restarted",
								Toast.LENGTH_SHORT).show();

					}
				}

			}
		});

		play.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/*
				 * Toast.makeText( getApplicationContext(),
				 * "Censé rejouer ce qui a été sauvegardé depuis le dernier appui sur start recording"
				 * , Toast.LENGTH_SHORT).show(); final Chronometer chronometer1
				 * = (Chronometer) findViewById(R.id.chronometer1);
				 * chronometer1.setBase(0); chronometer1.start();
				 * 
				 * new Thread(new Runnable() {
				 * 
				 * @Override public void run() { int i = 0; int diffTime = 0; //
				 * TODO Auto-generated method stub while (// tableau[0][i] != 0
				 * piste.get(i) != null) { if (i != 0) { // diffTime =
				 * tableau[0][i] - tableau[0][i-1]; diffTime =
				 * piste.get(i).getTime() - piste.get(i - 1).getTime(); } if (i
				 * % 3 == 0) playSound(piste.get(i).getId(), mPlayer1); else if
				 * (i % 3 == 1) playSound(piste.get(i).getId(), mPlayer2); else
				 * playSound(piste.get(i).getId(), mPlayer3); i++;
				 * Thread.currentThread(); try { Thread.sleep(diffTime); } catch
				 * (InterruptedException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } } } }).start();
				 */

				String result = "result";
			}
		});

		loop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				isLooping = !isLooping;
				if (isLooping == true)
					ref = SystemClock.uptimeMillis();

			}
		});

	}

	/* FONCTION RELATIVE AU CHRONOMETRE */

	void startProgress() {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!stopped && !paused) // call ui only when the progress is
											// not stopped
				{
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								pb.setProgress(pb.getProgress() + 1000);
							} catch (Exception e) {
							}
						}
					});
				}
			}
		}, 1, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dance, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* FONCTIONS RELATIVES AU MEDIAPLAYER */

	public void playSound(int resId, MediaPlayer mPlayer) {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
		}
		mPlayer = MediaPlayer.create(this, resId);
		// mPlayer.setLooping(isLooping);
		mPlayer.start();
	}

	public void onPause(int k, MediaPlayer mPlayer) {
		super.onPause();
		if (mPlayer != null) {
			mPlayer.pause();
		}
	}

	public void resume(MediaPlayer mPlayer) {
		if (mPlayer != null) {
			mPlayer.start();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mPlayer1 != null) {
			mPlayer1.stop();

		}
	}

	/* FONCTION RELATIVE A LA SYNTHESE DU SON WAV */

	public void concatMusic(String resultingfile, String music1, String music2)
			throws IOException {
		try {

			FileInputStream fistream1 = new FileInputStream(music1); // first
																		// source
																		// file
			FileInputStream fistream2 = new FileInputStream(music2);// second
																	// source
																	// file
			SequenceInputStream sistream = new SequenceInputStream(fistream1,
					fistream2);
			FileOutputStream fostream = new FileOutputStream(resultingfile);// destinationfile

			int temp;

			try {
				while ((temp = sistream.read()) != -1)
					// System.out.print( (char) temp ); // to print at DOS
					// prompt
					fostream.write(temp); // to write to file
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * public void compileMusic(ArrayList<Pas> piste) {
	 * 
	 * int k = 1; int j = 0;
	 * 
	 * while (piste.get(k) != null) { int diffTime2 = piste.get(k).getTime() -
	 * piste.get(k - 1).getTime(); int numberOfBlanks = (int)
	 * Math.floor((double) diffTime2 / 0.2);
	 * 
	 * while (j <= numberOfBlanks) { try {
	 * concatMusic("res/raw/saved_sound.wav", "res/raw/saved_sound.wav",
	 * "res/raw/empty_sound.wav"); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } j++; } String
	 * soundToAdd = "res/raw/" + getResources().getString(piste.get(k).getId())
	 * + ".wav"; try { concatMusic("res/raw/saved_sound.wav",
	 * "res/raw/saved_sound.wav", soundToAdd); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } k++; } }
	 */

	/*
	 * public void compileMusic(ArrayList<Pas> piste, double duration, String
	 * result) {
	 * 
	 * int k = 1; int j = 0;
	 * 
	 * WavFile sortie = null;
	 * 
	 * sortie = newWaveFile("result" + ".wav", duration); double[] buffersortie
	 * = new double[48000 * 30];
	 * 
	 * while (piste.get(k) != null) { int diffTime3 = piste.get(k).getTime() -
	 * piste.get(0).getTime(); diffTime3 = diffTime3 * 48000; double[] entree =
	 * new double[48000 * 5]; WavFile wavFile =
	 * getTheGoodWavFile(piste.get(k).getId()); readFile(wavFile, entree);
	 * 
	 * for (j = 0; j < 48000 * 5; j++) { buffersortie[diffTime3 + j] =
	 * entree[j]; } try { sortie.writeFrames(buffersortie, 10000); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } catch (WavFileException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } k++; }
	 * 
	 * }
	 */
	public void compileMusic(ArrayList<Pas> piste,String sortie){
		int n=piste.size();
		ArrayList<Integer> Sortie= new ArrayList<Integer>();
		ajoutMemoire(Sortie,TAILLE_ENTETE);
		entete(Sortie);
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		int diffTime4=0;
		for(int i=0;i<n;i++){
			try {
				list.add(toUnsignedList(lireWav(getTheGoodFile(piste.get(i).getId()))));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			diffTime4=piste.get(i).getTime()-piste.get(0).getTime();
			inserer(Sortie,diffTime4,list.get(i));
		}
		finir(Sortie);
		try {
			ecrireWav(sortie+".wav", toArray(Sortie));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

	/* FONCTIONS RELATIVES AU BLUETOOTH */

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		btAdapter.cancelDiscovery();
		unregisterReceiver(receiver);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(), "Bluetooth must be enable",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void startDiscovery() {
		// TODO Auto-generated method stub
		btAdapter.cancelDiscovery();
		btAdapter.startDiscovery();

	}

	private void turnOnBT() {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, 1);
	}

	private void getPaireDevices() {
		devicesArray = btAdapter.getBondedDevices();
		if (devicesArray.size() > 0) {
			for (BluetoothDevice device : devicesArray) {
				// pairedDevices.add(device.getName());
				listAdapter.add(device.getName());
				if (device.getName().equals("PACT51")) {
					myDevice = device;
				}
			}
		}
	}

	private void init() {
		bSend = (Button) findViewById(R.id.bSend);
		textViewReceive = (TextView) findViewById(R.id.textViewReceive);
		editTextSend = (EditText) findViewById(R.id.editTextSend);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (btAdapter.isDiscovering()) {
					btAdapter.cancelDiscovery();
				}
				if (listAdapter.getItem(arg2).contains(myDevice.getName())) {
					Toast.makeText(getApplicationContext(), "device is paried",
							0).show();

					ConnectThread connect = new ConnectThread(myDevice);
					connect.start();
				} else {
					Toast.makeText(getApplicationContext(),
							"device is not paried", 0).show();
				}
			}
		});

		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 0);
		listView.setAdapter(listAdapter);

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = new ArrayList<String>();
		devices = new ArrayList<BluetoothDevice>();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					devices.add(device);
					String s = "";
					for (int a = 0; a < pairedDevices.size(); a++) {
						if (device.getName().equals(pairedDevices.get(a))) {
							s = "(Paired)";
							break;
						}

					}

					listAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}

				else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {
					// run some code
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					// run some code
				} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					if (btAdapter.getState() == btAdapter.STATE_OFF) {
						turnOnBT();
					}
				}
			}
		};

		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);

	}

	private class ConnectThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private InputStream mmInputStream;
		private OutputStream mmOutputStream;
		private String msg;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			btAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
				mmOutputStream = mmSocket.getOutputStream();
				mmInputStream = mmSocket.getInputStream();

			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)

			// manageConnectedSocket(mmSocket);

			mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();

			bSend.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String editTextMsg = editTextSend.getText().toString();
					msg = editTextMsg + "\n";
					try {
						mmOutputStream.write(msg.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		}

		private void manageConnectedSocket(BluetoothSocket mmSocket2) {
			// TODO Auto-generated method stub

		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; // bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					// Send the obtained bytes to the UI activity
					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
							.sendToTarget();
				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/* FONCTION RELATIVE AU SPINNER */

	public void setListData(String str) {
		for (int i = 1; i < 6; i++) {
			final SpinnerModel sched = new SpinnerModel();
			sched.setSoundName(str + i);
			CustomListViewValuesArr.add(sched);
		}
	}

	public void setListTheme(String str) {
		for (int i = 1; i < 11; i++) {
			final SpinnerModel sched = new SpinnerModel();
			sched.setSoundName(str + i);
			sched.setImage("sound_icon_" + i);
			CustomListViewValuesThemes.add(sched);
		}
	}

	// Partie pour merger
	// Partie pour faire d'une séquence de pas un seul et unique fichier audio
	public final static int TAILLE_ENTETE = 44;
	public final static int FREQUENCE = 11025;

	public static void entete(ArrayList<Integer> data) {
		byte[] str = { 0x52, 0x49, 0x46, 0x46, // "RIFF"
				0x00, 0x00, 0x00, 0x00, // taille du fichier - 8
				0x57, 0x41, 0x56, 0x45, // "WAVE"
				0x66, 0x6D, 0x74, 0x20, // "fmt "
				0x10, 0x00, 0x00, 0x00, // 0x10
				0x01, 0x00, // format audio
				0x01, 0x00, // nombre de canaux
				0x00, 0x00, 0x00, 0x00, // fréquence d'échantillonnage 11025 /
										// 22050 / 44100
				0x00, 0x00, 0x00, 0x00, // nombre octets par seconde
				0x01, 0x00, // nombre de canaux * bits par échantillon/8
				0x08, 0x00, // bits par échantillon
				0x64, 0x61, 0x74, 0x61, // "data"
				0x00, 0x00, 0x00, 0x00 }; // taille du fichier - 44

		ecrire(str, 24, FREQUENCE, 4);
		ecrire(str, 28, FREQUENCE, 4);

		for (int i = 0; i < TAILLE_ENTETE; i++)
			data.set(i, (int) str[i]);

	}

	private static void ecrire(byte[] data, int pos, int n, int size) {
		for (int i = pos; i < pos + size; i++) {
			data[i] = (byte) (n % 256);
			n /= 256;
		}
	}

	private static void ecrire(ArrayList<Integer> data, int pos, int n, int size) {
		for (int i = pos; i < pos + size; i++) {
			data.set(i, (int) (n % 256));
			n /= 256;
		}
	}

	private static void finir(ArrayList<Integer> s) {
		ecrire(s, 4, s.size() - 8, 4);
		ecrire(s, 40, s.size() - TAILLE_ENTETE, 4);
	}

	public static int find(final byte[] str, final byte[] str2) {
		int r = 0;
		for (int i = 0; i < str.length - str2.length; i++) {
			r = i;
			for (int j = 0; j < str2.length; j++)
				if (str[i + j] != str2[j])
					r = 0;
			if (r != 0)
				break;
		}
		return r;
	}

	public static void ajoutMemoire(ArrayList<Integer> s, int size) {
		for (int i = s.size(); i < size; i++)
			s.add(null);
	}

	public static void inserer(ArrayList<Integer> s, double t,
			ArrayList<Integer> r) {
		int l = (int) (TAILLE_ENTETE + t * FREQUENCE);
		if (l + r.size() > s.size())
			ajoutMemoire(s, l + r.size());
		for (int i = 0; i < r.size(); i++) {
			if (i < 1000)
				// System.out.println(i + " " + s.get(i+l) + "+" + r.get(i));

				s.set(i + l, s.get(i + l) + r.get(i));

		}
	}

	public static int toUnsigned(Integer integer) {
		// return a < 0 ? 127- a : a;
		return (int) integer & 0xFF;
	}

	public static int toUnsigned1(byte a) {
		// return a < 0 ? 255+ a : a;
		return (int) a & 0xFF;
	}

	public static ArrayList<Integer> toUnsignedList(ArrayList<Integer> list) {
		for (int k = 0; k < list.size(); k++) {
			list.set(k, toUnsigned(list.get(k)));
		}
		/*
		 * do{ list.set(k,toUnsigned(list.get(k)));
		 * System.out.println(list.get(k)); k++;} while(list.get(k)!=null);
		 */
		/*
		 * for(Integer i:list){ toUnsigned(i); System.out.println(i); }
		 */

		return list;

	}

	public static ArrayList<Integer> lireWav(String s) throws IOException {
		File file = new File(s);
		byte[] fileData = new byte[(int) file.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		dis.readFully(fileData);
		dis.close();
		byte[] str = { 'd', 'a', 't', 'a' };
		int tailleEntete = find(fileData, str) + 8;
		ArrayList<Integer> r = new ArrayList<Integer>();
		for (int i = tailleEntete; i < fileData.length; i++)
			r.add((int) fileData[i]);
		// r.add(toUnsigned(fileData[i]));
		return r;
	}

	public static void ecrireWav(String s, byte[] fileData) throws IOException {
		File file = new File(s);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		dos.write(fileData, 0, fileData.length);
		dos.close();
	}

	static byte[] toArray(ArrayList<Integer> t) {
		int m = 0;
		for (Integer i : t) {
			m = Math.max(m, i);
		}
		System.out.println("m=" + m);
		m=256;//malheursement plante si m!256

		byte[] r = new byte[t.size()];
		for (int i = 0; i < t.size(); i++) {
			byte a = (byte) ((t.get(i) * 256 / (m)));			
			r[i] = a;
		}
		return r;
	}

	public String getTheGoodFile(int id) {
		/*
		 * if (id == R.raw.accordeon1) return wavFile11; else if (id ==
		 * R.raw.accordeon2) return wavFile12; else if (id == R.raw.accordeon3)
		 * return wavFile13; else if (id == R.raw.accordeon4) return wavFile14;
		 * else if (id == R.raw.accordeon5) return wavFile15; else if (id ==
		 * R.raw.clarinette1) return wavFile21; else if (id ==
		 * R.raw.clarinette2) return wavFile22; else if (id ==
		 * R.raw.clarinette3) return wavFile23; else if (id ==
		 * R.raw.clarinette4) return wavFile24; else if (id ==
		 * R.raw.clarinette5) return wavFile25; else if (id ==
		 * R.raw.clarinette6) return wavFile26;
		 */
		if (id == R.raw.contrebasse1)
			return "res/raw/contrebasse1.wav";
		/*
		 * else if (id == R.raw.contrebasse2) return wavFile32; else if (id ==
		 * R.raw.contrebasse3) return wavFile33; else if (id ==
		 * R.raw.contrebasse4) return wavFile34; else if (id ==
		 * R.raw.contrebasse5) return wavFile35; else if (id ==
		 * R.raw.contrebasse7) return wavFile37; else if (id ==
		 * R.raw.contrebasse8) return wavFile38;
		 */
		else if (id == R.raw.percu1)
			return "res/raw/percu1.wav";
		/*
		 * else if (id == R.raw.percu2) return wavFile42; else if (id ==
		 * R.raw.percu3) return wavFile43; else if (id == R.raw.percu4) return
		 * wavFile44; else if (id == R.raw.percu5) return wavFile45;
		 */
		else
			return "res/raw/percu1.wav";
	}

	// Finalement je n'utilise pas l'objet wavfile
	/*
	 * WavFile wavFile11 = newWaveFile("res/raw/accordeon1.wav", 2); WavFile
	 * wavFile12 = newWaveFile("res/raw/accordeon2.wav", 4); WavFile wavFile13 =
	 * newWaveFile("res/raw/accordeon3.wav", 5); WavFile wavFile14 =
	 * newWaveFile("res/raw/accordeon4.wav", 7); WavFile wavFile15 =
	 * newWaveFile("res/raw/accordeon5.wav", 2); WavFile wavFile21 =
	 * newWaveFile("res/raw/clarinette1.wav", 4); WavFile wavFile22 =
	 * newWaveFile("res/raw/clarinette2.wav", 4); WavFile wavFile23 =
	 * newWaveFile("res/raw/clarinette3.wav", 4); WavFile wavFile24 =
	 * newWaveFile("res/raw/clarinette4.wav", 4); WavFile wavFile25 =
	 * newWaveFile("res/raw/clarinette5.wav", 4); WavFile wavFile26 =
	 * newWaveFile("res/raw/clarinette6.wav", 4); WavFile wavFile31 =
	 * newWaveFile("res/raw/contrebasse1.wav", 2); WavFile wavFile32 =
	 * newWaveFile("res/raw/contrebasse2.wav", 2); WavFile wavFile33 =
	 * newWaveFile("res/raw/contrebasse3.wav", 2); WavFile wavFile34 =
	 * newWaveFile("res/raw/contrebasse4.wav", 0.5); WavFile wavFile35 =
	 * newWaveFile("res/raw/contrebasse5.wav", 0.5); WavFile wavFile37 =
	 * newWaveFile("res/raw/contrebasse7.wav", 1); WavFile wavFile38 =
	 * newWaveFile("res/raw/contrebasse8.wav", 2); WavFile wavFile41 =
	 * newWaveFile("res/raw/percu1", 0.5); WavFile wavFile42 =
	 * newWaveFile("res/raw/percu2", 0.5); WavFile wavFile43 =
	 * newWaveFile("res/raw/percu3", 0.5); WavFile wavFile44 =
	 * newWaveFile("res/raw/percu4", 0.5); WavFile wavFile45 =
	 * newWaveFile("res/raw/percu5", 0.5);
	 * 
	 * 
	 * 
	 * public WavFile newWaveFile(String soundTitle, double duration) { int
	 * sampleRate = 48000; // Samples per second
	 * 
	 * WavFile wavFile = null; // Calculate the number of frames required for
	 * specified duration long numFrames = (long) (duration * sampleRate);
	 * 
	 * try { wavFile = WavFile.newWavFile(new File(soundTitle), 2, numFrames,
	 * 16, sampleRate); } catch (Exception e) { e.printStackTrace(); } return
	 * wavFile; }
	 * 
	 * public void readFile(WavFile wavFile, double[] buffer) {
	 * 
	 * //int numChannels = wavFile.getNumChannels();
	 * 
	 * // Create a buffer of 10000 frames buffer = new double[48000*5];
	 * 
	 * int framesRead = 0;
	 * 
	 * do { // Read frames into buffer try { framesRead =
	 * wavFile.readFrames(buffer, 10000); }
	 * 
	 * catch (Exception e) { System.err.println(e); }
	 * 
	 * } while (framesRead != 0);
	 * 
	 * try { wavFile.close(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 * 
	 * }
	 */
}
