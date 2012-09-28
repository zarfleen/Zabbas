package org.burnix.zabbas.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.burnix.zabbas.R;

public class HostSettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// load the preferences
		addPreferencesFromResource(R.xml.host_edit);
	}
}
