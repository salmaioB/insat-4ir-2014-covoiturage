package tda2.insa.com.be_covoiturage;

import android.app.DownloadManager;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by remi on 15/12/14.
 */
public class Network {
	private com.android.volley.RequestQueue _queue;
	private static Network _instance;

	public static String getHost() {
		return "felix-host.ddns.net:" + getPort();
	}

	public static int getPort() {
		return 80;
	}

	public static String getHostAndProtocol() {
		return "http://" + getHost();
	}

	public static String pathToRequest(String request) {
		return Network.getHostAndProtocol() + "/Server/android/" + request;
	}

	private Network() {
		_queue = Volley.newRequestQueue(MyApplication.getAppContext());
		this.allowAllSSL();
	}

	public static Network getInstance() {
		if(_instance == null) {
			_instance = new Network();
		}
		return _instance;
	}

	public static abstract class NetworkResponseListener {
		public NetworkResponseListener() {}

		public abstract void onResponse(JSONObject data, JSONObject headers);
	}

	public static abstract class NetworkErrorListener {
		public NetworkErrorListener() {}

		public abstract void onError(String reason, VolleyError error);
	}

	public static NetworkResponseListener getDefaultListener() {
		return new NetworkResponseListener() {
			@Override
			public void onResponse(JSONObject data, JSONObject headers) {
				Log.w("NetworkResponseListener: ", data.toString());
			}
		};
	}

	public static NetworkErrorListener getDefaultErrorListener() {
		return new NetworkErrorListener() {
			@Override
			public void onError(String reason, VolleyError error) {
				Log.e("ErrorListener: ", error.toString() + "\n" + reason);
			}
		};
	}

	public void sendPostRequest(String url, JSONObject body, NetworkResponseListener listener, NetworkErrorListener errorListener) {
		if(listener == null) {
			listener = Network.getDefaultListener();
		}
		if(errorListener == null) {
			errorListener = Network.getDefaultErrorListener();
		}

		Log.w("Sending request", "to " + url + ", payload " + body.toString());
		// Request a string response from the provided URL.

		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
				Network.getVolleyListener(listener),
				Network.getVolleyListener(errorListener));

		// Add the request to the RequestQueue.
		_queue.add(request);
	}

	public void sendAuthenticatedPostRequest(String url, final AuthToken token, JSONObject body, NetworkResponseListener listener, NetworkErrorListener errorListener) {
		if(listener == null) {
			listener = Network.getDefaultListener();
		}
		if(errorListener == null) {
			errorListener = Network.getDefaultErrorListener();
		}

		Log.w("Sending authenticated request", "to " + url + ", payload " + body.toString());
		// Request a string response from the provided URL.
		final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
				Network.getVolleyListener(listener),
				Network.getVolleyListener(errorListener)) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String>  params = new HashMap<String, String>();
				params.put("Cookie", token.getToken());

				return params;
			}
		};

		// Add the request to the RequestQueue.
		_queue.add(request);
	}

	private static Response.Listener<JSONObject> getVolleyListener(final NetworkResponseListener listener) {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JSONObject data, headers;
				try {
					data = response.getJSONObject("data");
				}
				catch(JSONException e) {
					data = new JSONObject();
				}

				try {
					headers = response.getJSONObject("headers");
				}
				catch(JSONException e) {
					headers = new JSONObject();
				}

				listener.onResponse(data, headers);
			}
		};
	}

	private static Response.ErrorListener getVolleyListener(final NetworkErrorListener listener) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String reason = (error.networkResponse == null) ? "" : new String(error.networkResponse.data);
				listener.onError(reason, error);
			}
		};
	}

	private static TrustManager[] trustManagers;

	private static class _FakeX509TrustManager implements
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

	private void allowAllSSL() {

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
