package org.burnix.zabbas.content;

import android.net.Uri;
import android.provider.BaseColumns;

public class Host implements BaseColumns
{
	public static final long UNKNOWN = -1;
	
	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String API_KEY = "api_key";
	public static final String TIMEOUT = "timeout";
	public static final String REFRESH_INTERVAL = "refresh_interval";
	public static final String BACKGROUND_REFRESH_INTERVAL
		 = "background_refresh_interval";
	public static final String ENABLED = "enabled";
	public static final String LAST_REFRESH = "last_refresh";
	public static final String DATA = "data";

	public static final String DEFAULT_ORDER_BY = NAME;
	public static final int DEFAULT_TIMEOUT = 30;
	public static final int DEFAULT_REFRESH_INTERVAL = 15;
	public static final int DEFAULT_BACKGROUND_REFRESH_INTERVAL = 3600;

	public static final String MIME_DIR_PREFIX =
		"vnd.android.cursor.dir";
	public static final String MIME_ITEM_PREFIX =
		"vnd.android.cursor.item";
	public static final String MIME_ITEM = "vnd.burnix.zabbas.content.host";
	public static final String MIME_TYPE_SINGLE =
		MIME_ITEM_PREFIX + "/" + MIME_ITEM;
	public static final String MIME_TYPE_MULTIPLE =
		MIME_DIR_PREFIX + "/" + MIME_ITEM;
	public static final String AUTHORITY = "org.burnix.zabbas.content";
	public static final String PATH_SINGLE = "hosts/#";
	public static final String PATH_MULTIPLE = "hosts";
	public static final Uri CONTENT_URI =
		Uri.parse("content://" + AUTHORITY + "/" + PATH_MULTIPLE);
}
