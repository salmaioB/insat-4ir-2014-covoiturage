package tda2.insa.com.be_covoiturage;

//import Volley.*;
/**
 * Created by remi on 15/12/14.
 */
public enum Network {
	Instance

	public String getHost() {
		return "felix-host.ddns.net";
	}

	public int getPort() {
		return 443;
	}

	/*RequestQueue queue = Volley.newRequestQueue(this);
	String url ="http://www.google.com";

	// Request a string response from the provided URL.
	StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
			new Response.Listener() {
				@Override
				public void onResponse(String response) {
					// Display the first 500 characters of the response string.
					mTextView.setText("Response is: "+ response.substring(0,500));
				}
			}, new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			mTextView.setText("That didn't work!");
		}
	});
// Add the request to the RequestQueue.
	queue.add(stringRequest);*/
}
