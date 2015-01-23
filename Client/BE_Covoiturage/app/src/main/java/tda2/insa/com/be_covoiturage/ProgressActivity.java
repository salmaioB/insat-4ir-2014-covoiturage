package tda2.insa.com.be_covoiturage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by remi on 04/01/15.
 */
public class ProgressActivity extends Activity {
	// UI references.
	private LinearLayout _view;
	private ProgressBar _progressView;
	private View _childView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_view = new LinearLayout(this.getApplicationContext());
		_view.setOrientation(LinearLayout.VERTICAL);

		_progressView = new ProgressBar(getApplicationContext());

		_view.addView(_progressView);
		_progressView.setVisibility(View.GONE);

		super.setContentView(_view);
	}

	@Override
	public void setContentView(int id) {
		View view = this.getLayoutInflater().inflate(id, null);
		this.setContentView(view);
	}

	@Override
	public void setContentView(View view) {
		_childView = view;
		_view.addView(view);
	}

	/**
	 * Affiche ou masque un indicateur de progression en fonction de la valeur du paramètre.
	 * @param show Si true, alors affiche la progression, sinon elle est masquée et l'interface réapparait
	 */
	protected void showProgress(final boolean show) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		_childView.setVisibility(show ? View.GONE : View.VISIBLE);
		_childView.animate().setDuration(shortAnimTime).alpha(
				show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				_childView.setVisibility(show ? View.GONE : View.VISIBLE);
			}
		});

		_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
		_progressView.animate().setDuration(shortAnimTime).alpha(
				show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
		});
	}

}
