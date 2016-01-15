package com.example.pact;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.widget.AdapterView.OnItemSelectedListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class OnlineResources extends ActionBarActivity {

	private Button download = null;
	private Button upload = null;
	private Button connect = null;
	private String fileName = "";
	private String hostName = null;

	/* ATTRIBUTS DU SPINNER */
	public ArrayList<SpinnerModel> CustomListViewValuesSongs = new

	ArrayList<SpinnerModel>();
	CustomAdapter songsAdapter;
	Dance activity2 = null;
	private String[] availableSongs;
	private Spinner SpinnerAvailableSongs;
	private String songToDL;
	private boolean isConnected = false;
	int portNumber = 2064;
	private Socket socket = null;
	private BufferedInputStream in = null;
	private BufferedOutputStream out = null;
	private PrintWriter pw = null;
	private BufferedReader lecture = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_resources);

		/* PARTIE SPINNER */

		
		SpinnerAvailableSongs = (Spinner) findViewById(R.id.spinnerAvailableSongs);
		setListSongs();
		Resources res2 = getResources();
		songsAdapter = new CustomAdapter(activity2, R.layout.spinner_rows,
				CustomListViewValuesSongs, res2);

		SpinnerAvailableSongs.setAdapter(songsAdapter);

		SpinnerAvailableSongs.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View v, int position, long id) {
						songToDL = availableSongs[position];
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
					}
				});

		download = (Button) findViewById(R.id.download);
		upload = (Button) findViewById(R.id.upload);
		connect = (Button) findViewById(R.id.connect);

		connect.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				isConnected = !isConnected;
				try {
					hostName = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					socket = new Socket(hostName, portNumber);
					in = new BufferedInputStream(socket.getInputStream());
					out = new BufferedOutputStream(socket.getOutputStream());
					pw = new PrintWriter(socket.getOutputStream(), true);
					lecture = new BufferedReader(new InputStreamReader(in));
				} catch (UnknownHostException e) {
					System.err.println("Don't know about host " + hostName);
					System.exit(1);
				} catch (IOException e) {
					System.err
							.println("Couldn't get I/O for the connection to "
									+ hostName);
					System.exit(1);
				} finally {
					try {
						pw.close();
						lecture.close();
						out.close();
						in.close();
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		upload.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (isConnected = true) {
					/* premier cas: on envoie le nom du fichier puis le fichier */
					pw.println(0);
					fileName = "cloccloc.wav";
					pw.println(fileName);
					envoyerFichier(
							"C:\\Users\\oussama\\Documents\\Client_Serveur\\Client\\"
									+ fileName, out);

				}
				Toast.makeText(getBaseContext(), "Please connect before",
						Toast.LENGTH_SHORT).show();

			}
		});

		download.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				/*
				 * deuxieme cas: on demande la liste des fichiers disponibles
				 * sur le serveur
				 */

				if (isConnected = true) {
					pw.println(1);
					String nbSons = null;
					try {
						nbSons = lecture.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					

					int j = Integer.parseInt(nbSons);
					System.out.println(j);
					availableSongs = new String[j];
					for (int i = 0; i < j; i++) {
						try {
							availableSongs[i] = lecture.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					/* on choisit un fichier et on le recoit */
					
					pw.println(songToDL);
					recevoirFichier(
							"C:\\Users\\oussama\\Documents\\Client_Serveur\\Client\\"
									+ songToDL, in);

				}
				Toast.makeText(getBaseContext(), "Please connect before",
						Toast.LENGTH_SHORT).show();
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.online_resources, menu);
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

	public void setListSongs() {
		int i = 0;
		while (availableSongs[i] != null) {
			final SpinnerModel sched = new SpinnerModel();
			sched.setSoundName(availableSongs[i]);
			CustomListViewValuesSongs.add(sched);
			i++;
		}
	}

	static private void envoyerFichier(String fileName, BufferedOutputStream out) {
		int fromFile = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fileName);
			do {
				fromFile = fis.read();
				list.add(new Integer(fromFile));
				out.flush();
			} while (fromFile != -1);
			Integer[] tab = new Integer[list.size()];
			tab = list.toArray(tab);
			byte[] fichier = new byte[tab.length];
			for (int i = 0; i < tab.length; i++) {
				fichier[i] = (byte) tab[i].intValue();
			}
			out.write(fichier);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	static private void recevoirFichier(String fileName, BufferedInputStream in) {
		int fromServer;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
			do {
				fromServer = in.read();
				if (fromServer != -1) {
					fos.write(fromServer);
				}
			} while (fromServer != -1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
