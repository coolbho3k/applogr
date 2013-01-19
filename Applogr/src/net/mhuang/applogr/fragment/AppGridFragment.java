package net.mhuang.applogr.fragment;

import java.io.File;

import net.mhuang.applogr.R;
import net.mhuang.applogr.core.App;
import net.mhuang.applogr.core.AppList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppGridFragment extends Fragment {
	
	private Context mContext;
	private GridView mGridView;
	private AppList mAppList;
	private File mSaveFile;
	
	@Override
	public void onResume() {
		super.onResume();
		populateGrid();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = getActivity();
		
		GridView grid = (GridView) inflater.inflate(R.layout.app_grid, container);
		populateGrid();

		return grid;
	}
	
	public void setAppList(AppList list) {
		mAppList = list;
	}
	
	public void populateGrid() {
		for(App app : mAppList.getApps()) {
			AppEntry entry = new AppEntry(app);
			app.setAppEntry(entry);
			
		}
	}
	
	public static class AppEntry {
		private LinearLayout mLayout;
		private TextView mUser, mCount, mTime;
		private TextView mTitle;
		private App mParent;
		private ImageView icon;
		private boolean installed = true;
		
		public AppEntry(App parent) {
			mParent = parent;
			
		}

		public boolean setUser() {
			return false;
		}
		
		public boolean setCount() {
			return false;
		}
		
		public boolean setTime() {
			return false;
		}	
		
		public View getView() {
			return mLayout;
		}
		
	}
}
