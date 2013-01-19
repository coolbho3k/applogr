package net.mhuang.applogr.core;

import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.gson.Gson;

public class AppList {
	public static final transient int SORT_MODE_TIME = 0;
	public static final transient int SORT_MODE_LAUNCH = 1;
	
	protected Set<App> apps;
	private transient static Gson mJson;
	protected transient int mSortMode = SORT_MODE_TIME;
	protected transient Context mContext;
	
	static {
		mJson = new Gson();
	}
	
	public AppList() {		
		if(apps == null) {
			apps = new TreeSet<App>();
		}
	}
	
	public void setContext(Context context) {
		mContext = context;
		
		if(!apps.isEmpty()) {
			for(App app : apps) {
				if(app.getPackage() != null) {
					try {
						app.setInfo(mContext.getPackageManager()
						.getApplicationInfo(app.getPackage(), 0));
					} catch (NameNotFoundException e) {
						removeApp(app);
					}
				}
			}
		}
	}
	
	public String toJson() {
		return mJson.toJson(this);
	}
	
	public boolean addApp(App app) {
		if(app.getParent() == null) {
			app.setParent(this);
			return apps.add(app);
		}
		return false;
	}
	
	public boolean removeApp(App app) {
		if(apps.contains(app)) {
			app.setParent(null);
			return apps.remove(app);
		}
		return false;
	}
	
	public App getApp(String pkg) {
		for(App app : apps) {
			if(app.getPackage().equals(pkg)) {
				return app;
			}
		}
		return null;
	}
	
	public boolean hasPackage(String pkg) {
		for(App app : apps) {
			if(app.getPackage().equals(pkg)) {
				return true;
			}
		}
		return false;
	}
	
	public int getSortMode() {
		return mSortMode;
	}
	
	public void setSortMode(int sortMode) {
		mSortMode = sortMode;
	}
	
	public void sort() {
		Set<App> newApps = new TreeSet<App>(apps);
		apps = newApps;
	}
	
	public Set<App> getApps() {
		return apps;
	}
}
