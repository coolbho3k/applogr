package net.mhuang.applogr.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import net.mhuang.applogr.core.App;
import net.mhuang.applogr.core.AppList;
import net.mhuang.applogr.fragment.AppGridFragment.AppEntry;

import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.parse.ParseFacebookUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

public class TopFragment extends AppGridFragment {
	private Gson mJson;
	private Session mSession;

	@Override
	public void onResume() {
		super.onResume();
		
		mSession = ParseFacebookUtils.getSession();
		
		refresh();
		populateGrid();
		mEntryAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mJson = new Gson();
		
		final File directory = new File("/sdcard");
		mSaveFile = new File(directory.getAbsolutePath() + "/"
				+ "applist.json");
		Log.d("applogr", mSaveFile.getAbsolutePath());
		
		/*if(!mSaveFile.exists()) {
			mAppList = new AppList();
			mAppList.setContext(mContext);
		}*/
		
		refresh();
	}
	
	public void refresh() {
		try {
			mJson = new Gson();
			mAppList = new AppList();
			mAppList = mJson.fromJson(new FileReader(mSaveFile),
					AppList.class);
			mAppList.setContext(mContext);
			//mAppList.resort();
			Log.d("Applogr Fragment", mJson.toJson(mAppList));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			mAppList = new AppList();
			mAppList.setContext(mContext);
			return;
		} catch (JsonIOException e) {
			e.printStackTrace();
			mAppList = new AppList();
			mAppList.setContext(mContext);
			return;
		} catch (NullPointerException e) {
			e.printStackTrace();
			mAppList = new AppList();
			mAppList.setContext(mContext);
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mAppList = new AppList();
			mAppList.setContext(mContext);
			return;
		}
	}
	
	@Override
	public void populateGrid() {
		super.populateGrid();
				
		for(App app : mAppList.getApps()) {
			AppEntry entry = new AppEntry(app, mInflater);
			app.setAppEntry(entry);
			final App finalApp = app;
			entry.getView().setOnClickListener(new View.OnClickListener() {
				
				App clickApp = finalApp;

				@Override
				public void onClick(View v) {
					clickApp.getPackage();
					Intent LaunchIntent = v.getContext().getPackageManager()
							.getLaunchIntentForPackage(clickApp.getPackage());
					startActivity(LaunchIntent);
					
				}
				
			});
			entry.setIcon(app.getInfo().loadIcon(mContext.getPackageManager()));
			entry.setTitle((String)
					app.getInfo().loadLabel(mContext.getPackageManager()));
			if(app.getLaunchCount() == 1) {
				entry.setCount("1 launch");
			}
			else {
				entry.setCount(app.getLaunchCount()+" launches");
			}
			if(app.getTimeUsed() < 1000*60) {
				entry.setTime(String.format("%d s", 
					    TimeUnit.MILLISECONDS.toSeconds(app.getTimeUsed())
					)
				);
			}
			else if(app.getTimeUsed() < 1000*60*60) {
				entry.setTime(String.format("%d m %d s", 
					    TimeUnit.MILLISECONDS.toMinutes(app.getTimeUsed()),
					    TimeUnit.MILLISECONDS.toSeconds(app.getTimeUsed()) - 
					    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(app.getTimeUsed()))
					)
				);
			}
			else {
			entry.setTime(String.format("%d hr %d m", 
				    TimeUnit.MILLISECONDS.toHours(app.getTimeUsed()),
				    TimeUnit.MILLISECONDS.toMinutes(app.getTimeUsed()) - 
				    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(app.getTimeUsed()))
				)
			);
			}
		}
	}
}
