package net.mhuang.applogr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

import android.content.Context;
import android.os.Build;
import android.util.Log;


/* LogcatBuffer.java
 * 
 * A threaded class for listening to logcat. Designed only to read from the
 * events log for now.
 */
public class LogcatBuffer implements Runnable {
	private boolean run = true;
	private Thread thread;
	private String options = null;
	private BufferedReader reader;
	CharBuffer buffer;
	private OnLineReadListener listener;
	String yeshup;
	
	public LogcatBuffer(Context context) {
		thread = new Thread(this);
		yeshup = Utils.getYeshup(context);
		thread.start();
	}
	
	
	public LogcatBuffer(String options, Context context) {
		this.options = options;
		thread = new Thread(this);
		yeshup = Utils.getYeshup(context);
		thread.start();
	}
	
	public void setOnLineReadListener(OnLineReadListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		android.os.Process.setThreadPriority(
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		
		Process process = null;
		
		//Log.d("setcpu", "Logcat Buffer");
				
    	try {
			process = Runtime.getRuntime().exec(yeshup + " logcat -b events -c");
			
			if(process.waitFor() != 0) {
				run = false;
				Log.e("setcpu", "Error: could not scan for running apps.");
			}
			
			/* Jelly Bean and up: Because of security improvements, we have to
			 * run logcat as root to see everything. */
			if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				process = Runtime.getRuntime().exec(yeshup + " su");
				process.getOutputStream().write((yeshup + " logcat " + options+"\n")
						.getBytes());
			}
			else {
				process = Runtime.getRuntime().exec(yeshup + " logcat " + options);
			}
			reader = new BufferedReader(
							new InputStreamReader(process.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();
			run = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			run = false;
		} catch(NullPointerException e) {
			e.printStackTrace();
			run = false;
		}
    	
    	while(run) {
    		try {		
				final String read = reader.readLine();												
				if(read == null) {
					/* It seems as though our logcat was forcibly terminated.
					 * Start it again. */
					/* This is currently unclean, because it prints stack traces, but safe */
					stop();
					listener.onLineRead(null);
					listener = null;
					thread = null;
					
				}
					
				if(listener != null)
					listener.onLineRead(read);
			} catch (IOException e) {
				e.printStackTrace();
				run = false;
			}
    	}
    	    	
    	if(process != null) {
    		process.destroy();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	
	public void stop() {
		run = false;
	}
	
	public boolean isAlive() {
		return thread.isAlive();
	}
	
	public static interface OnLineReadListener {
        public void onLineRead(String read);
    }
}
