package com.drumm.drumm_android;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";    
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a67");
    SoundManager soundManager;
    
    private OnItemClickListener drumkitClickedHandler = new OnItemClickListener() {
    	public void onItemClick(AdapterView parent, View v, int position, long id) {
    		Toast.makeText(parent.getContext(), "item clicked", Toast.LENGTH_LONG)
			.show();
    		Intent drumKitIntent = new Intent(parent.getContext(), DrumkitActivity.class);
    		startActivity(drumKitIntent);
    	}
	};
	
   private OnPadTappedListener padTappedListener = new OnPadTappedListener() {
		
		@Override
		public void onPadTapped(Pad pad) {
			Log.d(TAG, "Pad " + pad.getId() + " tapped");
			soundManager.play(pad.getId());
			
		}
	};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
         initialiseBluetooth();
         List<String> drumKits = new ArrayList<String>();
         drumKits.add("Rock");
         drumKits.add("Jazz");
         drumKits.add("Blues");
         drumKits.add("Hip-Hop");
         drumKits.add("Folk");
         
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drumKits);
         ListView drumkitListView = (ListView)findViewById(R.id.drumKitListView);
         drumkitListView.setAdapter(adapter);
         drumkitListView.setOnItemClickListener(drumkitClickedHandler);
    }
    
 
    private void initialiseBluetooth(){
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
						BluetoothManager bluetoothManager = new BluetoothManager(socket);
						bluetoothManager.setPadTappedListener(padTappedListener);
						bluetoothManager.start();
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
}

