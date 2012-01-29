package com.teamblobby.studybeacon.network;

import android.app.Activity;

public abstract class ActivityAPIHandler implements APIHandler {

	
	public abstract Activity getActivity();
	
	public final void onFailure(final APIClient.APICode code, final Throwable e) {
		getActivity().runOnUiThread(new Runnable() {

			public void run() {
				handleFailure(code, e);
			}
		});
	}

	public final void onSuccess(final APIClient.APICode code, final Object response) {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				handleSuccess(code, response);
			}
		});
	}

	protected abstract void handleSuccess(APIClient.APICode code, Object response);
	protected abstract void handleFailure(APIClient.APICode code, Throwable e);

}
