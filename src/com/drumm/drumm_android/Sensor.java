package com.drumm.drumm_android;

import java.util.LinkedList;
import java.util.Queue;

public class Sensor {
	private static final int ACTIVE_SENSOR_THRESHOLD = 400;
	private static final int INACTIVE_SENSOR_THRESHOLD = 600;
	private static final int HISTORY_SIZE = 6;
	private boolean isActive;
	private Queue<Integer> sensorHistory;
	
	public Sensor(){
		sensorHistory = new LinkedList<Integer>();
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean add(int value) {
		sensorHistory.add(value);
		//keep history to only 100
		if(sensorHistory.size() > HISTORY_SIZE) {
			sensorHistory.remove();
		}
		if(!isActive && getAverageSensorValue() > ACTIVE_SENSOR_THRESHOLD) {
			isActive = true;
		}
		else if (isActive && getAverageSensorValue() < INACTIVE_SENSOR_THRESHOLD) {
			isActive = false;
		}
		
		return isActive;
	}

	private int getAverageSensorValue() {
		int valueSum = 0;
		for (Integer sensorValue : sensorHistory) {
			valueSum += sensorValue;
		}
		return valueSum / sensorHistory.size();
	}
	
	
}
