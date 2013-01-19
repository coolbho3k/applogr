package net.mhuang.applogr;

import java.util.regex.Pattern;

import net.mhuang.applogr.util.LogcatBuffer;
import net.mhuang.applogr.util.LogcatBuffer.OnLineReadListener;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LogService extends Service implements LogcatBuffer.OnLineReadListener {
	
	/* The line offset by one on Android 4.2+ */
	private int mOffset = 0;
	
	/* The logcat filter command that we will pass on to LogcatBuffer. */
	public static final String COMMAND = 
		"-b events -v tag activity_launch_time:I am_create_activity:I am_resume_activity:I *:S";
	
	/* Regex for String.split() to parse LogcatBuffer lines */
	public static final String COMMAND_REGEX = "[\\s/:,\\[\\]]+";
	private Pattern mCommandPattern;
	
	/* Strings to match for LogcatBuffer lines to parse the type of event */
	public static final String ACTIVITY_LAUNCH_TIME = "activity_launch_time";
	public static final String AM_RESUME_ACTIVITY = "am_resume_activity";
	public static final String AM_CREATE_ACTIVITY = "am_create_activity";
	
	private LogcatBuffer mLogcat;

	@Override
	public void onCreate() {
		super.onCreate();
		
		mLogcat = new LogcatBuffer(COMMAND, this);
		mLogcat.setOnLineReadListener(this);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLineRead(String read) {
		boolean updated = false;
		
		if(read == null) {
			mLogcat.stop();
			mLogcat = new LogcatBuffer(COMMAND, this);
			mLogcat.setOnLineReadListener(this);
			return;
		}
		
		String packageName = getPackageName(read);
		String activityName = getActivityName(read);
		String eventName = getEventName(read);
						
		/* If we've resumed or launched an Activity, that Activity MUST now
		 * be in the foreground. So set all AppStates that don't match it to
		 * false.
		 */
		if(eventName.equals(AM_RESUME_ACTIVITY)
				|| eventName.equals(ACTIVITY_LAUNCH_TIME)
				|| eventName.equals(AM_CREATE_ACTIVITY)) {
			
				}
			
		
		/* If we updated, we want the Profiles service to know that we did. */
		if(updated && updater != null) {
			updater.onScannerUpdate();
		}
	}
	
	private String getActivityFromCondition(App c) {
		return c.getActivity().split("/")[1];
	}
	
	private String getPackageFromCondition(App c) {
		return c.getActivity().split("/")[0];
	}
	
	private boolean getMatchPackageOnlyFromCondition(App c) {
		return c.getMatchPackageOnly();
	}
	
	/* Gets the event name String from a raw logcat line */
	private String getEventName(String read) {
		return mCommandPattern.split(read, 3)[1];
	}
	
	/* Gets the Activity name String from a raw logcat line */
	private String getActivityName(String read) {
		String[] s = mCommandPattern.split(read, 7);
		if(!s[1].equals(ACTIVITY_LAUNCH_TIME)) {
			return s[5+mOffset];
		}
		else {
			return s[4+mOffset];
		}
	}
	
	/* Gets the Package name String from a raw logcat line */
	private String getPackageName(String read) {
		String[] s = mCommandPattern.split(read, 7);
		if(!s[1].equals(ACTIVITY_LAUNCH_TIME)) {
			return s[4+mOffset];
		}
		else {
			return s[3+mOffset];
		}
	}
}
