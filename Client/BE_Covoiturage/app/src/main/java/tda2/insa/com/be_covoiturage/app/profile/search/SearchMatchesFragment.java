package tda2.insa.com.be_covoiturage.app.profile.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tda2.insa.com.be_covoiturage.app.DataFragment;
import tda2.insa.com.be_covoiturage.app.MyApplication;
import tda2.insa.com.be_covoiturage.app.profile.ProfileViewActivity;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 */
public class SearchMatchesFragment extends ListFragment implements DataFragment {
	public static String MATCHES = "matches";
	public static String DIRECTION = "direction";

	private ArrayList<ShortUser> _users;
	private boolean _direction;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SearchMatchesFragment() {
		_users = new ArrayList<>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<String> matches = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(this.getArguments().getString(MATCHES));
			for(int i = 0; i < array.length(); ++i) {
				JSONObject o = array.getJSONObject(i);
				ShortUser su = new ShortUser(o);

				_users.add(su);
				matches.add(su.getFirstName() + " " + su.getLastName());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		_direction = this.getArguments().getBoolean(DIRECTION);

		this.setListAdapter(new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_list_item_1, matches));
	}

	public String fragmentTitle() {
		return "Résultats de recherche";
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final ShortUser u = _users.get(position);

		EmailDialogFragment f = EmailDialogFragment.newInstance("Voulez-vous contater " + u.getFirstName() + " " + u.getLastName()
				+ " à propos du trajet " + (_direction ? "retour" : "aller") + " du " + ProfileViewActivity.getRoute().getWeekdayName() + " ?", new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				SearchMatchesFragment.this.sendMail(u);
			}
		}, new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {}
		});
		f.show(this.getActivity().getFragmentManager(), "emaildialog");
	}

	private void sendMail(ShortUser user) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{user.getName()});
		i.putExtra(Intent.EXTRA_SUBJECT, "Covoiturage Sopra");
		i.putExtra(Intent.EXTRA_TEXT   , "Bonjour,\nJe prend contact avec vous pour vous proposer un covoiturage pour le trajet "
				+ (_direction ? "retour" : "aller") + " du " + ProfileViewActivity.getRoute().getWeekdayName()
				+ ".\n\nCordialement,\n\n" + MyApplication.getUser().getFirstName() + " " + MyApplication.getUser().getLastName());
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		}
	}

	@Override
	public void onExit() {

	}

	public static class EmailDialogFragment extends DialogFragment {
		private DialogInterface.OnDismissListener _okListener;
		private DialogInterface.OnDismissListener _cancelListener;

		public EmailDialogFragment() {
			super();
		}

		public void setOKListener(DialogInterface.OnDismissListener l) {
			_okListener = l;
		}
		public void setCancelListener(DialogInterface.OnDismissListener l) {
			_cancelListener = l;
		}


		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage((String)getArguments().get("message"));
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					_okListener.onDismiss(dialog);
				}
			});
			builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					_cancelListener.onDismiss(dialog);
				}
			});

			// Create the AlertDialog object and return it
			return builder.create();
		}

		public static EmailDialogFragment newInstance(String message, DialogInterface.OnDismissListener okListener, DialogInterface.OnDismissListener cancelListener) {
			EmailDialogFragment myFragment = new EmailDialogFragment();
			myFragment.setOKListener(okListener);
			myFragment.setCancelListener(cancelListener);

			Bundle args = new Bundle();
			args.putString("message", message);
			myFragment.setArguments(args);

			return myFragment;
		}
	}

}
