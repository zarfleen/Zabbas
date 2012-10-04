package org.burnix.zabbas.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.burnix.zabbas.manager.HostSettings;
import org.burnix.zabbas.manager.SABManager;

public class AddNzbTask extends AsyncTask<String, Void, Void>
{
	final Context mContext;

	public AddNzbTask(Context context)
	{
		mContext = context;
	}

	protected Void doInBackground(String... urls)
	{
		// update the slots for this host

		Log.i(this.getClass().toString(), "Adding NZBs");

		HostSettings settings = new HostSettings(mContext);

		if(!settings.isValid())
		{
			Log.w(this.getClass().toString(), "Host settings not valid");
			return null;
		}

		SABManager sabManager = new SABManager(mContext,
				settings.getHostUrl(),
				settings.getApiKey(),
				settings.getTimeout());

		sabManager.addByUrl(urls[0]);

		return null;
	}
}
