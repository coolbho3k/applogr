package net.mhuang.applogr;

import net.mhuang.applogr.fragment.FriendsFragment;
import net.mhuang.applogr.fragment.LauncherFragment;
import net.mhuang.applogr.fragment.TopFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	
	private Fragment mFriendsFragment, mLauncherFragment, mTopFragment;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		startService(new Intent(this, LogService.class));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment ret = null;
			if(position == 0) {
				if(mLauncherFragment == null) {
					mLauncherFragment = new LauncherFragment();
				}
				ret = mLauncherFragment;
			}
			else if(position == 1) {
				if(mFriendsFragment == null) {
					mFriendsFragment = new FriendsFragment();
				}
				ret = mFriendsFragment;
			}
			else if(position == 2) {
				if(mTopFragment == null) {
					mTopFragment = new TopFragment();
				}
				ret = mTopFragment;
			}
			else {
				Log.e("Applogr","Fragment not found!");
			}
			return ret;
		}
			


		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "My Apps".toUpperCase();
			case 1:
				return "All Apps".toUpperCase();
			case 2:
				return "Friends".toUpperCase();
			}
			return null;
		}
	}
}
