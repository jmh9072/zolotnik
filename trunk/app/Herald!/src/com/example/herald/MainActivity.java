package com.example.herald;

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

public class MainActivity extends Activity {
	private Spinner interval_spinner; //controls the interval spinner
	private Button contactsBtn; //controls the contacts button
	private Button mapBtn; //controls the maps button
	private EditText recipientNumber; //controls the text field for recipient number
	public static final int PICK_CONTACT = 1; //used in contact selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //adds a listener to the spinner
        addListenerOnSpinnerItemSelection();
        
        contactsBtn = (Button) findViewById(R.id.contacts_btn);
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
            	Intent mapActivityIntent = new Intent(MainActivity.this, MapActivity.class);
            	startActivityForResult(mapActivityIntent, 1);
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
    
}
