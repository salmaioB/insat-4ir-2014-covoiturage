package tda2.insa.com.be_covoiturage;

import android.app.DownloadManager;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by remi on 15/12/14.
 */
public class Network {
	private com.android.volley.RequestQueue _queue;
	private static Network _instance;

	public static String getHost() {
		return "felix-host.ddns.net:80";
	}

	public static int getPort() {
		return 443;
	}

	private Network() {
		_queue = Volley.newRequestQueue(MyApplication.getAppContext());
	}

	public static Network getInstance() {
		if(_instance == null) {
			_instance = new Network();
		}
		return _instance;
	}

	public void sendGetRequest(String url) {
		// Request a string response from the provided URL.
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Display the first 500 characters of the response string.
						//mTextView.setText("Response is: " + response.substring(0, 500));
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						//mTextView.setText("That didn't work!");
					}
		});
		// Add the request to the RequestQueue.
		_queue.add(stringRequest);
	}

	public static Response.Listener<JSONObject> getDefaultListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.w("Response.Listener: ", response.toString());
			}
		};
	}

	public static Response.ErrorListener getDefaultErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("ErrorListener: ", error.toString() + "\n" + ((error.networkResponse == null) ? "" : new String(error.networkResponse.data)));
			}
		};
	}

	public void sendPostRequest(String url, JSONObject body) {
		this.sendPostRequest(url, body, Network.getDefaultListener(), Network.getDefaultErrorListener());
	}

	public void sendPostRequest(String url, JSONObject body, Response.Listener<JSONObject> listener) {
		this.sendPostRequest(url, body, listener, Network.getDefaultErrorListener());
	}

	public void sendPostRequest(String url, JSONObject body, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		allowAllSSL();
		Log.w("Sending request to " + url, body.toString());
		// Request a string response from the provided URL.
		JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, body,
				listener,
				errorListener);
		// Add the request to the RequestQueue.

		_queue.add(stringRequest);
	}

	private static TrustManager[] trustManagers;

	public static class _FakeX509TrustManager implements
			javax.net.ssl.X509TrustManager {
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public boolean isClientTrusted(X509Certificate[] chain) {
			return (true);
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return (true);
		}

		public X509Certificate[] getAcceptedIssuers() {
			return (_AcceptedIssuers);
		}
	}

	public static void allowAllSSL() {

		javax.net.ssl.HttpsURLConnection
				.setDefaultHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});

		javax.net.ssl.SSLContext context = null;

		if (trustManagers == null) {
			trustManagers = new javax.net.ssl.TrustManager[] { new _FakeX509TrustManager() };
		}

		try {
			context = javax.net.ssl.SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			Log.e("allowAllSSL", e.toString());
		} catch (KeyManagementException e) {
			Log.e("allowAllSSL", e.toString());
		}
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context
				.getSocketFactory());
	}
}
