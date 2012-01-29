package com.teamblobby.studybeacon;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

public class TitleBar extends LinearLayout {

	private static final String XML_NAMESPACE = "http://schemas.android.com/apk/res/android";
	
	public static final String ATTR_TEXT = "text";

	protected ImageView titleIcon;
	protected TextView titleText;
	protected Activity parentActivity;
	
	public TitleBar(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if (context instanceof Activity)
			parentActivity = (Activity) context;
		else
			parentActivity = null;
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.title_bar, this);
		
		titleIcon = (ImageView) findViewById(R.id.titleIcon);
		titleText = (TextView) findViewById(R.id.titleText);
	
		String initialText = attrs.getAttributeValue(XML_NAMESPACE, ATTR_TEXT);
		if(initialText != null) {
			if (initialText.charAt(0) == '@')
				setTitle(Global.res.getString(
						attrs.getAttributeResourceValue(XML_NAMESPACE, ATTR_TEXT,
								R.string.defaultTitle)));
			else
				setTitle(initialText);
		}
		
		// Clicking the icon will make you go home
		titleIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Global.goHome(context);
				
				if (parentActivity!=null)
					parentActivity.finish(); // slightly hackish way to fix the notifictation->edit->homebutton problem
			}
		});

	}

	public void setTitle(String text) {
		titleText.setText(text);
	}

}
