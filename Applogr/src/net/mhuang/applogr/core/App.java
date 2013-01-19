package net.mhuang.applogr.core;

import com.google.gson.Gson;

public class App implements Comparable<App> {
	private transient AppList mParent;
	protected String packageName = "";
	protected long timeUsedMs = 0;
	protected long launchCount = 0;
	private transient static Gson mJson;
	protected transient long mTimer = -1;
	
	static {
		mJson = new Gson();
	}
	
	public App(String pkg) {
		packageName = pkg;
	}
	
	public String toJson() {
		return mJson.toJson(this);
	}
	
	public void setParent(AppList parent) {
		mParent = parent;
	}
	
	public AppList getParent() {
		return mParent;
	}
	
	public String getPackage() {
		return packageName;
	}
	
	public void setTimer() {
		if(mTimer == -1)
			mTimer = System.currentTimeMillis();
	}
	
	public void saveTimer() {
		if(mTimer != -1) {
			long timeUsed = System.currentTimeMillis() - mTimer;
			timeUsedMs += timeUsed;
			mTimer = -1;
		}
	}
	
	public boolean timerIsStarted() {
		return mTimer != -1;
	}
	
	public 

	@Override
	public int compareTo(App app) {
		if(mParent.getSortMode() == AppList.SORT_MODE_TIME) {
			
		}
		else if(mParent.getSortMode() == AppList.SORT_MODE_LAUNCH) {
			
		}
	}
}
