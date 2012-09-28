package org.burnix.zabbas.ui;

import android.app.Activity;
import android.os.Bundle;

import org.burnix.zabbas.R;

public class HostSettingsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new HostSettingsFragment())
			.commit();
	}
}
