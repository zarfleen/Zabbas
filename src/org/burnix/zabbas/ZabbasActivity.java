package org.burnix.zabbas;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

import java.util.HashSet;
import java.util.Set;

import org.burnix.zabbas.content.HostSlotsCursorAdapter;
import org.burnix.zabbas.content.Slot;
import org.burnix.zabbas.manager.UpdateSlotsTask;
import org.burnix.zabbas.ui.HostSettingsActivity;

public class ZabbasActivity extends Activity
{
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_refresh:
			refreshAllHosts();
			break;
		case R.id.menu_settings:
			Intent intent = new Intent();
			intent.setClass(this, HostSettingsActivity.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void refreshAllHosts()
	{
		new UpdateSlotsTask().execute(this);
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
			implements LoaderManager.LoaderCallbacks<Cursor>
	{
		HostSlotsCursorAdapter mAdapter;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			mAdapter = new HostSlotsCursorAdapter(getActivity(), null);
			setListAdapter(mAdapter);

			getLoaderManager().initLoader(0, null, this);
		}

		@Override
		public void onSaveInstanceState(Bundle outState)
		{
			super.onSaveInstanceState(outState);
		}

		@Override
		public void onListItemClick(ListView l, View v, int index, long id)
		{
			showDetails(index);
		}

		private void showDetails(int index)
		{
			Intent intent = new Intent();
			intent.setClass(getActivity(), SlotDetailsActivity.class);
			intent.putExtra("index", index);
			startActivity(intent);
		}

		public Loader<Cursor> onCreateLoader(int id, Bundle args)
		{
			return new CursorLoader(getActivity(),
				Slot.CONTENT_URI, new String[] { Slot._ID, Slot.DATA }, 
				null, null, null);
		}

		public void onLoadFinished(Loader<Cursor> loader, Cursor data)
		{
			mAdapter.swapCursor(data);
		}

		public void onLoaderReset(Loader<Cursor> loader)
		{
			mAdapter.swapCursor(null);
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
