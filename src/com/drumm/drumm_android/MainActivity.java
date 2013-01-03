package com.drumm.drumm_android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
	SoundManager soundManager;
    
    
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a67");

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
         
         soundManager = new SoundManager(this, audioManager);
         
         BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
         if(adapter == null) {
        	 Toast.makeText(this, "Could not find bluetooth adapter", Toast.LENGTH_LONG)
				.show();
         }
         else {
	         Set <BluetoothDevice> devices = adapter.getBondedDevices();
	         BluetoothDevice actual;
	         for (BluetoothDevice bondedDevice : devices) {
				Log.d("test", "Device name: " + bondedDevice.getName());
				if(bondedDevice.getName().equals("RN42-B6CD")){
					actual = adapter.getRemoteDevice(bondedDevice.getAddress());
					try {
			            adapter.cancelDiscovery();
	
						 actual.createRfcommSocketToServiceRecord(MY_UUID);
						Method m = actual.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
						BluetoothSocket socket = (BluetoothSocket) m.invoke(actual, 1);
						socket.connect();
						ConnectedThread thread = new ConnectedThread(socket);
						thread.start();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
						.show();
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
						.show();
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
						.show();
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
						.show();
						e.printStackTrace();
					}
				}
			}
         }
    }

    
    
 
    public void Audio1ButtonHandler(View target) {
    	RadioButton radioSound1 =
                 (RadioButton)this.findViewById(R.id.radioSound1);
      // select the sound file
      if (radioSound1.isChecked()) {
        //soundManager.play(SOUND1);
      }
      else {
        //playSound(SOUND2, 1.0f);
      }
    }
    
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                    //        .sendToTarget();
                    String readMessage = new String(buffer, 0, bytes);
                    int sound = Integer.parseInt(readMessage.substring(0,1));
                    //playSound(sound, 1.0f);
                    Log.d(TAG, readMessage);
                } catch (IOException e) {
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
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
