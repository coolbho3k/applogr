package net.mhuang.applogr.core;

import java.util.Set;
import java.util.TreeSet;

import com.google.gson.Gson;

public class AppList {
	public static final transient int SORT_MODE_TIME = 0;
	public static final transient int SORT_MODE_LAUNCH = 1;
	
	protected Set<App> apps;
	private transient static Gson mJson;
	protected transient int mSortMode = SORT_MODE_TIME;
	
	static {
		mJson = new Gson();
	}
	
	public AppList() {
		if(apps == null) {
			apps = new TreeSet<App>();
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
			if(app.getPackage() == pkg) {
				return app;
			}
		}
		return null;
	}
	
	public boolean hasPackage(String pkg) {
		for(App app : apps) {
			if(app.getPackage() == pkg) {
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
}
