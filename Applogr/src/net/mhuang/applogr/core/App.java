package net.mhuang.applogr.core;

import net.mhuang.applogr.fragment.AppGridFragment.AppEntry;
import android.content.pm.ApplicationInfo;

import com.google.gson.Gson;

public class App implements Comparable<App> {
	private transient AppList mParent;
	protected String packageName = "";
	protected long timeUsedMs = 0;
	protected int launchCount = 0;
	private transient static Gson mJson;
	protected transient long mTimer = -1;
	protected transient ApplicationInfo mInfo;
	protected transient AppEntry mEntry;
	
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
		if(mTimer < 0)
			mTimer = System.currentTimeMillis();
	}
	
	public void saveTimer() {
		if(mTimer > 0) {
			long timeUsed = System.currentTimeMillis() - mTimer;
			timeUsedMs += timeUsed;
			mTimer = -1;
		}
	}
	
	public boolean timerIsStarted() {
		return mTimer != -1;
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
		if(mParent.getSortMode() == AppList.SORT_MODE_TIME) {
			long comp = timeUsedMs - app.getTimeUsed();
			if(comp > 0) {
				return 1;
			}
			else if(comp < 0) {
				return -1;
			}
			else if(comp == 0) {
				return 0;
			}
		}
		else if(mParent.getSortMode() == AppList.SORT_MODE_LAUNCH) {
			return launchCount - app.getLaunchCount();
		}
		return 0;
	}
}
