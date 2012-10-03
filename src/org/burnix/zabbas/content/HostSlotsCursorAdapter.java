package org.burnix.zabbas.content;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.burnix.zabbas.R;

import org.json.JSONObject;
import org.json.JSONException;

public class HostSlotsCursorAdapter extends CursorAdapter
{
	private final LayoutInflater mInflater;

	public HostSlotsCursorAdapter(Context context, Cursor cursor)
	{
		super(context, cursor);

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		TextView fileName = (TextView)view.findViewById(R.id.filename);
		TextView bytesLeft = (TextView)view.findViewById(R.id.bytesleft);
		TextView timeLeft = (TextView)view.findViewById(R.id.timeleft);

		ProgressBar progress = (ProgressBar)view.findViewById(R.id.progress);

		try
		{
			JSONObject slot = new JSONObject(cursor.getString(
						cursor.getColumnIndex(Slot.DATA)));

			fileName.setText(slot.getString("filename"));
			bytesLeft.setText(slot.getString("sizeleft"));
			timeLeft.setText(slot.getString("timeleft"));

			progress.setProgress(slot.getInt("percentage"));
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return mInflater.inflate(R.layout.host_slots_item, null);
	}
}
