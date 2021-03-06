package net.mhuang.applogr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.mhuang.applogr.core.App;
import net.mhuang.applogr.core.AppList;
import net.mhuang.applogr.util.LogcatBuffer;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class LogService extends Service implements LogcatBuffer.OnLineReadListener {
	
	private AppList mAppList;
	private Gson mJson;
	
	
	Notification mNotify;
		
	private App mForegroundApp;
	private File mSaveFile;
	private String mLauncherPackage;
	
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
	
	NotificationCompat.Builder mBuilder =
	        new NotificationCompat.Builder(this)
	        .setSmallIcon(android.R.drawable.ic_media_play)
	        .setContentTitle("Applogr")
	        .setContentText("You are sharing your activity!");
	
	private LogcatBuffer mLogcat;
	
	private int mRetval = START_STICKY;
	
	private ResolveInfo mSystemResolver;

	@Override
	public void onCreate() {
		super.onCreate();
		
		mJson = new Gson();
		
		Intent intent = new Intent(); 
		intent.setAction(Intent.ACTION_MAIN); 
		intent.addCategory(Intent.CATEGORY_HOME); 
        PackageManager pm = this.getPackageManager(); 
        ResolveInfo info = pm.resolveActivity(intent, 0); 
        mLauncherPackage = info.activityInfo.packageName;
        
        Intent systemIntent = new Intent(); 
		intent.setAction(Intent.ACTION_MAIN); 
        mSystemResolver = pm.resolveActivity(systemIntent, 0); 
		
		Intent resultIntent = new Intent(this, MainActivity.class);
		
		mJson = new Gson();
		
		if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			mOffset = 1;
		}
		
		mLogcat = new LogcatBuffer(COMMAND, this);
		mLogcat.setOnLineReadListener(this);
		
		mCommandPattern = Pattern.compile(COMMAND_REGEX);
	
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);

		startForeground(1, mBuilder.build());
				
		final File directory = new File("/sdcard");
		mSaveFile = new File(directory.getAbsolutePath() + "/"
				+ "applist.json");
		Log.d("applogr", mSaveFile.getAbsolutePath());
		
		
		if(!mSaveFile.exists()) {
			mAppList = new AppList();
			mAppList.setContext(this);
			save();
		}
		
		save();
		
		try {
				mAppList = mJson.fromJson(new FileReader(mSaveFile),
						AppList.class);
				mAppList.setContext(this);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			mRetval = START_NOT_STICKY;
			return;
		} catch (JsonIOException e) {
			e.printStackTrace();
			mRetval = START_NOT_STICKY;
			return;
		} catch (NullPointerException e) {
			e.printStackTrace();
			mRetval = START_NOT_STICKY;
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mRetval = START_NOT_STICKY;
			return;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Log.d("Applogr","Service started");
		return mRetval;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLineRead(String read) {
		boolean log = true;
		
		if(read == null) {
			mLogcat.stop();
			mLogcat = new LogcatBuffer(COMMAND, this);
			mLogcat.setOnLineReadListener(this);
			return;
		}
		
		String packageName = getPackageName(read);
		String eventName = getEventName(read);
						
		/* If we've resumed or launched an Activity, that Activity MUST now
		 * be in the foreground.
		 */
		if(eventName.equals(AM_RESUME_ACTIVITY)
				|| eventName.equals(ACTIVITY_LAUNCH_TIME)
				|| eventName.equals(AM_CREATE_ACTIVITY)) {
						
					//mAppList.resort();
			
					Log.d("Applogr", packageName);
			
					/* Check if this app is a launcher app or this app */
					if(packageName.equals(getPackageName())) {
						log = false;
					}
					else if(packageName.equals(mLauncherPackage)) {
						log = false;
					}
					else if(packageName.equals("com.android.systemui")) {
						log = false;
					}
					
					/* Stop recording time for the previous app */
					if(mForegroundApp != null && mForegroundApp.timerIsStarted() &&
							!mForegroundApp.getPackage().equals(packageName)) {
						Log.d("Applogr", "Stopped timer");
						mForegroundApp.saveTimer();
					}
					
					/* Start recording time for this app */
					if(log) {
						if(mAppList.hasPackage(packageName)) {
							if(mForegroundApp != mAppList.getApp(packageName)) {
								if(!eventName.equals(AM_RESUME_ACTIVITY)) {
									mAppList.getApp(packageName).incrementLaunchCount();
								}
								mForegroundApp = mAppList.getApp(packageName);
							}
							
						}
						else {
							App newApp = new App(packageName);
							mAppList.addApp(newApp);
							mForegroundApp = newApp;
							mForegroundApp.incrementLaunchCount();
						}
							Log.d("Applogr","Starting timer");
							mForegroundApp.setTimer();
					}
					save();
				}
	}
	
	/* Gets the event name String from a raw logcat line */
	private String getEventName(String read) {
		return mCommandPattern.split(read, 3)[1];
	}
	
	/* Gets the Activity name String from a raw logcat line */
	private String getActivityName(String read) {
		String[] s = mCommandPattern.split(read, 7+mOffset);
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
	
	private void save() {
		try {
			if(!mSaveFile.exists()) {
				mSaveFile.createNewFile();
			} 
			
			if(mAppList != null) {
			BufferedWriter out = new BufferedWriter(new FileWriter(mSaveFile));
			out.write(mAppList.toJson());
			out.flush();
			out.close();
			Log.d("Applogr", mAppList.toJson());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
