package org.xbmc.android.remote.business;

import org.xbmc.api.business.DataResponse;
import org.xbmc.api.business.INotifiableManager;
import org.xbmc.api.presentation.INotifiableController;
import org.xbmc.httpapi.WifiStateException;

import android.util.Log;

/**
 * Class to asynchronous execute backend stuff and send the result back to the GUI.
 * Holds some extras to check how often this request failed and catch all to not have the backend crashing
 * and force closing the app.
 * @author till
 *
 */
public abstract class Command<T> implements Runnable {

	public int mRetryCount = 0;
	public final INotifiableManager mManager;
	public final DataResponse<T> response;
	
	public static final int MAX_RETRY = 5;
	
	public Command(DataResponse<T> response, INotifiableManager manager) {
		this.mManager = manager;
		this.response = response;
	}
	
	public void run() {
		try {
			mRetryCount ++;
			Log.d("Command", "Running command counter: " + mRetryCount);
			if(mRetryCount > MAX_RETRY) return;
			doRun();
			mManager.onFinish(response);
		}catch (WifiStateException e) {
			mManager.onWrongConnectionState(e.getState(), this);
		}catch (Exception e) {
			mManager.onError(e);
		}
	}
	
	public abstract void doRun() throws Exception;

}
