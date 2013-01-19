package net.mhuang.applogr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.gson.Gson;

public class AppList {
	public static final transient int SORT_MODE_TIME = 0;
	public static final transient int SORT_MODE_LAUNCH = 1;
	
	protected List<App> apps;
	private transient static Gson mJson;
	protected transient int mSortMode = SORT_MODE_LAUNCH;
	protected transient Context mContext;
	
	static {
		mJson = new Gson();
	}
	
	public AppList() {		
		if(apps == null) {
			apps = new ArrayList<App>();
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
			resort();
		}
	}
	
	public String toJson() {
		return mJson.toJson(this);
	}
	
	public boolean addApp(App app) {
		return apps.add(app);
	}
	
	public boolean removeApp(App app) {
		return apps.remove(app);
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
	
	public List<App> getApps() {
		return apps;
	}
	
	public void resort() {
		Collections.sort(apps);
	}
}
