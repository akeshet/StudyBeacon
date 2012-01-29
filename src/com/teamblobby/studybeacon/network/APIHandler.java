package com.teamblobby.studybeacon.network;

import android.app.Activity;

public interface APIHandler {

	public enum APICode {CODE_QUERY, CODE_ADD, CODE_JOIN, CODE_LEAVE, CODE_EDIT, CODE_SYNC, CODE_GETBEACON};

	public void onSuccess(APICode code, Object response);
	public void onFailure(APICode code, Throwable e);

}
