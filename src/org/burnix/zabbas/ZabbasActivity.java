package org.burnix.zabbas;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ZabbasActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zabbas);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.zabbas, menu);
		return true;
	}

	public static class SlotDetailsActivity extends Activity
	{
		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			if(savedInstanceState == null)
			{
				ActionBar actionBar = getActionBar();
				actionBar.setDisplayHomeAsUpEnabled(true);

				SlotDetailsFragment details = new SlotDetailsFragment();
				details.setArguments(getIntent().getExtras());
				getFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, details)
					.commit();
			}
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch(item.getItemId())
			{
				case android.R.id.home:
					finish();
					return true;
				default:
					return super.onOptionsItemSelected(item);
			}
		}
	}

	public static class HostFragment extends ListFragment 
	{
		private int mCurrentIndex = -1;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			// populate the list
			setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1, 
				new String[] { "Test1", "Test2" }));

			if(savedInstanceState != null)
			{
				mCurrentIndex = savedInstanceState.getInt("currentIndex", -1);
			}
		}

		@Override
		public void onSaveInstanceState(Bundle outState)
		{
			super.onSaveInstanceState(outState);
			outState.putInt("currentIndex", mCurrentIndex);
		}

		@Override
		public void onListItemClick(ListView l, View v, int index, long id)
		{
			showDetails(index);
		}

		private void showDetails(int index)
		{
			mCurrentIndex = index;

			Intent intent = new Intent();
			intent.setClass(getActivity(), SlotDetailsActivity.class);
			intent.putExtra("index", index);
			startActivity(intent);
		}
	}

	public static class SlotDetailsFragment extends Fragment
	{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			if(container == null)
				return null;

			TextView text = new TextView(getActivity());
			text.setText("Details");
			return text;
		}
	}
}
