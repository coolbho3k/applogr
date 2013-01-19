package net.mhuang.applogr.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.mhuang.applogr.R;
import net.mhuang.applogr.core.App;
import net.mhuang.applogr.core.AppList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppGridFragment extends Fragment {
	
	protected Context mContext;
	protected GridView mGridView;
	protected AppList mAppList;
	protected File mSaveFile;
	protected LayoutInflater mInflater;
	protected EntryAdapter mEntryAdapter;
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		setAppList(new AppList());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mGridView = (GridView) inflater.inflate(R.layout.app_grid, null);
		
		mInflater = inflater;
		
		populateGrid();
		
		mEntryAdapter = new EntryAdapter(mContext);
		
		mGridView.setAdapter(mEntryAdapter);
		
		return mGridView;
	}
	
	public void setAppList(AppList list) {
		mAppList = list;
	}
	
	/* Override these methods */
	public void populateGrid() {
		return;
	}
	
    public class EntryAdapter extends BaseAdapter
    {
    	Context context;


    	public EntryAdapter(Context context) {
    		this.context = context;
    	}

    	@Override
    	public int getCount() {
    		//if(mAppList.getApps().size() > 20) {
    			//return 20;
    	//	}
    		return mAppList.getApps().size();
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {  
    		return ((App)getItem(position)).getAppEntry().getView();
    	}

		@Override
		public Object getItem(int position) {
			int i = 0;
			for (App app : mAppList.getApps()) {
				if (position == i) {
					return app;
				}
				i++;
			}

			return null;
		}

    	@Override
    	public long getItemId(int arg0) {
    		return 0;
    	}
  }
	
	public static class AppEntry {
		private LinearLayout mLayout;
		private TextView mUser, mCount, mTime;
		private TextView mTitle;
		private App mParent;
		private ImageView mIcon;
		private boolean mInstalled = true;
		
		public AppEntry(App parent, LayoutInflater inflater) {
			mParent = parent;
			mLayout = (LinearLayout) inflater.inflate(R.layout.app_entry, null);
			mIcon = (ImageView) mLayout.findViewById(R.id.icon);
			mUser = (TextView) mLayout.findViewById(R.id.user);
			mCount = (TextView) mLayout.findViewById(R.id.count);
			mTime = (TextView) mLayout.findViewById(R.id.time);
			mTitle = (TextView) mLayout.findViewById(R.id.title);
		}
		
		public void setParent(App parent) {
			mParent = parent;
		}
		
		public App getParent() {
			return mParent;
		}
		
		public boolean getInstalled() {
			return mInstalled;
		}
		
		public void setInstalled(boolean installed) {
			mInstalled = installed;
		}

		public void setUser(String user) {
			mUser.setText(user);
		}
		
		public void setCount(String count) {
			mCount.setText(count);
		}
		
		public void setTime(String time) {
			mTime.setText(time);
		}
		
		public void setIcon(Drawable icon) {
			mIcon.setImageDrawable(icon);
		}
		
		public void setTitle(String title) {
			mTitle.setText(title);
		}
		
		public View getView() {
			return mLayout;
		}
		
	}
}
