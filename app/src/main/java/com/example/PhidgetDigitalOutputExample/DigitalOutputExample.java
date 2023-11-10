package com.example.PhidgetDigitalOutputExample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.widget.ListView;
import android.content.Intent;

import com.phidget22.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DigitalOutputExample extends Activity {

	String[] gameArray = {"1. Order your Planets", "2. Match Weights", "3. Axis Rotation"};


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		// Game mode list
		ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listview, gameArray);
		ListView listView = findViewById(R.id.game_array);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {

				//getting game mode selected
				String gameValue = (String) listView.getItemAtPosition(position);

				Intent intentOrder = new Intent(DigitalOutputExample.this, Ordering.class);
				Intent intentWeight = new Intent(DigitalOutputExample.this, Weights.class);
				Intent intentAngle = new Intent(DigitalOutputExample.this, Angles.class);

				if(gameValue == "1. Order your Planets"){
					startActivity(intentOrder);
				}
				else if(gameValue == "2. Match Weights"){
					startActivity(intentWeight);
				}
				else if(gameValue == "3. Axis Rotation"){
					startActivity(intentAngle);
				}

			}
		});

    }

}

