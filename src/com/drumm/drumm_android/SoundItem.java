package com.drumm.drumm_android;

public class SoundItem {

    private int id;
    public float volume;
    public boolean stop = false;

    /**
     * Default constructor
     * 
     * @param int soundID
     * @param float volume
     */
    public SoundItem(int id, float volume) {

        this.id = id;
        this.volume = volume;
    }

    /**
     * Constructor for the item
     * which will kill the thread
     * 
     * @param boolean stop
     */
    public SoundItem(boolean stop) {

        this.stop = stop;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
