package com.teamblobby.studybeacon;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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
	
	private class CheckExpiration extends TimerTask {
		@Override
		public void run() {
			if (Global.atBeacon()) {
				
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
