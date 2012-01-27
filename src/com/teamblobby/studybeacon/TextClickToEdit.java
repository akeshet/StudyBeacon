package com.teamblobby.studybeacon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

class TextClickToEdit extends LinearLayout {
	
	private static final String TAG = "TextClickToEdit";
	private TextView textView;
	private ImageButton button;
	private ImageButton smsButton;
	private Context context;

	public TextClickToEdit(Context c) {
		super(c);
		LayoutInflater inflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.textclicktoedit, this);
		
		textView = (TextView) findViewById(R.id.clicktoedittext);
		button = (ImageButton) findViewById(R.id.clicktoeditbutton);
		smsButton = (ImageButton) findViewById(R.id.smsbutton);
		context = c;
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
	
	public void enableSmsButton(String uriString){
		final Intent smsIntent = new Intent(Intent.ACTION_VIEW);   
		Uri uri = Uri.parse(uriString);
		smsIntent.setData(uri);
		Log.d(TAG, "uristring: "+uriString+" number:"+uri.getEncodedSchemeSpecificPart());
		smsIntent.putExtra("address", uri.getEncodedSchemeSpecificPart()); // google voice is stupid
		
		smsButton.setVisibility(View.VISIBLE);
		smsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				context.startActivity(smsIntent);
			}
		});
	}

}
