package com.drumm.drumm_android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothManager extends Thread {
	
    private static final String TAG = "BluetoothManager";

	private final BluetoothSocket socket;
    private final InputStream inStream;
    private List<OnPadTappedListener> padTappedListeners;
    private List<Pad> pads;
     
    public BluetoothManager(BluetoothSocket socket) {
         Log.d(TAG, "create BluetoothManager");
         this.socket = socket;
         pads = new ArrayList<Pad>();
         pads.add(new Pad(1));
         pads.add(new Pad(2));
         pads.add(new Pad(3));
         
         padTappedListeners = new ArrayList<OnPadTappedListener>();
         InputStream tmpIn = null;

         // Get the BluetoothSocket input and output streams
         try {
             tmpIn = socket.getInputStream();
         } catch (IOException e) {
             Log.e(TAG, "temp sockets not created", e);
         }

         inStream = tmpIn;
     }
     
     public void setPadTappedListener(OnPadTappedListener padTappedListener) {
 		padTappedListeners.add(padTappedListener);
 	 }

     public void run() {
         Log.i(TAG, "BEGIN BluetoothManager Thread");
         
         // Keep listening to the InputStream while connected
         while (true) {
             try {
                 List<Boolean> padValues = readPadValues();
                 int count = 1;
                 for (Boolean active : padValues) {
                	 pads.get(count).setValue(active);
                	 count++;
                 }
                     
                 for(Pad pad : pads) {
                	 if(pad.justTapped()){
                		for (OnPadTappedListener padTappedListener : padTappedListeners) {
                			 padTappedListener.onPadTapped(pad);	
                 		}
                	 }
                 }
             } 
             catch (IOException e) {
                 Log.e(TAG, "disconnected", e);
                 //connectionLost();
                 break;
             }
             catch (NumberFormatException e){
             	Log.w(TAG, "could not parse int");
             }
         }
     }

     public void cancel() {
         try {
             socket.close();
         } catch (IOException e) {
             Log.e(TAG, "close() of connect socket failed", e);
         }
     }
     
     private List<Boolean> readPadValues() throws IOException{
    	 byte[] buffer = new byte[1024];
         int bytes;
         List<Boolean> padValues = new ArrayList<Boolean>();
         
         bytes = inStream.read(buffer);

         String bufferString = new String(buffer, 0, bytes);
         //split on a new line as the buffer can have many liines
         String[] bufferArray = bufferString.split("\\r\\n");
         
         //get second line as this is more likely fully buffered
         if(bufferArray.length > 1) {
        	 String padString = bufferArray[1];
             Log.d(TAG, padString);
             List<String> padValuesList = Arrays.asList(padString.split(":"));
             
             for(String padValue : padValuesList) {
            	 boolean active = false;
            	 if(padValue.equals("1")){
            		 active = true;
            	 }
            	 padValues.add(active);
             }
         }
         return padValues;
     }
}
