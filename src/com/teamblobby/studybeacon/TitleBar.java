package com.teamblobby.studybeacon;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBar extends LinearLayout {

	private static final String XML_NAMESPACE = "http://schemas.android.com/apk/res/android";
	
	public static final String ATTR_TEXT = "text";
	
	protected ImageView titleIcon;
	protected TextView titleText;
	
	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.title_bar, this);
		
		titleIcon = (ImageView) findViewById(R.id.titleIcon);
		titleText = (TextView) findViewById(R.id.titleText);
	
		String initialText = attrs.getAttributeValue(XML_NAMESPACE, ATTR_TEXT);
		if(initialText != null) {
			setTitle(initialText);
		}
		
	}

	public void setTitle(String text) {
		titleText.setText(text);
	}

}
