package org.burnix.zabbas.manager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import org.burnix.zabbas.content.Slot;
import org.burnix.zabbas.manager.HostSettings;
import org.burnix.zabbas.manager.SABManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateSlotsTask extends AsyncTask<Context, Void, Void>
{
	protected Void doInBackground(Context... contexts)
	{
		// update the slots for this host

		Log.i(this.getClass().toString(), "Updating host");

		HostSettings settings = new HostSettings(contexts[0]);

		if(!settings.isValid())
		{
			Log.w(this.getClass().toString(), "Host settings not valid");
			return null;
		}

		try
		{
			SABManager sabManager = new SABManager(contexts[0],
					settings.getHostUrl(),
					settings.getApiKey(),
					settings.getTimeout());

			String data = sabManager.getQueue();

			if(data != null)
			{
				settings.setLastRefresh(SystemClock.elapsedRealtime());

				JSONObject queue = new JSONObject(data);
				JSONArray slots = queue.getJSONObject("queue")
					.optJSONArray("slots");

				if(slots != null)
				{
					ContentValues values;

					ContentResolver resolver = contexts[0].getContentResolver();

					for(int s = 0;s < slots.length();s++)
					{
						String slotData = slots.getJSONObject(s)
							.toString();

						values = new ContentValues();
						values.put(Slot.HOST_ID, -1);
						values.put(Slot.DATA, slotData);

						resolver.insert(Slot.CONTENT_URI, values);
					}
				
					resolver.notifyChange(Slot.CONTENT_URI, null);
				}
			}
		}
		catch(JSONException e)
		{
			Log.e(this.getClass().toString(), e.getMessage());
		}

		return null;
	}
}
