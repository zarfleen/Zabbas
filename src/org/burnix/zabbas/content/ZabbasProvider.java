package org.burnix.zabbas.content;

import android.content.Context;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.burnix.zabbas.R;

public class ZabbasProvider extends ContentProvider
{
	private static final String CLASSNAME =
		ZabbasProvider.class.getSimpleName();
		
	private static final int HOSTS = 1;
	private static final int HOST = 2;
	private static final int SLOTS = 3;
	private static final int SLOT = 4;
	private static final int SLOT_HISTORIES = 5;
	private static final int SLOT_HISTORY = 6;
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "zabbas_data";
	public static final String TABLE_HOSTS = "hosts";
	public static final String TABLE_SLOTS = "slots";
	public static final String TABLE_SLOT_HISTORY = "slot_history";
	
	private static UriMatcher URI_MATCHER = null;

	private SQLiteDatabase mDb;
	
	static
	{
		ZabbasProvider.URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		ZabbasProvider.URI_MATCHER.addURI(Host.AUTHORITY,
			Host.PATH_MULTIPLE, ZabbasProvider.HOSTS);
		ZabbasProvider.URI_MATCHER.addURI(Host.AUTHORITY,
			Host.PATH_SINGLE, ZabbasProvider.HOST);
		ZabbasProvider.URI_MATCHER.addURI(Slot.AUTHORITY,
			Slot.PATH_MULTIPLE, ZabbasProvider.SLOTS);
		ZabbasProvider.URI_MATCHER.addURI(Slot.AUTHORITY,
			Slot.PATH_SINGLE, ZabbasProvider.SLOT);
		ZabbasProvider.URI_MATCHER.addURI(SlotHistory.AUTHORITY,
			SlotHistory.PATH_SINGLE, ZabbasProvider.SLOT_HISTORY);
		ZabbasProvider.URI_MATCHER.addURI(SlotHistory.AUTHORITY,
			SlotHistory.PATH_MULTIPLE, ZabbasProvider.SLOT_HISTORIES);
	}
	
	private static class DBHelper extends SQLiteOpenHelper
	{
		private static final String DATABASE_CREATE_HOSTS =
			"create table " + ZabbasProvider.TABLE_HOSTS + " ("
			+ Host._ID + " integer primary key autoincrement,"
			+ Host.NAME + " text not null,"
			+ Host.URL + " text null,"
			+ Host.API_KEY + " text null,"
			+ Host.TIMEOUT + " integer not null,"
			+ Host.REFRESH_INTERVAL + " integer not null,"
			+ Host.BACKGROUND_REFRESH_INTERVAL + " integer not null,"
			+ Host.ENABLED + " string not null,"
			+ Host.LAST_REFRESH + " integer null,"
			+ Host.DATA + " text null);";
		private static final String DATABASE_CREATE_SLOTS =
			"create table " + ZabbasProvider.TABLE_SLOTS + " ("
			+ Slot._ID + " integer primary key autoincrement,"
			+ Slot.HOST_ID + " integer not null,"
			+ Slot.DATA + " text not null);";
		private static final String DATABASE_CREATE_SLOT_HISTORY =
			"create table " + ZabbasProvider.TABLE_SLOT_HISTORY + " ("
			+ SlotHistory._ID + " integer primary key autoincrement,"
			+ SlotHistory.HOST_ID + " integer not null,"
			+ SlotHistory.IDENTIFIER + " text not null,"
			+ SlotHistory.DATA + " text not null);";
		
		public DBHelper(Context context)
		{
			super(context, ZabbasProvider.DATABASE_NAME, null, 
				ZabbasProvider.DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DBHelper.DATABASE_CREATE_HOSTS);
			db.execSQL(DBHelper.DATABASE_CREATE_SLOTS);
			db.execSQL(DBHelper.DATABASE_CREATE_SLOT_HISTORY);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion)
		{
			Log.i(ZabbasProvider.CLASSNAME, "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
		}
	}
	
	@Override
	public boolean onCreate()
	{
		DBHelper helper = new DBHelper(getContext());
		mDb = helper.getWritableDatabase();
		if(mDb == null)
			return false;

		return true;
	}
	
	@Override
	public String getType(Uri uri)
	{
		switch(ZabbasProvider.URI_MATCHER.match(uri))
		{
		case HOSTS:
			return Host.MIME_TYPE_MULTIPLE;
		case HOST:
			return Host.MIME_TYPE_SINGLE;
		case SLOTS:
			return Slot.MIME_TYPE_MULTIPLE;
		case SLOT:
			return Slot.MIME_TYPE_SINGLE;
		case SLOT_HISTORIES:
			return SlotHistory.MIME_TYPE_MULTIPLE;
		case SLOT_HISTORY:
			return SlotHistory.MIME_TYPE_SINGLE;
		}
		
		throw new IllegalArgumentException(
			getContext().getString(R.string.unknown_uri) + " " + uri);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
		String[] selectionArgs, String sortOrder)
	{
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		String orderBy = sortOrder;
		
		switch(ZabbasProvider.URI_MATCHER.match(uri))
		{
		case HOSTS:
			builder.setTables(ZabbasProvider.TABLE_HOSTS);
			if(sortOrder == null || sortOrder.equals(""))
				orderBy = Host.DEFAULT_ORDER_BY;
			break;
		case HOST:
			builder.setTables(ZabbasProvider.TABLE_HOSTS);
			builder.appendWhere(Host._ID + "="
				+ uri.getPathSegments().get(1));
			break;
		case SLOTS:
			builder.setTables(ZabbasProvider.TABLE_SLOTS);
			if(sortOrder == null || sortOrder.equals(""))
				orderBy = Slot.DEFAULT_ORDER_BY;
			break;
		case SLOT:
			builder.setTables(ZabbasProvider.TABLE_SLOTS);
			builder.appendWhere(Slot._ID + "="
				+ uri.getPathSegments().get(1));
			break;
		case SLOT_HISTORIES:
			builder.setTables(ZabbasProvider.TABLE_SLOT_HISTORY);
			if(sortOrder == null || sortOrder.equals(""))
				orderBy = SlotHistory.DEFAULT_ORDER_BY;
			break;
		case SLOT_HISTORY:
			builder.setTables(ZabbasProvider.TABLE_SLOT_HISTORY);
			builder.appendWhere(SlotHistory._ID + "="
				+ uri.getPathSegments().get(1));
		default:
			throw new IllegalArgumentException(
				getContext().getString(R.string.unknown_uri) + " " + uri);
		}

		Cursor c = builder.query(mDb, projection, selection,
			selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		String table = null;
		Uri contentUri = null;
		
		switch(ZabbasProvider.URI_MATCHER.match(uri))
		{
		case HOSTS:
			table = ZabbasProvider.TABLE_HOSTS;
			contentUri = Host.CONTENT_URI;
			break;
		case SLOTS:
			table = ZabbasProvider.TABLE_SLOTS;
			contentUri = Slot.CONTENT_URI;
			break;
		case SLOT_HISTORIES:
			table = ZabbasProvider.TABLE_SLOT_HISTORY;
			contentUri = SlotHistory.CONTENT_URI;
			break;
		default:
			throw new IllegalArgumentException(
				getContext().getString(R.string.unknown_uri) + " " + uri);
		}
		
		long rowId = mDb.insert(table, null, values);
		Uri result = ContentUris.withAppendedId(contentUri, rowId);
//		getContext().getContentResolver().notifyChange(result, null);
		return result;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
		String[] selectionArgs)
	{
		int count = 0;
		String where = "";
		String segment = null;
		
		switch(ZabbasProvider.URI_MATCHER.match(uri))
		{
		case HOSTS:
			count = mDb.update(ZabbasProvider.TABLE_HOSTS, values, selection,
				selectionArgs);
			break;
		case HOST:
			segment = uri.getPathSegments().get(1);
			where = "";
			if(selection != null && !selection.equals(""))
			{
				where = " AND (" + selection + ")";
			}
			count = mDb.update(ZabbasProvider.TABLE_HOSTS, values,
				Host._ID + "=" + segment + where, selectionArgs);
			break;
		case SLOTS:
			count = mDb.update(ZabbasProvider.TABLE_SLOTS, values, selection,
				selectionArgs);
			break;
		case SLOT:
			segment = uri.getPathSegments().get(1);
			where = "";
			if(selection != null && !selection.equals(""))
			{
				where = " AND (" + selection + ")";
			}
			count = mDb.update(ZabbasProvider.TABLE_SLOTS, values,
				Slot._ID + "=" + segment + where, selectionArgs);
			break;
		case SLOT_HISTORIES:
			count = mDb.update(ZabbasProvider.TABLE_SLOT_HISTORY, values, selection,
				selectionArgs);
			break;
		case SLOT_HISTORY:
			segment = uri.getPathSegments().get(1);
			where = "";
			if(selection != null && !selection.equals(""))
			{
				where = " AND (" + selection + ")";
			}
			count = mDb.update(ZabbasProvider.TABLE_SLOT_HISTORY, values,
				SlotHistory._ID + "=" + segment + where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException(
				getContext().getString(R.string.unknown_uri) + " " + uri);
		}
		
//		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		int count = 0;
		String where = "";
		String segment = null;
		
		switch(ZabbasProvider.URI_MATCHER.match(uri))
		{
		case HOSTS:
			count = mDb.delete(ZabbasProvider.TABLE_HOSTS, selection,
				selectionArgs);
			break;
		case HOST:
			segment = uri.getPathSegments().get(1);
			if(selection != null && !selection.equals(""))
			{
				where = " AND (" + selection + ")";
			}
			count = mDb.delete(ZabbasProvider.TABLE_HOSTS,	
				Host._ID + "=" + segment + where, selectionArgs);
			break;
		case SLOTS:
			count = mDb.delete(ZabbasProvider.TABLE_SLOTS, selection,
				selectionArgs);
			break;
		case SLOT:
			segment = uri.getPathSegments().get(1);
			if(selection != null && !selection.equals(""))
			{
				where = " AND (" + selection + ")";
			}
			count = mDb.delete(ZabbasProvider.TABLE_SLOTS,
				Slot._ID + "=" + segment + where, selectionArgs);
			break;
		case SLOT_HISTORIES:
			count = mDb.delete(ZabbasProvider.TABLE_SLOT_HISTORY, selection,
				selectionArgs);
			break;
		case SLOT_HISTORY:
			segment = uri.getPathSegments().get(1);
			if(selection != null && !selection.equals(""))
			{
				where = " AND (" + selection + ")";
			}
			count = mDb.delete(ZabbasProvider.TABLE_SLOT_HISTORY,
				Slot._ID + "=" + segment + where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException(
				getContext().getString(R.string.unknown_uri) + " " + uri);
		}
		
//		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
