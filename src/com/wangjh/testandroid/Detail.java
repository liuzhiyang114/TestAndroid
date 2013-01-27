package com.wangjh.testandroid;

import com.baidu.mapapi.MapActivity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Detail extends MapActivity implements OnClickListener{

	Button btnre;
	EditText residue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		btnre = (Button)findViewById(R.id.btnre);
		btnre.setOnClickListener(this);
		//设置剩余车位数字粗体
//		residue = (EditText)findViewById(R.id.residue) ;
//		TextPaint paint = residue.getPaint();  
//		paint.setFakeBoldText(true); 
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}
	
}
