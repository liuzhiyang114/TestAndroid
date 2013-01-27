package com.wangjh.testandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.Window;

import com.baidu.mapapi.MapActivity;

public class firstpage extends MapActivity {

	private final int TIME_UP = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.what == TIME_UP)
			{
				Intent intent = new Intent();
				intent.setClass(firstpage.this, MainActivity.class);
				startActivity(intent);
				//overridePendingTransition(R.anim.splash_screen_fade, R.anim.splash_screen_hold);
				firstpage.this.finish();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		new Thread() 
		{
			public void run() 
			{
				try 
				{
					Thread.sleep(2000);
				} 
				catch (Exception e) 
				{

				}
				Message msg = new Message();
				msg.what = TIME_UP;
				handler.sendMessage(msg);
			}
		}.start();
		 
		
            
            
            
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
