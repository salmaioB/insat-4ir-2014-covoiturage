package tda2.insa.com.be_covoiturage;

import com.android.volley.*;
import com.android.volley.toolbox.*;
/**
 * Created by remi on 15/12/14.
 */
public class Network {
	private com.android.volley.RequestQueue _queue;
	private static Network _instance;

	public String getHost() {
		return "felix-host.ddns.net";
	}

	public int getPort() {
		return 443;
	}

	private Network() {
		_queue = Volley.newRequestQueue(MyApplication.getAppContext());
	}

	public void sendRequest(String url) {
		// Request a string response from the provided URL.
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Display the first 500 characters of the response string.
						//mTextView.setText("Response is: " + response.substring(0, 500));
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				//mTextView.setText("That didn't work!");
			}
		});
		// Add the request to the RequestQueue.
		_queue.add(stringRequest);
	}
}
