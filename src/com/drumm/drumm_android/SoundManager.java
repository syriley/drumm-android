package com.drumm.drumm_android;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.example.soundsensor.R;

public class SoundManager {
	
	private static final String TAG = "SoundManager";
	private static final int SOUND1=1;
	private static final int SOUND2=2;
	private static final int SOUND3=3;
    
	private Context context;
	private AudioManager audioManager;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundsMap;
	
	public SoundManager(Context context, AudioManager audioManager) {
		this.context = context;
		this.audioManager = audioManager;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

        soundsMap = new HashMap<Integer, Integer>();
        soundsMap.put(SOUND1, soundPool.load(context, R.raw.sound1, 1));
        soundsMap.put(SOUND2, soundPool.load(context, R.raw.sound2, 1));
        soundsMap.put(SOUND3, soundPool.load(context, R.raw.sound3, 1));
	}
	
	public void play (int sound) {
		this.play(sound, 1.0f);
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
