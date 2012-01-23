/*
 * This follows closely MITMapView
 */

package com.teamblobby.studybeacon;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.google.android.maps.*;

public class SBMapView extends MapView {

	public final static String TAG ="SBMapView";

	boolean touched = false;
	boolean mPinchZoom = false;
	int mLastZoomDrawn = -1;

	boolean tapped_overlay = false;

	private MyLocationOverlay myLocOverlay;

	SimpleOnGestureListener mTapDetector;

	Context ctx;

	public SBMapView(Context context, String key) {
		super(context, key);
		ctx = context;
		setup();
	}
	
	public SBMapView(Context context,AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		setup();
	}
	
	public SBMapView(Context context, AttributeSet attrs, int defStyle) {
		super( context,  attrs,  defStyle);
		ctx = context;
		setup();
	}


	void setup() {

		mTapDetector = new SimpleOnGestureListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// TODO Auto-generated method stub
				return super.onSingleTapConfirmed(e);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
				//return super.onSingleTapUp(e);
			}
			
		};


		// Initialize...
		List<Overlay>  ovrlys = getOverlays();
		
		myLocOverlay = new MyLocationOverlay(ctx, this);
		
		ovrlys.add(myLocOverlay);

	}

	public void pause() {
		myLocOverlay.disableMyLocation();
	}

	public void resume() {
		myLocOverlay.enableMyLocation();
	}
	
	public void setDefaultMapPosition() {
		final MapController mapViewController = getController();
		// begin at MIT
		mapViewController.setCenter(new GeoPoint( Global.res.getInteger(R.integer.mapDefaultLatE6),Global.res.getInteger(R.integer.mapDefaultLongE6)));
		mapViewController.setZoom(Global.res.getInteger(R.integer.mapDefaultZoom));
		// show user's position
		myLocOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapViewController.animateTo(myLocOverlay.getMyLocation());
			}});
	}
	
	public void clearItemOverlays() {
		List<Overlay> overlays = getOverlays();
		
		// the first overlay is my location
		int minimumNumberOfOverlays = 1;
		while(overlays.size() > minimumNumberOfOverlays) {
			overlays.remove(overlays.size()-1);
		}		
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
            	touched = true;
            	//postInvalidate();
            	invalidate();
            }
            case MotionEvent.ACTION_UP:
            {	
            	touched = false;  // draw!
            }
            case MotionEvent.ACTION_MOVE:
            {	
            	int count = ev.getPointerCount();
            	if (count>1) {
            		mPinchZoom = true;
            		touched = true;
            	}
            	else {
            		mPinchZoom = false;
            		touched = false;
            	}
            }
        }

		
        try {
        	boolean consumedTouch = super.onTouchEvent(ev);
        	// FIXME something is always consuming event even when overlay not tapped
        	/*
        	if (!consumedTouch) {
        		if (mTapDetector.onSingleTapUp(ev)) removeAllViews();
        	}
        	*/
        	if (tapped_overlay) {
        		tapped_overlay = false;  // ignore and reset flag
        	} else {
        		if (mTapDetector.onSingleTapUp(ev)) {
        			removeAllViews();  // remove bubble bcos non-overlay was tapped
        		}	
        	}
        	return consumedTouch;
        } catch (OutOfMemoryError memoryError) {
        	Log.d(TAG, "Memory error in onTouch handler");
        	memoryError.printStackTrace();
        	System.gc();
        	return false;
        }
	}

}
