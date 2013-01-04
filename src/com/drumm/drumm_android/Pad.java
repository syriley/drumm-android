package com.drumm.drumm_android;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class Pad {
	private int id;
	private static final int HISTORY_SIZE = 1;
	private boolean isActive;
	private Queue<Boolean> padHistory;
	private Date lastUpdated;
	
	public Pad(int id){
		this.id = id;
		padHistory = new LinkedList<Boolean>();
		setValue(false);
	}

	public void setValue(boolean value) {
		isActive = value;
		lastUpdated = new Date();
		padHistory.add(value);
		
		if(padHistory.size() > HISTORY_SIZE) {
			padHistory.remove();
		}
	}
	
	public boolean justTapped(){
		Date now = new Date();
		long tapTimeElapsed = now.getTime() - lastUpdated.getTime(); 
		if(isActive && 
    			(padHistory.peek() || tapTimeElapsed > 300)){
			return true;
		}
		return false;
	}

	public int getId(){
		return id;
	}
	
	
}
