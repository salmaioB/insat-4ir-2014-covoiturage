package tda2.insa.com.be_covoiturage.app.profile.route;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

/**
 * Created by remi on 07/01/15.
 */
public class ImageDownloader extends AsyncTask<Object, Void, Bitmap> {
	Route _route;
	ImageView _view;
	@Override
	protected Bitmap doInBackground(Object... param) {
		_route = (Route)param[1];
		return downloadBitmap((String)param[0]);
	}

	@Override
	protected void onPreExecute() {


	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if(result != null) {
			_route.setBitmap(result);
		}
	}

	private Bitmap downloadBitmap(String url) {
		// initilize the default HTTP client object
		final DefaultHttpClient client = new DefaultHttpClient();

		//forming a HttoGet request
		final HttpGet getRequest = new HttpGet(url);
		try {

			HttpResponse response = client.execute(getRequest);

			//check 200 OK for success
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode +
						" while retrieving bitmap from " + url);
				return null;

			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					// getting contents from the stream
					inputStream = entity.getContent();

					// decoding stream data back into image Bitmap that android understands
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// You Could provide a more explicit error message for IOException
			getRequest.abort();
		}

		return null;
	}
}
