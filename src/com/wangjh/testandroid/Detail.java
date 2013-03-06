package com.wangjh.testandroid;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.baidu.mapapi.MapActivity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Detail extends MapActivity implements OnClickListener{

	Button btnre;
	TextView residue;
	TextView name;
	TextView count;
	TextView ParkDetail;

	String sendmsg ="{" +   
		    "   \"action\" : \"getGPS\"," +  
		    "   \"success\" : \"true\"," + 
		    "   \"cardNO\" : \"1234567\"," +  
		    "   \"lat\" : \"38.61748660991222\"," + 
		    "   \"lon\" : \"104.12037670612494\"," +  
		    "   \"parkName\" : \"北京市万达停车场\"," +  
		    "   \"parkamount\" : \"1000\"," +  
		    "   \"restamount\" : \"25\"," +  
		    "   \"parkinfo\" : \"每小时收费情况说明\"" +  
		"}";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		btnre = (Button)findViewById(R.id.btnre);
		btnre.setOnClickListener(this);
		residue =(TextView)findViewById(R.id.residue);//剩余停车场位
		name =(TextView)findViewById(R.id.name);//停车场名
		count =(TextView)findViewById(R.id.count);//总停车位
		ParkDetail = (TextView)findViewById(R.id.ParkDetail);//停车场详细信息
		//设置剩余车位数字粗体
//		residue = (EditText)findViewById(R.id.residue) ;
//		TextPaint paint = residue.getPaint();  
//		paint.setFakeBoldText(true); 
	
		jjson();	
		
	}
	private void  jjson(){
		try{  
		    JSONTokener jsonParser = new JSONTokener(sendmsg);  
		    // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。  
		    // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）  
		    JSONObject detail = (JSONObject) jsonParser.nextValue();  
		   
		    // 接下来的就是JSON对象的操作了  
		    detail.getString("action");
		    detail.getString("success");
		    detail.getString("cardNO");
		    detail.getString("lat");
		    detail.getString("lon");
		    String strname=detail.getString("parkName");
		    String strcount=detail.getString("parkamount");
		    String strresidue=detail.getString("restamount");
		    String strParkDetail=detail.getString("parkinfo");
		    //修改Activity
		    name.setText(strname);//停车场名
		    count.setText(strcount);//总停车位
		    residue.setText(strresidue);//剩余停车场位
		    ParkDetail.setText(strParkDetail);//停车场详细信息
		} catch (JSONException ex) {  
		    // 异常处理代码  
			Toast.makeText(getApplicationContext(), "获取数据失败！",Toast.LENGTH_SHORT).show();
			this.finish();
		}  
		
	
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
