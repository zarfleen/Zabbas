package org.burnix.zabbas.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import java.util.List;

import org.burnix.zabbas.R;
import org.burnix.zabbas.manager.AddNzbTask;

public class AddNzbActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final String action = intent.getAction();
	
		if(Intent.ACTION_VIEW.equals(action))
		{
			String urlData = intent.getData().toString();

			final EditText input = new EditText(this);
			input.setText(urlData);

			new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_add_nzb))
				.setMessage(getString(R.string.message_add_nzb))
				.setView(input)
				.setPositiveButton(getString(R.string.ok),
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,
							int whichButton)
						{
							new AddNzbTask(getApplicationContext())
								.execute(input.getText().toString());
							finish();
						}
					})
				.setNegativeButton(getString(R.string.cancel),
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,
							int whichButton)
						{
							finish();
						}
					})
				.show();
		}
	}
}
