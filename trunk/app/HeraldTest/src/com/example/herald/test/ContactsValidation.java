package com.example.herald.test;

import junit.framework.TestCase;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.EditText;
import com.example.herald.MainActivity;

public class ContactsValidation extends ActivityInstrumentationTestCase2<MainActivity> {
	MainActivity mainActivity;
	EditText recipientNumber;
	public ContactsValidation() {
		super("com.example.herald.MainActivity", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mainActivity = getActivity();
		recipientNumber = (EditText) mainActivity.findViewById(com.example.herald.R.id.recipient_number);
	}
	
	private static final String STUFF = "S T U F F ENTER ";
	public void testNonNumber() {
		sendKeys(STUFF);
		String result = recipientNumber.getText().toString();
		assertEquals("letters do not go into the number field", "", result);
	}
	
	public void testNumber() {
		sendKeys("1");
		sendKeys(KeyEvent.KEYCODE_SPACE);
		sendKeys("5 5 5");
		sendKeys(KeyEvent.KEYCODE_NUMPAD_SUBTRACT);
		sendKeys("5 5 5");
		sendKeys(KeyEvent.KEYCODE_NUMPAD_SUBTRACT);
		sendKeys("5 5 5 5");
		
		String result = recipientNumber.getText().toString();
		assertEquals("phone numbers with select special characters do", "1 555-555-5555", result);
	}
	
	public void testSelectContacts() {
		for(int i = 0; i<5; i++)
		{
			sendKeys("TAB");
		}
		sendKeys("ENTER");
		
		//to do finish this test might look into content provider testing
	}

}
