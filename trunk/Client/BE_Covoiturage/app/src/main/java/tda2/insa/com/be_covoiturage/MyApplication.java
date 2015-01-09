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
	private static MyApplication _instance;
	private static User _user;

	public void onCreate(){
		super.onCreate();
		_instance = this;
	}

	public static MyApplication getInstance() {
		return _instance;
	}

	public static Context getAppContext() {
		return _instance.getApplicationContext();
	}

	public static User getUser() {
		return _user;
	}

	public static void setUser(User u) {
		_user = u;
	}

	public static void presentError(Activity activity, String message, DialogInterface.OnDismissListener listener) {
		MyApplication.ErrorDialogFragment f = MyApplication.ErrorDialogFragment.newInstance(message, listener);
		f.show(activity.getFragmentManager(), "errordialog");
	}

	public static void presentError(Activity activity, String message) {
		MyApplication.presentError(activity, message, new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {}
		});
	}

	public static class ErrorDialogFragment extends DialogFragment {
		private DialogInterface.OnDismissListener _listener;

		public ErrorDialogFragment() {
			super();
		}

		public void setListener(DialogInterface.OnDismissListener l) {
			_listener = l;
		}


		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage((String)getArguments().get("message"));
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					_listener.onDismiss(dialog);
				}
			});

			// Create the AlertDialog object and return it
			return builder.create();
		}

		public static ErrorDialogFragment newInstance(String message, DialogInterface.OnDismissListener listener) {
			ErrorDialogFragment myFragment = new ErrorDialogFragment();
			myFragment.setListener(listener);

			Bundle args = new Bundle();
			args.putString("message", message);
			myFragment.setArguments(args);

			return myFragment;
		}
	}

}