package com.ucsoftwareeng.herald;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class SmsService extends IntentService{
	
	private SmsManager sms = SmsManager.getDefault();
	private Timer timer = new Timer();
	private PowerManager pMgr;
	private WakeLock wakeLock;
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
		Bundle extras = intent.getExtras();//pulls bundle of variables from mainactivity
		destination = extras.getString("DESTINATION");//pulls the destination address
		recipient = extras.getString("RECIPIENT");//pulls the recipient number
		interval = extras.getInt("INTERVAL");//pulls the update interval
		
		pMgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock = pMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");//sets up the wake lock so the cpu can continue the timer on screenlock
		wakeLock.acquire();//begins the wakelock
		
		startRouteMessage();
		
		timer.scheduleAtFixedRate(new TimerTask() { 
			@Override 
			public void run() {
				travelUpdateMessage(); 
				} 
			}, interval, interval);//fires the travel update message after 0 milliseconds and repeatedly after the interval 
	}
	
	@Override
	public void onDestroy() {
		wakeLock.release();//releases the wakelock
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
