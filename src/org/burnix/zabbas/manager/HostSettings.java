package org.burnix.zabbas.manager;

import android.content.Context;
import android.content.SharedPreferences;

import org.burnix.zabbas.content.Host;

public class HostSettings
{
	static final String PREFIX = "";
	static final String PREFS_NAME = "org.burnix.zabbas_preferences";

	final SharedPreferences mPreferences;

	public HostSettings(Context context)
	{
		mPreferences = context.getSharedPreferences(PREFS_NAME, 
				Context.MODE_PRIVATE);
	}

	public boolean isValid()
	{
		if(getHostUrl().length() == 0)
			return false;

		if(getApiKey().length() == 0)
			return false;

		return true;
	}

	public String getHostUrl()
	{
		return mPreferences.getString(PREFIX + Host.URL, "");
	}

	public String getApiKey()
	{
		return mPreferences.getString(PREFIX + Host.API_KEY, "");
	}

	public int getTimeout()
	{
		Integer defaultTimeout = new Integer(Host.DEFAULT_TIMEOUT);

		return Integer.parseInt(
				mPreferences.getString(PREFIX + Host.TIMEOUT, 
					defaultTimeout.toString()));
	}

	public void setLastRefresh(long time)
	{
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putLong(PREFIX + Host.LAST_REFRESH, time);
		editor.commit();
	}

	public void setCachedData(String data)
	{
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREFIX + Host.DATA, data);
		editor.commit();
	}

	public String getCachedData()
	{
		return mPreferences.getString(PREFIX + Host.DATA, "");
	}
}
