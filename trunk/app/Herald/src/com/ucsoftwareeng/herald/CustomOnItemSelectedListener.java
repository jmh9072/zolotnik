package com.ucsoftwareeng.herald;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
/**
 * @CustomOnItemSelectedListener - listens for changes in spinner selection and pops toast when it changes
 *
 */
public class CustomOnItemSelectedListener implements OnItemSelectedListener {
	//makes toast on spinner selection
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		Toast.makeText(parent.getContext(), "Interval Time : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

}
