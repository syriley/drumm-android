package com.drumm.drumm_android;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager extends Thread{
	
	private static final String TAG = "SoundManager";
	private static final int SOUND1=0;
	private static final int SOUND2=1;
	private static final int SOUND3=2;
    
	private AudioManager audioManager;
	private SoundPool soundPool;
	private boolean stop;
    public BlockingQueue<SoundItem> soundQueue = new LinkedBlockingQueue<SoundItem>();

	private HashMap<Integer, Integer> soundsMap;
	
	public SoundManager(Context context) {
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);;
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        soundsMap = new HashMap<Integer, Integer>();
        soundsMap.put(SOUND1, soundPool.load(context, R.raw.sound1, 1));
        soundsMap.put(SOUND2, soundPool.load(context, R.raw.sound2, 1));
        soundsMap.put(SOUND3, soundPool.load(context, R.raw.sound3, 1));
        
	}
	
	@Override
	public void run() {
	    try {
	        Log.i(TAG, "Checking for sounds:" + System.currentTimeMillis());
            SoundItem item;
            while (!this.stop) {

                item = this.soundQueue.take();
                if (item.stop) {

                    this.stop = true;
                    break;
                }
                Log.i(TAG, "START playing:" + System.currentTimeMillis());
                this.soundPool.play(soundsMap.get(item.getId()), item.volume, item.volume, 0, 0, 1);
            }

        } catch (InterruptedException e) {}
    }

    public void play(SoundItem sound) {
        Log.i(TAG, "Adding Sound:" + System.currentTimeMillis());
        soundQueue.add(sound);
        // this.play(sound.getId(), 1.0f);
    }

    public void play(int sound, float fSpeed) {
        float streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax; 
 
        try {
        	soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, fSpeed);
        }
        catch(NullPointerException e) {
        	Log.e(TAG, "Could not play sound" + sound, e);
        }
   }
	
	
}
