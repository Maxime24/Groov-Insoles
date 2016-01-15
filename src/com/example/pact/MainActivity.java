package com.example.pact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	private Button dance = null;
	private Button resources = null;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dance = (Button) findViewById(R.id.preview);
		resources = (Button) findViewById(R.id.button3);

		resources.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Intent i = new Intent(MainActivity.this, OnlineResources.class);
				//startActivity(i);
			}
		});

		dance.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(MainActivity.this, Dance.class);
				startActivity(i);

			}

		});
		


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
