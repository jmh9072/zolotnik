package com.ucsoftwareeng.herald.test;
import junit.framework.TestCase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ucsoftwareeng.herald.MainActivity;


public class UIEnableDisableValidation extends ActivityInstrumentationTestCase2<MainActivity> {
	MainActivity mainActivity;
	private Spinner interval_spinner; 
	private Button contactsBtn; 
	private Button mapBtn; 
	private Button startBtn; 
	private Button stopBtn; 
	private EditText recipientNumber; 
	private EditText destinationAddress; 
	public UIEnableDisableValidation(String name) {
		super("com.ucsoftwareeng.herald.MainActivity", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mainActivity = getActivity();
		interval_spinner = (Spinner) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.interval_spinner);
		contactsBtn = (Button) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.contacts_btn);
		mapBtn = (Button) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.map_btn);
		startBtn = (Button) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.start_button);
		stopBtn = (Button) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.stop_button);
		recipientNumber = (EditText) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.recipient_number);
		destinationAddress = (EditText) mainActivity.findViewById(com.ucsoftwareeng.herald.R.id.destination_address);
	}
	
	public void testDisableUI() {
		mainActivity.disableUI();
		assertFalse(interval_spinner.isEnabled());
		assertFalse(contactsBtn.isEnabled());
		assertFalse(mapBtn.isEnabled());
		assertFalse(startBtn.isEnabled());
		assertTrue(stopBtn.isEnabled());
		assertFalse(recipientNumber.isEnabled());
		assertFalse(destinationAddress.isEnabled());
		
	}
	
	public void testEnableUI() {
		mainActivity.enableUI();
		assertTrue(interval_spinner.isEnabled());
		assertTrue(contactsBtn.isEnabled());
		assertTrue(mapBtn.isEnabled());
		assertTrue(startBtn.isEnabled());
		assertFalse(stopBtn.isEnabled());
		assertTrue(recipientNumber.isEnabled());
		assertTrue(destinationAddress.isEnabled());
	}

}