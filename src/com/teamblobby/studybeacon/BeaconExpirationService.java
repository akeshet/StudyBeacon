package com.teamblobby.studybeacon;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.teamblobby.studybeacon.datastructures.*;
import com.teamblobby.studybeacon.network.*;

public class BeaconExpirationService extends Service {


	private static final String TAG = BeaconExpirationService.class.getSimpleName();
	private static final long TIMER_POLL_PERIOD_MS = 1000*60*10; // 10 minute interval

	public final Binder mBinder = new Binder() {
		BeaconExpirationService getService() {
			return BeaconExpirationService.this;
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private class MyApiHandler implements APIHandler {
		public void onFailure(APIClient.APICode code, Throwable e) {
			if (code==APIClient.APICode.CODE_SYNC) {
				Log.e(TAG, "Failure when syncing from server.");
			}
			checkBeaconExpiration();
		}

		public void onSuccess(APIClient.APICode code, Object response) {
			if (code == APIClient.APICode.CODE_SYNC) {
				if (! (response instanceof BeaconInfo) ) {
					Log.e(TAG, "Received non-beaconinfo response in onSuccess.");
				} 
				else {
					BeaconInfo beacon = (BeaconInfo) response;
					Global.setCurrentBeaconUpdateNotification(beacon);
					checkBeaconExpiration();
				}
			}
		}
	}

	MyApiHandler myApiHandler = new MyApiHandler();

	private void checkBeaconExpiration() {
		BeaconInfo beacon = Global.getCurrentBeacon();
		if (beacon==null) {
			this.stopSelf();
			return;
		}
		if (beacon.getExpires().before(new Date())) {
			Global.setCurrentBeaconUpdateNotification(null);
			Global.updateBeaconRunningNotification();
			this.stopSelf();
		}
	}

	private class CheckExpiration extends TimerTask {
		@Override
		public void run() {
			if (Global.atBeacon()) {
				APIClient.sync(myApiHandler, Global.application.getApplicationContext());
			}
			else {
				timer.cancel();
				BeaconExpirationService.this.stopSelf();
			}
		}

	}

	Timer timer;

	@Override
	public void onCreate() {
			super.onCreate();

		timer = new Timer();
		timer.scheduleAtFixedRate(new CheckExpiration(), TIMER_POLL_PERIOD_MS, 
				TIMER_POLL_PERIOD_MS);

	}
	
}
