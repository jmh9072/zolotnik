package com.example.herald;

import android.app.Activity;
import android.os.Bundle;
//import com.google.android.gms.maps;


/**
 * @MapActivity - displays the map interface to search for a destination
 *
 */
public class MapActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

  //  private GoogleMap mMap;
    
   // mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    
	public MapActivity() {
		// TODO Auto-generated constructor stub
	}

}
