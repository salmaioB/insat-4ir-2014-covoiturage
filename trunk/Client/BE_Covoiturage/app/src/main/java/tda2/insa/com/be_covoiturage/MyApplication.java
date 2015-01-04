package tda2.insa.com.be_covoiturage;

/**
 * Created by remi on 15/12/14.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class MyApplication extends Application {

	private static Context context;

	public void onCreate(){
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}

	public static void presentError(Activity activity, String message) {
		MyApplication.ErrorDialogFragment f = MyApplication.ErrorDialogFragment.newInstance(message);
		f.show(activity.getFragmentManager(), "errordialog");
	}

	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() {
			super();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage((String)getArguments().get("message"));
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});

			// Create the AlertDialog object and return it
			return builder.create();
		}

		public static ErrorDialogFragment newInstance(String message) {
			ErrorDialogFragment myFragment = new ErrorDialogFragment();

			Bundle args = new Bundle();
			args.putString("message", message);
			myFragment.setArguments(args);

			return myFragment;
		}
	}

}