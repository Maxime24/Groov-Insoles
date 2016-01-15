package com.example.pact;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {

	private Activity activity;
	private ArrayList data;
	public Resources res;
	SpinnerModel tempValues = null;
	LayoutInflater inflater;

	
	public CustomAdapter(Dance activitySpinner, int textViewResourceId,
			ArrayList objects, Resources resLocal) {
		super(activitySpinner, textViewResourceId, objects);

		activity = activitySpinner;
		data = objects;
		res = resLocal;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	
	/*public CustomAdapter(OnlineResources activitySpinner, int textViewResourceId,
			ArrayList objects, Resources resLocal) {
		super(activitySpinner, textViewResourceId, objects);

		activity = activitySpinner;
		data = objects;
		res = resLocal;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}*/

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	// This funtion called for each row ( Called data.size() times )
	public View getCustomView(int position, View convertView, ViewGroup parent) {

		View row = inflater.inflate(R.layout.spinner_rows, parent, false);

		tempValues = null;
		tempValues = (SpinnerModel) data.get(position);

		TextView label = (TextView) row.findViewById(R.id.sound);

		ImageView soundLogo = (ImageView) row.findViewById(R.id.image);

		label.setText(tempValues.getSoundName());

		soundLogo.setImageResource(res.getIdentifier(
				"com.example.pact:drawable/" + tempValues.getImage(),
				null, null));

		return row;
	}
}