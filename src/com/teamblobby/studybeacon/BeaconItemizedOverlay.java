package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;

public class BeaconItemizedOverlay extends ItemizedOverlay {

	private static final String TAG = "BeaconItemizedOverlay";
	protected ArrayList<BeaconOverlayItem> mOverlays = new ArrayList<BeaconOverlayItem>();
	protected Context mContext;
	protected MapView mapView;
	protected MapController mc;
	private int BubbleOffset = 0;
	
	public BeaconItemizedOverlay(Drawable defaultMarker, Context context, MapView mv) {
		super(boundCenter(defaultMarker));
		mContext = context;
		this.mapView = mv;
		this.mc = mv.getController();
	}

	public void addOverlay(BeaconOverlayItem overlay) {
	    mOverlays.add(overlay);
	    // this.addBalloon(overlay);
	    populate();
	}
	
	public void removeOverlay(BeaconOverlayItem overlay) {
		mOverlays.remove(overlay);
		// TODO Do I need to do this?
		populate();
	}
	
	public void removeByBeaconId(int BeaconId) {
		removeOverlay(getByBeaconId(BeaconId));
	}
	
	public BeaconOverlayItem getByBeaconId(int BeaconId) {
		Iterator<BeaconOverlayItem> iter = mOverlays.iterator();
		while (iter.hasNext()) {
			BeaconOverlayItem item = iter.next();
			if (BeaconId == item.getBeacon().getBeaconId())
				return item;
		}
		// Failure?
		return null;
	}
	
	@Override
	protected BeaconOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  Log.d(TAG,"onTap()");
	  OverlayItem item = mOverlays.get(index);
	  mc.animateTo(item.getPoint());
	  
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  
	  return true;
	}
	
	/*
	 * 
	 * This code is borrowed from the MIT mobile app
	 * 
	 * 
	 */
	protected void addBalloon(final BeaconOverlayItem p) {
		 
		if (p == null)
			return;
		
		 GeoPoint gp = p.getPoint();
		 
		 //mapView.removeView(balloonView);
		    
		 BalloonOverlayView balloonView = new BalloonOverlayView(mContext, BubbleOffset );  
		 
		 View clickableRegion = balloonView.findViewById(R.id.balloon_inner_layout);
		 
		 clickableRegion.setOnTouchListener(new OnTouchListener() {
			 public boolean onTouch(View v, MotionEvent event) {
				 View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout); 
				 Drawable d = l.getBackground();
				 if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 int[] states = {android.R.attr.state_pressed};
					 if (d.setState(states)) {
						 d.invalidateSelf();
					 }
					 return true;
				 } else if (event.getAction() == MotionEvent.ACTION_UP) {
					 int newStates[] = {};
					 if (d.setState(newStates)) {
						 d.invalidateSelf();
					 }
					
					 handleTap(p);
					 
					 return true;
				 } else {
					 return false;
				 }
			 }
		 });

		 balloonView.setData(p);
				 
	     MapView.LayoutParams params = new MapView.LayoutParams(
	                     LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, gp,
	                     MapView.LayoutParams.BOTTOM_CENTER);
	     
	     params.mode = MapView.LayoutParams.MODE_MAP;
	     
	     balloonView.setVisibility(View.VISIBLE);
	
	     balloonView.setLayoutParams(params);
	    
	     mc.animateTo(gp); 

		 mapView.addView(balloonView);
        //mapView.addView(balloonView, params);
			
	 }


	 protected void handleTap(OverlayItem p) {
		 // TODO Figure out what to do here later
	 }
	
}
