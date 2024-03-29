package com.ucsoftwareeng.herald;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

public class SmsService extends IntentService implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	
	private SmsManager sms = SmsManager.getDefault();
	private Timer timer = new Timer();
	private String destination; //holds destination
	private String eta;//holds estimated time of arrival 
	private String city;//holds current city
	private String state;//holds current state
	private String recipient;//holds recipient
	private int interval; // holds interval
	private Geocoder coder;
	private LocationClient locationClient;
	private LatLng destinationLocation;
	private long durationTime = 0;
	
	private GeoPoint origin;
	private GeoPoint endpoint;
	public SmsService() {
		super("SmsService");
	}
	
    /**
     * @onHandleIntent - called when an intent is passed to the service, sets up location, sends start route, starts time to send location updates 
     *
     */
	@Override
	protected void onHandleIntent(Intent intent) {
		locationClient = new LocationClient(this, this, this);
		locationClient.connect(); //start keeping location up to date
		Bundle extras = intent.getExtras();//pulls bundle of variables from mainactivity
		destination = extras.getString("DESTINATION");//pulls the destination address
		recipient = extras.getString("RECIPIENT");//pulls the recipient number
		interval = extras.getInt("INTERVAL");//pulls the update interval
		
		coder = new Geocoder(this); //Geocoder to translate City/Location names into Latitudes/Longitudes

		List<Address> address;
		
		try{
			address = coder.getFromLocationName(destination, 1);
			if (address != null){
				Address location = address.get(0);
				destinationLocation = new LatLng(location.getLatitude(), location.getLongitude());
			}
		}
		catch(IOException e)
		{
			//Log.v("IOException", "No Latitude/Longitude found for city " + destinationAddress.getText().toString());
		}
		
		while (!locationClient.isConnected()) //wait for locationClient to connect before sending first message
		{
			
		}
		startRouteMessage();
		/*timer.scheduleAtFixedRate(new TimerTask() { 
			@Override 
			public void run() {
				travelUpdateMessage(); 
				} 
			}, interval, interval);//fires the travel update message after the interval and repeatedly after the interval*/ 
	}
    /**
     * @onDestroy - called when the service is stopped
     *
     */
	@Override
	public void onDestroy() {
	}
	
    /**
     * @startRouteMessage - builds and sends message sent when user begins trip
     *
     */
    public void startRouteMessage(){
    	getMapData();
    	String message = getString(R.string.startRoute1) + destination + getString(R.string.startRoute2) + eta + getString(R.string.startRoute3);
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
    	String message = getString(R.string.travelUpdate1) + eta + getString(R.string.travelUpdate2) + destination; //+ getString(R.string.travelUpdate3) + city + getString(R.string.apostrophe) + state;
    	
    	int stringLength = message.length();
    	Log.v("SecondsToDestination", String.valueOf(durationTime));
    	if (durationTime > 120)
    	{
        	if(stringLength<=160)
        	{
        		sms.sendTextMessage(recipient, null, message, null , null);
        	}
        	else
        	{
        		sms.sendMultipartTextMessage(recipient, null, parseString(message), null, null);
        	}
        }
    	else
    	{
    		arrivalIndicationMessage();
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
    	timer.cancel();
    	this.stopSelf();
    }
    /**
     * @getMapData - retrieves eta, current city and current state from google maps
     *
     */
    public void getMapData(){
    	//something here to pull data from google maps

    	Location currentLocation = locationClient.getLastLocation();
    	Log.v("LAT", Double.toString(currentLocation.getLatitude()));
    	Log.v("LONG", Double.toString(currentLocation.getLongitude()));
    	
    	LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    	try{
    		eta = getCurrentTravelETA(currentLatLng, destinationLocation); //"12 parsecs";//temporary string replaced when data can be retrieved
    	}
    	catch (Exception e)
    	{
    		Log.v("getCurrentTravelETA", "error");
    	}
    	//city = "Gotham";//temp string replaced when data can be retrieved
    	//state = "Unified Dakota";//temp string replace when data can be retrieved
    }
    /**
     * @parseString - when a string is too long to be sent in a text message this cuts the string into multiple 160 character strings in an array to be sent in a multipart message
     *
     */
    public ArrayList<String> parseString(String message){
    	ArrayList<String> splitMessage = new ArrayList<String>();
    	int counter = 0;
    	while(counter<message.length()){
    		splitMessage.add(message.substring(counter, Math.min(counter+160, message.length()) ));
    		message+=160;
    	}
    	return splitMessage;
    }
    /**
     * @getCurrentTravelETA - returns time to travel between two Latitude and Longitudes
     *
     */
    private String getCurrentTravelETA(LatLng start, LatLng end) throws ClientProtocolException, IOException, JSONException, URISyntaxException
    {
    //build URL to send to Google to get travel time
    HttpClient httpclient = new DefaultHttpClient();
    StringBuilder urlstring = new StringBuilder();
    urlstring.append("https://maps.googleapis.com/maps/api/directions/json?origin=")
    .append(Double.toString((double)start.latitude)).append(",").append(Double.toString((double)start.longitude)).append("&destination=")
    .append(Double.toString((double)end.latitude)).append(",").append(Double.toString((double)end.longitude))
    .append("&sensor=false");
    //urlstring.append("http://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&sensor=true");
    URI url = new URI(urlstring.toString());

    HttpPost httppost = new HttpPost(url); //send URL

    HttpResponse response = httpclient.execute(httppost); //receive response
    HttpEntity entity = response.getEntity();
    InputStream is = null;
    is = entity.getContent();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
    StringBuilder sb = new StringBuilder();
    sb.append(reader.readLine() + "\n");
    String line = "0";
    while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
        Log.v("GMAPS: ", line);
    }
    is.close();
    reader.close();
    String result = sb.toString();
    
    //parse JSON file
    JSONObject jsonObject = new JSONObject(result);
    JSONArray routeArray = jsonObject.getJSONArray("routes");
    Log.v("route", routeArray.getString(0));
    JSONObject route = routeArray.getJSONObject(0);
    JSONArray legArray = route.getJSONArray("legs");
    Log.v("legs", legArray.getString(0));
    JSONObject legs = legArray.getJSONObject(0);
    JSONObject duration = legs.getJSONObject("duration");
    Log.v("LOG", duration.getString("text"));
    String durationText = duration.getString("text");
    durationTime = duration.getLong("value");

    return durationText;
    }
    
    /**
     * @onConnected - called when locationClient is connected
     *
     */
    @Override
    public void onConnected(Bundle dataBundle) {
    	getMapData();
    	Log.v("Connected!", "Connected!");
		timer.scheduleAtFixedRate(new TimerTask() { 
			@Override 
			public void run() {
				travelUpdateMessage();
				} 
			}, 60000, interval);//fires the travel update message after interval milliseconds and repeatedly after the interval
    	
    }
    /**
     * @onDisconnected - called when locationClient is disconnected
     *
     */
    @Override
    public void onDisconnected() {

    }
    
    /**
     * @onConnectionFailed - called is locationClient connection fails
     *
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
