package com.ucsoftwareeng.herald;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.telephony.SmsManager;
import java.util.Timer;
import java.util.TimerTask;

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
	public static final int PICK_CONTACT = 1; //used in contact selection
	private SmsManager sms = SmsManager.getDefault();
	private Timer timer = new Timer();
	private String destination; //holds destination
	private String eta;//holds estimated time of arrival 
	private String city;//holds current city
	private String state;//holds current state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //adds a listener to the spinner
        addListenerOnSpinnerItemSelection();
        startBtn = (Button) findViewById(R.id.start_button);
        stopBtn = (Button) findViewById(R.id.stop_button);
        contactsBtn = (Button) findViewById(R.id.contacts_btn);
        
        //startBtn.setVisibility(View.INVISIBLE); will make the start button invsible need adressing functionality before implementing 
        stopBtn.setVisibility(View.INVISIBLE);// makes the stop button invisible

        recipientNumber = (EditText) findViewById(R.id.recipient_number);
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
        		startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
        

        startBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
		    	destination = "Mordor"; //temporary string will be replaced when data is pullled down from googleMaps
				
				String interval = interval_spinner.getSelectedItem().toString();//retrieves interval from spinner
				String[] hoursMinutes = interval.split("[:]");//parses the interval string at the ":" character splitting to hours and minutes
				int realInterval = (Integer.parseInt(hoursMinutes[0])*3600000)+(Integer.parseInt(hoursMinutes[1])*60000);//converts hours and minutes to miliseconds and adds them together for total interval in miliseconds
				
				startBtn.setVisibility(View.INVISIBLE);//gets rid of the start button so it will not be pressed multiple times
				stopBtn.setVisibility(View.VISIBLE);//shows the stop button so that it may be pressed
				startRouteMessage();
				timer.scheduleAtFixedRate(new TimerTask() { 
					@Override 
					public void run() {
						travelUpdateMessage(); 
						} 
					}, 0, realInterval);//fires the travel update message after 0 milliseconds and repeatedly after the interval 

				//();
				//ToDo start countdown to next message and send arrival message also add function for data retrieval when possible
				Toast.makeText(getApplicationContext(), "Starting Route", Toast.LENGTH_SHORT).show();//makes toast so the user can tell its working, maybe include test after every message?
			}
		});
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				timer.cancel();
				stopBtn.setVisibility(View.INVISIBLE);//hides the stop button so it will not be pressed multiple times
				startBtn.setVisibility(View.VISIBLE);//shows the start btn idk if we want stop to clear fields might lose
				
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
    
    public void startRouteMessage(){
    	String message = "Hello! I've designated you as the recipient for travel updates on my trip to " + destination + ".\n" + "Powered by Herald!";
    	sms.sendTextMessage(recipientNumber.getText().toString(), null, message, null , null);
    }
    
    public void travelUpdateMessage(){
    	getMapData();
    	String message = "Herald! Location Update: \nI'm currently " + eta + " from my destination of " + destination + ". My current location is " + city + ", " + state;
    	sms.sendTextMessage(recipientNumber.getText().toString(), null, message, null, null);
    }
    
    public void arrivalIndicationMessage(){//need to know how the eta in google maps is returned to write when this is sent
    	String message = "Herald! Location Update: \n I have arrived at " + destination + "!";
    	sms.sendTextMessage(recipientNumber.getText().toString(), null, message, null, null);
    }
    
    public void getMapData(){
    	//something here to pull data from google maps

    	eta = "12 parsecs";//temporary string replaced when data can be retrieved
    	city = "Gotham";//temp string replaced when data can be retrieved
    	state = "Unified Dakota";//temp string replace when data can be retrieved
    }
    
}
