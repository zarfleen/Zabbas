package org.burnix.zabbas.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HTTP;
import org.apache.http.client.HttpClient;

import org.json.JSONObject;

import java.security.KeyStoreException;
import javax.net.ssl.SSLContext;
import java.security.UnrecoverableKeyException;
import java.net.UnknownHostException;
import java.net.Socket;
import javax.net.ssl.TrustManager;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class SABManager
{
	private static final String PARAM_MODE = "${MODE}";
	private static final String PARAM_EXTRA = "${EXTRA}";
	private static final String PARAM_API_KEY = "${API_KEY}";
	
	private static final String URL_APPEND = "api?mode=" 
		+ PARAM_MODE + "&output=json&apikey=" + PARAM_API_KEY
		+ PARAM_EXTRA;
			
	private HttpClient mHttpClient;
	private Context mContext;
	private final String mBaseUrl;
	
	public SABManager(Context context, String url, String apiKey, int timeout)
	{
		mContext = context;
		mHttpClient = getNewHttpClient();
		
		HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(),
			timeout * 1000);
		HttpConnectionParams.setSoTimeout(mHttpClient.getParams(),
			timeout * 1000);
		
		if(url.endsWith("/"))
			mBaseUrl = (url + URL_APPEND).replace(PARAM_API_KEY,
				apiKey);
		else
			mBaseUrl = (url + "/" + URL_APPEND).replace(PARAM_API_KEY,
				apiKey);
	}
	
	public String getQueue()
	{
		String data = null;
		
		try
		{
			String mode = "queue";
			String extra = "";
			
			String url = mBaseUrl.replace(PARAM_MODE, mode)
				.replace(PARAM_EXTRA, extra);
				
			data = makeRequest(url);
		}
		catch(SABException e)
		{
			Log.e(this.getClass().toString(), e.getMessage());
		}
		catch(Throwable e)
		{
			Log.e(this.getClass().toString(), e.getMessage());
		}
		
		return data;
	}

	public boolean addByUrl(String nzb)
	{
		boolean success = false;

		try
		{
			String mode = "addurl";
			String extra = "&name=" + URLEncoder.encode(nzb, "UTF-8");

			String url = mBaseUrl.replace(PARAM_MODE, mode)
				.replace(PARAM_EXTRA, extra);

			String data = makeRequest(url);

			success = new JSONObject(data).getBoolean("status");
		}
		catch(SABException e)
		{
			Log.e(this.getClass().toString(), e.getMessage());
		}
		catch(Throwable e)
		{
			Log.e(this.getClass().toString(), e.getMessage());
		}

		return success;
	}
	
	private String makeRequest(String url) throws SABException
	{
		try
		{
			Log.d(this.getClass().toString(), url);

			HttpGet request = new HttpGet(url);
			HttpResponse response = mHttpClient.execute(request);
		
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				InputStream stream = response.getEntity().getContent();
			
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream));
				StringBuilder builder = new StringBuilder();
			
				String read = null;
				while((read = reader.readLine()) != null)
				{
					builder.append(read + "\n");
				}
			
				reader.close();
			
				String requestResponse = builder.toString();
				stream.close();
			
				return requestResponse;
			}
			else
			{
				throw new SABException(response.getStatusLine()
					.getReasonPhrase());
			}
		}
		catch(SABException e)
		{
			throw new SABException(e.getMessage());
		}
		catch(IOException e)
		{
			throw new SABException(e.getMessage());
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public class SABException extends Exception
	{
		public SABException(String message)
		{
			super(message);
		}
	}

	private class SABSocketFactory extends SSLSocketFactory
	{
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SABSocketFactory(KeyStore trustStore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException
		{
			super(trustStore);

			TrustManager tm = new X509TrustManager()
				{
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException
					{
					}

					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException
					{
					}

					public X509Certificate[] getAcceptedIssuers()
					{
						return null;
					}

				};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException
		{
			return sslContext.getSocketFactory().createSocket(
					socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException
		{
			return sslContext.getSocketFactory().createSocket();
		}
	}

	HttpClient getNewHttpClient()
	{
		try
		{
			KeyStore trustStore = KeyStore.getInstance(
				KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SABSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", 
						PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
				params, registry);

			return new DefaultHttpClient(ccm, params);

		}
		catch(Exception e)
		{
			return new DefaultHttpClient();
		}
	}
}
