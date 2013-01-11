package com.drumm.drumm_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

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
         pads.add(new Pad(0));
         pads.add(new Pad(1));
         pads.add(new Pad(2));
         
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
         StringWriter writer = new StringWriter();
         boolean start = false;
         boolean error = false;
         int count = 0;
         byte[] buffer = new byte[3];
         while(true) {
            int theByte = -1;
            
            
            try {
                theByte = inStream.read();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(theByte > 0) {
                if(theByte == 60) {
                    buffer = new byte[3];
                    start = true;
                    error = false;
                    count = 0;
                }
                else if(theByte == 62 && !error) {
                    String sensorValues = new String(buffer);
                    Log.d(TAG, sensorValues + " : " +  + System.currentTimeMillis());
                    for (int i = 0; i < 3; i++) {
                        boolean active = false;
                        if(buffer[i] == 49) {
                            active = true;
                        }
                        pads.get(i).setValue(active);
                        
                        
                    }
                    
                    for (Pad pad : pads) {
                        if (pad.justTapped()) {
                            for (OnPadTappedListener padTappedListener : padTappedListeners) {
                                padTappedListener.onPadTapped(pad);
                            }
                        }
                    }
                }
                else {
                    try{
                        buffer[count - 1] = (byte)theByte;
                    }
                    catch(ArrayIndexOutOfBoundsException e) {
                        error = true;
                        Log.e(TAG, "sensor reading error");
                    }
                }
                count ++;
            }
         }
//         while (true) {
//             try {
//                 List<Boolean> padValues = readPadValues();
//                 int count = 0;
//                 for (Boolean active : padValues) {
//                	 pads.get(count).setValue(active);
//                	 count++;
//                 }
//                 
//                for (Pad pad : pads) {
//                    if (pad.justTapped()) {
//                        for (OnPadTappedListener padTappedListener : padTappedListeners) {
//                            padTappedListener.onPadTapped(pad);
//                        }
//                    }
//                }
//             } 
//             catch (IOException e) {
//                 Log.e(TAG, "disconnected", e);
//                 //connectionLost();
//                 break;
//             }
//             catch (NumberFormatException e){
//             	Log.w(TAG, "could not parse int");
//             }
//         }
     }
     

     public void cancel() {
         try {
             socket.close();
         } catch (IOException e) {
             Log.e(TAG, "close() of connect socket failed", e);
         }
     }
     
     private List<Boolean> readPadValues() throws IOException{
         List<Boolean> padValues = new ArrayList<Boolean>();
         Log.d(TAG, "Reading Buffer: " + System.currentTimeMillis());

         InputStreamReader reader = new InputStreamReader(inStream);
         BufferedReader bufferedReader = new BufferedReader(reader);
         Log.d(TAG, "Buffer To String: " + System.currentTimeMillis());
         //get second line as this is more likely fully buffered
         String line = bufferedReader.readLine(); 
         if(line != null) {
             //Log.d(TAG, padString);
             List<String> padValuesList = Arrays.asList(line.split(":"));
             
             for(String padValue : padValuesList) {
            	 boolean active = false;
            	 if(padValue.equals("1")){
            	     Log.i(TAG, "Pad Tapped:" + System.currentTimeMillis());
            		 active = true;
            	 }
            	 padValues.add(active);
             }
         }
         return padValues;
     }
     
     
}
