package com.ucsoftwareeng.herald;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @MainActivity - displays the main interface for the application
 *
 */
public class MainActivity extends Activity {
	private Spinner interval_spinner; //controls the interval spinner
	private Button contactsBtn; //controls the contacts button
	private Button mapBtn; //controls the maps button
	private Button startBtn; //controls the Start Route Button
	private Button stopBtn; //controls the Stop Route Button
	private EditText recipientNumber; //controls the text field for recipient number
	private EditText destinationAddress; //controls the text field for destination address
	public static final int PICK_CONTACT = 1; //used in contact selection
	private String destination; //holds destination
	private String recipient; //holds recipeint 
	
	private GoogleMap gMap;
	Geocoder coder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //adds a listener to the spinner
        addListenerOnSpinnerItemSelection();
        startBtn = (Button) findViewById(R.id.start_button);
        stopBtn = (Button) findViewById(R.id.stop_button);
        contactsBtn = (Button) findViewById(R.id.contacts_btn);
        
        //Set up default map location over Cincinnati
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng Cincinnati = new LatLng(39.1619, -100);
		gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cincinnati, 4.0f));
		
		coder = new Geocoder(this); //Geocoder to translate City/Location names into Latitudes/Longitudes

        recipientNumber = (EditText) findViewById(R.id.recipient_number);
        destinationAddress = (EditText) findViewById(R.id.destination_address);
        //listener for contacts buttons
        contactsBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);//intent to open contacts
            	startActivityForResult(intent, PICK_CONTACT); //starts intent
            }
        });
        
        mapBtn = (Button) findViewById(R.id.map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View arg0) {
            	//Intent mapActivityIntent = new Intent(MainActivity.this, MapActivity.class);
            	
            	//startActivityForResult(mapActivityIntent, 1);
        		//startActivity(new Intent(MainActivity.this, MapActivity.class));
        		List<Address> address;
        		
        		try{
        			address = coder.getFromLocationName(destinationAddress.getText().toString(), 1);
        			if (address != null){
        				Address location = address.get(0);
        				LatLng destinationLocation = new LatLng(location.getLatitude(), location.getLongitude());
        				gMap.clear();
        				gMap.addMarker(new MarkerOptions().position(destinationLocation));
                		gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 6.0f));
        			}
        		}
        		catch(IOException e)
        		{
        			Log.v("IOException", "No Latitude/Longitude found for city " + destinationAddress.getText().toString());
        		}
        		
        		//LatLng Cincinnati = new LatLng(39.1619, -84.4569);
        		//gMap.addMarker(new MarkerOptions().position(Cincinnati));
        		//gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cincinnati, 6.0f));
            }
        });
        

        startBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
		    	destination = destinationAddress.getText().toString(); //temporary string will be replaced when data is pullled down from googleMaps
				recipient = recipientNumber.getText().toString();
				String interval = interval_spinner.getSelectedItem().toString();//retrieves interval from spinner
				String[] hoursMinutes = interval.split("[:]");//parses the interval string at the ":" character splitting to hours and minutes
				int realInterval = (Integer.parseInt(hoursMinutes[0])*3600000)+(Integer.parseInt(hoursMinutes[1])*60000);//converts hours and minutes to miliseconds and adds them together for total interval in miliseconds
				
				Intent intent = new Intent(getBaseContext(), SmsService.class);//intent for SmsService
				intent.putExtra("DESTINATION", destination);//sends destination
				intent.putExtra("RECIPIENT", recipient);//sends recipient number
				intent.putExtra("INTERVAL", realInterval);//sends interval
				startService(intent);//starts intent
				
				disableUI(); //disables UI elements

				//ToDo start countdown to next message and send arrival message also add function for data retrieval when possible
				Toast.makeText(getApplicationContext(), "Starting Route", Toast.LENGTH_SHORT).show();//makes toast so the user can tell its working, maybe include test after every message?
			}
		});
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				enableUI(); //enables the UI 
			}
		});

    }
    // writes the selected contact's number to the recipientnumber edittext field
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
    	super.onActivityResult(reqCode, resultCode, data);
    	
    	switch(reqCode) {
    		case(PICK_CONTACT):
    			if(resultCode == Activity.RESULT_OK){
    				Uri contactData = data.getData();
    				Cursor c = getContentResolver().query(contactData, null, null, null, null);
    				if(c.moveToFirst()) {
    					String phone_number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
    					recipientNumber.setText(phone_number);
    				}
    			}
    			break;
    	}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //spinner listener
    public void addListenerOnSpinnerItemSelection(){
    	interval_spinner = (Spinner) findViewById(R.id.interval_spinner);
    	interval_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    	
    }

    /**
     * @disableUI - disables the UI called when route is in progress
     *
     */
    public void disableUI(){
		startBtn.setVisibility(View.INVISIBLE);//gets rid of the start button so it will not be pressed multiple times
		startBtn.setEnabled(false); //disables the start btn from use
		stopBtn.setVisibility(View.VISIBLE);//shows the stop button so that it may be pressed
		stopBtn.setEnabled(true); //enables the stop btn 
		interval_spinner.setEnabled(false); //disables interval spinner
		contactsBtn.setEnabled(false);//disables contacts btn
		mapBtn.setEnabled(false); //disable map btn
		recipientNumber.setEnabled(false); //disables recipient number field
		destinationAddress.setEnabled(false); //disable destination address field
    }
    /**
     * @enableUI - enables the UI called when route is stopped 
     *
     */
    public void enableUI(){
		stopBtn.setVisibility(View.INVISIBLE);//hides the stop button so it will not be pressed multiple times
		stopBtn.setEnabled(false); //disables the stop btn 
		startBtn.setVisibility(View.VISIBLE);//shows the start
		startBtn.setEnabled(true); //enables the start btn
		interval_spinner.setEnabled(true); //enables interval spinner
		contactsBtn.setEnabled(true);//enables contacts btn
		mapBtn.setEnabled(true); //enable map btn
		recipientNumber.setEnabled(true); //enables recipient number field
		destinationAddress.setEnabled(true); //enable destination address field
		
    }
    
}
