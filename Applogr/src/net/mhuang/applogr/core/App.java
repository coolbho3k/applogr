package net.mhuang.applogr.core;

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import net.mhuang.applogr.fragment.AppGridFragment.AppEntry;
import android.content.pm.ApplicationInfo;

import com.google.gson.Gson;

public class App implements Comparable<App> {
	
	protected String packageName = "";
	protected long timeUsedMs = 0;
	protected int launchCount = 0;

	private transient static Gson mJson;
	protected transient long mTimer = 0;
	protected transient ApplicationInfo mInfo;
	protected transient AppEntry mEntry;
	protected transient HttpsURLConnection mConnection;
	
	static {
		mJson = new Gson();
	}
	
	public App(String pkg) {
		packageName = pkg;
		mTimer = 0;
	}
	
	public String toJson() {
		return mJson.toJson(this);
	}
	
	public String getPackage() {
		return packageName;
	}
	
	public void setTimer() {
		mTimer = System.currentTimeMillis();
	}
	
	public void saveTimer() {
		long timeUsed = System.currentTimeMillis() - mTimer;
		timeUsedMs += timeUsed;
		mTimer = 0;
	}
	
	public boolean timerIsStarted() {
		return mTimer != 0;
	}
	
	public long getTimeUsed() {
		return timeUsedMs;
	}
	
	public int getLaunchCount() {
		return launchCount;
	}
	
	public void incrementLaunchCount() {
		launchCount++;
	}
	
	public void resetLaunchCount() {
		launchCount = 0;
	}
	
	public ApplicationInfo getInfo() {
		return mInfo;
	}
	
	public void setInfo(ApplicationInfo info) {
		mInfo = info;
	}
	
	public AppEntry getAppEntry() {
		return mEntry;
	}
	
	public void setAppEntry(AppEntry entry) {
		mEntry = entry;
	}

	@Override
	public int compareTo(App app) {
			return app.getLaunchCount() - launchCount;
	}
	
	public String getPlayURL(String packageName) {
		try {
			URL url = new URL("https://play.google.com/store/apps/details?id="+packageName.trim());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		if(mConnection == null) {
			
		}
		
		return null;
	}
}
