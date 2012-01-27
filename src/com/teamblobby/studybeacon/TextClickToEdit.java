package com.teamblobby.studybeacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

class TextClickToEdit extends LinearLayout {
	
	private TextView textView;
	private ImageButton button;

	public TextClickToEdit(Context context) {
		super(context);
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.textclicktoedit, this);
		
		textView = (TextView) findViewById(R.id.clicktoedittext);
		button = (ImageButton) findViewById(R.id.clicktoeditbutton);
	}
	
	public void setText(CharSequence text){
		textView.setText(text);
	}
	
	public void setButtonClickListener(OnClickListener l){
		button.setOnClickListener(l);
	}

	public TextView getTextView() {
		return textView;
	}

	public ImageButton getButton() {
		return button;
	}
	
	public void hideButton() {
		button.setVisibility(View.GONE);
	}

}
