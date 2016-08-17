package utils;

public class Timer {

	private long startTime, finishTime;
	
	public Timer(){
	}
	
	public void start(){
			startTime = System.currentTimeMillis();
	}
	
	public void stop(){
			finishTime = System.currentTimeMillis();
	}
	
	public long getTime(){
		return finishTime-startTime;
	}
}