package com.androidsrc.headlessfragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsrc.headlessfragment.HeadlessFragment.TaskStatusCallback;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends Activity implements TaskStatusCallback,
		OnClickListener {

	private HeadlessFragment mFragment;
	private ProgressBar mProgressBar;
	private TextView mProgressvalue;


	ProgressDialog _progressDialog = null;

	/**
	 * Called when activity is starting. Most initialization part is done here.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressvalue = (TextView) findViewById(R.id.progressValue);

		if (savedInstanceState != null) {
			int progress = savedInstanceState.getInt("progress_value");
			mProgressvalue.setText(progress + "%");
			mProgressBar.setProgress(progress);
		}

		FragmentManager mMgr = getFragmentManager();
		mFragment = (HeadlessFragment) mMgr
				.findFragmentByTag(HeadlessFragment.TAG_HEADLESS_FRAGMENT);

		if (mFragment == null) {
			mFragment = new HeadlessFragment();
			mMgr.beginTransaction()
					.add(mFragment, HeadlessFragment.TAG_HEADLESS_FRAGMENT)
					.commit();
		}
	}

	/**
	 * This method is called before an activity may be killed Store info in
	 * bundle if required.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("progress_value", mProgressBar.getProgress());
	}

	// Background task Callbacks

	@Override
	public void onPreExecute() {

		_progressDialog = new ProgressDialog(MainActivity.this);
		_progressDialog.setMessage("loading");
		_progressDialog.setCancelable(false);
		_progressDialog.show();

		System.out.println("====== inside onPreExecute  ");

		Toast.makeText(getApplicationContext(), "onPreExecute",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPostExecute() {

		System.out.println("====== inside onPostExecute  ");

		if(_progressDialog!=null){

			_progressDialog.dismiss();
		}
		Toast.makeText(getApplicationContext(), "onPostExecute",
				Toast.LENGTH_SHORT).show();
		if (mFragment != null)
			mFragment.updateExecutingStatus(false);
	}

	@Override
	public void onCancelled() {

		System.out.println("====== inside onCancelled  ");

		if(_progressDialog!=null){

			_progressDialog.dismiss();
		}
		Toast.makeText(getApplicationContext(), "onCancelled",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProgressUpdate(int progress) {
		System.out.println("====== inside onProgressUpdate  ");

		mProgressvalue.setText(progress + "%");
		mProgressBar.setProgress(progress);
	}

	/**
	 * Called when a view has been clicked
	 */
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
			case R.id.start:
				if (mFragment != null)
					mFragment.startBackgroundTask();

				break;
			case R.id.cancel:
				if (mFragment != null)
					mFragment.cancelBackgroundTask();

				break;
			case R.id.recreate:
				/**
				 * Cause this Activity to be recreated with a new instance. This
				 * results in essentially the same flow as when the Activity is
				 * created due to a configuration change the current instance will
				 * go through its lifecycle to onDestroy and a new instance then
				 * created after it.
				 */
				recreate();
				break;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this); // unregister EventBus
	}



}
