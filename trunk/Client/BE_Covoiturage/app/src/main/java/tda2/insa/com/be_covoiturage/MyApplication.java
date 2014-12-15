package tda2.insa.com.be_covoiturage;

/**
 * Created by remi on 15/12/14.
 */

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	private static Context context;

	public void onCreate(){
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}
}