package com.teamblobby.studybeacon.network;

public interface APIHandler {

	public void onSuccess(APIClient.APICode code, Object response);
	public void onFailure(APIClient.APICode code, Throwable e);

}
