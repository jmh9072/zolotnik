package com.ucsoftwareeng.herald;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class SmsService extends IntentService{
	
	private SmsManager sms = SmsManager.getDefault();
	private Timer timer = new Timer();
	private String destination; //holds destination
	private String eta;//holds estimated time of arrival 
	private String city;//holds current city
	private String state;//holds current state
	private String recipient;//holds recipient
	private int interval; // holds interval 
	
	public SmsService() {
		super("SmsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		destination = extras.getString("DESTINATION");
		recipient = extras.getString("RECIPIENT");
		interval = extras.getInt("INTERVAL");
		startRouteMessage();
		timer.scheduleAtFixedRate(new TimerTask() { 
			@Override 
			public void run() {
				travelUpdateMessage(); 
				} 
			}, interval, interval);//fires the travel update message after 0 milliseconds and repeatedly after the interval 
	}
	
    /**
     * @startRouteMessage - builds and sends message sent when user begins trip
     *
     */
    public void startRouteMessage(){
    	String message = getString(R.string.startRoute1) + destination + getString(R.string.startRoute2);
    	int stringLength = message.length();
    	if(stringLength<=160)
    	{
    		sms.sendTextMessage(recipient, null, message, null , null);
    	}
    	else
    	{
    		sms.sendMultipartTextMessage(recipient, null, parseString(message), null, null);
    	}
    }
    /**
     * @travelUpdateMessage - builds and sends travel update messages
     *
     */
    public void travelUpdateMessage(){
    	getMapData();
    	String message = getString(R.string.travelUpdate1) + eta + getString(R.string.travelUpdate2) + destination + getString(R.string.travelUpdate3) + city + getString(R.string.apostrophe) + state;
    	int stringLength = message.length();
    	if(stringLength<=160)
    	{
    		sms.sendTextMessage(recipient, null, message, null , null);
    	}
    	else
    	{
    		sms.sendMultipartTextMessage(recipient, null, parseString(message), null, null);
    	}
    }
    /**
     * @arrivalIndicationMessage - builds and sends message sent when user arrives at destination
     *
     */
    public void arrivalIndicationMessage(){//need to know how the eta in google maps is returned to write when this is sent
    	String message = getString(R.string.arivalMessage) + destination + getString(R.string.exclamationMark);
    	int stringLength = message.length();
    	if(stringLength<=160)
    	{
    		sms.sendTextMessage(recipient, null, message, null , null);
    	}
    	else
    	{
    		sms.sendMultipartTextMessage(recipient, null, parseString(message), null, null);
    	}
    }
    /**
     * @getMapData - retrieves eta, current city and current state from google maps
     *
     */
    public void getMapData(){
    	//something here to pull data from google maps

    	eta = "12 parsecs";//temporary string replaced when data can be retrieved
    	city = "Gotham";//temp string replaced when data can be retrieved
    	state = "Unified Dakota";//temp string replace when data can be retrieved
    }
    
    public ArrayList<String> parseString(String message){
    	ArrayList<String> splitMessage = new ArrayList<String>();
    	int counter = 0;
    	while(counter<message.length()){
    		splitMessage.add(message.substring(counter, Math.min(counter+160, message.length()) ));
    		message+=160;
    	}
    	return splitMessage;
    }
}
