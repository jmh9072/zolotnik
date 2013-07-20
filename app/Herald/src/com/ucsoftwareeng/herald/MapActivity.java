package com.ucsoftwareeng.herald;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

public class MapActivity extends Activity {
	
    private GoogleMap mMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    }
	public MapActivity() {
		// TODO Auto-generated constructor stub
	}

}
