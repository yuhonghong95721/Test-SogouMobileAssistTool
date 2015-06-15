package com.sogou.mobiletoolassist.ui;

import com.sogou.mobiletoolassist.R;

import android.R.raw;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AboutTabFragment extends Fragment {
	private View view = null;
	private ToggleButton ftpsetButton = null;
	static final String ACTION_START_FTPSERVER = "be.ppareit.swiftp.ACTION_START_FTPSERVER";
	static final String ACTION_STOP_FTPSERVER = "be.ppareit.swiftp.ACTION_STOP_FTPSERVER";
	static final String FTPSERVER_STARTED = "be.ppareit.swiftp.FTPSERVER_STARTED";
	static final String FTPSERVER_STOPPED = "be.ppareit.swiftp.FTPSERVER_STOPPED";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.broadcastsetting, container, false);
		return view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		ftpsetButton = (ToggleButton) getActivity().findViewById(
				R.id.scheduleToggle);

		IntentFilter intents = new IntentFilter();
		intents.addAction(FTPSERVER_STARTED);
		intents.addAction(FTPSERVER_STOPPED);
		getActivity().registerReceiver(mStartStopReceiver, intents);
		ftpsetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onSetFtp();
			}

		});
		boolean State = getActivity().getSharedPreferences(
				getString(R.string.cfg_ftp_state), Context.MODE_PRIVATE)
				.getBoolean("isRunning", false);
		if (State) {
			ftpsetButton.setChecked(true);
			ftpsetButton.setText(getString(R.string.ftpup));
		}else {
			ftpsetButton.setChecked(false);
			ftpsetButton.setText(getString(R.string.ftpdown));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mStartStopReceiver);
	}

	public void onSetFtp() {
		if (Build.VERSION.SDK_INT < 15) {
			Toast.makeText(getActivity(),
					getString(R.string.ttFtpVersionLimit), Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (!ftpsetButton.isChecked()) {// 点击后状态会立即改变，所以这里是改变后的状态
			Intent startIntent = new Intent(ACTION_STOP_FTPSERVER);

			getActivity().sendBroadcast(startIntent);
			ftpsetButton.setChecked(true);
			ftpsetButton.setText(getString(R.string.ftpdowning));
		} else {
			Intent startIntent = new Intent(ACTION_START_FTPSERVER);
			getActivity().sendBroadcast(startIntent);
			ftpsetButton.setChecked(false);
			ftpsetButton.setText(getString(R.string.ftpuping));
		}

	}

	BroadcastReceiver mStartStopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Swiftp: notify user if ftp server is running or not
			if (intent.getAction().equals(FTPSERVER_STARTED)) {
				ftpsetButton.setChecked(true);
				ftpsetButton.setText(getString(R.string.ftpup));
				// mStatusText.setText("FTP Server is running");
			} else if (intent.getAction().equals(FTPSERVER_STOPPED)) {
				ftpsetButton.setChecked(false);
				ftpsetButton.setText(getString(R.string.ftpdown));
				// mStatusText.setText("FTP Server is down");
			}
		}
	};
}